package io.vertigo.chatbot.engine.plugins.bt.command.bot;

import io.vertigo.ai.bb.BBKey;
import io.vertigo.ai.bb.BBKeyPattern;
import io.vertigo.ai.bb.BBKeyTemplate;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTCondition;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.chatbot.engine.BotEngine;
import io.vertigo.chatbot.engine.model.choice.BotButton;
import io.vertigo.chatbot.engine.model.choice.BotButtonUrl;
import io.vertigo.chatbot.engine.model.choice.BotCard;
import io.vertigo.chatbot.engine.model.choice.BotFileButton;
import io.vertigo.chatbot.engine.model.choice.IBotChoice;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.util.StringUtil;

import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import static io.vertigo.ai.bt.BTNodes.condition;
import static io.vertigo.ai.bt.BTNodes.running;
import static io.vertigo.ai.bt.BTNodes.selector;
import static io.vertigo.ai.bt.BTNodes.sequence;
import static io.vertigo.ai.bt.BTNodes.succeed;

public final class BotNodeProvider {

	private static final Random RND = new Random();

	private BotNodeProvider() {
		// only static
	}

	public static BTNode set(final BlackBoard bb, final String keyTemplate, final int value) {
		return () -> {
			bb.putInteger(bb.eval(BBKeyTemplate.of(keyTemplate)), value);
			return BTStatus.Succeeded;
		};
	}

	public static BTNode copy(final BlackBoard bb, final String sourceKeyTemplate, final String targetKeyTemplate) {
		return () -> {
			bb.putString(bb.eval(BBKeyTemplate.of(targetKeyTemplate)), bb.getString(bb.eval(BBKeyTemplate.of(sourceKeyTemplate))));
			return BTStatus.Succeeded;
		};
	}

	public static BTNode set(final BlackBoard bb, final String keyTemplate, final String value) {
		return () -> {
			bb.putString(bb.eval(BBKeyTemplate.of(keyTemplate)), value);
			return BTStatus.Succeeded;
		};
	}

	public static BTNode incr(final BlackBoard bb, final String keyTemplate) {
		return () -> {
			bb.incr(bb.eval(BBKeyTemplate.of(keyTemplate)));
			return BTStatus.Succeeded;
		};
	}

	public static BTNode incrBy(final BlackBoard bb, final String keyTemplate, final int value) {
		return () -> {
			bb.incrBy(bb.eval(BBKeyTemplate.of(keyTemplate)), value);
			return BTStatus.Succeeded;
		};
	}

	public static BTNode decr(final BlackBoard bb, final String keyTemplate) {
		return () -> {
			bb.decr(bb.eval(BBKeyTemplate.of(keyTemplate)));
			return BTStatus.Succeeded;
		};
	}

	public static BTNode append(final BlackBoard bb, final String keyTemplate, final String something) {
		return () -> {
			bb.append(bb.eval(BBKeyTemplate.of(keyTemplate)), something);
			return BTStatus.Succeeded;
		};
	}

	public static BTNode remove(final BlackBoard bb, final String keyPattern) {
		return () -> {
			bb.delete(BBKeyPattern.of(keyPattern));
			return BTStatus.Succeeded;
		};
	}

	public static BTNode removeAll(final BlackBoard bb) {
		return () -> {
			bb.delete(BBKeyPattern.of("*"));
			return BTStatus.Succeeded;
		};
	}

	public static BTNode inputString(final BlackBoard bb, final String keyTemplate, final String question, final Predicate<String> validator) {
		return selector(
				fulfilled(bb, keyTemplate),
				queryString(bb, keyTemplate, question, validator));
	}

	public static BTNode inputInteger(final BlackBoard bb, final String keyTemplate, final String question, final Predicate<String> validator) {
		return selector(
				fulfilledInteger(bb, keyTemplate),
				queryInteger(bb, keyTemplate, question, validator));
	}

