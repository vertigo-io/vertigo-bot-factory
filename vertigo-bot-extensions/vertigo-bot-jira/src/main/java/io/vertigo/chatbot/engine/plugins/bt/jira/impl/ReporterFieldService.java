package io.vertigo.chatbot.engine.plugins.bt.jira.impl;

import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.vertigo.ai.bb.BBKey;
import io.vertigo.ai.bb.BBKeyPattern;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.chatbot.commons.utils.CommonsStringUtils;
import io.vertigo.chatbot.engine.model.choice.BotButton;
import io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JiraField;
import io.vertigo.chatbot.engine.plugins.bt.jira.multilingual.JiraMultilingualResources;
import io.vertigo.chatbot.executor.manager.ExecutorConfigManager;
import io.vertigo.core.locale.LocaleMessageText;
import io.vertigo.core.node.Node;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;

import static io.vertigo.ai.bt.BTNodes.selector;
import static io.vertigo.ai.bt.BTNodes.sequence;
import static io.vertigo.chatbot.engine.plugins.bt.jira.helper.JiraUtils.reporterWsBBPath;
import static io.vertigo.chatbot.engine.plugins.bt.jira.helper.JiraUtils.wsValue;

public class ReporterFieldService implements IJiraFieldService, Component {

    @Inject
    private JiraServerService jiraServerService;
    private boolean isEmailAddressInvalid = false;

    @Override
    public boolean supports(String fieldKey) {
        return IssueFieldId.REPORTER_FIELD.id.equals(fieldKey);
    }

    public void processConversation(BlackBoard bb, JiraField jiraField, List<BTNode> sequence, final boolean checkJiraFields) {
        sequence.add(BotNodeProvider.doNodeOncePerTree(bb,
                sequence(BotNodeProvider.inputString(bb, jiraField.getKey(), jiraField.getQuestion()),
                        getUserFromInput(bb, jiraField, checkJiraFields)), jiraField.getKey()));
        // If input reporter email address was invalid, the new input value must be checked
        if (isEmailAddressInvalid) {
            if (bb.exists(reporterWsBBPath)) {
                sequence.add(getUser(bb, jiraField, checkJiraFields));
            } else {
                // Case of a new conversation
                isEmailAddressInvalid = false;
            }
        }
    }

    private BTNode getUserFromInput(final BlackBoard bb, JiraField jiraField, final boolean checkJiraFields) {
        return selector(
                BotNodeProvider.fulfilled(bb, reporterWsBBPath.key()),
                getUser(bb, jiraField, checkJiraFields));
    }

    private BTNode getUser(final BlackBoard bb, JiraField jiraField, final boolean checkJiraFields) {
        return () -> {
            isEmailAddressInvalid = false;
            bb.putString(reporterWsBBPath, wsValue);
            String jiraReporterField = bb.getString(BBKey.of(jiraField.getKey()));
            List<User> users = jiraServerService.findUserByUsername(jiraReporterField);
            if (users.isEmpty()) {
                if (checkJiraFields) {
                    bb.delete(BBKeyPattern.of(jiraField.getKey()));
                    return BotNodeProvider.say(bb,
                            LocaleMessageText.of(JiraMultilingualResources.NO_USER_FOUND).getDisplay()).eval();
                }
                // If no Jira verification is required, check the validity of the email address format
                if (!CommonsStringUtils.isValidEmailAdress(jiraReporterField)) {
                    bb.delete(BBKeyPattern.of(jiraField.getKey()));
                    isEmailAddressInvalid = true;
                    String invalidAddressMsg =
                            MessageText.of(JiraMultilingualResources.INVALID_EMAIL_ADDRESS, jiraReporterField).getDisplay();
                    BotNodeProvider.say(bb, invalidAddressMsg).eval();
                    return BotNodeProvider.inputString(bb, jiraField.getKey(), jiraField.getQuestion()).eval();
                }
                return BTStatus.Succeeded;
            } else if (users.size() == 1) {
                bb.putString(BBKey.of(jiraField.getKey()), jiraServerService.isCloud() ? users.get(0).getAccountId()
                        : users.get(0).getName());
            } else {
                return getUserButtons(bb, users, jiraField).eval();
            }
            return BTStatus.Succeeded;
        };
    }

    public BTNode getUserButtons(final BlackBoard bb, List<User> users, JiraField issueTypeField) {
        final List<BotButton> buttons = new ArrayList<>();
        users.forEach(user -> buttons.add(new BotButton(user.getDisplayName(), jiraServerService.isCloud() ?
                user.getAccountId() : user.getName())));
        bb.delete(BBKeyPattern.of(issueTypeField.getKey()));
        return BotNodeProvider.chooseButton(bb, issueTypeField.getKey(), issueTypeField.getQuestion(), buttons);
    }

    @Override
    public void processTicket(BlackBoard bb, IssueInputBuilder iib, JiraField jiraField) {
        if (jiraServerService.isCloud()) {
            iib.setFieldInput(new FieldInput(IssueFieldId.REPORTER_FIELD,
                    ComplexIssueInputFieldValue.with("accountId", jiraField.getValue())));
        } else {
            iib.setReporterName(jiraField.getValue());
        }
    }
}
