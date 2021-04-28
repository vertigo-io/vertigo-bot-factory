package io.vertigo.chatbot.engine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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
	public static final String START_TOPIC_NAME = "!START";
	public static final String END_TOPIC_NAME = "!END";
	public static final String FALLBACK_TOPIC_NAME = "!FALLBACK";

	public static final BBKey BOT_IN_PATH = BBKey.of("/bot/in");
	public static final BBKey BOT_OUT_PATH = BBKey.of("/bot/out");
	public static final BBKey BOT_STATUS_PATH = BBKey.of("/bot/status");
	public static final BBKey USER_GLOBAL_PATH = BBKey.of("/user/global");
	public static final BBKey USER_LOCAL_PATH = BBKey.of("/user/local");

	public static final BBKey BOT_IN_MESSAGE_KEY = BBKey.of(BOT_IN_PATH, "/message");
	public static final BBKey BOT_IN_BUTTON_KEY = BBKey.of(BOT_IN_PATH, "/button");
	public static final BBKey BOT_NEXT_TOPIC_KEY = BBKey.of(BOT_IN_PATH, "/nexttopic");

	public static final BBKey BOT_RESPONSE_KEY = BBKey.of(BOT_OUT_PATH, "/responses");
	public static final BBKey BOT_CHOICES_KEY = BBKey.of(BOT_OUT_PATH, "/choices");
	public static final BBKey BOT_OUT_METADATA_PATH = BBKey.of(BOT_OUT_PATH, "/metadata");

	public static final BBKey BOT_TOPIC_KEY = BBKey.of(BOT_STATUS_PATH, "/topic");
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
		// clear old input/outputs context
		bb.delete(BBKeyPattern.ofRoot(BOT_IN_PATH));
		bb.delete(BBKeyPattern.ofRoot(BOT_OUT_PATH));

		// save raw inputs into BB (BOT_IN_MESSAGE_KEY)
		inputToBB(input);

		// Handle text/button answers questions inside a BT (put into provided bb target key)
		handleExpected(input);

		// Continue on previous topic or start from scratch
		TopicDefinition topic = topicDefinitionMap.get(getTopic());

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
				switchToTopic(END_TOPIC_NAME);
			} else if (status.isSucceeded() && FALLBACK_TOPIC_NAME.equals(topic.getCode())) {
				nextTopic = topicDefinitionMap.get(START_TOPIC_NAME);
				switchToTopic(START_TOPIC_NAME);
			} else if (bb.exists(BOT_NEXT_TOPIC_KEY)) {
				final var nextTopicName = bb.getString(BOT_NEXT_TOPIC_KEY);
				nextTopic = topicDefinitionMap.get(nextTopicName); // handle forward to another topic
				Assertion.check().isNotNull(nextTopic, "Topic '{0}' not found, cant forward to it", nextTopicName);
				bb.delete(BBKeyPattern.of(BOT_NEXT_TOPIC_KEY.key()));
				switchToTopic(nextTopicName);
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

		// add all metadatas
		for (final BBKey key : bb.keys(BBKeyPattern.ofRoot(BOT_OUT_METADATA_PATH))) {
			botResponseBuilder.addMetadata(getKeyName(key), getKeyValue(key));
		}

		return botResponseBuilder.build();
	}

	private static String getKeyName(final BBKey key) {
		final var fullName = key.key();
		return fullName.substring(fullName.lastIndexOf('/') + 1);
	}

	private Object getKeyValue(final BBKey key) {
		switch (bb.getType(key)) {
			case String:
				return bb.getString(key);
			case Integer:
				return bb.getInteger(key);
			case List:
				final var list = new ArrayList<String>();
				for (int i = 0; i < bb.listSize(key); i++) {
					list.add(bb.listGet(key, i));
				}
				return list;
			default:
				throw new VSystemException("Unknown key type '{0}'", bb.getType(key));
		}
	}

	private void handleExpected(final BotInput input) {
		final String textMessage = input.getMessage();
		final String buttonPayload = (String) input.getMetadatas().get("payload");

		doHandleExpected("text", textMessage);
		doHandleExpected("nlu", textMessage);
		doHandleExpected("button", buttonPayload);

		bb.delete(BBKeyPattern.ofRoot(BOT_EXPECT_INPUT_PATH));
	}

	private void doHandleExpected(final String prefix, final String value) {
		if (value != null && bb.exists(BBKey.of(BOT_EXPECT_INPUT_PATH, "/" + prefix + "/key"))) { // if value and expected
			final var key = bb.getString(BBKey.of(BOT_EXPECT_INPUT_PATH, "/" + prefix + "/key"));
			final var type = bb.getString(BBKey.of(BOT_EXPECT_INPUT_PATH, "/" + prefix + "/type"));
			switch (type) {
				case "integer":
					bb.putInteger(BBKey.of(key), Integer.valueOf(value));
					break;
				case "string":
					bb.putString(BBKey.of(key), value);
					break;
				case "nlu":
					bb.putString(BBKey.of(key), getTopicFromNlu(value));
					break;
				default:
					throw new VSystemException("Unknown expected type '{0}'", type);
			}
		}
	}

	private void inputToBB(final BotInput input) {
		if (input.getMessage() != null) {
			bb.putString(BotEngine.BOT_IN_MESSAGE_KEY, input.getMessage());
		}

		if (input.getMetadatas() != null && !input.getMetadatas().isEmpty()) {
			for (final var metadata : input.getMetadatas().entrySet()) {
				bb.putString(BBKey.of(BotEngine.BOT_IN_MESSAGE_KEY, "/" + metadata.getKey()), metadata.getValue().toString());
			}
		}
	}

	private String getTopic() {
		if (bb.exists(BOT_NEXT_TOPIC_KEY)) {
			final var nextTopic = bb.getString(BOT_NEXT_TOPIC_KEY);
			bb.delete(BBKeyPattern.of(BOT_NEXT_TOPIC_KEY.key())); // consume it
			switchToTopic(nextTopic);
			return nextTopic;
		}
		if (bb.exists(BOT_TOPIC_KEY)) {
			return bb.getString(BOT_TOPIC_KEY);
		}
		return START_TOPIC_NAME;
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

	private void switchToTopic(final String newTopic) {
		if (newTopic == null) {
			bb.delete(BBKeyPattern.of(BOT_TOPIC_KEY.key()));
		} else {
			bb.putString(BOT_TOPIC_KEY, newTopic);
		}
		bb.delete(BBKeyPattern.ofRoot(USER_LOCAL_PATH)); // clean context relative to a BT
	}

	private String getTopicFromNlu(final String sentence) {
		final NluResult nluResponse = nluManager.recognize(sentence, NluManager.DEFAULT_ENGINE_NAME);
		final var scoredIntents = nluResponse.getScoredIntents();
		scoredIntents.sort(Comparator.comparing(ScoredIntent::getAccuracy, Comparator.reverseOrder()));

		// intents are sorted by decreasing accuracy
		for (final var intent : scoredIntents) {
			final var topic = topicDefinitionMap.get(intent.getIntent().getCode());
			Assertion.check().isNotNull(topic, "Topic '{0}' not found, is NLU backend up to date ?", intent.getIntent().getCode());
			if (intent.getAccuracy() >= topic.getNluThreshold().doubleValue()) { // dont take if not accurate enough
				return topic.getCode();
			}
		}
		return FALLBACK_TOPIC_NAME;
	}

	private List<IBotChoice> buildChoices() {
		if (!bb.exists(BBKey.of(BOT_CHOICES_KEY, "/class"))) {
			return Collections.emptyList();
		}

		final Method method = resolveChoiceConstructMethod();

		final List<IBotChoice> choices = new ArrayList<>();
		int choiceNumber = 0;
		while (bb.listSize(BBKey.of(BOT_CHOICES_KEY, "/" + choiceNumber)) > 0) {
			final var choiceKey = BBKey.of(BOT_CHOICES_KEY, "/" + choiceNumber);
			final int paramCount = bb.listSize(choiceKey);
			final String[] params = new String[paramCount];
			for (int i = 0; i < paramCount; i++) {
				params[i] = bb.listGet(choiceKey, i);
			}

			try {
				final Object[] invokeParams = { params };
				choices.add((IBotChoice) method.invoke(null, invokeParams));
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
