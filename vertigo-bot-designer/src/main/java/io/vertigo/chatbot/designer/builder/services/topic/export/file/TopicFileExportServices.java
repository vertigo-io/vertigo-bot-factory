package io.vertigo.chatbot.designer.builder.services.topic.export.file;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

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
import io.vertigo.chatbot.designer.builder.services.topic.NluTrainingSentenceServices;
import io.vertigo.chatbot.designer.builder.services.topic.ScriptIntentionServices;
import io.vertigo.chatbot.designer.builder.services.topic.SmallTalkServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicCategoryServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.chatbot.designer.builder.topicFileExport.TopicFileExportPAO;
import io.vertigo.chatbot.domain.DtDefinitions.TopicFileExportFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.VUserException;
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
	private ExporterManager exportManager;

	public VFile exportTopicFile(final Chatbot bot, final DtList<TopicFileExport> dtc) {

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
				.endSheet()
				.build();
		final VFile result = exportManager.createExportFile(export);

		return result;

	}

	public DtList<TopicFileExport> getTopicFileExport(final Long botId, final Long topCatId) {
		final Optional<Long> topCatIdOpt = topCatId != null ? Optional.of(topCatId) : Optional.empty();
		return topicFileExportPAO.getTopicFileExport(botId, topCatIdOpt);
	}

	public List<TopicFileExport> transformFileToList(final CSVReader csvReader) {
		try {
			// Check length of header, to make sure all columns are there
			final String[] header = csvReader.readNext();
			if (header.length != 14) {
				throw new VUserException("Please make sure that your csv is delimited by ';' and has 14 columns.");
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
					TopicFileExportFields.isEnd.name()
			};
			//Setting the colums for mappingStrategy
			mappingStrategy.setColumnMapping(columns);
			final List<TopicFileExport> list = csvToBean.parse(mappingStrategy, csvReader);
			return list;
		} catch (final Exception e) {
			throw new VUserException("Error during the file mapping : " + e);
		}
	}

	public void importTopicFromList(final Chatbot chatbot, final List<TopicFileExport> list) throws IOException {

		// Unicity check
		final HashSet<String> codeSet = new HashSet<String>();
		int i = 1;
		for (final TopicFileExport tfe : list) {
			i++;
			if (tfe.getCode().isEmpty() || tfe.getCode().isBlank()) {
				throw new VUserException(lineError(i) + "Please provide a code for every topic.");
			}
			if (!codeSet.add(tfe.getCode())) {
				throw new VUserException(lineError(i) + "Please provide a file with uniques topics (" + tfe.getCode() + " is duplicated).");
			}
		}
		final Map<String, Topic> mapTopic = new HashMap<>();
		final Map<String, Boolean> mapCreation = new HashMap<>();

		final DtList<TopicCategory> listCategory = topicCategoryServices.getAllCategoriesByBot(chatbot);

		//Map category initialization (only one call to database)
		final Map<String, Long> mapCategory = new HashMap<>();
		for (final TopicCategory category : listCategory) {
			mapCategory.put(category.getCode(), category.getTopCatId());
		}

		//First, create/modify topic (topic might be referenced by small talk, so it has to be done in another loop)
		i = 1;
		for (final TopicFileExport tfe : list) {
			i++;
			try {
				//Check if category is in mapCategory (in database)
				final Long topCatId = mapCategory.get(tfe.getCategory());
				if (topCatId == null) {
					throw new VUserException(lineError(i) + "The category " + tfe.getCategory() + " does not exist.");
				}
				Topic topic = new Topic();
				boolean creation = true;
				DtList<NluTrainingSentence> nluTSToDelete = new DtList<NluTrainingSentence>(NluTrainingSentence.class);
				final boolean isEnabled = "ACTIVE".equals(tfe.getActive());
				//Try to find the topic in database
				final Topic topicBase = topicServices.getTopicByCodeBotId(chatbot.getBotId(), tfe.getCode());

				if (topicBase != null) {
					//If it already exists, then modification
					topic = topicBase;
					creation = false;
					//All preexisting nlu will be replaced by the ones in the file
					nluTSToDelete = topicServices.getNluTrainingSentenceByTopic(chatbot, topic);

				}

				topic.setBotId(chatbot.getBotId());
				topic.setTitle(tfe.getTitle());
				topic.setDescription(tfe.getDescription());
				topic.setTopCatId(topCatId);
				topic.setKtoCd(KindTopicEnum.NORMAL.name());
				topic.setTtoCd(tfe.getTypeTopic());
				topic.setCode(tfe.getCode());
				topic.setIsEnabled(isEnabled);

				final Topic topicSaved = topicServices.save(topic, topic.getIsEnabled(), new DtList<NluTrainingSentence>(NluTrainingSentence.class),
						nluTSToDelete);

				// At this point, topicSaved isEnabled is false, because the topic has no nluTrainingSentences.
				// So isEnabled is resetted anyway. it will be calculated again in the next loop

				topic.setIsEnabled(isEnabled);
				mapTopic.put(tfe.getCode(), topicSaved);
				mapCreation.put(tfe.getCode(), creation);
			} catch (final Exception e) {
				throw new VUserException(lineError(i) + "Error in topic " + tfe.getCode() + " : " + e.getMessage());
			}
		}

		//Then, create/modify smallTalk/ScriptIntention (topics just created may be referenced in the response button)
		i = 1;
		for (final TopicFileExport tfe : list) {
			i++;
			try {
				final Topic topicSaved = mapTopic.get(tfe.getCode());
				final boolean creation = mapCreation.get(tfe.getCode());
				final DtList<NluTrainingSentence> nluTrainingSentences = nluTrainingSentenceServices.extractNlutsFromTfe(tfe);

				if (TypeTopicEnum.SCRIPTINTENTION.name().equals(tfe.getTypeTopic())) {
					gestionScriptIntention(chatbot, topicSaved, tfe, creation, nluTrainingSentences);
				} else if (TypeTopicEnum.SMALLTALK.name().equals(tfe.getTypeTopic())) {
					gestionSmallTalk(chatbot, topicSaved, tfe, creation, nluTrainingSentences);
				}
			} catch (final Exception e) {
				throw new VUserException(lineError(i) + "Error in topic " + tfe.getCode() + " : " + e.getMessage());
			}
		}

	}

	public void gestionScriptIntention(final Chatbot chatbot, final Topic topic, final TopicFileExport tfe, final boolean creation, final DtList<NluTrainingSentence> nluTrainingSentences) {
		ScriptIntention sin = new ScriptIntention();
		if (!creation) {
			sin = scriptIntentionServices.findByTopId(topic.getTopId());
		}
		sin.setScript(tfe.getScript());
		sin.setTopId(topic.getTopId());

		scriptIntentionServices.save(chatbot, sin, nluTrainingSentences, new DtList<NluTrainingSentence>(NluTrainingSentence.class), topic, topic.getIsEnabled() && !nluTrainingSentences.isEmpty());
	}

	public void gestionSmallTalk(final Chatbot chatbot, final Topic topic, final TopicFileExport tfe, final boolean creation, final DtList<NluTrainingSentence> nluTrainingSentences) {
		SmallTalk smt = new SmallTalk();
		if (!creation) {
			smt = smallTalkServices.findByTopId(topic.getTopId());
		}
		smt.setIsEnd("TRUE".equals(tfe.getIsEnd()));
		smt.setRtyId(ResponseTypeEnum.RICH_TEXT.name());
		smt.setTopId(topic.getTopId());

		final DtList<UtterText> listResponse = new DtList<UtterText>(UtterText.class);
		final UtterText response = new UtterText();
		response.setText(tfe.getResponse());
		listResponse.add(response);

		final DtList<ResponseButton> listButtons = extractButtonsFromTfe(chatbot.getBotId(), tfe);

		smallTalkServices.saveSmallTalk(chatbot, smt, nluTrainingSentences, new DtList<NluTrainingSentence>(NluTrainingSentence.class), listResponse,
				listButtons, topic, topic.getIsEnabled() && !nluTrainingSentences.isEmpty());
	}

	public DtList<ResponseButton> extractButtonsFromTfe(final Long botId, final TopicFileExport tfe) {

		final DtList<ResponseButton> listButtons = new DtList<ResponseButton>(ResponseButton.class);
		// if there are buttons, they must have the following shape : [name¤topicCode] and be separated by |
		if (!tfe.getButtons().isEmpty()) {
			final String[] listDoublons = tfe.getButtons().split("\\|");
			for (final String doublon : listDoublons) {
				final ResponseButton button = new ResponseButton();
				button.setText(StringUtils.substringBetween(doublon, "[", "¤"));

				final String code = StringUtils.substringBetween(doublon, "¤", "]");
				final Topic topic = topicServices.getTopicByCodeBotId(botId, code);
				button.setTopIdResponse(topic.getTopId());

				listButtons.add(button);
			}
		}
		return listButtons;
	}

	public String lineError(final int i) {
		return "[Line " + i + "] ";
	}

}
