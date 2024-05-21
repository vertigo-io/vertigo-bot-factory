package io.vertigo.chatbot.engine.plugins.bt.jira.impl;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import io.vertigo.ai.bb.BBKey;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.chatbot.engine.model.choice.BotFileButton;
import io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JiraField;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


public class AttachmentFieldService implements IJiraFieldService, Component {


    @Override
    public boolean supports(String fieldKey) {
        return IssueFieldId.ATTACHMENT_FIELD.id.equals(fieldKey);
    }

    @Override
    public void processConversation(BlackBoard bb, JiraField jiraField, List<BTNode> sequence) {
        sequence.add(getAttachmentButtons(bb, jiraField));
    }

    private BTNode getAttachmentButtons(final BlackBoard bb, JiraField attachmentField) {
        final List<BotFileButton> buttons = new ArrayList<>();
        buttons.add(new BotFileButton("Uploader", "", "", ""));
        return BotNodeProvider.chooseFileButton(bb, attachmentField.getKey(), attachmentField.getQuestion(), buttons);
    }

    @Override
    public void processTicket(final BlackBoard bb, final IssueInputBuilder iib, final JiraField jiraField) {
    }

    public void addingAttachmentToIssue(final BlackBoard bb, final JiraField jiraField, final URI issueUri, final IssueRestClient issueClient) {

        final String filename = bb.getString(BBKey.of(BBKey.of(jiraField.getKey()), "/filename"));
        final String[] fileDataTable = bb.getString(BBKey.of(BBKey.of(jiraField.getKey()), "/filecontent")).split(",");
        Assertion.check().isTrue(fileDataTable.length == 2, "Attachment " + filename + " is not Base64 encoded");

        byte[] decodedBytes = Base64.getDecoder().decode(fileDataTable[1]);
        ByteArrayInputStream decodedAttachment = new ByteArrayInputStream(decodedBytes);

        issueClient.addAttachment(issueUri, decodedAttachment, filename);
    }
}