	private static BTNode queryString(final BlackBoard bb, final String keyTemplate, final String question, final Predicate<String> validator) {
		return sequence(
				say(bb, question),
				() -> {
					bb.putString(BBKey.of(BotEngine.BOT_EXPECT_INPUT_PATH, "/text/key"), keyTemplate);
					bb.putString(BBKey.of(BotEngine.BOT_EXPECT_INPUT_PATH, "/text/type"), "string");
					bb.putInteger(BBKey.of(BotEngine.BOT_OUT_METADATA_PATH, "/accepttext"), 1);
					return BTStatus.Running;
				});
	}

	private static BTNode queryInteger(final BlackBoard bb, final String keyTemplate, final String question, final Predicate<String> validator) {
		return sequence(
				set(bb, BotEngine.BOT_RATING_KEY.key(), "true"),
				say(bb, question),
				() -> {
					bb.putString(BBKey.of(BotEngine.BOT_EXPECT_INPUT_PATH, "/integer/key"), keyTemplate);
					bb.putString(BBKey.of(BotEngine.BOT_EXPECT_INPUT_PATH, "/integer/type"), "integer");
					bb.putInteger(BBKey.of(BotEngine.BOT_OUT_METADATA_PATH, "/accepttext"), 1);
					return BTStatus.Running;
				});
	}

	public static BTCondition fulfilled(final BlackBoard bb, final String keyTemplate) {
		return condition(() -> bb.getString(bb.eval(BBKeyTemplate.of(keyTemplate))) != null);
	}

	public static BTCondition fulfilledInteger(final BlackBoard bb, final String keyTemplate) {
		return condition(() -> bb.getInteger(bb.eval(BBKeyTemplate.of(keyTemplate))) != null);
	}

	//2 args
	public static BTNode inputString(final BlackBoard bb, final String keyTemplate, final String question) {
		final Predicate<String> validator = t -> !StringUtil.isBlank(t);
		return inputString(bb, keyTemplate, question, validator);
	}

	public static BTNode inputInteger(final BlackBoard bb, final String keyTemplate, final String question) {
		final Predicate<String> validator = t -> !StringUtil.isBlank(t);
		return inputInteger(bb, keyTemplate, question, validator);
	}

	private static Predicate<String> buildChoices(final String choice, final String... otherChoices) {
		final List<String> choices = new ArrayList<>();
		choices.add(choice);
		choices.addAll(List.of(otherChoices));
		return t -> choices.contains(t);
	}

	//3+ args
	public static BTNode inputString(final BlackBoard bb, final String keyTemplate, final String question, final String choice, final String... otherChoices) {
		return inputString(bb, keyTemplate, question, buildChoices(choice, otherChoices));
	}

	public static BTNode inputInteger(final BlackBoard bb, final String keyTemplate, final String question, final String choice, final String... otherChoices) {
		return inputInteger(bb, keyTemplate, question, buildChoices(choice, otherChoices));
	}

	public static BTNode random(final List<BTNode> childs) {
		return () -> childs.get(RND.nextInt(childs.size())).eval();
	}

	public static BTNode say(final BlackBoard bb, final String msg) {
		if (StringUtil.isBlank(msg)) {
			return succeed();
		}
		return () -> {
			bb.listPush(BotEngine.BOT_RESPONSE_KEY, bb.format(msg));
			return BTStatus.Succeeded;
		};
	}

	public static BTNode sayOnce(final BlackBoard bb, final String msg) {
		return doSayOnce(bb, msg, BotEngine.BOT_STATUS_PATH);
	}

	public static BTNode sayOncePerTree(final BlackBoard bb, final String msg) {
		return doSayOnce(bb, msg, BotEngine.USER_LOCAL_PATH);
	}

	public static BTNode rating(final BlackBoard bb, final String keyTemplate, final String msg) {
		return inputInteger(bb, keyTemplate, msg);
	}


