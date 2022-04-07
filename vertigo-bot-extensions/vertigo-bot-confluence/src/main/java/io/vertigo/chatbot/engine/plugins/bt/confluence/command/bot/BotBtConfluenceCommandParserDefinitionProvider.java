package io.vertigo.chatbot.engine.plugins.bt.confluence.command.bot;

import io.vertigo.ai.impl.command.BtCommandParserDefinition;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.SimpleDefinitionProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.List;

import static io.vertigo.chatbot.engine.util.BlackBoardUtils.getBB;

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

}
