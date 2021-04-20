package io.vertigo.chatbot.engine;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.vertigo.ai.bb.BBKey;
import io.vertigo.ai.bb.BBKeyPattern;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.ai.bt.BehaviorTreeManager;
import io.vertigo.ai.nlu.NluManager;
import io.vertigo.ai.nlu.RecognitionResult;
import io.vertigo.chatbot.engine.model.BotInput;
import io.vertigo.chatbot.engine.model.BotResponse;
import io.vertigo.chatbot.engine.model.BotResponse.BotStatus;
import io.vertigo.chatbot.engine.model.BotResponseBuilder;
import io.vertigo.chatbot.engine.model.TopicDefinition;
import io.vertigo.core.lang.Assertion;

/**
 * Bot engine that handle user interactions through a Vertigo BlackBoard.
 * <br>
 * bb conventions :
 * <ul>
 * <li>/i/ : inputs</li>
 * <li>/o/ : outputs</li>
 * <li>/p/ : persisting informations</li>
 * <li>/v/ : volatile informations (local to a topic / BT)</li>
 * </ul>
 *
 * @author skerdudou, mlaroche
 */
public class BotEngine {
	public static final BBKey BOT_IN_PATH = BBKey.of("bot/in");
	public static final BBKey BOT_OUT_PATH = BBKey.of("bot/out");
	public static final BBKey BOT_STATUS_PATH = BBKey.of("bot/status");

	public static final BBKey BOT_RESPONSE_PATH = BBKey.of(BOT_OUT_PATH, "/response");
	public static final BBKey BOT_TOPIC_PATH = BBKey.of(BOT_STATUS_PATH, "/topic");

	private final BlackBoard bb;
	private final BehaviorTreeManager behaviorTreeManager;
	private final NluManager nluManager;

	private final Map<String, TopicDefinition> topicDefinitionMap;

	public BotEngine(final BlackBoard blackBoard, final Map<String, TopicDefinition> topicDefinitionMap,
			final BehaviorTreeManager behaviorTreeManager, final NluManager nluManager) {
		Assertion.check()
				.isNotNull(blackBoard)
				.isNotNull(topicDefinitionMap)
				.isNotNull(behaviorTreeManager)
				.isNotNull(nluManager);
		// ---
		bb = blackBoard;
		this.topicDefinitionMap = topicDefinitionMap;

		this.behaviorTreeManager = behaviorTreeManager;
		this.nluManager = nluManager;
	}

	public BotResponse runTick(final BotInput input) {
		// set IN
		if (input.getMessage() != null) { // TODO find a better switch
			final var key = BBKey.of(bb.getString(BBKey.of(BOT_IN_PATH, "/key")));
			final var type = bb.getString(BBKey.of(BOT_IN_PATH, "/type"));
			if ("integer".equals(type)) {
				bb.putInteger(key, Integer.valueOf(input.getMessage()));
			} else {
				bb.putString(key, input.getMessage());
			}
		}
		// prepare exec
		bb.delete(BBKeyPattern.ofRoot(BOT_IN_PATH));
		bb.delete(BBKeyPattern.ofRoot(BOT_OUT_PATH));

		// resolve topic
		final var topic = Optional.ofNullable(bb.getString(BOT_TOPIC_PATH)).map(topicDefinitionMap::get)
				.orElseGet(this::resolveNewTopic); // if no current topic

		// exec
		final var status = behaviorTreeManager.run(topic.getBtRoot(List.of(bb)));

		// clean
		if (status.isSucceeded()) {
			// topic ended, clear curent topic in bb
			bb.delete(BBKeyPattern.of(BOT_TOPIC_PATH.key()));
		}

		// build response
		final var botStatus = status == BTStatus.Running ? BotStatus.Talking : BotStatus.Ended;
		final var botResponseBuilder = new BotResponseBuilder(botStatus);
		for (int i = 0; i < bb.listSize(BOT_RESPONSE_PATH); i++) {
			botResponseBuilder.addMessage(bb.listGet(BOT_RESPONSE_PATH, i));
		}

		return botResponseBuilder.build();
		/*
				userResponseOpt.ifPresent(response -> {
					final var key = blackBoard.getString("bot/response");
					final var type = blackBoard.getString("bot/response/type");
					if ("integer".equals(type)) {
						blackBoard.putInteger(key, Integer.valueOf(response));
					} else {
						blackBoard.putString(key, response);
					}
				});
				if (behaviorTreeManager.run(bot) == BTStatus.Running) {
					return BotResponse.talk(blackBoard.getString("bot/question"));
				}
				return BotResponse.BOT_RESPONSE_END;
				*/
	}

	private TopicDefinition resolveNewTopic() {
		// select topic from one of this scenario :
		// - button
		// - NLU on incoming text message
		// - welcome routine
		return topicDefinitionMap.get("START");
	}

	private Optional<TopicDefinition> getTopicFromNlu(final String sentence) {
		final RecognitionResult nluResponse = nluManager.recognize(sentence, NluManager.DEFAULT_ENGINE_NAME);
		// intents are sorted by decreasing accuracy
		for (final var intent : nluResponse.getScoredIntents()) {
			final var topic = getTopicByCode(intent.getIntent().getCode());
			if (intent.getAccuracy() > topic.getNluThreshold()) { // dont take if not accurate enough
				return Optional.of(topic);
			}
		}
		return Optional.empty();
	}

	private TopicDefinition getTopicByCode(final String code) {
		final var topic = topicDefinitionMap.get(code);

		Assertion.check().isNotNull(topic, "Topic '{0}' not found, is NLU backend up to date ?", code);
		return topic;
	}

}
