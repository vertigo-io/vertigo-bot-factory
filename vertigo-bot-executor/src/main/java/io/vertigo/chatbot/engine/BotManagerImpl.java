package io.vertigo.chatbot.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import io.vertigo.ai.bb.BlackBoardManager;
import io.vertigo.ai.bt.BehaviorTreeManager;
import io.vertigo.ai.nlu.Intent;
import io.vertigo.ai.nlu.NluManager;
import io.vertigo.chatbot.engine.model.TopicDefinition;
import io.vertigo.core.lang.Assertion;

public final class BotManagerImpl implements BotManager {
	private final BlackBoardManager blackBoardManager;
	private final BehaviorTreeManager behaviorTreeManager;
	private final NluManager nluManager;

	private Map<String, TopicDefinition> topicDefinitionMap; // immutable map of topics
	private Map<String, TopicDefinition> topicDefinitionTempMap;

	@Inject
	public BotManagerImpl(
			final BlackBoardManager blackBoardManager,
			final BehaviorTreeManager behaviorTreeManager,
			final NluManager nluManager) {
		Assertion.check()
				.isNotNull(blackBoardManager)
				.isNotNull(behaviorTreeManager)
				.isNotNull(nluManager);
		//---
		this.blackBoardManager = blackBoardManager;
		this.behaviorTreeManager = behaviorTreeManager;
		this.nluManager = nluManager;

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
		final var bb = blackBoardManager.connect(storeName);
		// TODO : bb.shift on convId
		return new BotEngine(bb, topicDefinitionMap, behaviorTreeManager, nluManager);
	}

	@Override
	public synchronized void updateConfig(final Iterable<TopicDefinition> newTopics) {
		final var nluTtrainingData = new HashMap<Intent, List<String>>();
		topicDefinitionTempMap = new HashMap<>();
		for (final TopicDefinition t : newTopics) {
			nluTtrainingData.put(Intent.of(t.getCode()), t.getTrainingPhrases()); // build NLU training data
			topicDefinitionTempMap.put(t.getCode(), t);
		}

		nluManager.train(nluTtrainingData, NluManager.DEFAULT_ENGINE_NAME); // the new NLU model is effectively running after this line

		// clean state
		topicDefinitionMap = Collections.unmodifiableMap(topicDefinitionTempMap);
		topicDefinitionTempMap = null;
	}

}
