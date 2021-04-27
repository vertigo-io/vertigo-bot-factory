package io.vertigo.chatbot.engine.plugins.bt.command.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.impl.command.BtCommand;
import io.vertigo.ai.impl.command.BtCommandParserDefinition;
import io.vertigo.chatbot.engine.model.choice.BotButton;
import io.vertigo.core.lang.Assertion;
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
				BtCommandParserDefinition.basicCommand("say", (c, p) -> BotNodeProvider.sayOnce(getBB(p), c.getStringParam(0))),
				BtCommandParserDefinition.basicCommand("say:always", (c, p) -> BotNodeProvider.say(getBB(p), c.getStringParam(0))),
				BtCommandParserDefinition.basicCommand("eqInt", (c, p) -> BotNodeProvider.eq(getBB(p), c.getStringParam(0), c.getIntParam(1))),
				BtCommandParserDefinition.basicCommand("eq", (c, p) -> BotNodeProvider.eq(getBB(p), c.getStringParam(0), c.getStringParam(1))),
				BtCommandParserDefinition.basicCommand("gt", (c, p) -> BotNodeProvider.gt(getBB(p), c.getStringParam(0), c.getIntParam(1))),
				BtCommandParserDefinition.basicCommand("lt", (c, p) -> BotNodeProvider.lt(getBB(p), c.getStringParam(0), c.getIntParam(1))),
				BtCommandParserDefinition.basicCommand("topic", (c, p) -> BotNodeProvider.switchTopic(getBB(p), c.getStringParam(0))),
				BtCommandParserDefinition.basicCommand("inputString", (c, p) -> {
					final var optFirstChoice = c.getOptStringParam(2);
					if (optFirstChoice.isEmpty()) {
						return BotNodeProvider.inputString(getBB(p), c.getStringParam(0), c.getStringParam(1));
					}
					return BotNodeProvider.inputString(getBB(p), c.getStringParam(0), c.getStringParam(1), optFirstChoice.get(), c.getRemainingStringParam(3));
				}),
				BtCommandParserDefinition.basicCommand("fulfilled", (c, p) -> BotNodeProvider.fulfilled(getBB(p), c.getStringParam(0))),

				BtCommandParserDefinition.compositeCommand("switch", BotBtCommandParserDefinitionProvider::buildSwitchNode),
				BtCommandParserDefinition.compositeCommand("case", (c, p, l) -> new BotCase(c.getStringParam(0), l)),

				BtCommandParserDefinition.compositeCommand("choose:button", BotBtCommandParserDefinitionProvider::buildChooseButtonNode),
				BtCommandParserDefinition.basicCommand("button", (c, p) -> new BotButton(c.getStringParam(0), c.getStringParam(1))));
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

	private static BTNode buildChooseButtonNode(final BtCommand command, final List<Object> params, final List<BTNode> childs) {
		Assertion.check()
				.isTrue(childs.stream().allMatch(b -> b instanceof BotButton), "Only 'button' is allowen inside 'choose:button'");

		final var botButtons = childs.stream()
				.map(b -> (BotButton) b)
				.collect(Collectors.toList());

		return BotNodeProvider.chooseButton(getBB(params), command.getStringParam(0), command.getStringParam(1), botButtons);
	}

	private static BlackBoard getBB(final List<Object> params) {
		return params.stream()
				.filter(o -> o instanceof BlackBoard)
				.map(o -> (BlackBoard) o)
				.findFirst()
				.orElseThrow(() -> new VSystemException("Bot parser plugin needs a BackBoard in parameters."));

	}
}
