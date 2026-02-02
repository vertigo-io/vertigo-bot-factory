package io.vertigo.chatbot.engine.plugins.bt.jira.command.bot;

import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.impl.command.BtCommand;
import io.vertigo.ai.impl.command.BtCommandParserDefinition;
import io.vertigo.chatbot.commons.domain.JiraCustomFieldSettingExport;
import io.vertigo.chatbot.commons.domain.JiraFieldSettingExport;
import io.vertigo.chatbot.engine.plugins.bt.jira.impl.JiraServerService;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JiraField;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.node.definition.Definition;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.core.node.definition.SimpleDefinitionProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static io.vertigo.chatbot.engine.util.BlackBoardUtils.getBB;

public class BotBtJiraCommandParserDefinitionProvider implements SimpleDefinitionProvider, Component {

	private static final Logger LOGGER = LogManager.getLogger(BotBtJiraCommandParserDefinitionProvider.class);
	private static final String RAISE_ON_BEHALF_OF = "raiseOnBehalfOf";
	private static final String REPORTER_FIELD_KEY = "reporter";

	@Inject
	private BotJiraNodeProvider botJiraNodeProvider;

	@Inject
	private JiraServerService jiraServerService;

	@Override
	public List<? extends Definition> provideDefinitions(final DefinitionSpace definitionSpace) {
		LOGGER.info("loading jira plugin");
		return List.of(
				BtCommandParserDefinition.compositeCommand("jira:issue:create",
						this::buildJiraCreation),
				BtCommandParserDefinition.basicCommand("jira:field", (c, p) -> new JiraField(c.getStringParam(0), c.getStringParam(1), c.getStringParam(2))));
	}

	private BTNode buildJiraCreation(final BtCommand command, final List<Object> params, final List<BTNode> childs) {
		Assertion.check()
				.isTrue(childs.stream().allMatch(x -> x instanceof JiraField), "Only 'jira field' is allowed inside 'jira create issue'");

		final List<JiraFieldSettingExport> jiraFieldSettingExports = jiraServerService.getJiraFieldSettingExports();
		final DtList<JiraCustomFieldSettingExport> jiraCustomFieldSettingExports = jiraServerService.getJiraCustomFieldSettingExports();
		final var jiraFields = childs.stream().map(n -> (JiraField) n).collect(Collectors.toList());

		// Validate that each field used is either a predefined enabled field or a custom enabled field
		jiraFields.forEach(jiraField -> {
			final String fieldTypeToCheck = resolveEnabledReporterAlias(jiraField.getFieldType());
			final boolean isPredefinedField = jiraFieldSettingExports.stream()
					.anyMatch(it -> it.getFieldKey().equals(fieldTypeToCheck) && it.getEnabled());
			final boolean isCustomField = jiraCustomFieldSettingExports != null && jiraCustomFieldSettingExports.stream()
					.anyMatch(it -> it.getFieldKey().equals(jiraField.getFieldType()) && it.getEnabled());
			Assertion.check().isTrue(isPredefinedField || isCustomField, jiraField.getFieldType() + " is not allowed here.");
		});

		jiraFieldSettingExports.stream().filter(JiraFieldSettingExport::getMandatory).forEach(jiraFieldSettingExport -> {
			final boolean isPresent = jiraFields.stream().anyMatch(jiraField ->
					resolveRequiredReporterAlias(jiraField.getFieldType(), jiraFieldSettingExport.getFieldKey()));
			Assertion.check().isTrue(isPresent, jiraFieldSettingExport.getFieldKey() + " is missing.");
		});

		// Validate mandatory custom fields are present (only in JSM mode)
		if (jiraCustomFieldSettingExports != null && jiraServerService.isJsmMode()) {
			jiraCustomFieldSettingExports.stream().filter(JiraCustomFieldSettingExport::getMandatory).forEach(customFieldExport ->
					Assertion.check().isTrue(jiraFields.stream()
							.anyMatch(jiraField -> jiraField.getFieldType().equals(customFieldExport.getFieldKey())), customFieldExport.getFieldKey() + " is missing."));
		}

		return botJiraNodeProvider.buildJiraCreateIssue(getBB(params), jiraFields, command.getStringParam(0));
	}

	/** In JSM mode, raiseOnBehalfOf is an alias for reporter. */
	private String resolveEnabledReporterAlias(final String fieldType) {
		if (jiraServerService.isJsmMode() && RAISE_ON_BEHALF_OF.equals(fieldType)) {
			return REPORTER_FIELD_KEY;
		}
		return fieldType;
	}

	/**  In JSM mode, raiseOnBehalfOf is an alias for reporter. */
	private boolean resolveRequiredReporterAlias(final String fieldType, final String requiredFieldKey) {
		if (jiraServerService.isJsmMode() && REPORTER_FIELD_KEY.equals(requiredFieldKey)) {
			return RAISE_ON_BEHALF_OF.equals(fieldType);
		}
		return fieldType.equals(requiredFieldKey);
	}

}