	private static BTNode doSayOnce(final BlackBoard bb, final String msg, final BBKey storeTree) {
		final MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (final NoSuchAlgorithmException e) {
			throw new VSystemException(e, "Error calculating message hash");
		}
		md.update(msg.getBytes(StandardCharsets.UTF_8));
		final var digest = new BigInteger(1, md.digest()).toString(16);

		return selector(
				fulfilled(bb, storeTree.key() + "/displayedmessages/" + digest),
				sequence(
						say(bb, msg),
						set(bb, storeTree.key() + "/displayedmessages/" + digest, "1")));
	}

	public static BTNode doNodeOncePerTree(final BlackBoard bb, final BTNode node, final String msg) {
		final MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (final NoSuchAlgorithmException e) {
			throw new VSystemException(e, "Error calculating message hash");
		}
		md.update(msg.getBytes(StandardCharsets.UTF_8));
		final var digest = new BigInteger(1, md.digest()).toString(16);

		return selector(
				fulfilled(bb, BotEngine.USER_LOCAL_PATH.key() + "/displayedmessages/" + digest),
				sequence(
						node,
						set(bb, BotEngine.USER_LOCAL_PATH.key() + "/displayedmessages/" + digest, "1")));
	}

	// Integer
	public static BTCondition eq(final BlackBoard bb, final String keyTemplate, final Integer compare) {
		return condition(() -> bb.eq(bb.eval(BBKeyTemplate.of(keyTemplate)), compare));
	}

	public static BTCondition eqIntegerByValue(final BlackBoard bb, final String keyTemplate, final String otherKeyTemplate) {
		return condition(() -> bb.eq(bb.eval(BBKeyTemplate.of(keyTemplate)), bb.getInteger(bb.eval(BBKeyTemplate.of(otherKeyTemplate)))));
	}

	public static BTCondition gt(final BlackBoard bb, final String keyTemplate, final Integer compare) {
		return condition(() -> bb.gt(bb.eval(BBKeyTemplate.of(keyTemplate)), compare));
	}

	public static BTCondition lt(final BlackBoard bb, final String keyTemplate, final Integer compare) {
		return condition(() -> bb.lt(bb.eval(BBKeyTemplate.of(keyTemplate)), compare));
	}

	public static BTCondition gtByValue(final BlackBoard bb, final String keyTemplate, final String compare) {
		return condition(() -> bb.gt(bb.eval(BBKeyTemplate.of(keyTemplate)), bb.getInteger(bb.eval(BBKeyTemplate.of(compare)))));
	}

	public static BTCondition ltByValue(final BlackBoard bb, final String keyTemplate, final String compare) {
		return condition(() -> bb.lt(bb.eval(BBKeyTemplate.of(keyTemplate)), bb.getInteger(bb.eval(BBKeyTemplate.of(compare)))));
	}

	//String

	public static BTCondition eqByValue(final BlackBoard bb, final String keyTemplate, final String otherKeyTemplate) {
		return condition(() -> bb.eq(bb.eval(BBKeyTemplate.of(keyTemplate)), bb.getString(bb.eval(BBKeyTemplate.of(otherKeyTemplate)))));
	}

	public static BTCondition eq(final BlackBoard bb, final String keyTemplate, final String compare) {
		return condition(() -> bb.eq(bb.eval(BBKeyTemplate.of(keyTemplate)), compare));
	}

	public static BTCondition contains(final BlackBoard bb, final String keyTemplate, final String compare) {
		return condition(() -> bb.contains(bb.eval(BBKeyTemplate.of(keyTemplate)), compare));
	}

	public static BTCondition eqCaseInsensitive(final BlackBoard bb, final String keyTemplate, final String compare) {
		return condition(() -> bb.eqCaseInsensitive(bb.eval(BBKeyTemplate.of(keyTemplate)), compare));
	}

	public static BTCondition startsWith(final BlackBoard bb, final String keyTemplate, final String compare) {
		return condition(() -> bb.startsWith(bb.eval(BBKeyTemplate.of(keyTemplate)), compare));
	}

