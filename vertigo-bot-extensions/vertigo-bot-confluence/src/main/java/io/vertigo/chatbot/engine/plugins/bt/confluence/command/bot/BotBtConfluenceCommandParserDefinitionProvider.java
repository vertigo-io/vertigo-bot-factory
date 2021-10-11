package io.vertigo.chatbot.engine.plugins.bt.confluence.command.bot;

import java.util.List;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.impl.command.BtCommandParserDefinition;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.SimpleDefinitionProvider;

public class BotBtConfluenceCommandParserDefinitionProvider implements SimpleDefinitionProvider, Component {

	private static final Logger LOGGER = LogManager.getLogger(BotBtConfluenceCommandParserDefinitionProvider.class);

	@Inject
	private BotConfluenceNodeProvider botConfluenceNodeProvider;

	@Override
	public List<BtCommandParserDefinition> provideDefinitions(final DefinitionSpace definitionSpace) {
		LOGGER.info("loading confluence plugin");
		return List.of(
				BtCommandParserDefinition.basicCommand("confluence:search",
						(c, p) -> botConfluenceNodeProvider.confluenceSearch(getBB(p), c.getStringParam(0), c.getStringParam(1), c.getStringParam(2), c.getStringParam(3))));
	}

	private static BlackBoard getBB(final List<Object> params) {
		return params.stream()
				.filter(o -> o instanceof BlackBoard)
				.map(o -> (BlackBoard) o)
				.findFirst()
				.orElseThrow(() -> new VSystemException("No BlackBoard found"));
	}

}
