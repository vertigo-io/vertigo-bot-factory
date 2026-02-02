package io.vertigo.chatbot.engine.plugins.bt.jira.impl;

import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;

import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.chatbot.commons.domain.JiraCustomFieldSettingExport;
import io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JiraField;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.data.model.DtList;

import javax.inject.Inject;
import java.util.List;

/**
 * Service for handling custom Jira fields (JSM mode).
 * This service handles any field that is configured as a custom field in the bot settings.
 */
public class CustomFieldService implements IJiraFieldService, Component {

	@Inject
	private JiraServerService jiraServerService;

	@Override
	public boolean supports(final String fieldKey) {
		// Check if this field is a custom field configured in the bot settings
		final DtList<JiraCustomFieldSettingExport> customFields = jiraServerService.getJiraCustomFieldSettingExports();
		if (customFields == null) {
			return false;
		}
		return customFields.stream()
				.anyMatch(cf -> cf.getFieldKey().equals(fieldKey) && cf.getEnabled());
	}

	@Override
	public void processConversation(final BlackBoard bb, final JiraField jiraField, final List<BTNode> sequence, final boolean checkJiraFields) {
		// Simply ask the user for input - similar to summary field
		sequence.add(BotNodeProvider.inputString(bb, jiraField.getKey(), jiraField.getQuestion()));
	}

	@Override
	public void processTicket(final BlackBoard bb, final IssueInputBuilder iib, final JiraField jiraField) {
		// Custom fields are handled separately in JiraServerService.createJsmRequest
		// For classic Jira mode, we can set the field directly
		if (jiraField.getValue() != null && !jiraField.getValue().isBlank()) {
			iib.setFieldValue(jiraField.getFieldType(), jiraField.getValue());
		}
	}
}