	public static BotSwitch doSwitch(final BlackBoard bb, final String keyTemplate) {
		return new BotSwitch(value -> eq(bb, keyTemplate, value));
	}

	public static BTNode switchTopic(final BlackBoard bb, final String topicCode) {
		return () -> {
			bb.putString(BotEngine.BOT_NEXT_TOPIC_KEY, bb.format(topicCode));
			return BTStatus.Running;
		};
	}

	public static BTNode launchJsEvent(final BlackBoard bb, final String eventName) {
		return () -> {
			bb.putString(BBKey.of(BotEngine.BOT_OUT_METADATA_PATH, "/jsevent"), eventName);
			return BTStatus.Succeeded;
		};
	}

	public static BTNode launchWelcomeTour(final BlackBoard bb, final String welcomeTourTechnicalCode) {
		return () -> {
			bb.putString(BBKey.of(BotEngine.BOT_OUT_METADATA_PATH, "/welcometour"), welcomeTourTechnicalCode);
			return BTStatus.Succeeded;
		};
	}


	public static BTNode link(final BlackBoard bb, final String url, final Boolean newTab) {
		return () -> {
			bb.listPush(BotEngine.BOT_RESPONSE_KEY, formatLink(url, newTab));
			return BTStatus.Succeeded;
		};
	}

	public static BTNode image(final BlackBoard bb, final String url) {
		return () -> {
			bb.listPush(BotEngine.BOT_RESPONSE_KEY, formatImageUrl(url));
			return BTStatus.Succeeded;
		};
	}

	public static String formatImageUrl(final String url) {
		Assertion.check().isNotNull(url);
		Assertion.check().isTrue(isValidURL(url), "Not a valid URL");
		return "<img src='" + url + "' class='imgClass' />";
	}

	public static String formatLink(final String url, final Boolean newTab) {
		Assertion.check().isNotNull(url);
		Assertion.check().isTrue(isValidURL(url), "Not a valid URL");
		String target = "_top";
		if (newTab) {
			target = "_blank";
		}
		return "<a href='" + url + "' target='"+target+"' >" + url + "</a>";
	}

	public static boolean isValidURL(final String url) {
		try {
			final URL validUrl = new URL(url);
			validUrl.toURI();
			return true;
		} catch (final Exception e) {
			return false;
		}
	}


	public static BTNode switchTopicStart(final BlackBoard bb) {
		return switchTopic(bb, BotEngine.START_TOPIC_NAME);
	}

	public static BTNode switchTopicFallback(final BlackBoard bb) {
		return switchTopic(bb, BotEngine.FALLBACK_TOPIC_NAME);
	}

	public static BTNode switchTopicEnd(final BlackBoard bb) {
		return switchTopic(bb, BotEngine.END_TOPIC_NAME);
	}

	public static BTNode switchTopicIdle(final BlackBoard bb) {
		return switchTopic(bb, BotEngine.IDLE_TOPIC_NAME);
	}

	public static BTNode chooseButton(final BlackBoard bb, final String keyTemplate, final String question, final Iterable<? extends IBotChoice > buttons) {
		return selector(
				fulfilled(bb, keyTemplate),
				sequence(
						say(bb, question),
						storeButtons(bb, buttons, List.of(BotButton.class, BotButtonUrl.class)),
						queryButton(bb, keyTemplate),
						running()));
	}

	public static BTNode chooseFileButton(final BlackBoard bb, final String keyTemplate, final String question, final Iterable<? extends IBotChoice > buttons) {
		return selector(
				fulfilled(bb, keyTemplate),
				sequence(
						say(bb, question),
						storeButtons(bb, buttons, List.of(BotFileButton.class, BotButton.class)),
						queryFileButton(bb, keyTemplate),
						running())
				);
	}

