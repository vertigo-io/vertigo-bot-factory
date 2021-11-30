package io.vertigo.chatbot.designer.builder.services.topic;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.multilingual.export.ExportMultilingualResources;
import io.vertigo.chatbot.commons.multilingual.dictionaryEntities.DictionaryEntityMultilingualResources;
import io.vertigo.chatbot.designer.builder.dictionaryEntity.DictionaryEntityPAO;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.designer.commons.services.FileServices;
import io.vertigo.chatbot.designer.dao.DictionaryEntityDAO;
import io.vertigo.chatbot.designer.domain.DictionaryEntity;
import io.vertigo.chatbot.designer.domain.DictionaryEntityWrapper;
import io.vertigo.chatbot.designer.domain.Synonym;
import io.vertigo.chatbot.designer.domain.TupleSynonymIhm;
import io.vertigo.chatbot.designer.utils.HashUtils;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.locale.MessageText;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.util.StringUtil;
import io.vertigo.datamodel.criteria.Criteria;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.util.VCollectors;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.datastore.filestore.util.VFileUtil;
import io.vertigo.quarto.exporter.ExporterManager;
import io.vertigo.quarto.exporter.model.Export;
import io.vertigo.quarto.exporter.model.ExportBuilder;
import io.vertigo.quarto.exporter.model.ExportFormat;
import liquibase.util.csv.CSVReader;
import liquibase.util.csv.opencsv.bean.ColumnPositionMappingStrategy;
import liquibase.util.csv.opencsv.bean.CsvToBean;

import static io.vertigo.chatbot.designer.utils.StringUtils.errorManagement;

@Transactional
public class DictionaryEntityServices implements Component {

	@Inject
	private DictionaryEntityDAO dictionaryEntityDAO;

	@Inject
	private DictionaryEntityPAO dictionaryEntityPAO;

	@Inject
	private SynonymServices synonymServices;

	@Inject
	private NodeServices nodeServices;

	@Inject
	private ExporterManager exportManager;

	@Inject
	private FileServices fileServices;

	private final int SIZE_FILE = 2;

	public DictionaryEntityServices() {
	}

	/**
	 * get Dictionary entity by id
	 *
	 * @param id
	 * @return dictionaryEntity
	 */
	public DictionaryEntity findDictionaryEntityById(final Long id) {
		return dictionaryEntityDAO.get(id);
	}

	/**
	 * Save dictionary entity
	 *
	 * @param dictionaryEntity
	 * @return dictionaryEntity
	 */
	public DictionaryEntity save(@SecuredOperation("botAdm") final Chatbot chatbot, final DictionaryEntity dictionaryEntity) {
		dictionaryEntity.setBotId(chatbot.getBotId());
		final boolean creation = dictionaryEntity.getDicEntId() == null;
		dictionaryEntity.setLabel(dictionaryEntity.getLabel().toLowerCase());

		final DictionaryEntity dictionaryEntitySaved = dictionaryEntityDAO.save(dictionaryEntity);
		if (creation) {
			final Synonym synonym = new Synonym();
			synonym.setBotId(chatbot.getBotId());
			synonym.setDicEntId(dictionaryEntitySaved.getDicEntId());
			synonym.setLabel(dictionaryEntitySaved.getLabel());
			if (findDictionaryEntityBySynonymLabelAndBotId(dictionaryEntitySaved.getLabel(), chatbot.getBotId()) != null) {
				throw new VUserException(DictionaryEntityMultilingualResources.ERR_UNIQUE_SYNONYM);
			}
			synonymServices.save(synonym);
		}
		return dictionaryEntitySaved;
	}

