package io.vertigo.chatbot.designer.builder.services.topic.export.file;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.KindTopicEnum;
import io.vertigo.chatbot.commons.domain.topic.NluTrainingSentence;
import io.vertigo.chatbot.commons.domain.topic.ResponseButton;
import io.vertigo.chatbot.commons.domain.topic.ResponseTypeEnum;
import io.vertigo.chatbot.commons.domain.topic.ScriptIntention;
import io.vertigo.chatbot.commons.domain.topic.SmallTalk;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TopicCategory;
import io.vertigo.chatbot.commons.domain.topic.TopicFileExport;
import io.vertigo.chatbot.commons.domain.topic.TypeTopicEnum;
import io.vertigo.chatbot.commons.domain.topic.UtterText;
import io.vertigo.chatbot.commons.multilingual.topicFileExport.TopicFileExportMultilingualResources;
import io.vertigo.chatbot.designer.builder.services.topic.NluTrainingSentenceServices;
import io.vertigo.chatbot.designer.builder.services.topic.ScriptIntentionServices;
import io.vertigo.chatbot.designer.builder.services.topic.SmallTalkServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicCategoryServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicLabelServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.chatbot.designer.builder.topicFileExport.TopicFileExportPAO;
import io.vertigo.chatbot.domain.DtDefinitions.TopicFileExportFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.locale.MessageText;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.quarto.exporter.ExporterManager;
import io.vertigo.quarto.exporter.model.Export;
import io.vertigo.quarto.exporter.model.ExportBuilder;
import io.vertigo.quarto.exporter.model.ExportFormat;
import liquibase.util.csv.CSVReader;
import liquibase.util.csv.opencsv.bean.ColumnPositionMappingStrategy;
import liquibase.util.csv.opencsv.bean.CsvToBean;

@Transactional
public class TopicFileExportServices implements Component {

	@Inject
	private TopicFileExportPAO topicFileExportPAO;

	@Inject
	private TopicCategoryServices topicCategoryServices;

	@Inject
	private ScriptIntentionServices scriptIntentionServices;

	@Inject
	private SmallTalkServices smallTalkServices;

	@Inject
	private TopicServices topicServices;

	@Inject
	private NluTrainingSentenceServices nluTrainingSentenceServices;

	@Inject
	private TopicLabelServices topicLabelServices;

	@Inject
	private ExporterManager exportManager;

	/*
	 * Return a File from a list of topicFileExport
	 */
	public VFile exportTopicFile(@SecuredOperation("SuperAdm") final Chatbot bot, final DtList<TopicFileExport> dtc) {

		final Export export = new ExportBuilder(ExportFormat.CSV, "export " + bot.getName())
				.beginSheet(dtc, null)
				.addField(TopicFileExportFields.code)
				.addField(TopicFileExportFields.typeTopic)
				.addField(TopicFileExportFields.title)
				.addField(TopicFileExportFields.category)
				.addField(TopicFileExportFields.description)
				.addField(TopicFileExportFields.tag)
				.addField(TopicFileExportFields.dateStart)
				.addField(TopicFileExportFields.dateEnd)
				.addField(TopicFileExportFields.active)
				.addField(TopicFileExportFields.script)
				.addField(TopicFileExportFields.trainingPhrases)
				.addField(TopicFileExportFields.response)
				.addField(TopicFileExportFields.buttons)
				.addField(TopicFileExportFields.isEnd)
				.addField(TopicFileExportFields.labels)
				.endSheet()
				.build();
		final VFile result = exportManager.createExportFile(export);

		return result;

	}

	/*
	 * Return a list of TopicFileExport from a bot (and possibly a category)
	 */
	public DtList<TopicFileExport> getTopicFileExport(@SecuredOperation("SuperAdm") final Long botId, final Long topCatId) {
		final Optional<Long> topCatIdOpt = topCatId != null ? Optional.of(topCatId) : Optional.empty();
		return topicFileExportPAO.getTopicFileExport(botId, topCatIdOpt);
	}

