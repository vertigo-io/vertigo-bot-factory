package io.vertigo.chatbot.engine;

import io.vertigo.ai.bb.BBKey;
import io.vertigo.ai.bb.BlackBoardManager;
import io.vertigo.ai.bt.BehaviorTreeManager;
import io.vertigo.ai.nlu.NluIntent;
import io.vertigo.ai.nlu.NluManager;
import io.vertigo.chatbot.commons.LogsUtils;
import io.vertigo.chatbot.engine.model.TopicDefinition;
import io.vertigo.commons.codec.CodecManager;
import io.vertigo.core.lang.Assertion;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public final class BotManagerImpl implements BotManager {
	private final BlackBoardManager blackBoardManager;
	private final BehaviorTreeManager behaviorTreeManager;
	private final NluManager nluManager;
	private final CodecManager codecManager;

	private Map<String, TopicDefinition> topicDefinitionMap; // immutable map of topics

	@Inject
	public BotManagerImpl(
			final CodecManager codecManager,
			final BlackBoardManager blackBoardManager,
			final BehaviorTreeManager behaviorTreeManager,
			final NluManager nluManager) {
		Assertion.check()
				.isNotNull(codecManager)
				.isNotNull(blackBoardManager)
				.isNotNull(behaviorTreeManager)
				.isNotNull(nluManager);
		//---
		this.blackBoardManager = blackBoardManager;
		this.behaviorTreeManager = behaviorTreeManager;
		this.nluManager = nluManager;
		this.codecManager = codecManager;

		topicDefinitionMap = Collections.emptyMap();
	}

	@Override
	public BotEngine createBotEngine() {
		return createBotEngine(UUID.randomUUID(), BlackBoardManager.MAIN_STORE_NAME);
	}

	@Override
	public BotEngine createBotEngine(final String storeName) {
		return createBotEngine(UUID.randomUUID(), storeName);
	}

	@Override
	public BotEngine createBotEngine(final UUID convId) {
		return createBotEngine(convId, BlackBoardManager.MAIN_STORE_NAME);
	}

	@Override
	public BotEngine createBotEngine(final UUID convId, final String storeName) {
		final var bb = blackBoardManager.connect(storeName, BBKey.of("/" + codecManager.getHexEncoder().encode(convId.toString().getBytes(StandardCharsets.UTF_8))));
		return new BotEngine(bb, topicDefinitionMap, behaviorTreeManager, nluManager);
	}

	@Override
	public synchronized void updateConfig(final Iterable<TopicDefinition> newTopics, final StringBuilder logs) {
		final var nluTtrainingData = new HashMap<NluIntent, List<String>>();
		final Map<String, TopicDefinition> topicDefinitionTempMap = new HashMap<>();

		for (final TopicDefinition t : newTopics) {
			LogsUtils.addLogs(logs, t.getCode(), " mapping : ");
			if (!t.getTrainingPhrases().isEmpty()) {
				LogsUtils.addLogs(logs, t.getTrainingPhrases());
				LogsUtils.breakLine(logs);
				nluTtrainingData.put(NluIntent.of(t.getCode()), t.getTrainingPhrases()); // build NLU training data
			}
			topicDefinitionTempMap.put(t.getCode(), t);
			LogsUtils.addLogs(logs, t.getCode(), " mapping ");
			LogsUtils.logOK(logs);
		}
		LogsUtils.addLogs(logs, "Rasa training mapping ");
		if (!generateTopicDefinitionMapHash(topicDefinitionMap).equals(generateTopicDefinitionMapHash(topicDefinitionTempMap))) {
			nluManager.train(nluTtrainingData, NluManager.DEFAULT_ENGINE_NAME); // the new NLU model is effectively running after this line
			LogsUtils.logOK(logs);
		} else {
			LogsUtils.addLogs(logs, "Topic definition map is the same as before, no nlu training necessary.");
		}
		topicDefinitionMap = Collections.unmodifiableMap(topicDefinitionTempMap);
	}

	private String generateTopicDefinitionMapHash(final Map<String, TopicDefinition> topicDefinitionMap) {
		HashCodeBuilder hashCodeBuilder = new HashCodeBuilder(17, 37);
		topicDefinitionMap.forEach((key, topicDefinition) -> {
			hashCodeBuilder.append(key);
			topicDefinition.getTrainingPhrases().forEach(hashCodeBuilder::append);
		});
		return Integer.toString(hashCodeBuilder.toHashCode());
	}

}
