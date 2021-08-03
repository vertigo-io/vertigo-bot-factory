package io.vertigo.chatbot.designer.analytics.services;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.Training;
import io.vertigo.chatbot.designer.analytics.multilingual.AnalyticsMultilingualResources;
import io.vertigo.chatbot.designer.builder.services.TrainingServices;
import io.vertigo.chatbot.designer.builder.services.bot.ChatbotServices;
import io.vertigo.chatbot.designer.domain.analytics.SessionExport;
import io.vertigo.chatbot.designer.domain.analytics.StatCriteria;
import io.vertigo.chatbot.designer.domain.analytics.UnknownSentenseExport;
import io.vertigo.chatbot.domain.DtDefinitions.SessionExportFields;
import io.vertigo.chatbot.domain.DtDefinitions.UnknownSentenseExportFields;
import io.vertigo.core.locale.MessageText;
import io.vertigo.core.node.component.Component;
import io.vertigo.database.timeseries.TimedDataSerie;
import io.vertigo.database.timeseries.TimedDatas;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.quarto.exporter.ExporterManager;
import io.vertigo.quarto.exporter.model.Export;
import io.vertigo.quarto.exporter.model.ExportBuilder;
import io.vertigo.quarto.exporter.model.ExportFormat;

public class AnalyticsExportServices implements Component {

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
		final TimedDatas tabularTimedData = timeSerieServices.getSessionsExport(criteria);

		// build DtList from InfluxDb data
		final DtList<SessionExport> retour = new DtList<>(SessionExport.class);
		for (final TimedDataSerie timedData : tabularTimedData.getTimedDataSeries()) {
			final Map<String, Object> values = timedData.getValues();

			final SessionExport newSessionExport = new SessionExport();
			newSessionExport.setDate(timedData.getTime());
			newSessionExport.setModelName((String) values.get("modelName"));
			final Long botId = Long.valueOf((String) values.get("botId"));
			//It's possible in local environment that the database was reinitialized, but eventlogs still have obsolete values
			if (botId != null) {
				final Chatbot bot = chatbotServices.getChatbotById(botId);
				final String botName = chatbotServices.getBotNameDisplay(bot);
				final String dateBot = chatbotServices.getBotDateDisplay(bot);
				newSessionExport.setBotName(botName);
				newSessionExport.setCreationBot(dateBot);
				final Long traId = Long.valueOf((String) values.get("traId"));
				if (traId != null) {
					final Optional<Training> training = trainingServices.getTrainingByTraIdAndBotId(botId, traId);
					final java.time.Instant dateTraining = trainingServices.getInstantEndDisplay(training);
					newSessionExport.setDateTraining(dateTraining);
				}
			}
			retour.add(newSessionExport);
		}

		return retour;
	}

	/*
	 * Return a File from a list of SessionsExport
	 */
	public VFile exportSessions(final DtList<SessionExport> dtc) {
		final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		final Date date = new Date();
		final Export export = new ExportBuilder(ExportFormat.CSV, MessageText.of(AnalyticsMultilingualResources.SESSIONS_FILENAME).getDisplay() + dateFormat.format(date))
				.beginSheet(dtc, null)
				.addField(SessionExportFields.date)
				.addField(SessionExportFields.modelName)
				.addField(SessionExportFields.dateTraining)
				.addField(SessionExportFields.botName)
				.addField(SessionExportFields.creationBot)
				.endSheet()
				.build();
		final VFile result = exportManager.createExportFile(export);

		return result;

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
		for (final TimedDataSerie timedData : tabularTimedData.getTimedDataSeries()) {
			final Map<String, Object> values = timedData.getValues();

			final UnknownSentenseExport newUnknownSentenseExport = new UnknownSentenseExport();
			newUnknownSentenseExport.setDate(timedData.getTime());
			newUnknownSentenseExport.setText((String) values.get("text"));
			newUnknownSentenseExport.setConfidence(BigDecimal.valueOf((Double) values.get("confidence")));
			newUnknownSentenseExport.setModelName((String) values.get("modelName"));
			final Long botId = Long.valueOf((String) values.get("botId"));
			//It's possible in local environment that the database was reinitialized, but eventlogs still have obsolete values
			if (botId != null) {
				final Chatbot bot = chatbotServices.getChatbotById(botId);
				final String botName = chatbotServices.getBotNameDisplay(bot);
				final String dateBot = chatbotServices.getBotDateDisplay(bot);
				newUnknownSentenseExport.setBotName(botName);
				newUnknownSentenseExport.setCreationBot(dateBot);
				final Long traId = Long.valueOf((String) values.get("traId"));
				if (traId != null) {
					final Optional<Training> training = trainingServices.getTrainingByTraIdAndBotId(botId, traId);
					final java.time.Instant dateTraining = trainingServices.getInstantEndDisplay(training);
					newUnknownSentenseExport.setDateTraining(dateTraining);
				}
			}
			retour.add(newUnknownSentenseExport);
		}

		return retour;
	}

	/*
	 * Return a file from a list of unknown messages
	 */
	public VFile exportUnknownMessages(final DtList<UnknownSentenseExport> dtc) {
		final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		final Date date = new Date();
		final Export export = new ExportBuilder(ExportFormat.CSV, MessageText.of(AnalyticsMultilingualResources.UNKNOWN_MESSAGES_FILENAME).getDisplay() + dateFormat.format(date))
				.beginSheet(dtc, null)
				.addField(UnknownSentenseExportFields.date)
				.addField(UnknownSentenseExportFields.text)
				.addField(UnknownSentenseExportFields.confidence)
				.addField(UnknownSentenseExportFields.modelName)
				.addField(UnknownSentenseExportFields.dateTraining)
				.addField(UnknownSentenseExportFields.botName)
				.addField(UnknownSentenseExportFields.creationBot)
				.endSheet()
				.build();
		final VFile result = exportManager.createExportFile(export);

		return result;

	}
}
