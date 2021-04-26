package io.vertigo.chatbot.engine.plugins.bt.command.bot;

import static io.vertigo.ai.bt.BTNodes.condition;
import static io.vertigo.ai.bt.BTNodes.selector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import io.vertigo.ai.bb.BBKey;
import io.vertigo.ai.bb.BBKeyPattern;
import io.vertigo.ai.bb.BBKeyTemplate;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTCondition;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.chatbot.engine.BotEngine;
import io.vertigo.core.util.StringUtil;

public final class BotNodeProvider {

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
		return () -> {
			bb.listPush(BotEngine.BOT_RESPONSE_KEY, bb.format(question));
			bb.putString(BBKey.of(BotEngine.BOT_EXPECT_INPUT_PATH, "/key"), keyTemplate);
			bb.putString(BBKey.of(BotEngine.BOT_EXPECT_INPUT_PATH, "/type"), "string");
			return BTStatus.Running;
		};
	}

	private static BTNode queryInteger(final BlackBoard bb, final String keyTemplate, final String question, final Predicate<String> validator) {
		return () -> {
			bb.listPush(BotEngine.BOT_RESPONSE_KEY, bb.format(question));
			bb.putString(BBKey.of(BotEngine.BOT_EXPECT_INPUT_PATH, "/key"), keyTemplate);
			bb.putString(BBKey.of(BotEngine.BOT_EXPECT_INPUT_PATH, "/type"), "integer");
			return BTStatus.Running;
		};
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

	public static BTNode say(final BlackBoard bb, final String msg) {
		return () -> {
			bb.listPush(BotEngine.BOT_RESPONSE_KEY, bb.format(msg));
			return BTStatus.Succeeded;
		};
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

	public static BTCondition eqCaseInsensitive(final BlackBoard bb, final String keyTemplate, final String compare) {
		return condition(() -> bb.eqCaseInsensitive(bb.eval(BBKeyTemplate.of(keyTemplate)), compare));
	}

	public static BTCondition startsWith(final BlackBoard bb, final String keyTemplate, final String compare) {
		return condition(() -> bb.startsWith(bb.eval(BBKeyTemplate.of(keyTemplate)), compare));
	}

	public static BotSwitch doSwitch(final BlackBoard bb, final String keyTemplate) {
		return new BotSwitch(value -> eq(bb, keyTemplate, value));
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