	/*
	 * Return a list of TopicFileExport from a CSV file
	 */
	public List<TopicFileExport> transformFileToList(@SecuredOperation("SuperAdm") final CSVReader csvReader) {
		try {
			// Check length of header, to make sure all columns are there
			final String[] header = csvReader.readNext();
			if (header.length != 15) {
				throw new VUserException(TopicFileExportMultilingualResources.ERR_SIZE_FILE);
			}
			final CsvToBean<TopicFileExport> csvToBean = new CsvToBean<TopicFileExport>();
			final ColumnPositionMappingStrategy<TopicFileExport> mappingStrategy = new ColumnPositionMappingStrategy<TopicFileExport>();
			//Set mappingStrategy type to TopicFileExport Type
			mappingStrategy.setType(TopicFileExport.class);
			//Fields in TopicFileExport Bean (to avoid alphabetical order)
			final String[] columns = new String[] {
					TopicFileExportFields.code.name(),
					TopicFileExportFields.typeTopic.name(),
					TopicFileExportFields.title.name(),
					TopicFileExportFields.category.name(),
					TopicFileExportFields.description.name(),
					TopicFileExportFields.tag.name(),
					TopicFileExportFields.dateStart.name(),
					TopicFileExportFields.dateEnd.name(),
					TopicFileExportFields.active.name(),
					TopicFileExportFields.script.name(),
					TopicFileExportFields.trainingPhrases.name(),
					TopicFileExportFields.response.name(),
					TopicFileExportFields.buttons.name(),
					TopicFileExportFields.isEnd.name(),
					TopicFileExportFields.labels.name()
			};
			//Setting the colums for mappingStrategy
			mappingStrategy.setColumnMapping(columns);
			final List<TopicFileExport> list = csvToBean.parse(mappingStrategy, csvReader);
			return list;
		} catch (final Exception e) {
			final StringBuilder errorMessage = new StringBuilder(MessageText.of(TopicFileExportMultilingualResources.ERR_MAPPING_FILE).getDisplay());
			errorMessage.append(e);
			throw new VUserException(errorMessage.toString());
		}
	}

	/*
	 * Use a list of TopicFileExport to create/modify topics
	 */
	public void importTopicFromList(@SecuredOperation("SuperAdm") final Chatbot chatbot, final List<TopicFileExport> list) throws IOException {

		codeCheck(list);

		final Map<String, Topic> mapTopic = new HashMap<>();
		final Map<String, Boolean> mapCreation = new HashMap<>();

		//Map category initialization (only one call to database)
		final Map<String, Long> mapCategory = mapCategoryInitialization(chatbot);

		//First, create/modify topic (topic might be referenced by small talk, so it has to be done in another loop)
		int line = 1;
		for (final TopicFileExport tfe : list) {
			line++;
			generateTopicShellFromTopicFileExport(tfe, mapCategory, chatbot, line, mapTopic, mapCreation);
		}

		//Then, create/modify smallTalk/ScriptIntention (topics just created may be referenced in the response button)
		line = 1;
		for (final TopicFileExport tfe : list) {
			line++;
			generateTopicCompleteFromTopicFileExport(tfe, mapCategory, chatbot, line, mapTopic, mapCreation);

		}

	}

	/*
	 * Return a map of all categories of a chatbot
	 */
	private Map<String, Long> mapCategoryInitialization(final Chatbot chatbot) {
		final DtList<TopicCategory> listCategory = topicCategoryServices.getAllCategoriesByBot(chatbot);
		final Map<String, Long> mapCategory = new HashMap<>();
		for (final TopicCategory category : listCategory) {
			mapCategory.put(category.getCode(), category.getTopCatId());
		}
		return mapCategory;
	}

