package io.vertigo.chatbot.analytics;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.ExecutorConfiguration;
import io.vertigo.chatbot.engine.BotEngine;
import io.vertigo.chatbot.engine.model.BotInput;
import io.vertigo.chatbot.engine.model.TopicDefinition;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.analytics.AnalyticsManager;
import io.vertigo.core.analytics.process.AProcess;
import io.vertigo.core.analytics.process.AProcessBuilder;
import io.vertigo.core.node.component.Component;

@Transactional
public class AnalyticsSenderServices implements Component {

	@Inject
	private AnalyticsManager analyticsManager;

	//Analytics keys
	private static final String DB_KEY = "chatbotmessages";
	private static final String TEXT_KEY = "text";
	private static final String TYPE_KEY = "type";
	private static final String NLU_KEY = "isNlu";
	private static final String USER_MESSAGE_KEY = "isUserMessage";
	private static final String CONFIDENCE_KEY = "confidence";
	private static final String TECHNICAL_KEY = "isTechnical";
	private static final String FALLBACK_KEY = "isFallback";
	private static final String SESSION_START_KEY = "isSessionStart";

	//Input types key
	private static final String BUTTONS_INPUT_KEY = "button";
	//for the topic {} instructions
	private static final String SWITCH_INPUT_KEY = "switch";
	private static final String TECHNICAL_INPUT_KEY = "technical";

	//Training keys
	private static final String BOT_KEY = "botId";
	private static final String NODE_KEY = "nodId";
	private static final String TRAINING_KEY = "traId";
	private static final String MODEL_KEY = "modelName";

	private static final Double TRUE_BIGDECIMAL = 1D;
	private static final Double FALSE_BIGDECIMAL = 0D;

	/**
	 * Send all the events to the events database
	 *
	 * @param metadatas data from botResponse
	 * @param executorConfiguration the executor configuration (i.e. node, bot)
	 * @param input the user input
	 */
	public void sendEventToDb(final Map<String, Object> metadatas, final ExecutorConfiguration executorConfiguration, final BotInput input) {
		//Get values from response
		final AnalyticsObjectSend analytics = (AnalyticsObjectSend) metadatas.get(BotEngine.ANALYTICS_KEY);
		final String codeTopic = analytics.getTopic().getCode();
		final Double accuracy = analytics.getAccuracy();
		final List<TopicDefinition> topicsPast = analytics.getTopicsPast();

		final boolean isNlu = accuracy != null;

		if (isNlu) {
			sendNluEvent(input, codeTopic, accuracy, topicsPast, executorConfiguration);
		} else {
			//Get a button response
			sendPastTopics(topicsPast, executorConfiguration);
		}
	}

	//Send an event with the nlu topics
	//Send also all the topics passed with topic {} instruction
	private void sendNluEvent(final BotInput input, final String codeTopic, final Double accuracy, final List<TopicDefinition> topicsPast, final ExecutorConfiguration executorConfiguration) {
		final AProcessBuilder processBuilder = AProcess.builder(DB_KEY, codeTopic, Instant.now(), Instant.now()) // timestamp of emitted event
				.addTag(TEXT_KEY, input.getMessage())
				.addTag(TYPE_KEY, TEXT_KEY)
				.setMeasure(NLU_KEY, TRUE_BIGDECIMAL)
				.setMeasure(USER_MESSAGE_KEY, TRUE_BIGDECIMAL)
				.setMeasure(CONFIDENCE_KEY, accuracy);
		//test if topic is a fallback topic
		if (codeTopic.equals(BotEngine.FALLBACK_TOPIC_NAME)) {
			prepareFallBackEvent(processBuilder);
		} else {
			processBuilder.setMeasure(TECHNICAL_KEY, FALSE_BIGDECIMAL);
		}
		sendPastTopics(topicsPast, executorConfiguration);
		setConfigurationInformation(processBuilder, executorConfiguration);
		analyticsManager.addProcess(processBuilder.build());
	}

	private static void prepareFallBackEvent(final AProcessBuilder builder) {
		builder
				.setMeasure(TECHNICAL_KEY, TRUE_BIGDECIMAL)
				.setMeasure(FALLBACK_KEY, TRUE_BIGDECIMAL);

	}

	//Send all the topics passed during the sequence in BT
	private void sendPastTopics(final List<TopicDefinition> topicsPast, final ExecutorConfiguration executorConfiguration) {
		boolean isFirst = true;
		//Can't have fallback instructions
		for (TopicDefinition topic : topicsPast) {
			//Create the measurements
			final String codeTopic = topic.getCode();
			final boolean isEnd = codeTopic.equals(BotEngine.END_TOPIC_NAME);

			final AProcessBuilder processBuilder = AProcess.builder(DB_KEY, topic.getCode(), Instant.now(), Instant.now()) // timestamp of emitted event
					.addTag(TEXT_KEY, "")
					.addTag(TYPE_KEY, isFirst ? BUTTONS_INPUT_KEY : SWITCH_INPUT_KEY)
					.setMeasure(TECHNICAL_KEY, isEnd ? TRUE_BIGDECIMAL : FALSE_BIGDECIMAL)
					.setMeasure(CONFIDENCE_KEY, TRUE_BIGDECIMAL);
			setConfigurationInformation(processBuilder, executorConfiguration);
			analyticsManager.addProcess(processBuilder.build());
			isFirst = false;
		}

	}

	/**
	 * Set the event start topic to eventdb
	 *
	 * @param executorConfiguration the executor configuration (i.e. node, bot etc...)
	 */
	public void sendEventStartToDb(final ExecutorConfiguration executorConfiguration) {
		//Create the process
		final AProcessBuilder processBuilder = AProcess.builder(DB_KEY, BotEngine.START_TOPIC_NAME, Instant.now(), Instant.now()) // timestamp of emitted event
				.addTag(TEXT_KEY, "")
				.addTag(TYPE_KEY, TECHNICAL_INPUT_KEY)
				.setMeasure(SESSION_START_KEY, TRUE_BIGDECIMAL)
				.setMeasure(CONFIDENCE_KEY, TRUE_BIGDECIMAL)
				.setMeasure(TECHNICAL_KEY, TRUE_BIGDECIMAL);

		setConfigurationInformation(processBuilder, executorConfiguration);

		analyticsManager.addProcess(processBuilder.build());
	}

	private static void setConfigurationInformation(final AProcessBuilder builder, final ExecutorConfiguration executorConfiguration) {
		builder
				.addTag(BOT_KEY, String.valueOf(executorConfiguration.getBotId()))
				.addTag(NODE_KEY, String.valueOf(executorConfiguration.getNodId()))
				.addTag(TRAINING_KEY, String.valueOf(executorConfiguration.getTraId()))
				.addTag(MODEL_KEY, String.valueOf(executorConfiguration.getModelName()));
	}

}
