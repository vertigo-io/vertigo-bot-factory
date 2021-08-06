package io.vertigo.chatbot.engine.plugins.bt.jira.command.bot;

import static io.vertigo.ai.bt.BTNodes.sequence;
import static io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider.inputString;

import javax.inject.Inject;

import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.chatbot.engine.plugins.bt.jira.impl.JiraService;
import io.vertigo.core.node.component.Component;

public class BotJiraNodeProvider implements Component {

	@Inject
	private JiraService jiraService;

	public BTNode jiraIssueCreation(final BlackBoard bb) {
		return sequence(
				inputString(bb, "/user/local/jira/user", "Quel est votre nom utilisateur"),
				() -> {
					jiraService.createRequestJira();

					return BTStatus.Succeeded;
				});
	}

}
