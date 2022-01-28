package io.vertigo.chatbot.engine.plugins.bt.jira.impl;

import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JiraField;

import java.util.List;

public interface IJiraFieldService {

	boolean supports(String fieldKey);

	void processConversation(BlackBoard bb, JiraField jiraField,  List<BTNode> sequence);

	void processTicket(BlackBoard bb, IssueInputBuilder iib, JiraField jiraField);
}
