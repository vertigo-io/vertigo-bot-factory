package io.vertigo.chatbot.engine.plugins.bt.command.bot;

import java.util.function.Predicate;

import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTCondition;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.core.lang.Assertion;

/**
 * A user friendly API over the BotNodeProvider to easily create Bots directly in java without bothering about the blackboard param instance
 * @author mlaroche
 *
 */
public class BtBotDriver {
	private final BlackBoard bb;

	public BtBotDriver(final BlackBoard blackBoard) {
		Assertion.check()
				.isNotNull(blackBoard);
		// ---
		bb = blackBoard;
	}

	public BTNode set(final String keyTemplate, final int value) {
		return BotNodeProvider.set(bb, keyTemplate, value);
	}

	public BTNode copy(final String sourceKeyTemplate, final String targetKeyTemplate) {
		return BotNodeProvider.copy(bb, sourceKeyTemplate, targetKeyTemplate);
	}

	public BTNode set(final String keyTemplate, final String value) {
		return BotNodeProvider.set(bb, keyTemplate, value);
	}

	public BTNode incr(final String keyTemplate) {
		return BotNodeProvider.incr(bb, keyTemplate);
	}

	public BTNode incrBy(final String keyTemplate, final int value) {
		return BotNodeProvider.incrBy(bb, keyTemplate, value);
	}

	public BTNode decr(final String keyTemplate) {
		return BotNodeProvider.decr(bb, keyTemplate);
	}

	public BTNode append(final String keyTemplate, final String something) {
		return BotNodeProvider.append(bb, keyTemplate, something);
	}

	public BTNode remove(final String keyPattern) {
		return BotNodeProvider.remove(bb, keyPattern);
	}

	public BTNode removeAll() {
		return BotNodeProvider.removeAll(bb);
	}

	public BTNode inputString(final String keyTemplate, final String question, final Predicate<String> validator) {
		return BotNodeProvider.inputString(bb, keyTemplate, question, validator);
	}

	public BTNode inputInteger(final String keyTemplate, final String question, final Predicate<String> validator) {
		return BotNodeProvider.inputInteger(bb, keyTemplate, question, validator);
	}

	public BTCondition fulfilled(final String keyTemplate) {
		return BotNodeProvider.fulfilled(bb, keyTemplate);
	}

	public BTCondition fulfilledInteger(final String keyTemplate) {
		return BotNodeProvider.fulfilledInteger(bb, keyTemplate);
	}

	//2 args
	public BTNode inputString(final String keyTemplate, final String question) {
		return BotNodeProvider.inputString(bb, keyTemplate, question);
	}

	public BTNode inputInteger(final String keyTemplate, final String question) {
		return BotNodeProvider.inputInteger(bb, keyTemplate, question);
	}

	//3+ args
	public BTNode inputString(final String keyTemplate, final String question, final String choice, final String... otherChoices) {
		return BotNodeProvider.inputString(bb, keyTemplate, question, choice, otherChoices);
	}

	public BTNode inputInteger(final String keyTemplate, final String question, final String choice, final String... otherChoices) {
		return BotNodeProvider.inputInteger(bb, keyTemplate, question, choice, otherChoices);
	}

	public BTNode say(final String msg) {
		return BotNodeProvider.say(bb, msg);
	}

	// Integer
	public BTCondition eq(final String keyTemplate, final Integer compare) {
		return BotNodeProvider.eq(bb, keyTemplate, compare);
	}

	public BTCondition eqIntegerByValue(final String keyTemplate, final String otherKeyTemplate) {
		return BotNodeProvider.eqIntegerByValue(bb, keyTemplate, otherKeyTemplate);
	}

	public BTCondition gt(final String keyTemplate, final Integer compare) {
		return BotNodeProvider.gt(bb, keyTemplate, compare);
	}

	public BTCondition lt(final String keyTemplate, final Integer compare) {
		return BotNodeProvider.lt(bb, keyTemplate, compare);
	}

	public BTCondition gtByValue(final String keyTemplate, final String compare) {
		return BotNodeProvider.gtByValue(bb, keyTemplate, compare);
	}

	public BTCondition ltByValue(final String keyTemplate, final String compare) {
		return BotNodeProvider.ltByValue(bb, keyTemplate, compare);
	}

	//String

	public BTCondition eqByValue(final String keyTemplate, final String otherKeyTemplate) {
		return BotNodeProvider.eqByValue(bb, keyTemplate, otherKeyTemplate);
	}

	public BTCondition eq(final String keyTemplate, final String compare) {
		return BotNodeProvider.eq(bb, keyTemplate, compare);
	}

	public BTCondition eqCaseInsensitive(final String keyTemplate, final String compare) {
		return BotNodeProvider.eqCaseInsensitive(bb, keyTemplate, compare);
	}

	public BTCondition startsWith(final String keyTemplate, final String compare) {
		return BotNodeProvider.startsWith(bb, keyTemplate, compare);
	}

	public BotSwitch doSwitch(final String keyTemplate) {
		return new BotSwitch(value -> eq(keyTemplate, value));
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
