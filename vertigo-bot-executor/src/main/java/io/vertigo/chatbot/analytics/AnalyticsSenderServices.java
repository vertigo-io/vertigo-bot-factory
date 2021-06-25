package io.vertigo.chatbot.analytics;

import java.time.Instant;
import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.ExecutorConfiguration;
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

	public static final String START_TOPIC_NAME = "!START";
	public static final String END_TOPIC_NAME = "!END";
	public static final String FALLBACK_TOPIC_NAME = "!FALLBACK";

	@Inject
	private AnalyticsManager analyticsManager;

	public void sendEventToDb(final TopicDefinition currentTopic, final Optional<ExecutorConfiguration> execConfiguration, final Tuple<Double, String> eventLog, final BotInput input) {
		if (execConfiguration.isPresent()) {
			final boolean isSessionStart = currentTopic.getCode().equals(START_TOPIC_NAME);
			final boolean isFallback = currentTopic.getCode().equals(FALLBACK_TOPIC_NAME);
			final boolean isNlu = eventLog.getVal1() != null;
			final ExecutorConfiguration executorConfiguration = execConfiguration.get();
			final AProcessBuilder processBuilder = AProcess.builder("chatbotmessages", currentTopic.getCode(), Instant.now(), Instant.now()) // timestamp of emitted event
					.addTag("text", input.getMessage() != null ? input.getMessage() : input.getMetadatas().get("payload") != null ? input.getMetadatas().get("payload").toString() : "rien")
					.addTag("type", input.getMetadatas().get("payload") != null ? "button" : "text")
					.addTag("botId", String.valueOf(executorConfiguration.getBotId()))
					.addTag("nodId", String.valueOf(executorConfiguration.getNodId()))
					.addTag("traId", String.valueOf(executorConfiguration.getTraId()))
					.addTag("modelName", String.valueOf(executorConfiguration.getModelName()))
					.setMeasure("isNlu", isNlu ? 1d : 0d)
					.setMeasure("isUserMessage", 1d)
					.setMeasure("isSessionStart", isSessionStart ? 1d : 0d)
					.setMeasure("isFallback", isFallback ? 1d : 0d)
					.setMeasure("confidence", isNlu ? eventLog.getVal1() : 1d);

			analyticsManager.addProcess(processBuilder.build());
		}
	}
}
