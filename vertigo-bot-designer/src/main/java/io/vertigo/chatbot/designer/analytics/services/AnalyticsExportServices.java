package io.vertigo.chatbot.designer.analytics.services;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.influxDb.TimeSerieServices;
import io.vertigo.chatbot.designer.analytics.multilingual.AnalyticsMultilingualResources;
import io.vertigo.chatbot.designer.analytics.utils.AnalyticsServicesUtils;
import io.vertigo.chatbot.designer.builder.services.bot.ChatbotServices;
import io.vertigo.chatbot.designer.builder.services.training.TrainingServices;
import io.vertigo.chatbot.designer.domain.analytics.CategoryStat;
import io.vertigo.chatbot.designer.domain.analytics.ConversationStat;
import io.vertigo.chatbot.designer.domain.analytics.SessionExport;
import io.vertigo.chatbot.designer.domain.analytics.StatCriteria;
import io.vertigo.chatbot.designer.domain.analytics.TopIntent;
import io.vertigo.chatbot.designer.domain.analytics.UnknownSentenseExport;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.chatbot.domain.DtDefinitions.SessionExportFields;
import io.vertigo.chatbot.domain.DtDefinitions.UnknownSentenseExportFields;
import io.vertigo.core.locale.LocaleMessageText;
import io.vertigo.core.node.component.Component;
import io.vertigo.database.timeseries.TimedDataSerie;
import io.vertigo.database.timeseries.TimedDatas;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.quarto.exporter.ExporterManager;
import io.vertigo.quarto.exporter.model.Export;
import io.vertigo.quarto.exporter.model.ExportBuilder;
import io.vertigo.quarto.exporter.model.ExportFormat;

/**
 * Service for exporting analytics data to various formats (CSV, etc.).
 * This service provides methods to export statistics including sessions,
 * unknown messages, conversations, categories, top intents, and documentary resources.
 *
 * @author Chatbot Team
 */
public class AnalyticsExportServices implements Component {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Inject
	private ChatbotServices chatbotServices;

	@Inject
	private TrainingServices trainingServices;

	@Inject
	private TimeSerieServices timeSerieServices;

	@Inject
	private ExporterManager exportManager;

	/**
	 * Get sessions for export
	 *
	 * @param criteria statscriteria for sessions
	 * @return sessions
	 */
	public DtList<SessionExport> getSessionExport(final StatCriteria criteria) {
		// get data from influxdb
		final TimedDatas tabularTimedData = timeSerieServices.getRequestStats(criteria);

		// build DtList from InfluxDb data
		final DtList<SessionExport> retour = new DtList<>(SessionExport.class);
		for (final TimedDataSerie timedData : tabularTimedData.timedDataSeries()) {
			final SessionExport newSessionExport = new SessionExport();
			newSessionExport.setDate(timedData.getTime());
			newSessionExport.setUserActionsCount(AnalyticsServicesUtils.getLongValue(timedData, "userAction:count", 0L));
			newSessionExport.setConversationCount(AnalyticsServicesUtils.getLongValue(timedData, "isSessionStart:count", 0L));
			retour.add(newSessionExport);
		}

		return retour;
	}

	/**
	 * Export sessions to CSV file
	 *
	 * @param dtc list of session exports to export
	 * @return CSV file containing session data
	 */
	public VFile exportSessions(final DtList<SessionExport> dtc) {
		final String fileName = LocaleMessageText.of(AnalyticsMultilingualResources.SESSIONS_FILENAME).getDisplay() 
				+ LocalDate.now().format(DATE_FORMATTER);
		final Export export = new ExportBuilder(ExportFormat.CSV, fileName)
				.beginSheet(dtc, null)
				.addField(SessionExportFields.date)
				.addField(SessionExportFields.conversationCount)
				.addField(SessionExportFields.userActionsCount)
				.endSheet()
				.build();
		return exportManager.createExportFile(export);
	}

