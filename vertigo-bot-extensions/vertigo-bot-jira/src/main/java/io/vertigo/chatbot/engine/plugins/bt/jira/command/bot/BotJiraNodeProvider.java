package io.vertigo.chatbot.engine.plugins.bt.jira.command.bot;

import javax.inject.Inject;

import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.chatbot.engine.BotEngine;
import io.vertigo.chatbot.engine.plugins.bt.jira.impl.JiraServerService;
import io.vertigo.core.node.component.Component;

public class BotJiraNodeProvider implements Component {

	@Inject
	private JiraServerService jiraService;

	public BTNode jiraIssueCreation(final BlackBoard bb, final String response) {
		return () -> {
			final String result = jiraService.createIssueJiraCommand();
			bb.listPush(BotEngine.BOT_RESPONSE_KEY, result);
			return BTStatus.Succeeded;
		};
	}

}
