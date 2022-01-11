package io.vertigo.chatbot.executor.welcometour;

import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.impl.command.BtCommandParserDefinition;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.node.definition.Definition;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.SimpleDefinitionProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.List;

public class BotBtWelcomeTourCommandParserDefinitionProvider implements SimpleDefinitionProvider, Component {

	private static final Logger LOGGER = LogManager.getLogger(BotBtWelcomeTourCommandParserDefinitionProvider.class);

	@Inject
	private BtNodeWelcomeTourProvider btNodeWelcomeTourProvider;

	@Override
	public List<? extends Definition> provideDefinitions(DefinitionSpace definitionSpace) {
		LOGGER.info("loading Welcome tour grammar");
		return List.of(
				BtCommandParserDefinition.basicCommand("welcometour", (c, p) ->
						btNodeWelcomeTourProvider.startWelcomeTour(getBB(p), c.getStringParam(0))));
	}

	private static BlackBoard getBB(final List<Object> params) {
		return params.stream()
				.filter(o -> o instanceof BlackBoard)
				.map(o -> (BlackBoard) o)
				.findFirst()
				.orElseThrow(() -> new VSystemException("No BlackBoard found"));
	}
}
