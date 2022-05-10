package io.vertigo.chatbot.analytics;

import io.vertigo.chatbot.commons.domain.ExecutorConfiguration;
import io.vertigo.chatbot.engine.BotEngine;
import io.vertigo.chatbot.engine.model.BotInput;
import io.vertigo.chatbot.engine.model.BotResponse;
import io.vertigo.chatbot.engine.model.TopicDefinition;
import io.vertigo.chatbot.executor.model.IncomeRating;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.analytics.AnalyticsManager;
import io.vertigo.core.analytics.process.AProcessBuilder;
import io.vertigo.core.node.component.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Transactional
public class AnalyticsSenderServices implements Component {

	private static final Logger LOGGER = LogManager.getLogger(AnalyticsSenderServices.class);

	@Inject
	private AnalyticsManager analyticsManager;

	/**
	 * Send all the events to the events database
	 *
	 * @param botResponse botResponse
	 * @param executorConfiguration the executor configuration (i.e. node, bot)
	 * @param input the user input
	 */
	public void sendEventToDb(final UUID sessionId, final BotResponse botResponse, final ExecutorConfiguration executorConfiguration, final BotInput input) {
		//Get values from response
		final AnalyticsObjectSend analytics = (AnalyticsObjectSend) botResponse.getMetadatas().get(BotEngine.ANALYTICS_KEY);
		final String codeTopic = analytics.getTopic().getCode();
		final Double accuracy = analytics.getAccuracy();
		final List<TopicDefinition> topicsPast = analytics.getTopicsPast();

		final boolean isNlu = accuracy != null;

		if (isNlu) {
			sendNluEvent(sessionId, input, codeTopic, accuracy, executorConfiguration);
		}
		sendPastTopics(sessionId, topicsPast, executorConfiguration);

		sendBotInputEvent(sessionId, input, executorConfiguration);

		botResponse.getHtmlTexts().forEach(text -> sendConversationEvent(sessionId, text, false, executorConfiguration));
	}

	private void sendBotInputEvent(final UUID sessionId, final BotInput input, final ExecutorConfiguration executorConfiguration) {
		if (input.getMessage() != null) {
			sendConversationEvent(sessionId, input.getMessage(), true, executorConfiguration);
		} else if (input.getMetadatas().get("text") != null) {
			sendConversationEvent(sessionId, (String) input.getMetadatas().get("text"), true, executorConfiguration);
		} else if (input.getMetadatas().get("filename") != null) {
			sendConversationEvent(sessionId, (String) input.getMetadatas().get("filename"), true, executorConfiguration);
		}
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (final InterruptedException e) {
			LOGGER.error(e);
		}
	}

	//Send an event with the nlu topics
	//Send also all the topics passed with topic {} instruction
	private void sendNluEvent(final UUID sessionId, final BotInput input, final String codeTopic, final Double accuracy, final ExecutorConfiguration executorConfiguration) {
		final AProcessBuilder processBuilder = AnalyticsUtils.prepareMessageProcess(codeTopic, input.getMessage(), AnalyticsUtils.TEXT_KEY)
				.setMeasure(AnalyticsUtils.NLU_KEY, AnalyticsUtils.TRUE_BIGDECIMAL)
				.setMeasure(AnalyticsUtils.USER_MESSAGE_KEY, AnalyticsUtils.TRUE_BIGDECIMAL)
				.setMeasure(AnalyticsUtils.CONFIDENCE_KEY, accuracy);
		//test if topic is a fallback topic
		if (codeTopic.equals(BotEngine.FALLBACK_TOPIC_NAME)) {
			prepareFallBackEvent(processBuilder);
		} else {
			processBuilder.setMeasure(AnalyticsUtils.TECHNICAL_KEY, AnalyticsUtils.FALSE_BIGDECIMAL);
		}
		sendProcessWithConfiguration(sessionId, processBuilder, executorConfiguration);
	}

	private static void prepareFallBackEvent(final AProcessBuilder builder) {
		builder
				.setMeasure(AnalyticsUtils.TECHNICAL_KEY, AnalyticsUtils.TRUE_BIGDECIMAL)
				.setMeasure(AnalyticsUtils.FALLBACK_KEY, AnalyticsUtils.TRUE_BIGDECIMAL);

	}

