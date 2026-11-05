package io.vertigo.chatbot.engine.plugins.bt.jira.impl;

import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;

import java.util.List;
import java.util.Optional;

import io.vertigo.ai.bb.BBKey;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.chatbot.commons.domain.JiraCustomFieldSettingExport;
import io.vertigo.chatbot.commons.domain.JiraValueListExport;
import io.vertigo.chatbot.engine.model.choice.BotButton;
import io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JiraField;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.data.model.DtList;
import javax.inject.Inject;

import static io.vertigo.ai.bt.BTNodes.selector;

/**
 * Service for handling custom Jira fields (JSM mode).
 * This service handles any field that is configured as a custom field in the bot settings.
 */
public class CustomFieldService implements IJiraFieldService, Component {

	private static final String SINGLE_OPTION_FIELD_TYPE = "SINGLE_OPTION";

	@Inject
	private JiraServerService jiraServerService;

	@Override
	public boolean supports(final String fieldKey) {
		// Check if this field is a custom field configured in the bot settings
		return findCustomField(fieldKey).isPresent();
	}

	@Override
	public void processConversation(final BlackBoard bb, final JiraField jiraField, final List<BTNode> sequence, final boolean checkJiraFields) {
		processConversation(bb, jiraField, List.of(), sequence, checkJiraFields);
	}

	@Override
	public void processConversation(final BlackBoard bb, final JiraField jiraField, final List<JiraField> jiraFields, final List<BTNode> sequence, final boolean checkJiraFields) {
		// A configured value list closes the input. Its codes come from the model, so JSM is not queried for this field.
		final Optional<JiraValueListExport> valueList = resolveValueList(jiraField);
		if (valueList.isPresent()) {
			sequence.add(buildValueListQuestion(bb, jiraField, valueList.get()));
			return;
		}
		final Optional<JiraField> requestTypeField = jiraFields.stream()
				.filter(field -> IssueFieldId.ISSUE_TYPE_FIELD.id.equals(field.getFieldType()))
				.findFirst();
		if (!jiraServerService.isJsmMode() || requestTypeField.isEmpty() || !isSingleOptionField(jiraField.getFieldType())) {
			sequence.add(BotNodeProvider.inputString(bb, jiraField.getKey(), jiraField.getQuestion()));
			return;
		}
		// Valid values depend on the request type chosen during the conversation, so they are fetched when the node is reached
		sequence.add(selector(
				BotNodeProvider.fulfilled(bb, jiraField.getKey()),
				() -> buildCustomFieldQuestion(bb, jiraField, requestTypeField.get()).eval()));
	}

	@Override
	public void processTicket(final BlackBoard bb, final IssueInputBuilder iib, final JiraField jiraField) {
		// Custom fields are handled separately in JiraServerService.createJsmRequest
		// For classic Jira mode, we can set the field directly
		if (jiraField.getValue() != null && !jiraField.getValue().isBlank()) {
			iib.setFieldValue(jiraField.getFieldType(), jiraField.getValue());
		}
	}

	/** Value list declared on the field in the script, resolved against the loaded model. */
	private Optional<JiraValueListExport> resolveValueList(final JiraField jiraField) {
		return jiraServerService.findValueList(jiraField.getValueListCode());
	}

	private static BTNode buildValueListQuestion(final BlackBoard bb, final JiraField jiraField, final JiraValueListExport valueList) {
		final List<BotButton> buttons = valueList.getItems().stream()
				.map(item -> new BotButton(item.getLabel(), item.getCode()))
				.toList();
		return BotNodeProvider.chooseSelect(bb, jiraField.getKey(), jiraField.getQuestion(), buttons);
	}

	private BTNode buildCustomFieldQuestion(final BlackBoard bb, final JiraField jiraField, final JiraField requestTypeField) {
		final String requestTypeId = bb.getString(BBKey.of(requestTypeField.getKey()));
		if (requestTypeId == null || requestTypeId.isBlank()) {
			return BotNodeProvider.inputString(bb, jiraField.getKey(), jiraField.getQuestion());
		}
		final List<BotButton> buttons = jiraServerService.getRequestTypeFieldValidValues(requestTypeId, jiraField.getFieldType()).stream()
				.map(validValue -> new BotButton(validValue.label(), validValue.value()))
				.toList();
		if (buttons.isEmpty()) {
			return BotNodeProvider.inputString(bb, jiraField.getKey(), jiraField.getQuestion());
		}
		return BotNodeProvider.chooseButton(bb, jiraField.getKey(), jiraField.getQuestion(), buttons);
	}

	private boolean isSingleOptionField(final String fieldKey) {
		return findCustomField(fieldKey)
				.map(customField -> SINGLE_OPTION_FIELD_TYPE.equals(customField.getFieldType()))
				.orElse(false);
	}

	private Optional<JiraCustomFieldSettingExport> findCustomField(final String fieldKey) {
		final DtList<JiraCustomFieldSettingExport> customFields = jiraServerService.getJiraCustomFieldSettingExports();
		if (customFields == null) {
			return Optional.empty();
		}
		return customFields.stream()
				.filter(cf -> cf.getFieldKey().equals(fieldKey) && cf.getEnabled())
				.findFirst();
	}
}
