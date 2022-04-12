package io.vertigo.chatbot.executor.welcometour;

import io.vertigo.ai.impl.command.BtCommandParserDefinition;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.node.definition.Definition;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.SimpleDefinitionProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.List;

import static io.vertigo.chatbot.engine.util.BlackBoardUtils.getBB;

public class BotBtWelcomeTourCommandParserDefinitionProvider implements SimpleDefinitionProvider, Component {

	private static final Logger LOGGER = LogManager.getLogger(BotBtWelcomeTourCommandParserDefinitionProvider.class);

	@Inject
	private BtNodeWelcomeTourProvider btNodeWelcomeTourProvider;

	@Override
	public List<? extends Definition> provideDefinitions(final DefinitionSpace definitionSpace) {
		LOGGER.info("loading Welcome tour grammar");
		return List.of(
				BtCommandParserDefinition.basicCommand("welcometour", (c, p) ->
						btNodeWelcomeTourProvider.startWelcomeTour(getBB(p), c.getStringParam(0))));
	}
}
