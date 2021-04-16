package io.vertigo.chatbot.engine.plugins.bt.parser.bot;

import java.util.ArrayList;
import java.util.List;

import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.impl.bt.parser.BtCommand;
import io.vertigo.ai.impl.bt.parser.SimpleBtTextParserPlugin;
import io.vertigo.chatbot.engine.BotCase;
import io.vertigo.chatbot.engine.BotNodeProvider;
import io.vertigo.chatbot.engine.BotSwitch;
import io.vertigo.core.lang.VSystemException;

/**
 * Default BT commands.
 *
 * @author skerdudou
 */
public class BotBtTextParserPlugin extends SimpleBtTextParserPlugin<BotNodeProvider> {

	@Override
	public BotNodeProvider getNodeProvider(final List<Object> params) {
		final var blackBoard = params.stream()
				.filter(o -> o instanceof BlackBoard)
				.map(o -> (BlackBoard) o)
				.findFirst()
				.orElseThrow(() -> new VSystemException("Bot parser plugin needs a BackBoard in parameters."));

		return new BotNodeProvider(blackBoard);
	}

	@Override
	protected void init() {
		registerBasicCommand("set", (c, p) -> p.set(c.getStringParam(0), c.getStringParam(1)));
		registerBasicCommand("setInt", (c, p) -> p.set(c.getStringParam(0), c.getIntParam(1)));
		registerBasicCommand("copy", (c, p) -> p.copy(c.getStringParam(0), c.getStringParam(1)));
		registerBasicCommand("incr", (c, p) -> p.incr(c.getStringParam(0)));
		registerBasicCommand("incrBy", (c, p) -> p.incrBy(c.getStringParam(0), c.getIntParam(1)));
		registerBasicCommand("decr", (c, p) -> p.decr(c.getStringParam(0)));
		registerBasicCommand("append", (c, p) -> p.append(c.getStringParam(0), c.getStringParam(1)));
		registerBasicCommand("remove", (c, p) -> p.remove(c.getStringParam(0)));
		registerBasicCommand("say", (c, p) -> p.say(c.getStringParam(0)));
		registerBasicCommand("eq", (c, p) -> p.eq(c.getStringParam(0), c.getIntParam(1)));
		registerBasicCommand("gt", (c, p) -> p.gt(c.getStringParam(0), c.getIntParam(1)));
		registerBasicCommand("lt", (c, p) -> p.lt(c.getStringParam(0), c.getIntParam(1)));
		registerBasicCommand("inputString", (c, p) -> {
			final var optFirstChoice = c.getOptStringParam(2);
			if (optFirstChoice.isEmpty()) {
				return p.inputString(c.getStringParam(0), c.getStringParam(1));
			}
			return p.inputString(c.getStringParam(0), c.getStringParam(1), optFirstChoice.get(), c.getRemainingStringParam(3));
		});
		registerBasicCommand("fulfilled", (c, p) -> p.fulfilled(c.getStringParam(0)));

		registerCompositeCommand("switch", BotBtTextParserPlugin::buildSwitchNode);
	}

	private static BTNode buildSwitchNode(final BtCommand command, final BotNodeProvider nodeProvider, final List<BTNode> childs) {
		var isOthers = false;
		final BotSwitch switchBuilder = nodeProvider.doSwitch(command.getStringParam(0));
		final var otherNodes = new ArrayList<BTNode>();

		for (final BTNode node : childs) {
			if (node instanceof BotCase) {
				if (isOthers) {
					throw new VSystemException("'case' must be placed before normal command");
				}
				final var botCase = (BotCase) node;
				switchBuilder.when(botCase.getCompare(), botCase.getNodes());
			} else {
				isOthers = true;
				otherNodes.add(node);
			}
		}
		if (!otherNodes.isEmpty()) {
			switchBuilder.whenOther(otherNodes);
		}

		return switchBuilder.build();
	}
}
