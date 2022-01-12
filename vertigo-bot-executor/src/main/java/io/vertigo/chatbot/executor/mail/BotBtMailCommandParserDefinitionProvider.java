package io.vertigo.chatbot.executor.mail;

import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.impl.command.BtCommandParserDefinition;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.SimpleDefinitionProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.List;

public class BotBtMailCommandParserDefinitionProvider implements SimpleDefinitionProvider, Component {

	private static final Logger LOGGER = LogManager.getLogger(BotBtMailCommandParserDefinitionProvider.class);

	@Inject
	private BtNodeMailProvider btNodeMailProvider;

	@Override
	public List<BtCommandParserDefinition> provideDefinitions(final DefinitionSpace definitionSpace) {
		LOGGER.info("loading mail grammar");
		return List.of(
				BtCommandParserDefinition.basicCommand("mail", (c, p) ->
						btNodeMailProvider.sendMail(getBB(p),
								c.getStringParam(0), c.getStringParam(1),
								c.getStringParam(2), c.getStringParam(3),
								c.getStringParam(4), c.getStringParam(5))));
	}

	private static BlackBoard getBB(final List<Object> params) {
		return params.stream()
				.filter(o -> o instanceof BlackBoard)
				.map(o -> (BlackBoard) o)
				.findFirst()
				.orElseThrow(() -> new VSystemException("No BlackBoard found"));
	}
}
