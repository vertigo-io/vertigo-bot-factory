package io.vertigo.chatbot.engine.plugins.bt.command.bot.file;

import io.vertigo.ai.impl.command.BtCommandParserDefinition;
import io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.SimpleDefinitionProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.List;

import static io.vertigo.chatbot.engine.util.BlackBoardUtils.getBB;

public class BotBtFileCommandParserDefinitionProvider implements SimpleDefinitionProvider, Component {

	private static final Logger LOGGER = LogManager.getLogger(BotBtFileCommandParserDefinitionProvider.class);

	@Inject
	private BtNodeFileProvider btNodeFileProvider;

	@Override
	public List<BtCommandParserDefinition> provideDefinitions(final DefinitionSpace definitionSpace) {
		LOGGER.info("loading file grammar");
		return List.of(
				BtCommandParserDefinition.basicCommand("file:image", (c, p) ->
						BotNodeProvider.doNodeOncePerTree(getBB(p), btNodeFileProvider.addImageFileNode(getBB(p), c.getStringParam(0)), c.getStringParam(0))),
				BtCommandParserDefinition.basicCommand("file", (c, p) ->
						BotNodeProvider.doNodeOncePerTree(getBB(p), btNodeFileProvider.addFileNode(getBB(p), c.getStringParam(0)), c.getStringParam(0))));
	}

}
