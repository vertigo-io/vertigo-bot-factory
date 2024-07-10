package io.vertigo.chatbot.engine.plugins.bt.jira.command.bot;

import io.vertigo.ai.bb.BBKey;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.chatbot.engine.BotEngine;
import io.vertigo.chatbot.engine.plugins.bt.jira.impl.*;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JiraField;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;

import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static io.vertigo.ai.bt.BTNodes.sequence;

public class BotJiraNodeProvider implements Component, Activeable {

    @Inject
    private JiraServerService jiraService;

    @Inject
    private SummaryFieldService summaryFieldService;

    @Inject
    private DescriptionFieldService descriptionFieldService;

    @Inject
    private IssueTypeFieldService issueTypeFieldService;

    @Inject
    private ComponentFieldService componentFieldService;

    @Inject
    private FixVersionFieldService fixVersionFieldService;

    @Inject
    private AffectedVersionFieldService affectedVersionFieldService;

    @Inject
    private PrioritiesFieldService prioritiesFieldService;

    @Inject
    private AssigneeFieldService assigneeFieldService;

    @Inject
    private ReporterFieldService reporterFieldService;

    @Inject
    private AttachmentFieldService attachmentFieldService;

    private List<IJiraFieldService> fieldServices = new ArrayList<>();

    public BTNode jiraIssueCreation(final BlackBoard bb, final List<JiraField> jiraFields, final String urlSentence) {
        return () -> {
            jiraFields.forEach(field -> field.setValue(bb.getString(BBKey.of(field.getKey()))));
            final String result = jiraService.createIssueJiraCommand(bb, jiraFields, fieldServices);
            bb.listPush(BotEngine.BOT_RESPONSE_KEY, urlSentence + " " + result);
            return BTStatus.Succeeded;
        };
    }

    public BTNode buildJiraCreateIssue(final BlackBoard bb, final List<JiraField> jiraFields, final String urlSentence) {
        final List<BTNode> sequence = new ArrayList<>();
        jiraFields.forEach(jiraField -> fieldServices.forEach(fieldService -> {
            if (fieldService.supports(jiraField.getFieldType())) {
                fieldService.processConversation(bb, jiraField, sequence);
            }
        }));

        sequence.add(jiraIssueCreation(bb, jiraFields, urlSentence));
        return sequence(sequence);
    }

    @Override
    public void start() {
        this.fieldServices.add(summaryFieldService);
        this.fieldServices.add(descriptionFieldService);
        this.fieldServices.add(issueTypeFieldService);
        this.fieldServices.add(componentFieldService);
        this.fieldServices.add(fixVersionFieldService);
        this.fieldServices.add(affectedVersionFieldService);
        this.fieldServices.add(prioritiesFieldService);
        this.fieldServices.add(assigneeFieldService);
        this.fieldServices.add(reporterFieldService);
        this.fieldServices.add(attachmentFieldService);
    }

    @Override
    public void stop() {

    }
}