	/**
	 * Save dictionaryEntity with synonyms
	 *
	 * @param dictionaryEntity
	 * @param synonyms
	 * @param synonymsToDelete
	 * @return dictionaryEntity
	 */
	public DictionaryEntity save(@SecuredOperation("botAdm") final Chatbot chatbot, final DictionaryEntity dictionaryEntity,
			final DtList<Synonym> synonyms,
			final DtList<Synonym> synonymsToDelete) {

		final DtList<Synonym> oldSynonyms = synonymServices.getAllSynonymByDictionaryEntity(findDictionaryEntityById(dictionaryEntity.getDicEntId()));
		if (!synonymsToDelete.isEmpty() || !HashUtils.generateHashCodeForSynonyms(oldSynonyms).equals(HashUtils.generateHashCodeForSynonyms(synonyms))) {
			nodeServices.updateNodes(chatbot);
		}
		saveAllNotBlankSynonym(dictionaryEntity, synonyms);
		synonymServices.removeSynonym(synonymsToDelete);

		DictionaryEntity dictionaryEntitySaved = dictionaryEntityDAO.save(dictionaryEntity);
		if (synonyms.isEmpty()) {
			//If all synonyms are removed, the dictionaryEntity is deleted
			deleteDictionaryEntity(chatbot, dictionaryEntity.getDicEntId());
		}
		return dictionaryEntitySaved;
	}

	/**
	 * delete dictionary entity
	 *
	 * @param bot
	 * @param dicEntId
	 */
	public void deleteDictionaryEntity(@SecuredOperation("botAdm") final Chatbot bot, final Long dicEntId) {
		dictionaryEntityDAO.delete(dicEntId);
	}

	/**
	 * Save synonyms
	 *
	 * @param dictionaryEntity
	 * @param synonyms
	 * @return list of synonyms
	 */
	protected DtList<Synonym> saveAllNotBlankSynonym(final DictionaryEntity dictionaryEntity, final DtList<Synonym> synonyms) {

		final Long dicEntId = dictionaryEntity.getDicEntId();
		final Long botId = dictionaryEntity.getBotId();
		return synonyms.stream()
				.filter(syn -> !StringUtil.isBlank(syn.getLabel()))
				.map(syn -> createSynonym(syn, dicEntId, botId))
				.collect(VCollectors.toDtList(Synonym.class));
	}

	/**
	 * create Synonym
	 *
	 * @param syn
	 * @param dicEntId
	 * @param botId
	 * @return synonym
	 */
	protected Synonym createSynonym(final Synonym syn, final Long dicEntId, final Long botId) {
		syn.setDicEntId(dicEntId);
		syn.setBotId(botId);
		return synonymServices.save(syn);
	}

	/**
	 * get dictionary entity by label and botId
	 *
	 * @param label
	 * @param botId
	 * @return dictionaryEntity
	 */
	public DictionaryEntity findDictionaryEntityBySynonymLabelAndBotId(final String label, final Long botId) {
		final Optional<DictionaryEntity> result = dictionaryEntityDAO.getDictionaryEntityBySynonymLabelAndBotId(botId, label);
		return result.orElse(null);
	}

	public Optional<DictionaryEntity> findDictionaryEntityByLabelAndBotId(final Long botId, final String label) {
		final Criteria<DictionaryEntity> criteria = Criterions.isEqualTo(DtDefinitions.DictionaryEntityFields.botId, botId).and(Criterions.isEqualTo(DtDefinitions.DictionaryEntityFields.label, label));
		return dictionaryEntityDAO.findOptional(criteria);
	}