	/**
	 * Get sentences unrecognized for export
	 *
	 * @param criteria statscriteria for unreconized sentences
	 * @return the unknown sentences
	 */
	public DtList<UnknownSentenseExport> getUnknownSentenseExport(final StatCriteria criteria) {
		// get data from influxdb
		final TimedDatas tabularTimedData = timeSerieServices.getUnknowSentenceExport(criteria);

		// build DtList from InfluxDb data
		final DtList<UnknownSentenseExport> retour = new DtList<>(UnknownSentenseExport.class);
		for (final TimedDataSerie timedData : tabularTimedData.timedDataSeries()) {
			final Map<String, Object> values = timedData.getValues();

			final UnknownSentenseExport newUnknownSentenseExport = new UnknownSentenseExport();
			newUnknownSentenseExport.setDate(timedData.getTime());
			newUnknownSentenseExport.setText((String) values.get("text"));
			newUnknownSentenseExport.setConfidence(BigDecimal.valueOf((Double) values.getOrDefault("confidence", (double) 0)));
			newUnknownSentenseExport.setModelName((String) values.get("modelName"));
			final Long botId = Long.valueOf((String) values.get("botId"));
			final Long nodId = Long.valueOf((String) values.get("nodId"));
			//It's possible in local environment that the database was reinitialized, but eventlogs still have obsolete values
			if (botId != null) {
				final Optional<Chatbot> bot = chatbotServices.getChatbotByBotId(botId);
				final String botName = chatbotServices.getBotNameDisplay(bot);
				final String dateBot = chatbotServices.getBotDateDisplay(bot);
				final String nodeName = chatbotServices.getNodeName(bot, nodId);
				newUnknownSentenseExport.setBotName(botName);
				newUnknownSentenseExport.setCreationBot(dateBot);
				newUnknownSentenseExport.setNode(nodeName);
				final Long traId = Long.valueOf((String) values.get("traId"));
				if (traId != null) {
					final Instant dateTraining = trainingServices.getInstantEndDisplay(botId, traId);
					newUnknownSentenseExport.setDateTraining(dateTraining);
				}
			}
			retour.add(newUnknownSentenseExport);
		}

		return retour;
	}

	/**
	 * Export unknown messages to CSV file
	 *
	 * @param dtc list of unknown messages to export
	 * @return CSV file containing unknown messages data
	 */
	public VFile exportUnknownMessages(final DtList<UnknownSentenseExport> dtc) {
		final String fileName = LocaleMessageText.of(AnalyticsMultilingualResources.UNKNOWN_MESSAGES_FILENAME).getDisplay() 
				+ LocalDate.now().format(DATE_FORMATTER);
		final Export export = new ExportBuilder(ExportFormat.CSV, fileName)
				.beginSheet(dtc, null)
				.addField(UnknownSentenseExportFields.date)
				.addField(UnknownSentenseExportFields.text)
				.addField(UnknownSentenseExportFields.confidence)
				.addField(UnknownSentenseExportFields.modelName)
				.addField(UnknownSentenseExportFields.dateTraining)
				.addField(UnknownSentenseExportFields.botName)
				.addField(UnknownSentenseExportFields.node)
				.addField(UnknownSentenseExportFields.creationBot)
				.endSheet()
				.build();
		return exportManager.createExportFile(export);
	}

	/**
	 * Export conversation statistics to CSV file
	 *
	 * @param dtc list of conversation statistics to export
	 * @return CSV file containing conversation statistics
	 */
	public VFile exportConversations(final DtList<ConversationStat> dtc) {
		final String fileName = LocaleMessageText.of(AnalyticsMultilingualResources.CONVERSATION_FILENAME).getDisplay() 
				+ LocalDate.now().format(DATE_FORMATTER);
		final Export export = new ExportBuilder(ExportFormat.CSV, fileName)
				.beginSheet(dtc, null)
				.addField(DtDefinitions.ConversationStatFields.date)
				.addField(DtDefinitions.ConversationStatFields.modelName)
				.addField(DtDefinitions.ConversationStatFields.interactions)
				.addField(DtDefinitions.ConversationStatFields.rating)
				.addField(DtDefinitions.ConversationStatFields.ratingComment)
				.addField(DtDefinitions.ConversationStatFields.lastTopic)
				.addField(DtDefinitions.ConversationStatFields.ended)
				.endSheet()
				.build();
		return exportManager.createExportFile(export);
	}

