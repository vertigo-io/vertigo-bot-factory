package io.vertigo.chatbot.engine.plugins.bt.jira.impl;

import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JiraField;

import java.util.List;

public interface IJiraService {

	String createIssueJiraCommand(final BlackBoard bb, final List<JiraField> jiraFields, List<IJiraFieldService> jiraFieldServices);

}
