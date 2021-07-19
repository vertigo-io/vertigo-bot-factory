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
			preparePastTopics(topicsPast, executorConfiguration);
		}
	}

	private void sendNluEvent(final BotInput input, final String codeTopic, final Double accuracy, final List<TopicDefinition> topicsPast, final ExecutorConfiguration executorConfiguration) {
		final AProcessBuilder processBuilder = AProcess.builder("chatbotmessages", codeTopic, Instant.now(), Instant.now()) // timestamp of emitted event
				.addTag("text", input.getMessage())
				.addTag("type", "text")
				.setMeasure("isNlu", 1D)
				.setMeasure("isUserMessage", 1D)
				.setMeasure("confidence", accuracy);
		if (codeTopic.equals(BotEngine.FALLBACK_TOPIC_NAME)) {
			prepareFallBackEvent(processBuilder, input, codeTopic, executorConfiguration, accuracy);
		}
		preparePastTopics(topicsPast, executorConfiguration);
		setConfigurationInformation(processBuilder, executorConfiguration);
		analyticsManager.addProcess(processBuilder.build());
	}

	private static void prepareFallBackEvent(final AProcessBuilder builder, final BotInput input, final String codeTopic, final ExecutorConfiguration executorConfiguration, final Double accuracy) {
		builder
				.setMeasure("isTechnical", 1D)
				.setMeasure("isFallback", 1D);

	}

	private void preparePastTopics(final List<TopicDefinition> topicsPast, final ExecutorConfiguration executorConfiguration) {
		for (TopicDefinition topic : topicsPast) {
			//Create the measurements
			final String codeTopic = topic.getCode();
			final boolean isFallback = codeTopic.equals(BotEngine.FALLBACK_TOPIC_NAME);
			final boolean isEnd = codeTopic.equals(BotEngine.END_TOPIC_NAME);

			final AProcessBuilder processBuilder = AProcess.builder("chatbotmessages", topic.getCode(), Instant.now(), Instant.now()) // timestamp of emitted event
					.addTag("text", "")
					.addTag("type", "text")
					.setMeasure("isTechnical", isTechnical(isFallback, isEnd))
					.setMeasure("confidence", 1D);
			setConfigurationInformation(processBuilder, executorConfiguration);
			analyticsManager.addProcess(processBuilder.build());
		}

	}

	public void sendEventStartToDb(final ExecutorConfiguration executorConfiguration) {
		//Create the process
		final AProcessBuilder processBuilder = AProcess.builder("chatbotmessages", BotEngine.START_TOPIC_NAME, Instant.now(), Instant.now()) // timestamp of emitted event
				.addTag("text", "")
				.addTag("type", "technical")
				.setMeasure("isSessionStart", 1D)
				.setMeasure("confidence", 1D)
				.setMeasure("isTechnical", 1D);

		setConfigurationInformation(processBuilder, executorConfiguration);

		analyticsManager.addProcess(processBuilder.build());
	}

	private static void setConfigurationInformation(final AProcessBuilder builder, final ExecutorConfiguration executorConfiguration) {
		builder
				.addTag("botId", String.valueOf(executorConfiguration.getBotId()))
				.addTag("nodId", String.valueOf(executorConfiguration.getNodId()))
				.addTag("traId", String.valueOf(executorConfiguration.getTraId()))
				.addTag("modelName", String.valueOf(executorConfiguration.getModelName()));
	}

	//return 1D if topic is technical
	private static double isTechnical(final boolean isFallback, final boolean isEnd) {
		return isFallback || isEnd ? 1D : 0D;
	}
}
