package io.vertigo.chatbot.engine.plugins.bt.jira.impl;

import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.chatbot.engine.model.choice.BotButton;
import io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JiraField;
import io.vertigo.core.node.component.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class ComponentFieldService implements IJiraFieldService, Component {

	@Inject
	private JiraServerService jiraServerService;

	@Override
	public boolean supports(String fieldKey) {
		return IssueFieldId.COMPONENTS_FIELD.id.equals(fieldKey);
	}

	@Override
	public void processConversation(BlackBoard bb, JiraField jiraField, List<BTNode> sequence) {
		List<BasicComponent> components = (List<BasicComponent>) jiraServerService.getComponents();
		if (!components.isEmpty()) {
			sequence.add(getComponentIssue(bb, jiraField.getKey(), jiraField.getQuestion(), components));
		}
	}

	@Override
	public void processTicket(BlackBoard bb, IssueInputBuilder iib, JiraField jiraField) {
		iib.setComponentsNames(List.of(jiraField.getValue()));
	}

	private BTNode getComponentIssue(final BlackBoard bb, final String keyTemplate, final String question, List<BasicComponent> components) {
		return () -> {
			final List<BotButton> listButtons = components.stream().map(this::mapComponentToButtonNode).collect(Collectors.toList());
			return BotNodeProvider.chooseButton(bb, keyTemplate, question, listButtons).eval();
		};
	}

	private BotButton mapComponentToButtonNode(final BasicComponent component) {
		return new BotButton(component.getName(), component.getName());
	}
}