	//Send all the topics passed during the sequence in BT
	private void sendPastTopics(final UUID sessionId, final List<TopicDefinition> topicsPast, final ExecutorConfiguration executorConfiguration) {
		boolean isFirst = true;
		//Can't have fallback instructions
		for (final TopicDefinition topic : topicsPast) {
			//Create the measurements
			final String codeTopic = topic.getCode();
			final boolean isEnd = codeTopic.equals(BotEngine.END_TOPIC_NAME);
			final String type = isFirst ? AnalyticsUtils.BUTTONS_INPUT_KEY : AnalyticsUtils.SWITCH_INPUT_KEY;
			final AProcessBuilder processBuilder = AnalyticsUtils.prepareEmptyMessageProcess(codeTopic, type)
					.addTag(AnalyticsUtils.TYPE_KEY, isFirst ? AnalyticsUtils.BUTTONS_INPUT_KEY : AnalyticsUtils.SWITCH_INPUT_KEY)
					.setMeasure(AnalyticsUtils.TECHNICAL_KEY, isEnd ? AnalyticsUtils.TRUE_BIGDECIMAL : AnalyticsUtils.FALSE_BIGDECIMAL)
					.setMeasure(AnalyticsUtils.CONFIDENCE_KEY, AnalyticsUtils.TRUE_BIGDECIMAL)
					.setMeasure(AnalyticsUtils.NLU_KEY, AnalyticsUtils.FALSE_BIGDECIMAL);
			sendProcessWithConfiguration(sessionId, processBuilder, executorConfiguration);
			isFirst = false;
		}

	}

	/**
	 * Set the event start topic to eventdb
	 *
	 * @param executorConfiguration the executor configuration (i.e. node, bot etc...)
	 */
	public void sendEventStartToDb(final UUID sessionId, final BotResponse botResponse, final ExecutorConfiguration executorConfiguration, final BotInput input) {
		//Create the process
		final AnalyticsObjectSend analytics = (AnalyticsObjectSend) botResponse.getMetadatas().get(BotEngine.ANALYTICS_KEY);
		final List<TopicDefinition> topicsPast = analytics.getTopicsPast();
		final AProcessBuilder processBuilder = AnalyticsUtils.prepareEmptyMessageProcess(BotEngine.START_TOPIC_NAME, AnalyticsUtils.TECHNICAL_INPUT_KEY)
				.setMeasure(AnalyticsUtils.SESSION_START_KEY, AnalyticsUtils.TRUE_BIGDECIMAL)
				.setMeasure(AnalyticsUtils.CONFIDENCE_KEY, AnalyticsUtils.TRUE_BIGDECIMAL)
				.setMeasure(AnalyticsUtils.TECHNICAL_KEY, AnalyticsUtils.TRUE_BIGDECIMAL);

		sendProcessWithConfiguration(sessionId, processBuilder, executorConfiguration);
		sendPastTopics(sessionId, topicsPast, executorConfiguration);
		sendBotInputEvent(sessionId, input, executorConfiguration);
		botResponse.getHtmlTexts().forEach(text -> sendConversationEvent(sessionId, text, false, executorConfiguration));
	}

	private void sendProcessWithConfiguration(final UUID sessionId, final AProcessBuilder builder, final ExecutorConfiguration executorConfiguration) {
		AnalyticsUtils.setConfiguration(sessionId, builder, executorConfiguration);
		analyticsManager.addProcess(builder.build());
	}

	public void rate(final UUID sessionId, final IncomeRating rating, final ExecutorConfiguration executorConfiguration) {
		sendProcessWithConfiguration(sessionId, AnalyticsUtils.prepareRatingProcess(rating.getNote()), executorConfiguration);
	}

	public void sendConversationEvent(final UUID sessionId, final String text, final boolean userMessage, final ExecutorConfiguration executorConfiguration) {
		sendProcessWithConfiguration(sessionId, AnalyticsUtils.prepareConversationProcess(text, userMessage), executorConfiguration);
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (final InterruptedException e) {
			LOGGER.error(e);
		}
	}

}
