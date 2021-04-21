package io.vertigo.chatbot.engine.plugins.bt.command.bot;

import java.util.ArrayList;
import java.util.List;

import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.impl.bt.command.BtCommand;
import io.vertigo.ai.impl.bt.command.BtCommandParserDefinition;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.SimpleDefinitionProvider;

/**
 * Bot commands.
 *
 * @author skerdudou
 */
public class BotBtCommandParserDefinitionProvider implements SimpleDefinitionProvider {

	@Override
	public List<BtCommandParserDefinition> provideDefinitions(final DefinitionSpace definitionSpace) {
		return List.of(
				BtCommandParserDefinition.basicCommand("set", (c, p) -> BotNodeProvider.set(getBB(p), c.getStringParam(0), c.getStringParam(1))),
				BtCommandParserDefinition.basicCommand("setInt", (c, p) -> BotNodeProvider.set(getBB(p), c.getStringParam(0), c.getIntParam(1))),
				BtCommandParserDefinition.basicCommand("copy", (c, p) -> BotNodeProvider.copy(getBB(p), c.getStringParam(0), c.getStringParam(1))),
				BtCommandParserDefinition.basicCommand("incr", (c, p) -> BotNodeProvider.incr(getBB(p), c.getStringParam(0))),
				BtCommandParserDefinition.basicCommand("incrBy", (c, p) -> BotNodeProvider.incrBy(getBB(p), c.getStringParam(0), c.getIntParam(1))),
				BtCommandParserDefinition.basicCommand("decr", (c, p) -> BotNodeProvider.decr(getBB(p), c.getStringParam(0))),
				BtCommandParserDefinition.basicCommand("append", (c, p) -> BotNodeProvider.append(getBB(p), c.getStringParam(0), c.getStringParam(1))),
				BtCommandParserDefinition.basicCommand("remove", (c, p) -> BotNodeProvider.remove(getBB(p), c.getStringParam(0))),
				BtCommandParserDefinition.basicCommand("say", (c, p) -> BotNodeProvider.say(getBB(p), c.getStringParam(0))),
				BtCommandParserDefinition.basicCommand("eqInt", (c, p) -> BotNodeProvider.eq(getBB(p), c.getStringParam(0), c.getIntParam(1))),
				BtCommandParserDefinition.basicCommand("eq", (c, p) -> BotNodeProvider.eq(getBB(p), c.getStringParam(0), c.getStringParam(1))),
				BtCommandParserDefinition.basicCommand("gt", (c, p) -> BotNodeProvider.gt(getBB(p), c.getStringParam(0), c.getIntParam(1))),
				BtCommandParserDefinition.basicCommand("lt", (c, p) -> BotNodeProvider.lt(getBB(p), c.getStringParam(0), c.getIntParam(1))),
				BtCommandParserDefinition.basicCommand("inputString", (c, p) -> {
					final var optFirstChoice = c.getOptStringParam(2);
					if (optFirstChoice.isEmpty()) {
						return BotNodeProvider.inputString(getBB(p), c.getStringParam(0), c.getStringParam(1));
					}
					return BotNodeProvider.inputString(getBB(p), c.getStringParam(0), c.getStringParam(1), optFirstChoice.get(), c.getRemainingStringParam(3));
				}),
				BtCommandParserDefinition.basicCommand("fulfilled", (c, p) -> BotNodeProvider.fulfilled(getBB(p), c.getStringParam(0))),

				BtCommandParserDefinition.compositeCommand("switch", BotBtCommandParserDefinitionProvider::buildSwitchNode),
				BtCommandParserDefinition.compositeCommand("case", (c, p, l) -> new BotCase(c.getStringParam(0), l)));
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
