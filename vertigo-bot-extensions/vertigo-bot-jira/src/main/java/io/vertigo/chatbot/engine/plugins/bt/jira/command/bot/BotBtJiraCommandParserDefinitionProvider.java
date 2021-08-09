package io.vertigo.chatbot.engine.plugins.bt.jira.command.bot;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.impl.command.BtCommand;
import io.vertigo.ai.impl.command.BtCommandParserDefinition;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JiraField;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.node.definition.Definition;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.SimpleDefinitionProvider;

public class BotBtJiraCommandParserDefinitionProvider implements SimpleDefinitionProvider, Component {

	private static final Logger LOGGER = LogManager.getLogger(BotBtJiraCommandParserDefinitionProvider.class);

	@Inject
	private BotJiraNodeProvider botJiraNodeProvider;

	@Override
	public List<? extends Definition> provideDefinitions(final DefinitionSpace definitionSpace) {
		LOGGER.info("loading jira plugin");
		return List.of(
				BtCommandParserDefinition.compositeCommand("jira:issue:create",
						(c, p, l) -> buildJiraCreation(c, p, l)),
				BtCommandParserDefinition.basicCommand("jira:field", (c, p) -> new JiraField(c.getStringParam(0), c.getStringParam(1))));
				BtCommandParserDefinition.basicCommand("jira:issue:create",
						(c, p) -> botJiraNodeProvider.jiraIssueCreation(getBB(p))),
				BtCommandParserDefinition.basicCommand("jira:issue:findany",
						(c, p) -> botJiraNodeProvider.jiraIssueSearchAny(getBB(p), c.getStringParam(0))));
	}

	private static BlackBoard getBB(final List<Object> params) {
		return params.stream()
				.filter(o -> o instanceof BlackBoard)
				.map(o -> (BlackBoard) o)
				.findFirst()
				.orElseThrow(() -> new VSystemException("No BlackBoard found"));
	}

	private BTNode buildJiraCreation(final BtCommand command, final List<Object> params, final List<BTNode> childs) {
		Assertion.check()
				.isTrue(childs.stream().allMatch(x -> x instanceof JiraField), "Only 'jira field' is allowed inside 'jira create issue'");

		final var jiraFields = childs.stream().map(n -> (JiraField) n).collect(Collectors.toList());

		return botJiraNodeProvider.buildJiraCreateIssue(getBB(params), jiraFields, command.getStringParam(0));

	}

}