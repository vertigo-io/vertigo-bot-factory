package io.vertigo.chatbot.engine.plugins.bt.jira.impl;

import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import io.vertigo.ai.bb.BBKey;
import io.vertigo.ai.bb.BBKeyPattern;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.chatbot.engine.model.choice.BotButton;
import io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JiraField;
import io.vertigo.chatbot.engine.plugins.bt.jira.multilingual.JiraMultilingualResources;
import io.vertigo.core.locale.MessageText;
import io.vertigo.core.node.component.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static io.vertigo.ai.bt.BTNodes.selector;
import static io.vertigo.chatbot.engine.plugins.bt.jira.helper.JiraUtils.assigneeWsBBPath;
import static io.vertigo.chatbot.engine.plugins.bt.jira.helper.JiraUtils.wsValue;

public class AssigneeFieldService implements IJiraFieldService, Component {

	@Inject
	private JiraServerService jiraServerService;

	@Override
	public boolean supports(String fieldKey) {
		return IssueFieldId.ASSIGNEE_FIELD.id.equals(fieldKey);
	}

	public void processConversation(BlackBoard bb, JiraField jiraField, List<BTNode> sequence) {
		sequence.add(BotNodeProvider.inputString(bb, jiraField.getKey(), jiraField.getQuestion()));
		sequence.add(getUserFromInput(bb, jiraField));
	}

	private BTNode getUserFromInput(final BlackBoard bb, JiraField jiraField) {
		return selector(
				BotNodeProvider.fulfilled(bb, assigneeWsBBPath.key()),
				getUser(bb, jiraField));
	}

	private BTNode getUser(final BlackBoard bb, JiraField jiraField) {
		return () -> {
			bb.putString(assigneeWsBBPath, wsValue);
			List<User> users = jiraServerService.findUserByUsername(bb.getString(BBKey.of(jiraField.getKey())));
			if (users.isEmpty()) {
				return BotNodeProvider.say(bb, MessageText.of(JiraMultilingualResources.NO_USER_FOUND).getDisplay()).eval();
			} else if (users.size() == 1) {
				bb.putString(BBKey.of(jiraField.getKey()), users.get(0).getName());
			} else {
				return getUserButtons(bb, users, jiraField).eval();
			}
			return BTStatus.Succeeded;
		};
	}

	public static BTNode getUserButtons(final BlackBoard bb, List<User> users, JiraField issueTypeField) {
		final List<BotButton> buttons = new ArrayList<>();
		users.forEach(user -> buttons.add(new BotButton(user.getDisplayName(), user.getName())));
		bb.delete(BBKeyPattern.of(issueTypeField.getKey()));
		return BotNodeProvider.chooseButton(bb, issueTypeField.getKey(), issueTypeField.getQuestion(), buttons);
	}

	@Override
	public void processTicket(BlackBoard bb, IssueInputBuilder iib, JiraField jiraField) {
		iib.setAssigneeName(jiraField.getValue());
	}
}
