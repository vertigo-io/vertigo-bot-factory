package io.vertigo.chatbot.engine.plugins.bt.confluence.command.bot;

import java.util.List;

import javax.inject.Inject;

import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.impl.command.BtCommandParserDefinition;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.SimpleDefinitionProvider;

public class BotBtConfluenceCommandParserDefinitionProvider implements SimpleDefinitionProvider, Component {

	@Inject
	private BotConfluenceNodeProvider botConfluenceNodeProvider;

	@Override
	public List<BtCommandParserDefinition> provideDefinitions(final DefinitionSpace definitionSpace) {
		return List.of(
				BtCommandParserDefinition.basicCommand("confluence", (c, p) -> botConfluenceNodeProvider.confluenceSearch(getBB(p), c.getStringParam(0), c.getStringParam(1), c.getStringParam(2))));
	}

	private static BlackBoard getBB(final List<Object> params) {
		return params.stream()
				.filter(o -> o instanceof BlackBoard)
				.map(o -> (BlackBoard) o)
				.findFirst()
				.orElseThrow(() -> new VSystemException("Bot parser plugin needs a BackBoard in parameters."));
	}

}
