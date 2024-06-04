package io.vertigo.chatbot.designer.builder.services.topic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.multilingual.dictionaryEntities.DictionaryEntityMultilingualResources;
import io.vertigo.chatbot.designer.builder.dictionaryEntity.DictionaryEntityPAO;
import io.vertigo.chatbot.designer.builder.services.HistoryServices;
import io.vertigo.chatbot.designer.builder.services.IRecordable;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.designer.commons.services.DesignerFileServices;
import io.vertigo.chatbot.designer.dao.DictionaryEntityDAO;
import io.vertigo.chatbot.designer.domain.DictionaryEntity;
import io.vertigo.chatbot.designer.domain.DictionaryEntityWrapper;
import io.vertigo.chatbot.designer.domain.History;
import io.vertigo.chatbot.designer.domain.HistoryActionEnum;
import io.vertigo.chatbot.designer.domain.Synonym;
import io.vertigo.chatbot.designer.domain.TupleSynonymIhm;
import io.vertigo.chatbot.designer.utils.HashUtils;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.locale.LocaleMessageText;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.util.StringUtil;
import io.vertigo.datamodel.criteria.Criteria;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.util.DtObjectUtil;
import io.vertigo.datamodel.structure.util.VCollectors;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.quarto.exporter.ExporterManager;
import io.vertigo.quarto.exporter.model.Export;
import io.vertigo.quarto.exporter.model.ExportBuilder;
import io.vertigo.quarto.exporter.model.ExportFormat;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
public class DictionaryEntityServices implements Component, IRecordable<DictionaryEntity> {

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
	private DesignerFileServices designerFileServices;

