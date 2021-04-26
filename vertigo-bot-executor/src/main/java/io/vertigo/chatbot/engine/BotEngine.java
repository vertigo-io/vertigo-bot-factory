package io.vertigo.chatbot.engine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.vertigo.ai.bb.BBKey;
import io.vertigo.ai.bb.BBKeyPattern;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.ai.bt.BehaviorTreeManager;
import io.vertigo.ai.nlu.NluManager;
import io.vertigo.ai.nlu.NluResult;
import io.vertigo.ai.nlu.ScoredIntent;
import io.vertigo.chatbot.engine.model.BotInput;
import io.vertigo.chatbot.engine.model.BotResponse;
import io.vertigo.chatbot.engine.model.BotResponse.BotStatus;
import io.vertigo.chatbot.engine.model.BotResponseBuilder;
import io.vertigo.chatbot.engine.model.TopicDefinition;
import io.vertigo.chatbot.engine.model.choice.IBotChoice;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.util.StringUtil;

/**
 * Bot engine that handle user interactions through a Vertigo BlackBoard.
 * <br>
 * bb conventions :
 * <ul>
 * <li>/bot/in/ : inputs</li>
 * <li>/bot/out/ : outputs</li>
 * <li>/bot/status/ : state (current topic, ...)</li>
 * <li>/user/global/ : persisting informations</li>
 * <li>/user/local/ : volatile informations (local to a topic / BT)</li>
 * </ul>
 *
 * @author skerdudou, mlaroche
 */
public class BotEngine {
	public static final String NEXT_TOPIC_KEY = "nexttopic";

	public static final String START_TOPIC_NAME = "!START";
	public static final String END_TOPIC_NAME = "!END";
	public static final String FALLBACK_TOPIC_NAME = "!FALLBACK";

	public static final BBKey BOT_IN_PATH = BBKey.of("/bot/in");
	public static final BBKey BOT_OUT_PATH = BBKey.of("/bot/out");
	public static final BBKey BOT_STATUS_PATH = BBKey.of("/bot/status");
	public static final BBKey USER_GLOBAL_PATH = BBKey.of("/user/global");
	public static final BBKey USER_LOCAL_PATH = BBKey.of("/user/local/");

	public static final BBKey BOT_RESPONSE_KEY = BBKey.of(BOT_OUT_PATH, "/responses");
	public static final BBKey BOT_CHOICES_KEY = BBKey.of(BOT_OUT_PATH, "/choices");

	public static final BBKey BOT_TOPIC_KEY = BBKey.of(BOT_STATUS_PATH, "/topic");
	public static final BBKey BOT_NEXT_TOPIC_KEY = BBKey.of(BOT_STATUS_PATH, "/" + NEXT_TOPIC_KEY);
	public static final BBKey BOT_EXPECT_INPUT_PATH = BBKey.of(BOT_STATUS_PATH, "/expect");

	private final BlackBoard bb;
	private final BehaviorTreeManager behaviorTreeManager;
	private final NluManager nluManager;
	private final Boolean hasBusinessTopic;
	private final Boolean hasEndTopic;

	private final Map<String, TopicDefinition> topicDefinitionMap;

	public BotEngine(final BlackBoard blackBoard, final Map<String, TopicDefinition> topicDefinitionMap,
			final BehaviorTreeManager behaviorTreeManager, final NluManager nluManager) {
		Assertion.check()
				.isNotNull(blackBoard)
				.isNotNull(topicDefinitionMap)
				.isNotNull(behaviorTreeManager)
				.isNotNull(nluManager)
				.isTrue(topicDefinitionMap.containsKey(START_TOPIC_NAME), "You need to provide a starting topic with key BotEngine.START_TOPIC_NAME");
		// ---
		bb = blackBoard;
		this.topicDefinitionMap = topicDefinitionMap;

		this.behaviorTreeManager = behaviorTreeManager;
		this.nluManager = nluManager;

		hasBusinessTopic = topicDefinitionMap.size() > 1;
		hasEndTopic = topicDefinitionMap.containsKey(END_TOPIC_NAME);
	}

	public BotResponse runTick(final BotInput input) {
		// Handle text answers questions inside a BT (put into bb target key)
		if (input.getMessage() != null && bb.exists(BBKey.of(BOT_EXPECT_INPUT_PATH, "/key"))) {
			final var key = BBKey.of(bb.getString(BBKey.of(BOT_EXPECT_INPUT_PATH, "/key")));
			final var type = bb.getString(BBKey.of(BOT_EXPECT_INPUT_PATH, "/type"));
			if ("integer".equals(type)) {
				bb.putInteger(key, Integer.valueOf(input.getMessage()));
			} else {
				bb.putString(key, input.getMessage());
			}
		}

		// Continue on previous topic or resolve a new one
		var topic = Optional.ofNullable(bb.getString(BOT_TOPIC_KEY)).map(topicDefinitionMap::get)
				.orElseGet(() -> {// if no current topic
					final var newTopic = resolveNewTopic(input);
					switchToTopic(newTopic);
					return newTopic;
				});

		// prepare exec
		bb.delete(BBKeyPattern.ofRoot(BOT_IN_PATH));
		bb.delete(BBKeyPattern.ofRoot(BOT_OUT_PATH));
		bb.delete(BBKeyPattern.of(BOT_NEXT_TOPIC_KEY.key()));
		BTStatus status;
		TopicDefinition nextTopic = null;
		do {
			if (nextTopic != null) {
				topic = nextTopic;
				nextTopic = null;
			}
			// exec
			status = behaviorTreeManager.run(topic.getBtRoot(List.of(bb)));

			if (status.isSucceeded() && !topic.getCode().startsWith("!")) { // end of business topic (special topics starts with !)
				nextTopic = topicDefinitionMap.get(END_TOPIC_NAME); // may be null if no end topic
				switchToTopic(nextTopic);
			} else if (bb.exists(BOT_NEXT_TOPIC_KEY)) {
				final var nextTopicName = bb.getString(BOT_NEXT_TOPIC_KEY);
				nextTopic = topicDefinitionMap.get(nextTopicName); // handle forward to another topic
				Assertion.check().isNotNull(nextTopic, "Topic '{0}' not found, cant forward to it", nextTopicName);
				bb.delete(BBKeyPattern.of(BOT_NEXT_TOPIC_KEY.key()));
				switchToTopic(nextTopic);
			}
		} while (nextTopic != null);

		// clean
		if (status.isSucceeded()) {
			// topic ended, clear curent topic in bb
			bb.delete(BBKeyPattern.of(BOT_TOPIC_KEY.key()));
		}

		// build response
		final BotStatus botStatus = resolveResponseStatus(topic, status);

		final var botResponseBuilder = new BotResponseBuilder(botStatus);
		for (int i = 0; i < bb.listSize(BOT_RESPONSE_KEY); i++) {
			botResponseBuilder.addMessage(bb.listGet(BOT_RESPONSE_KEY, i));
		}
		botResponseBuilder.addAllChoices(buildChoices());

		return botResponseBuilder.build();
	}

