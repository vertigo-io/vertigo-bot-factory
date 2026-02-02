package io.vertigo.chatbot.engine.plugins.bt.jira.impl;

import com.atlassian.jira.rest.client.api.OptionalIterable;
import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;

import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.chatbot.engine.model.choice.BotButton;
import io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JiraField;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JsmRequestType;
import io.vertigo.chatbot.engine.plugins.bt.jira.multilingual.JiraMultilingualResources;
import io.vertigo.core.locale.LocaleMessageText;
import io.vertigo.core.node.component.Component;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IssueTypeFieldService implements IJiraFieldService, Component {

    @Inject
    private JiraServerService jiraServerService;

    @Override
    public boolean supports(final String fieldKey) {
        return IssueFieldId.ISSUE_TYPE_FIELD.id.equals(fieldKey);
    }

    @Override
    public void processConversation(final BlackBoard bb, final JiraField jiraField, final List<BTNode> sequence, final boolean checkJiraFields) {
		if (jiraServerService.isJsmMode()) {
			final List<JsmRequestType> requestTypes = jiraServerService.getRequestTypes();
			if (!requestTypes.isEmpty()) {
				sequence.add(getRequestTypesButtons(bb, requestTypes, jiraField));
			} else {
				sequence.add(() -> BotNodeProvider.sayOnce(bb, LocaleMessageText.of(JiraMultilingualResources.NO_ISSUE_TYPE_FOUND).getDisplay()).eval());
			}
		} else {
			final OptionalIterable<IssueType> issueTypes = jiraServerService.getIssueTypes();
			if (issueTypes.iterator().hasNext()) {
				sequence.add(getIssueTypesButtons(bb, issueTypes.iterator(), jiraField));
			} else {
				sequence.add(() -> BotNodeProvider.sayOnce(bb, LocaleMessageText.of(JiraMultilingualResources.NO_ISSUE_TYPE_FOUND).getDisplay()).eval());
			}
        }
    }

    @Override
    public void processTicket(final BlackBoard bb, final IssueInputBuilder iib, final JiraField jiraField) {
        iib.setIssueTypeId(Long.parseLong(jiraField.getValue()));
    }

    private BTNode getIssueTypesButtons(final BlackBoard bb, final Iterator<IssueType> issueTypes, final JiraField issueTypeField) {
        final List<BotButton> buttons = new ArrayList<>();
        issueTypes.forEachRemaining(issueType -> buttons.add(new BotButton(issueType.getName(), issueType.getId().toString())));
        return BotNodeProvider.chooseButton(bb, issueTypeField.getKey(), issueTypeField.getQuestion(), buttons);
    }

	private BTNode getRequestTypesButtons(final BlackBoard bb, final List<JsmRequestType> requestTypes, final JiraField issueTypeField) {
		final List<BotButton> buttons = new ArrayList<>();
		requestTypes.forEach(requestType -> buttons.add(new BotButton(requestType.getName(), requestType.getId())));
		return BotNodeProvider.chooseButton(bb, issueTypeField.getKey(), issueTypeField.getQuestion(), buttons);
	}
}