	@Inject
	private HistoryServices historyServices;

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
	@Secured("BotUser")
	public DictionaryEntity save(@SecuredOperation("botContributor") final Chatbot bot, final DictionaryEntity dictionaryEntity) {
		dictionaryEntity.setBotId(bot.getBotId());
		final boolean creation = dictionaryEntity.getDicEntId() == null;
		dictionaryEntity.setLabel(dictionaryEntity.getLabel().toLowerCase());

		final DictionaryEntity dictionaryEntitySaved = dictionaryEntityDAO.save(dictionaryEntity);
		if (creation) {
			final Synonym synonym = new Synonym();
			synonym.setBotId(bot.getBotId());
			synonym.setDicEntId(dictionaryEntitySaved.getDicEntId());
			synonym.setLabel(dictionaryEntitySaved.getLabel());
			if (findDictionaryEntityBySynonymLabelAndBotId(dictionaryEntitySaved.getLabel(), bot.getBotId()) != null) {
				throw new VUserException(DictionaryEntityMultilingualResources.ERR_UNIQUE_SYNONYM);
			}
			synonymServices.save(synonym);
		}
		record(bot, dictionaryEntitySaved, creation ? HistoryActionEnum.ADDED : HistoryActionEnum.UPDATED);
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
	@Secured("BotUser")
	public DictionaryEntity save(@SecuredOperation("botContributor") final Chatbot bot, final DictionaryEntity dictionaryEntity,
			final DtList<Synonym> synonyms,
			final DtList<Synonym> synonymsToDelete) {

		final boolean isNew = DtObjectUtil.getId(dictionaryEntity) == null;
		final DtList<Synonym> oldSynonyms = synonymServices.getAllSynonymByDictionaryEntity(findDictionaryEntityById(dictionaryEntity.getDicEntId()));
		if (!synonymsToDelete.isEmpty() || !HashUtils.generateHashCodeForSynonyms(oldSynonyms).equals(HashUtils.generateHashCodeForSynonyms(synonyms))) {
			nodeServices.updateNodes(bot);
		}
		saveAllNotBlankSynonym(dictionaryEntity, synonyms);
		synonymServices.removeSynonym(synonymsToDelete);

		final DictionaryEntity dictionaryEntitySaved = dictionaryEntityDAO.save(dictionaryEntity);
		record(bot, dictionaryEntitySaved, isNew ? HistoryActionEnum.ADDED : HistoryActionEnum.UPDATED);

		return dictionaryEntitySaved;
	}

	/**
	 * delete dictionary entity
	 *
	 * @param bot
	 * @param dicEntId
	 */
	@Secured("BotUser")
	public void deleteDictionaryEntity(@SecuredOperation("botAdm") final Chatbot bot, final Long dicEntId) {
		final DictionaryEntity dictionaryEntity = dictionaryEntityDAO.get(dicEntId);
		dictionaryEntityDAO.delete(dicEntId);
		record(bot, dictionaryEntity, HistoryActionEnum.DELETED);
	}

	@Secured("BotUser")
	public void deleteAllByBot(@SecuredOperation("botAdm") final Chatbot bot) {
		dictionaryEntityDAO.findAll(Criterions.isEqualTo(DtDefinitions.DictionaryEntityFields.botId, bot.getBotId()),
				DtListState.of(MAX_ELEMENTS_PLUS_ONE)).forEach(dictionaryEntity ->  {
					synonymServices.removeSynonym(synonymServices.getAllSynonymByDictionaryEntity(dictionaryEntity));
					dictionaryEntityDAO.delete(dictionaryEntity.getDicEntId());
		});
	}

	/**
	 * Add a synonym and modify table
	 *
	 * @param newSynonymIn
	 * @param synonyms
	 */
	@Secured("BotUser")
	public void addSynonym(@SecuredOperation("botContributor") final Chatbot bot, final String newSynonymIn,
						   final DtList<Synonym> synonyms) {
		if (StringUtil.isBlank(newSynonymIn)) {
			return;
		}

		final String newSynonym = newSynonymIn.trim();

		final boolean exists = synonyms.stream()
				.anyMatch(its -> its.getLabel().equalsIgnoreCase(newSynonym));
		if (exists) {
			throw new VUserException(DictionaryEntityMultilingualResources.ERR_UNIQUE_SYNONYM);
		}

		final Synonym newText = new Synonym();
		newText.setLabel(newSynonym);

		synonyms.add(newText);
	}

	@Secured("BotUser")
	public Synonym removeSynonym(@SecuredOperation("botAdm") final Chatbot bot, final int index,
							  final DtList<Synonym> synonyms) {
		return synonyms.remove(index);
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
		final Criteria<DictionaryEntity> criteria = Criterions.isEqualTo(DtDefinitions.DictionaryEntityFields.botId, botId)
				.and(Criterions.isEqualTo(DtDefinitions.DictionaryEntityFields.label, label));
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
	 *
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
	 *
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
	public VFile exportDictionary(@SecuredOperation("botVisitor") final Chatbot bot, final DtList<DictionaryEntityWrapper> dtc) {
		final String exportName = "export_" + LocaleMessageText.of(DictionaryEntityMultilingualResources.DICTIONARY).getDisplay() + "_" + bot.getName();
		final Export export = new ExportBuilder(ExportFormat.CSV, exportName)
				.beginSheet(dtc, null)
				.addField(DtDefinitions.DictionaryEntityWrapperFields.dictionaryEntityLabel)
				.addField(DtDefinitions.DictionaryEntityWrapperFields.synonymsList)
				.endSheet()
				.build();

		return exportManager.createExportFile(export);

	}

	/*
	 * Return a list of DictionaryExport from a CSV file
	 */
	public List<DictionaryEntityWrapper> transformFileToList(@SecuredOperation("SuperAdm") final VFile file) {
		final String[] columns = new String[] {
				DtDefinitions.DictionaryEntityWrapperFields.dictionaryEntityLabel.name(),
				DtDefinitions.DictionaryEntityWrapperFields.synonymsList.name(),
		};
		return designerFileServices.readCsvFile(DictionaryEntityWrapper.class, file, columns);
	}

	/**
	 * Use a CSV file to import a dictionary within a specific chatbot
	 *
	 * @param chatbot
	 * @param importDictionaryFile
	 */
	public void importDictionaryFromCSVFile(final Chatbot chatbot, final FileInfoURI importDictionaryFile) {
		transformFileToList(designerFileServices.getFileTmp(importDictionaryFile)).forEach(dex -> generateDictionaryFromDictionaryExport(dex, chatbot));
	}

	/*
	 * Generate dictionary entity and synonyms from a DictionaryExport
	 */
	public void generateDictionaryFromDictionaryExport(final DictionaryEntityWrapper dex, final Chatbot chatbot) {

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
			dictionaryEntitySaved = save(chatbot, dictionaryEntity);
		}
		final DtList<Synonym> listSynonyms = synonymServices.extractSynonymsFromDictionaryExport(dex, dictionaryEntitySaved);
		for (final Synonym synonym : listSynonyms) {
			final Optional<Synonym> synonymBase = synonymServices.findSynonymByLabelAndMeaId(dictionaryEntitySaved.getDicEntId(), synonym.getLabel());
			//If the synonym is not already in database, it is saved
			if (!synonymBase.isPresent()) {
				synonymServices.save(synonym);
			}
		}

	}

	@Override
	public History record(final Chatbot bot, final DictionaryEntity dictionaryEntity, final HistoryActionEnum action) {
		return historyServices.record(bot, action, dictionaryEntity.getClass().getSimpleName(), dictionaryEntity.getLabel());
	}
}
