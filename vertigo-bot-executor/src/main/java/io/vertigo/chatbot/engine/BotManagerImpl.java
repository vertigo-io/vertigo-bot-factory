package io.vertigo.chatbot.engine;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import io.vertigo.ai.bb.BBKey;
import io.vertigo.ai.bb.BlackBoardManager;
import io.vertigo.ai.bt.BehaviorTreeManager;
import io.vertigo.ai.nlu.NluIntent;
import io.vertigo.ai.nlu.NluManager;
import io.vertigo.chatbot.analytics.AnalyticsSenderServices;
import io.vertigo.chatbot.engine.model.TopicDefinition;
import io.vertigo.commons.codec.CodecManager;
import io.vertigo.core.lang.Assertion;

public final class BotManagerImpl implements BotManager {
	private final BlackBoardManager blackBoardManager;
	private final BehaviorTreeManager behaviorTreeManager;
	private final NluManager nluManager;
	private final CodecManager codecManager;
	private final AnalyticsSenderServices analyticsSenderServices;

	private Map<String, TopicDefinition> topicDefinitionMap; // immutable map of topics

	@Inject
	public BotManagerImpl(
			final CodecManager codecManager,
			final BlackBoardManager blackBoardManager,
			final BehaviorTreeManager behaviorTreeManager,
			final NluManager nluManager,
			final AnalyticsSenderServices analyticsSenderServices) {
		Assertion.check()
				.isNotNull(codecManager)
				.isNotNull(blackBoardManager)
				.isNotNull(behaviorTreeManager)
				.isNotNull(nluManager)
				.isNotNull(analyticsSenderServices);
		//---
		this.blackBoardManager = blackBoardManager;
		this.behaviorTreeManager = behaviorTreeManager;
		this.nluManager = nluManager;
		this.codecManager = codecManager;
		this.analyticsSenderServices = analyticsSenderServices;

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
		return new BotEngine(bb, topicDefinitionMap, behaviorTreeManager, nluManager, analyticsSenderServices);
	}

	@Override
	public synchronized void updateConfig(final Iterable<TopicDefinition> newTopics) {
		final var nluTtrainingData = new HashMap<NluIntent, List<String>>();
		final Map<String, TopicDefinition> topicDefinitionTempMap = new HashMap<>();
		for (final TopicDefinition t : newTopics) {
			if (!t.getTrainingPhrases().isEmpty()) {
				nluTtrainingData.put(NluIntent.of(t.getCode()), t.getTrainingPhrases()); // build NLU training data
			}
			topicDefinitionTempMap.put(t.getCode(), t);
		}

		nluManager.train(nluTtrainingData, NluManager.DEFAULT_ENGINE_NAME); // the new NLU model is effectively running after this line

		// training ok, update state
		topicDefinitionMap = Collections.unmodifiableMap(topicDefinitionTempMap);
	}

}