	private BotStatus resolveResponseStatus(final TopicDefinition topic, final BTStatus status) {
		if (hasBusinessTopic) {
			if (hasEndTopic) {
				// end is after end topic
				return END_TOPIC_NAME.equals(topic.getCode()) && status.isSucceeded() ? BotStatus.Ended : BotStatus.Talking;
			}
			// end is after business topic
			return !topic.getCode().startsWith("!") && status.isSucceeded() ? BotStatus.Ended : BotStatus.Talking;
		}
		// end is after start topic
		return status.isSucceeded() ? BotStatus.Ended : BotStatus.Talking;
	}

	private void switchToTopic(final TopicDefinition newTopic) {
		if (newTopic == null) {
			bb.delete(BBKeyPattern.of(BOT_TOPIC_KEY.key()));
		} else {
			bb.putString(BOT_TOPIC_KEY, newTopic.getCode());
		}
		bb.delete(BBKeyPattern.of(USER_LOCAL_PATH.key())); // clean context relative to a BT
	}

	private TopicDefinition resolveNewTopic(final BotInput input) {
		// select topic from one of this scenario :
		// - Special metadata to choose topic
		// - NLU on incoming text message
		// - welcome routine
		if (input.getMetadatas().containsKey(NEXT_TOPIC_KEY)) {
			return topicDefinitionMap.getOrDefault(input.getMetadatas().get(NEXT_TOPIC_KEY), topicDefinitionMap.get(FALLBACK_TOPIC_NAME));
		}

		final var message = input.getMessage();
		if (!StringUtil.isBlank(message)) {
			return getTopicFromNlu(message).orElse(topicDefinitionMap.get(FALLBACK_TOPIC_NAME));
		}

		return topicDefinitionMap.get(START_TOPIC_NAME);
	}

	private Optional<TopicDefinition> getTopicFromNlu(final String sentence) {
		final NluResult nluResponse = nluManager.recognize(sentence, NluManager.DEFAULT_ENGINE_NAME);
		final var scoredIntents = nluResponse.getScoredIntents();
		scoredIntents.sort(Comparator.comparing(ScoredIntent::getAccuracy, Comparator.reverseOrder()));

		// intents are sorted by decreasing accuracy
		for (final var intent : scoredIntents) {
			final var topic = topicDefinitionMap.get(intent.getIntent().getCode());
			Assertion.check().isNotNull(topic, "Topic '{0}' not found, is NLU backend up to date ?", intent.getIntent().getCode());
			if (intent.getAccuracy() >= topic.getNluThreshold().doubleValue()) { // dont take if not accurate enough
				return Optional.of(topic);
			}
		}
		return Optional.empty();
	}

	private List<IBotChoice> buildChoices() {
		if (!bb.exists(BOT_CHOICES_KEY)) {
			return Collections.emptyList();
		}

		final Method method = resolveChoiceConstructMethod();

		final List<IBotChoice> choices = new ArrayList<>();
		int choiceNumber = 0;
		while (bb.exists(BBKey.of(BOT_CHOICES_KEY, "/" + choiceNumber))) {
			final var choiceKey = BBKey.of(BOT_CHOICES_KEY, "/" + choiceNumber);
			final int paramCount = bb.listSize(choiceKey);
			final Object[] params = new String[paramCount];
			for (int i = 0; i < paramCount; i++) {
				params[i] = bb.listGet(choiceKey, i);
			}

			try {
				choices.add((IBotChoice) method.invoke(null, params));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new VSystemException(e, "Error while calling choice construct method");
			}
			choiceNumber++;
		}

		return choices;
	}

	private Method resolveChoiceConstructMethod() {
		final var className = bb.getString(BBKey.of(BOT_CHOICES_KEY, "/class"));
		try {
			final Class<?> choiceClazz = Class.forName(className);
			final var method = choiceClazz.getMethod("of", String[].class);

			Assertion.check()
					.isTrue(IBotChoice.class.isAssignableFrom(choiceClazz), "Choice class '{0}' must be an IBotChoice", className)
					.isTrue(method.canAccess(null), "Method 'of' of class '{0}' must be public", className);

			return method;
		} catch (final ClassNotFoundException e) {
			throw new VSystemException(e, "Choice class '{0}' not found", className);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new VSystemException(e, "Choice class '{0}' do not implements constructing method 'of'", className);
		}
	}

}