	/*
	 * Recursive method to generate nlu with synonyms from a list of sentence
	 * For a dictionary entity, a sentence is generated from each sentence and each synonym,
	 * and then it is used to generate more sentences with the next group of synonyms.
	 *
	 * Example : original sentence T W1 T' W2 T''
	 * W1 has as a dictionary entity M1, with synonyms S1 and S'1 (S1 == W1)
	 * W2 has as a dictionary entity M2, with synonyms S2 and S'2 (S2 == W2)
	 * T, T' and T'' are strings with no dictionary entity found
	 *
	 * There are 2 differents dictionary entities in the sentence, so there will be two calls to the method combine.
	 * So first, the folowing sentence will be generated : 	T S1  T' W2 T''
	 * 														T S'1 T' W2 T''
	 *
	 * and then on the second loop, same work done but on the two sentences we just generated, and with the second dictionary entity :
	 * 														T S1  T' S2  T''
	 * 														T S1  T' S'2 T''
	 * 														T S'1 T' S2  T''
	 * 														T S'1 T' S'2 T''
	 *
	 *
	 */
	public ArrayList<String> combine(final Map<String, List<TupleSynonymIhm>> listTupleSynonymIhmPerWord, final String nluOriginal) {

		final ArrayList<String> listText = new ArrayList<String>();
		listText.add(nluOriginal);

		// loop on words with synonyms
		for (final Entry<String, List<TupleSynonymIhm>> entry : listTupleSynonymIhmPerWord.entrySet()) {

			final List<TupleSynonymIhm> listTupleSynonyms = entry.getValue();

			// The size of the listEntry is saved because listEntry is modified in the loops
			final int size = listText.size();
			// loop on text entries (sentences already generated)
			for (int j = 0; j < size; j++) {
				// loop on synonyms
				for (final TupleSynonymIhm tupleSyn : listTupleSynonyms) {
					// the sentence is generated with the word replaced by a synonym
					listText.add(populateExit(tupleSyn, listText.get(j)));
				}
			}

		}

		return listText;
	}

	/**
	 * Modify entry by replacing occurence of a word with its synonyms
	 * @param tupleSyn
	 * @param entry
	 * @return
	 */
	private String populateExit(final TupleSynonymIhm tupleSyn, final String entry) {
		return entry.replaceAll(tupleSyn.getWord(), tupleSyn.getSynonymLabel());
	}

	/**
	 * Return a list tuple <word,synonym>
	 *
	 * @param words
	 * @param botId
	 * @return list of TupleSynonymIhm
	 */
	public DtList<TupleSynonymIhm> getTuplesSynonym(final List<String> words, final Long botId) {
		return dictionaryEntityPAO.getTuplesSynonym(botId, words);
	}

	/**
	 * Return a list of DictionaryEntityWrapper by botId
	 * @param botId
	 * @param separator
	 * @return list of DictionaryEntityWrapper
	 */
	public DtList<DictionaryEntityWrapper> getDictionaryExportByBotId(final Long botId, final String separator) {
		return dictionaryEntityPAO.getDictionaryEntityWrapperByBotId(botId, separator);
	}

	/*
	 * Return a File from a list of DictionaryExport
	 */
	public VFile exportDictionary(@SecuredOperation("SuperAdm") final Chatbot bot, final DtList<DictionaryEntityWrapper> dtc) {
		final String exportName = "export " + MessageText.of(DictionaryEntityMultilingualResources.DICTIONARY).getDisplay() + " " + bot.getName();
		final Export export = new ExportBuilder(ExportFormat.CSV, exportName)
				.beginSheet(dtc, null)
				.addField(DtDefinitions.DictionaryEntityWrapperFields.dictionaryEntityLabel)
				.addField(DtDefinitions.DictionaryEntityWrapperFields.synonymsList)
				.endSheet()
				.build();
		final VFile result = exportManager.createExportFile(export);

		return result;

	}

	/*
	 * Return a list of DictionaryExport from a CSV file
	 */
	private List<DictionaryEntityWrapper> transformFileToList(@SecuredOperation("SuperAdm") final CSVReader csvReader) throws IOException {

		// Check length of header, to make sure all columns are there
		final String[] header = csvReader.readNext();
		if (header.length != SIZE_FILE) {
			throw new VUserException(ExportMultilingualResources.ERR_SIZE_FILE, SIZE_FILE);
		}
		try {
			final CsvToBean<DictionaryEntityWrapper> csvToBean = new CsvToBean<>();
			final ColumnPositionMappingStrategy<DictionaryEntityWrapper> mappingStrategy = new ColumnPositionMappingStrategy<>();
			//Set mappingStrategy type to DictionaryExport Type
			mappingStrategy.setType(DictionaryEntityWrapper.class);
			//Fields in TopicFileExport Bean (to avoid alphabetical order)
			final String[] columns = new String[] {
					DtDefinitions.DictionaryEntityWrapperFields.dictionaryEntityLabel.name(),
					DtDefinitions.DictionaryEntityWrapperFields.synonymsList.name(),

			};
			//Setting the colums for mappingStrategy
			mappingStrategy.setColumnMapping(columns);
			final List<DictionaryEntityWrapper> list = csvToBean.parse(mappingStrategy, csvReader);
			return list;
		} catch (final Exception e) {
			final StringBuilder errorMessage = new StringBuilder(MessageText.of(ExportMultilingualResources.ERR_MAPPING_FILE).getDisplay());
			errorMessage.append(e);
			throw new VUserException(errorMessage.toString());
		}
	}

