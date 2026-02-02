package io.vertigo.chatbot.engine.plugins.bt.jira.command.bot;

import io.vertigo.ai.bb.BBKey;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.chatbot.engine.BotEngine;
import io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider;
import io.vertigo.chatbot.engine.plugins.bt.jira.impl.AffectedVersionFieldService;
import io.vertigo.chatbot.engine.plugins.bt.jira.impl.AssigneeFieldService;
import io.vertigo.chatbot.engine.plugins.bt.jira.impl.AttachmentFieldService;
import io.vertigo.chatbot.engine.plugins.bt.jira.impl.ComponentFieldService;
import io.vertigo.chatbot.engine.plugins.bt.jira.impl.CustomFieldService;
import io.vertigo.chatbot.engine.plugins.bt.jira.impl.DescriptionFieldService;
import io.vertigo.chatbot.engine.plugins.bt.jira.impl.FixVersionFieldService;
import io.vertigo.chatbot.engine.plugins.bt.jira.impl.IJiraFieldService;
import io.vertigo.chatbot.engine.plugins.bt.jira.impl.IssueTypeFieldService;
import io.vertigo.chatbot.engine.plugins.bt.jira.impl.JiraServerService;
import io.vertigo.chatbot.engine.plugins.bt.jira.impl.PrioritiesFieldService;
import io.vertigo.chatbot.engine.plugins.bt.jira.impl.ReporterFieldService;
import io.vertigo.chatbot.engine.plugins.bt.jira.impl.SummaryFieldService;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JiraField;
import io.vertigo.chatbot.executor.manager.ExecutorConfigManager;
import io.vertigo.core.node.Node;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;

import javax.inject.Inject;

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
    @Inject
    private CustomFieldService customFieldService;

    private final List<IJiraFieldService> fieldServices = new ArrayList<>();
    private ExecutorConfigManager executorConfigManager;

    public BTNode jiraIssueCreation(final BlackBoard bb, final List<JiraField> jiraFields, final String urlSentence) {
        return () -> {
            jiraFields.forEach(field -> field.setValue(bb.getString(BBKey.of(field.getKey()))));
            final String result = jiraService.createIssueJiraCommand(bb, jiraFields, fieldServices);
            String createdIssueMsg = urlSentence;
            if (jiraService.getJiraCheckFields(executorConfigManager.getConfig())) {
                createdIssueMsg += " " + result;
            }
            bb.listPush(BotEngine.BOT_RESPONSE_KEY, createdIssueMsg);
            return BTStatus.Succeeded;
        };
    }

    public BTNode buildJiraCreateIssue(final BlackBoard bb, final List<JiraField> jiraFields, final String urlSentence) {
        final List<BTNode> sequence = new ArrayList<>();
        final boolean checkJiraFields = jiraService.getJiraCheckFields(executorConfigManager.getConfig());
        jiraFields.forEach(jiraField -> fieldServices.forEach(fieldService -> {
            if (fieldService.supports(jiraField.getFieldType())) {
                fieldService.processConversation(bb, jiraField, sequence, checkJiraFields);
            }
        }));

        sequence.add(jiraIssueCreation(bb, jiraFields, urlSentence));
        return sequence(sequence);
    }

    @Override
    public void start() {
        fieldServices.add(summaryFieldService);
        fieldServices.add(descriptionFieldService);
        fieldServices.add(issueTypeFieldService);
        fieldServices.add(componentFieldService);
        fieldServices.add(fixVersionFieldService);
        fieldServices.add(affectedVersionFieldService);
        fieldServices.add(prioritiesFieldService);
        fieldServices.add(assigneeFieldService);
        fieldServices.add(reporterFieldService);
        fieldServices.add(attachmentFieldService);
        fieldServices.add(customFieldService);


        executorConfigManager = Node.getNode().getComponentSpace().resolve(ExecutorConfigManager.class);
    }

    @Override
    public void stop() {

    }
}
