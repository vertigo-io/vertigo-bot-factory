package io.vertigo.chatbot.engine.plugins.bt.jira.command.bot;

import java.util.List;

import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.node.definition.Definition;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.SimpleDefinitionProvider;

public class BotBtJiraCommandParserDefinitionProvider implements SimpleDefinitionProvider, Component {

	@Override
	public List<? extends Definition> provideDefinitions(final DefinitionSpace definitionSpace) {
		// TODO Auto-generated method stub
		return null;
	}

	private static BlackBoard getBB(final List<Object> params) {
		return params.stream()
				.filter(o -> o instanceof BlackBoard)
				.map(o -> (BlackBoard) o)
				.findFirst()
				.orElseThrow(() -> new VSystemException("No BlackBoard found"));
	}

}