	/*
	 * Check that every code in a list of TopicFileExport exists and is unique
	 */
	private void codeCheck(final List<TopicFileExport> list) {
		// Unicity check
		final HashSet<String> codeSet = new HashSet<String>();
		int i = 1;
		for (final TopicFileExport tfe : list) {
			i++;
			if (tfe.getCode().isEmpty() || tfe.getCode().isBlank()) {
				errorManagement(i, MessageText.of(TopicFileExportMultilingualResources.ERR_CODE_EMPTY).getDisplay());
			}
			if (!codeSet.add(tfe.getCode())) {
				final StringBuilder erreur = new StringBuilder(MessageText.of(TopicFileExportMultilingualResources.ERR_CODE_DUPLICATED, tfe.getCode()).getDisplay());
				errorManagement(i, erreur.toString());
			}
		}

	}

	/*
	 * Generate an incomplete topic from a topicFileExport (topics may referenced other topics, so it have to be completed in another loop)
	 */
	private void generateTopicShellFromTopicFileExport(final TopicFileExport tfe,
			final Map<String, Long> mapCategory,
			final Chatbot chatbot,
			final int line,
			final Map<String, Topic> mapTopic,
			final Map<String, Boolean> mapCreation) {

		try {
			boolean creation = true;

			//Check if category is in mapCategory (in database)
			final Long topCatId = checkCategory(tfe, mapCategory);

			checkType(tfe);
			checkTitle(tfe);
			// if the field active is not correctly given, the topic will be import as inactive
			final boolean isEnabled = "ACTIVE".equals(tfe.getActive());

			// The list of nluTS to deleted can be modified if the topic already exists
			DtList<NluTrainingSentence> nluTSToDelete = new DtList<NluTrainingSentence>(NluTrainingSentence.class);

			Topic topic = new Topic();

			//Try to find the topic in database
			final Optional<Topic> topicBase = topicServices.getTopicByCode(tfe.getCode(), chatbot.getBotId());

			if (topicBase.isPresent()) {
				// If it already exists, then modification
				topic = topicBase.get();
				creation = false;
				// All preexisting nlu will be replaced by the ones in the file
				nluTSToDelete = topicServices.getNluTrainingSentenceByTopic(chatbot, topic);
			}

			// populate topic with infos from the TopicFileExport
			topic = populateTopic(topic, chatbot, tfe, isEnabled, topCatId, nluTSToDelete, creation);

			final Topic topicSaved = topicServices.save(topic, chatbot, topic.getIsEnabled(), new DtList<NluTrainingSentence>(NluTrainingSentence.class),
					nluTSToDelete);

			// At this point, topicSaved isEnabled is false, because the topic has no nluTrainingSentences.
			// So isEnabled is resetted anyway. it will be calculated again in the next loop
			topicSaved.setIsEnabled(isEnabled);

			// Informations are stored in maps to be used in the second loop
			mapTopic.put(tfe.getCode(), topicSaved);
			mapCreation.put(tfe.getCode(), creation);

		} catch (final Exception e) {
			final StringBuilder erreur = new StringBuilder(MessageText.of(TopicFileExportMultilingualResources.ERR_TOPIC, tfe.getCode()).getDisplay());
			erreur.append(e.getMessage());
			errorManagement(line, erreur.toString());
		}
	}

	/*
	 * Return id of category
	 */
	private Long checkCategory(final TopicFileExport tfe, final Map<String, Long> mapCategory) {
		final Long topCatId = mapCategory.get(tfe.getCategory());
		if (topCatId == null) {
			throw new VUserException(TopicFileExportMultilingualResources.ERR_CATEGORY, tfe.getCategory());
		}
		return topCatId;
	}

	/*
	 * Check that type is either SCRIPINTENTION or SMALLTALK
	 */
	private void checkType(final TopicFileExport tfe) {
		if (!TypeTopicEnum.SCRIPTINTENTION.name().equals(tfe.getTypeTopic()) && !TypeTopicEnum.SMALLTALK.name().equals(tfe.getTypeTopic())) {
			throw new VUserException(TopicFileExportMultilingualResources.ERR_TYPE_TOPIC);
		}
	}

