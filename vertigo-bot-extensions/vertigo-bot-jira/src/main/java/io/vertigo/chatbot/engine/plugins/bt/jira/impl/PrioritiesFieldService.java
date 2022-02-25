package io.vertigo.chatbot.engine.plugins.bt.jira.impl;

import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.Priority;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.chatbot.engine.model.choice.BotButton;
import io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JiraField;
import io.vertigo.core.node.component.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PrioritiesFieldService implements IJiraFieldService, Component {

	@Inject
	private JiraServerService jiraServerService;

	@Override
	public boolean supports(String fieldKey) {
		return IssueFieldId.PRIORITY_FIELD.id.equals(fieldKey);
	}

	@Override
	public void processConversation(BlackBoard bb, JiraField jiraField, List<BTNode> sequence) {
		sequence.add(getPrioritiesButtons(bb, jiraServerService.getPriorities(), jiraField));
	}

	@Override
	public void processTicket(BlackBoard bb, IssueInputBuilder iib, JiraField jiraField) {
		Priority priority = jiraServerService.getPriorities().stream()
				.filter(it -> it.getId() != null &&  it.getId() == Long.parseLong(jiraField.getValue())).findFirst().orElseThrow();
		iib.setPriorityId(priority.getId());
	}

	private BTNode getPrioritiesButtons(final BlackBoard bb, List<Priority> priorities, JiraField issueTypeField) {
		final List<BotButton> buttons = new ArrayList<>();
		priorities.forEach(priority -> buttons.add(new BotButton(priority.getName(), Objects.requireNonNull(priority.getId()).toString())));
		return BotNodeProvider.chooseButton(bb, issueTypeField.getKey(), issueTypeField.getQuestion(), buttons);
	}
}
