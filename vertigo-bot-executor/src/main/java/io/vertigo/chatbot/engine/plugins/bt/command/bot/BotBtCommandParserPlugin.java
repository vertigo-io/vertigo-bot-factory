package io.vertigo.chatbot.engine.plugins.bt.command.bot;

import java.util.ArrayList;
import java.util.List;

import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.impl.bt.command.BtCommand;
import io.vertigo.ai.impl.bt.command.BtCommandParser;
import io.vertigo.ai.impl.bt.command.BtCommandParserProviderPlugin;
import io.vertigo.core.lang.VSystemException;

/**
 * Bot commands.
 *
 * @author skerdudou
 */
public class BotBtCommandParserPlugin implements BtCommandParserProviderPlugin {

	@Override
	public List<BtCommandParser> get() {
		return List.of(
				BtCommandParser.basicCommand("set", (c, p) -> BotNodeProvider.set(getBB(p), c.getStringParam(0), c.getStringParam(1))),
				BtCommandParser.basicCommand("setInt", (c, p) -> BotNodeProvider.set(getBB(p), c.getStringParam(0), c.getIntParam(1))),
				BtCommandParser.basicCommand("copy", (c, p) -> BotNodeProvider.copy(getBB(p), c.getStringParam(0), c.getStringParam(1))),
				BtCommandParser.basicCommand("incr", (c, p) -> BotNodeProvider.incr(getBB(p), c.getStringParam(0))),
				BtCommandParser.basicCommand("incrBy", (c, p) -> BotNodeProvider.incrBy(getBB(p), c.getStringParam(0), c.getIntParam(1))),
				BtCommandParser.basicCommand("decr", (c, p) -> BotNodeProvider.decr(getBB(p), c.getStringParam(0))),
				BtCommandParser.basicCommand("append", (c, p) -> BotNodeProvider.append(getBB(p), c.getStringParam(0), c.getStringParam(1))),
				BtCommandParser.basicCommand("remove", (c, p) -> BotNodeProvider.remove(getBB(p), c.getStringParam(0))),
				BtCommandParser.basicCommand("say", (c, p) -> BotNodeProvider.say(getBB(p), c.getStringParam(0))),
				BtCommandParser.basicCommand("eqInt", (c, p) -> BotNodeProvider.eq(getBB(p), c.getStringParam(0), c.getIntParam(1))),
				BtCommandParser.basicCommand("eq", (c, p) -> BotNodeProvider.eq(getBB(p), c.getStringParam(0), c.getStringParam(1))),
				BtCommandParser.basicCommand("gt", (c, p) -> BotNodeProvider.gt(getBB(p), c.getStringParam(0), c.getIntParam(1))),
				BtCommandParser.basicCommand("lt", (c, p) -> BotNodeProvider.lt(getBB(p), c.getStringParam(0), c.getIntParam(1))),
				BtCommandParser.basicCommand("inputString", (c, p) -> {
					final var optFirstChoice = c.getOptStringParam(2);
					if (optFirstChoice.isEmpty()) {
						return BotNodeProvider.inputString(getBB(p), c.getStringParam(0), c.getStringParam(1));
					}
					return BotNodeProvider.inputString(getBB(p), c.getStringParam(0), c.getStringParam(1), optFirstChoice.get(), c.getRemainingStringParam(3));
				}),
				BtCommandParser.basicCommand("fulfilled", (c, p) -> BotNodeProvider.fulfilled(getBB(p), c.getStringParam(0))),

				BtCommandParser.compositeCommand("switch", BotBtCommandParserPlugin::buildSwitchNode),
				BtCommandParser.compositeCommand("case", (c, p, l) -> new BotCase(c.getStringParam(0), l)));
	}

	private static BTNode buildSwitchNode(final BtCommand command, final List<Object> params, final List<BTNode> childs) {
		var isOthers = false;
		final BotSwitch switchBuilder = BotNodeProvider.doSwitch(getBB(params), command.getStringParam(0));
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

	private static BlackBoard getBB(final List<Object> params) {
		return params.stream()
				.filter(o -> o instanceof BlackBoard)
				.map(o -> (BlackBoard) o)
				.findFirst()
				.orElseThrow(() -> new VSystemException("Bot parser plugin needs a BackBoard in parameters."));

	}
}