	public static BTNode chooseCard(final BlackBoard bb, final String keyTemplate, final String question, final Iterable<BotCard> cards) {
		return selector(
				fulfilled(bb, keyTemplate),
				sequence(
						say(bb, question),
						storeButtons(bb, cards, List.of(BotCard.class)),
						queryButton(bb, keyTemplate),
						running()));
	}

	public static BTNode chooseNlu(final BlackBoard bb, final String question) {
		return sequence(
				say(bb, question),
				queryNlu(bb, BotEngine.BOT_NEXT_TOPIC_KEY.key()),
				running());
	}

	public static BTNode chooseButtonOrNlu(final BlackBoard bb, final String keyTemplate, final String question, final Iterable<? extends IBotChoice > buttons) {
		return selector(
				fulfilled(bb, keyTemplate),
				sequence(
						say(bb, question),
						storeButtons(bb, buttons, List.of(BotButton.class, BotButtonUrl.class)),
						queryButton(bb, keyTemplate),
						queryNlu(bb, BotEngine.BOT_NEXT_TOPIC_KEY.key()),
						running()));
	}

	public static BTNode queryButton(final BlackBoard bb, final String keyTemplate) {
		return () -> {
			bb.putString(BBKey.of(BotEngine.BOT_EXPECT_INPUT_PATH, "/button/key"), keyTemplate);
			bb.putString(BBKey.of(BotEngine.BOT_EXPECT_INPUT_PATH, "/button/type"), "string");
			return BTStatus.Succeeded;
		};
	}

	public static BTNode queryFileButton(final BlackBoard bb, final String keyTemplate) {
		return () -> {
			bb.putString(BBKey.of(BotEngine.BOT_EXPECT_INPUT_PATH, "/filebutton/key"), keyTemplate);
			bb.putString(BBKey.of(BotEngine.BOT_EXPECT_INPUT_PATH, "/filebutton/type"), "file");
			return BTStatus.Succeeded;
		};
	}

	private static BTNode queryNlu(final BlackBoard bb, final String keyTemplate) {
		return () -> {
			bb.putString(BBKey.of(BotEngine.BOT_EXPECT_INPUT_PATH, "/nlu/key"), keyTemplate);
			bb.putString(BBKey.of(BotEngine.BOT_EXPECT_INPUT_PATH, "/nlu/type"), "nlu");
			bb.putInteger(BBKey.of(BotEngine.BOT_OUT_METADATA_PATH, "/accepttext"), 1);
			return BTStatus.Succeeded;
		};
	}

	// store all buttons in the BB, engine will reconstruct button back when constructing response object
	public static <T extends IBotChoice> BTNode storeButtons(final BlackBoard bb, final Iterable<? extends IBotChoice> buttons, final List<Class<? extends IBotChoice>> clazzs) {
		final List<BTNode> sequence = new ArrayList<>();

		int choiceNumber = 0;
		for (final IBotChoice button : buttons) {
			final String className = clazzs.stream().filter(clazz -> clazz.isAssignableFrom(button.getClass()))
					.findFirst().orElseThrow().getName();

			sequence.add(set(bb, BotEngine.BOT_CHOICES_KEY.key() + "/" + choiceNumber +  "/class", className));
			for (final String param : button.exportParams()) {
				sequence.add(listPush(bb, BotEngine.BOT_CHOICES_KEY.key() +  "/" + choiceNumber, param));
			}

			choiceNumber++;
		}
		return sequence(sequence);
	}

	public static BTNode listPush(final BlackBoard bb, final String keyTemplate, final String value) {
		return () -> {
			bb.listPush(bb.eval(BBKeyTemplate.of(keyTemplate)), value);
			return BTStatus.Succeeded;
		};
	}

	/*	public BTNode confirm(final String keyTemplate, final String confirmMsg) {
			return () -> {
				final String response = answer(confirmMsg);
				if (response.isBlank()) {
					return BTStatus.Succeeded;
				}
				bb.put(bb.format(keyTemplate), response);
				return BTStatus.Succeeded;
			};
		}*/

}
