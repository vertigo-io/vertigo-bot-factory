package io.vertigo.chatbot.engine.plugins.bt.jira.impl;

import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JiraField;
import io.vertigo.chatbot.engine.plugins.bt.jira.multilingual.JiraMultilingualResources;
import io.vertigo.core.locale.MessageText;
import io.vertigo.core.node.component.Component;

import javax.inject.Inject;
import java.util.List;

import static io.vertigo.chatbot.engine.plugins.bt.jira.helper.JiraUtils.getVersionButtons;

public class FixVersionFieldService implements IJiraFieldService, Component {


	@Inject
	private JiraServerService jiraServerService;

	@Override
	public boolean supports(String fieldKey) {
		return IssueFieldId.FIX_VERSIONS_FIELD.id.equals(fieldKey);
	}

	@Override
	public void processConversation(BlackBoard bb, JiraField jiraField, List<BTNode> sequence) {
		List<Version> versions = jiraServerService.getVersions();
		if (!versions.isEmpty()) {
			sequence.add(getVersionButtons(bb, jiraServerService.getVersions(), jiraField));
		} else {
			sequence.add(() -> BotNodeProvider.sayOnce(bb, MessageText.of(JiraMultilingualResources.NO_VERSION_FOUND).getDisplay()).eval());
		}

	}

	@Override
	public void processTicket(BlackBoard bb, IssueInputBuilder iib, JiraField jiraField) {
		iib.setFixVersionsNames(List.of(jiraField.getValue()));
	}
}
