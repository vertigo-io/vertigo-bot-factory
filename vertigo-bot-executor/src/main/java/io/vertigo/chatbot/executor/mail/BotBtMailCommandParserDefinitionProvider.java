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
import java.util.Optional;

public class BotBtMailCommandParserDefinitionProvider implements SimpleDefinitionProvider, Component {

	private static final Logger LOGGER = LogManager.getLogger(BotBtMailCommandParserDefinitionProvider.class);

	@Inject
	private BtNodeMailProvider btNodeMailProvider;

	@Override
	public List<BtCommandParserDefinition> provideDefinitions(final DefinitionSpace definitionSpace) {
		LOGGER.info("loading mail grammar");
		return List.of(
				BtCommandParserDefinition.basicCommand("mail", (c, p) ->
						btNodeMailProvider.sendMail(getBB(p), c.getStringParam(0), c.getStringParam(1), Optional.empty(), c.getRemainingStringParam(2))),
				BtCommandParserDefinition.basicCommand("mail:attachment", (c, p) ->
						btNodeMailProvider.sendMail(getBB(p), c.getStringParam(0), c.getStringParam(1), c.getOptStringParam(2), c.getRemainingStringParam(3))));
	}

	private static BlackBoard getBB(final List<Object> params) {
		return params.stream()
				.filter(o -> o instanceof BlackBoard)
				.map(o -> (BlackBoard) o)
				.findFirst()
				.orElseThrow(() -> new VSystemException("No BlackBoard found"));
	}
}