	/*
	 * Check that title is not empty
	 */
	private void checkTitle(final TopicFileExport tfe) {
		if (tfe.getTitle().isEmpty() || tfe.getTitle().isBlank()) {
			throw new VUserException(TopicFileExportMultilingualResources.ERR_TITLE);
		}
	}

	/*
	 * Return a topic with infos from the TopicFileExport
	 */
	private Topic populateTopic(final Topic topic,
			final Chatbot chatbot,
			final TopicFileExport tfe,
			final boolean isEnabled,
			final Long topCatId,
			final DtList<NluTrainingSentence> nluTSToDelete,
			final boolean creation) {

		topic.setBotId(chatbot.getBotId());
		topic.setTitle(tfe.getTitle());
		topic.setDescription(tfe.getDescription());
		topic.setTopCatId(topCatId);
		topic.setKtoCd(KindTopicEnum.NORMAL.name());
		topic.setTtoCd(tfe.getTypeTopic());
		topic.setCode(tfe.getCode());
		topic.setIsEnabled(isEnabled);
		return topic;
	}

	/*
	 * Generate and save NluTS and ScriptIntention or SmallTalk from TopicFileExport
	 */
	private void generateTopicCompleteFromTopicFileExport(final TopicFileExport tfe,
			final Map<String, Long> mapCategory,
			final Chatbot chatbot,
			final int line,
			final Map<String, Topic> mapTopic,
			final Map<String, Boolean> mapCreation) {

		try {

			final Topic topicSaved = mapTopic.get(tfe.getCode());
			final boolean creation = mapCreation.get(tfe.getCode());

			final DtList<NluTrainingSentence> nluTrainingSentences = nluTrainingSentenceServices.extractNlutsFromTfe(tfe);

			// The topic is completed as a smallTalk or a ScriptIntention accordingly to the type given
			if (TypeTopicEnum.SCRIPTINTENTION.name().equals(tfe.getTypeTopic())) {
				gestionScriptIntention(chatbot, topicSaved, tfe, creation, nluTrainingSentences);
			} else if (TypeTopicEnum.SMALLTALK.name().equals(tfe.getTypeTopic())) {
				gestionSmallTalk(chatbot, topicSaved, tfe, creation, nluTrainingSentences);
			}

			topicLabelServices.replaceLabel(tfe, topicSaved);

		} catch (final Exception e) {
			final StringBuilder erreur = new StringBuilder(MessageText.of(TopicFileExportMultilingualResources.ERR_TOPIC, tfe.getCode()).getDisplay());
			erreur.append(e.getMessage());
			errorManagement(line, erreur.toString());
		}
	}

	/*
	 * Generate a ScriptIntention from TopicFileExport
	 */
	private void gestionScriptIntention(final Chatbot chatbot, final Topic topic, final TopicFileExport tfe, final boolean creation, final DtList<NluTrainingSentence> nluTrainingSentences) {
		ScriptIntention sin = new ScriptIntention();
		if (!creation) {
			sin = scriptIntentionServices.findByTopId(topic.getTopId());
		}
		sin.setScript(tfe.getScript());
		sin.setTopId(topic.getTopId());

		final DtList<NluTrainingSentence> nluTrainingSentencesToDelete = topicServices.getNluTrainingSentenceByTopic(chatbot, topic);

		scriptIntentionServices.save(chatbot, sin, topic);
		topicServices.save(topic, chatbot, topic.getIsEnabled(), nluTrainingSentences, nluTrainingSentencesToDelete);

	}