	/**
	 * Export category statistics to CSV file
	 *
	 * @param categoryStats list of category statistics to export
	 * @return CSV file containing category statistics
	 */
	public VFile exportCategories(final DtList<CategoryStat> categoryStats) {
		final String fileName = LocaleMessageText.of(AnalyticsMultilingualResources.CATEGORIES_FILENAME).getDisplay() 
				+ LocalDate.now().format(DATE_FORMATTER);
		final Export export = new ExportBuilder(ExportFormat.CSV, fileName)
				.beginSheet(categoryStats, null)
				.addField(DtDefinitions.CategoryStatFields.label)
				.addField(DtDefinitions.CategoryStatFields.code)
				.addField(DtDefinitions.CategoryStatFields.percentage)
				.addField(DtDefinitions.CategoryStatFields.usage)
				.endSheet()
				.build();
		return exportManager.createExportFile(export);
	}

	/**
	 * Export top intents to CSV file
	 *
	 * @param topIntents list of top intents to export
	 * @return CSV file containing top intents
	 */
	public VFile exportTopIntents(final DtList<TopIntent> topIntents) {
		final String fileName = LocaleMessageText.of(AnalyticsMultilingualResources.TOP_INTENTS_FILENAME).getDisplay() 
				+ LocalDate.now().format(DATE_FORMATTER);
		final Export export = new ExportBuilder(ExportFormat.CSV, fileName)
				.beginSheet(topIntents, null)
				.addField(DtDefinitions.TopIntentFields.intentRasa)
				.addField(DtDefinitions.TopIntentFields.code)
				.addField(DtDefinitions.TopIntentFields.catLabel)
				.addField(DtDefinitions.TopIntentFields.labels)
				.addField(DtDefinitions.TopIntentFields.count)
				.endSheet()
				.build();
		return exportManager.createExportFile(export);
	}

	/**
	 * Export documentary resource statistics to CSV
	 *
	 * @param documentaryResourceStats list of stats to export
	 * @return CSV file
	 */
	public VFile exportDocumentaryResources(final DtList<io.vertigo.chatbot.designer.domain.analytics.DocumentaryResourceStat> documentaryResourceStats) {
		final String fileName = LocaleMessageText.of(AnalyticsMultilingualResources.DOCUMENTARY_RESOURCES_FILENAME).getDisplay() 
				+ LocalDate.now().format(DATE_FORMATTER);
		final Export export = new ExportBuilder(ExportFormat.CSV, fileName)
				.beginSheet(documentaryResourceStats, null)
				.addField(DtDefinitions.DocumentaryResourceStatFields.title)
				.addField(DtDefinitions.DocumentaryResourceStatFields.dreTypeCd)
				.addField(DtDefinitions.DocumentaryResourceStatFields.count)
				.endSheet()
				.build();
		return exportManager.createExportFile(export);
	}

	/**
	 * Export question/answer statistics to CSV
	 *
	 * @param questionAnswerStats list of stats to export
	 * @return CSV file
	 */
	public VFile exportQuestionAnswers(final DtList<io.vertigo.chatbot.designer.domain.analytics.QuestionAnswerStat> questionAnswerStats) {
		final String fileName = LocaleMessageText.of(AnalyticsMultilingualResources.QUESTION_ANSWERS_FILENAME).getDisplay() 
				+ LocalDate.now().format(DATE_FORMATTER);
		final Export export = new ExportBuilder(ExportFormat.CSV, fileName)
				.beginSheet(questionAnswerStats, null)
				.addField(DtDefinitions.QuestionAnswerStatFields.question)
				.addField(DtDefinitions.QuestionAnswerStatFields.catLabel)
				.addField(DtDefinitions.QuestionAnswerStatFields.count)
				.endSheet()
				.build();
		return exportManager.createExportFile(export);
	}
}
