package io.vertigo.chatbot.analytics;

import java.time.Instant;
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
import io.vertigo.core.lang.Tuple;
import io.vertigo.core.node.component.Component;

@Transactional
public class AnalyticsSenderServices implements Component {

	@Inject
	private AnalyticsManager analyticsManager;

	@SuppressWarnings("unchecked")
	public void sendEventToDb(final Map<String, Object> metadatas, final ExecutorConfiguration executorConfiguration, final BotInput input) {
		//Get values from response
		final TopicDefinition currentTopic = (TopicDefinition) metadatas.get("currentTopic");
		final Tuple<Double, String> eventLog = (Tuple<Double, String>) metadatas.get("eventLog");
		final String codeTopic = currentTopic.getCode();

		//Create the measurements
		final boolean isSessionStart = codeTopic.equals(BotEngine.START_TOPIC_NAME);
		final boolean isFallback = codeTopic.equals(BotEngine.FALLBACK_TOPIC_NAME);
		final boolean isEnd = codeTopic.equals(BotEngine.END_TOPIC_NAME);
		final boolean isNlu = eventLog.getVal1() != null;

		//Create the process
		final AProcessBuilder processBuilder = AProcess.builder("chatbotmessages", currentTopic.getCode(), Instant.now(), Instant.now()) // timestamp of emitted event
				.addTag("text", input.getMessage() != null ? input.getMessage() : input.getMetadatas().get("payload") != null ? input.getMetadatas().get("payload").toString() : "rien")
				.addTag("type", input.getMetadatas().get("payload") != null ? "button" : "text")
				.addTag("botId", String.valueOf(executorConfiguration.getBotId()))
				.addTag("nodId", String.valueOf(executorConfiguration.getNodId()))
				.addTag("traId", String.valueOf(executorConfiguration.getTraId()))
				.addTag("modelName", String.valueOf(executorConfiguration.getModelName()))
				.setMeasure("isNlu", isNlu ? 1D : 0D)
				.setMeasure("isUserMessage", 1D)
				.setMeasure("isSessionStart", isSessionStart ? 1D : 0D)
				.setMeasure("isFallback", isFallback ? 1D : 0D)
				.setMeasure("confidence", isNlu ? eventLog.getVal1() : 1D)
				.setMeasure("isTechnical", isTechnical(isSessionStart, isFallback, isEnd));

		analyticsManager.addProcess(processBuilder.build());
	}

	//return 1D if topic is technical
	private static double isTechnical(final boolean isSessionStart, final boolean isFallback, final boolean isEnd) {
		return isSessionStart || isFallback || isEnd ? 1D : 0D;
	}
}