	/*
	 * Generate a SmallTalk from TopicFileExport
	 */
	private void gestionSmallTalk(final Chatbot chatbot, final Topic topic, final TopicFileExport tfe, final boolean creation, final DtList<NluTrainingSentence> nluTrainingSentences) {

		final DtList<UtterText> listResponse = extractResponseFromTopicFileExport(tfe);

		final DtList<ResponseButton> listButtons = extractButtonsFromTfe(chatbot.getBotId(), tfe);

		final SmallTalk smt = populateSmallTalkFromTopicFileExport(topic, creation, tfe, listResponse);

		final DtList<NluTrainingSentence> nluTrainingSentencesToDelete = topicServices.getNluTrainingSentenceByTopic(chatbot, topic);

		topicServices.saveTtoCd(topic, TypeTopicEnum.SMALLTALK.name());
		smallTalkServices.saveSmallTalk(chatbot, smt, listResponse, listButtons, topic);
		topicServices.save(topic, chatbot, topic.getIsEnabled(), nluTrainingSentences, nluTrainingSentencesToDelete);
	}

	/*
	 * Return a smallTalk with infos from the TopicFileExport
	 */
	private SmallTalk populateSmallTalkFromTopicFileExport(final Topic topic, final boolean creation, final TopicFileExport tfe, final DtList<UtterText> listResponse) {
		SmallTalk smt = new SmallTalk();
		if (!creation) {
			smt = smallTalkServices.findByTopId(topic.getTopId());
		}
		smt.setIsEnd("TRUE".equals(tfe.getIsEnd()));

		// If one response or less, we assume it is a richText, else a randomText
		final String rtyId = listResponse.size() <= 1 ? ResponseTypeEnum.RICH_TEXT.name() : ResponseTypeEnum.RANDOM_TEXT.name();
		smt.setRtyId(rtyId);

		smt.setTopId(topic.getTopId());
		return smt;
	}

	/*
	 * Return a list of Response from TopicFileExport
	 */
	private DtList<UtterText> extractResponseFromTopicFileExport(final TopicFileExport tfe) {

		final String[] listResponses = tfe.getResponse().split("\\|");

		final DtList<UtterText> responses = new DtList<UtterText>(UtterText.class);
		//When a sentence is added to the list, first the unicity is checked
		for (final String newResponse : listResponses) {
			final UtterText response = new UtterText();
			response.setText(newResponse);
			responses.add(response);
		}
		return responses;
	}

	/*
	 * Return a list of ResponseButtons from TopicFileExport
	 */
	public DtList<ResponseButton> extractButtonsFromTfe(final Long botId, final TopicFileExport tfe) {

		final DtList<ResponseButton> listButtons = new DtList<ResponseButton>(ResponseButton.class);
		// if there are buttons, they must have the following shape : [name¤topicCode] and be separated by |
		if (!tfe.getButtons().isEmpty()) {
			final String[] listDoublons = tfe.getButtons().split("\\|");
			for (final String doublon : listDoublons) {
				final ResponseButton button = new ResponseButton();
				button.setText(StringUtils.substringBetween(doublon, "[", "¤"));

				final String code = StringUtils.substringBetween(doublon, "¤", "]");
				if (code == null) {
					throw new VUserException(TopicFileExportMultilingualResources.BUTTON_CODE_EMPTY);
				}
				final Optional<Topic> topic = topicServices.getTopicByCode(code, botId);
				if (topic.isEmpty()) {
					throw new VUserException(TopicFileExportMultilingualResources.BUTTON_CODE_NOT_FOUND);
				}
				button.setTopIdResponse(topic.get().getTopId());

				listButtons.add(button);
			}
		}
		return listButtons;
	}

	/*
	 * Return an error message with the line concerned
	 */
	public String lineError(final int i) {
		return "[Line " + i + "] ";
	}

	public void errorManagement(final int i, final String erreur) {
		final StringBuilder errorMessage = new StringBuilder(lineError(i));
		errorMessage.append(erreur);
		throw new VUserException(errorMessage.toString());
	}

}