	/*
	 * Use a list of DictionaryExport to create/modify dictionary entities and synonyms
	 */
	private void importDictionaryFromList(@SecuredOperation("SuperAdm") final Chatbot chatbot, final List<DictionaryEntityWrapper> list) throws IOException {

		int line = 1;
		for (final DictionaryEntityWrapper dex : list) {
			line++;
			generateDictionaryFromDictionaryExport(dex, chatbot, line);
		}

	}

	/**
	 * Use a CSV file to import a dictionary within a specific chatbot
	 * @param chatbot
	 * @param importDictionaryFile
	 */
	public void importDictionaryFromCSVFile(Chatbot chatbot, FileInfoURI importDictionaryFile) {
		final VFile fileTmp = fileServices.getFileTmp(importDictionaryFile);
		if (!fileServices.isCSVFile(fileTmp)) {
			throw new VUserException(ExportMultilingualResources.ERR_CSV_FILE);
		}
		try (CSVReader csvReader = new CSVReader(new FileReader(VFileUtil.obtainReadOnlyPath(fileTmp).toString(), Charset.forName("cp1252")), ';', CSVReader.DEFAULT_QUOTE_CHARACTER, 0)) {

			final List<DictionaryEntityWrapper> list = transformFileToList(csvReader);

			importDictionaryFromList(chatbot, list);
		} catch (final Exception e) {
			throw new VUserException(ExportMultilingualResources.ERR_UNEXPECTED);
		}
	}

	/*
	 * Generate dictionary entity and synonyms from a DictionaryExport
	 */
	private void generateDictionaryFromDictionaryExport(final DictionaryEntityWrapper dex,
			final Chatbot chatbot,
			final int line) {

		try {

			final DictionaryEntity dictionaryEntity = new DictionaryEntity();

			//Try to find the dictionaryEntity in database
			final Optional<DictionaryEntity> dictionaryEntityBase = findDictionaryEntityByLabelAndBotId(chatbot.getBotId(), dex.getDictionaryEntityLabel());
			final DictionaryEntity dictionaryEntitySaved;
			if (dictionaryEntityBase.isPresent()) {
				dictionaryEntitySaved = dictionaryEntityBase.get();
			} else {
				// if the dictionaryEntity is not already in database, it is saved
				dictionaryEntity.setBotId(chatbot.getBotId());
				dictionaryEntity.setLabel(dex.getDictionaryEntityLabel());
				dictionaryEntitySaved = this.save(chatbot, dictionaryEntity);
			}
			final DtList<Synonym> listSynonyms = synonymServices.extractSynonymsFromDictionaryExport(dex, dictionaryEntitySaved);
			for (final Synonym synonym : listSynonyms) {
				final Optional<Synonym> synonymBase = synonymServices.findSynonymByLabelAndMeaId(dictionaryEntitySaved.getDicEntId(), synonym.getLabel());
				//If the synonym is not already in database, it is saved
				if (!synonymBase.isPresent()) {
					synonymServices.save(synonym);
				}
			}

		} catch (final Exception e) {
			final StringBuilder erreur = new StringBuilder(MessageText.of(DictionaryEntityMultilingualResources.ERR_IMPORT, dex.getDictionaryEntityLabel()).getDisplay());
			erreur.append(e.getMessage());
			errorManagement(line, erreur.toString());
		}
	}

}
