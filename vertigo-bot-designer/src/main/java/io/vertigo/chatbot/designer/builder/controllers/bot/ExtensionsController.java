package io.vertigo.chatbot.designer.builder.controllers.bot;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.commons.domain.ConfluenceSetting;
import io.vertigo.chatbot.commons.domain.JiraField;
import io.vertigo.chatbot.commons.domain.JiraFieldSetting;
import io.vertigo.chatbot.commons.domain.JiraSetting;
import io.vertigo.chatbot.commons.domain.topic.ScriptIntention;
import io.vertigo.chatbot.commons.multilingual.extensions.ExtensionsMultilingualResources;
import io.vertigo.chatbot.designer.builder.services.ConfluenceSettingServices;
import io.vertigo.chatbot.designer.builder.services.JiraFieldService;
import io.vertigo.chatbot.designer.builder.services.JiraFieldSettingServices;
import io.vertigo.chatbot.designer.builder.services.JiraSettingServices;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.designer.builder.services.topic.ScriptIntentionServices;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.core.locale.MessageText;
import io.vertigo.datamodel.structure.definitions.DtField;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.stereotype.Validate;
import io.vertigo.vega.webservice.validation.AbstractDtObjectValidator;
import io.vertigo.vega.webservice.validation.DtObjectErrors;
import io.vertigo.vega.webservice.validation.UiMessageStack;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;

@Controller
@RequestMapping("/bot/{botId}/extensions")
@Secured("Chatbot$botAdm")
public class ExtensionsController extends AbstractBotController {

	private static final ViewContextKey<ConfluenceSetting> confluenceSettingsKey = ViewContextKey.of("confluenceSettings");

	private static final ViewContextKey<ConfluenceSetting> confluenceSettingsFilteredKey = ViewContextKey.of("confluenceSettingsFiltered");

	private static final ViewContextKey<ConfluenceSetting> newConfluenceSettingKey = ViewContextKey.of("newConfluenceSetting");

	private static final ViewContextKey<JiraSetting> jiraSettingsKey = ViewContextKey.of("jiraSettings");

	private static final ViewContextKey<JiraSetting> jiraSettingsFilteredKey = ViewContextKey.of("jiraSettingsFiltered");

	private static final ViewContextKey<JiraSetting> newJiraSettingKey = ViewContextKey.of("newJiraSetting");

	private static final ViewContextKey<ChatbotNode> nodeListKey = ViewContextKey.of("nodeList");

	private static final ViewContextKey<JiraFieldSetting> jiraFieldSettingsKey = ViewContextKey.of("jiraFieldSettings");

	private static final ViewContextKey<JiraField> jiraFieldsKey = ViewContextKey.of("jiraFields");

	private static final ViewContextKey<ScriptIntention> scriptIntentionKey = ViewContextKey.of("scriptIntention");

	@Inject
	private ConfluenceSettingServices confluenceSettingServices;

	@Inject
	private JiraSettingServices jiraSettingServices;

	@Inject
	private JiraFieldSettingServices jiraFieldSettingServices;

	@Inject
	private JiraFieldService jiraFieldService;

	@Inject
	private NodeServices nodeServices;

	@Inject
	private ScriptIntentionServices scriptIntentionServices;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
		final Chatbot bot = super.initCommonContext(viewContext, uiMessageStack, botId);
		final DtList<ConfluenceSetting> confluenceSettings = confluenceSettingServices.findAllByBotId(botId);
		viewContext.publishDtList(confluenceSettingsKey, confluenceSettings);
		viewContext.publishDtList(confluenceSettingsFilteredKey, confluenceSettings);
		viewContext.publishDto(newConfluenceSettingKey, new ConfluenceSetting());
		final DtList<JiraSetting> jiraSettings = jiraSettingServices.findAllByBotId(botId);
		viewContext.publishDtList(jiraFieldsKey, jiraFieldService.findAll());
		viewContext.publishDtList(jiraSettingsKey, jiraSettings);
		viewContext.publishDtList(jiraSettingsFilteredKey, jiraSettings);
		viewContext.publishDtList(jiraFieldSettingsKey, jiraFieldSettingServices.findAllByBotId(botId));
		viewContext.publishDto(newJiraSettingKey, new JiraSetting());
		viewContext.publishDtList(nodeListKey, nodeServices.getNodesByBot(bot));
		viewContext.publishDto(scriptIntentionKey, scriptIntentionServices.getNewScriptIntention(bot));
		super.initBreadCrums(viewContext, "EXTENSION");
	}

	@PostMapping("/_saveConfluenceSetting")
	public ViewContext saveConfluenceSetting(final ViewContext viewContext,
			final UiMessageStack uiMessageStack,
			@ViewAttribute("bot") final Chatbot bot,
			@ViewAttribute("newConfluenceSetting") @Validate(ConfluenceSettingNotEmptyValidator.class) final ConfluenceSetting confluenceSetting) {

		confluenceSettingServices.save(confluenceSetting);

		final DtList<ConfluenceSetting> confluenceSettings = confluenceSettingServices.findAllByBotId(bot.getBotId());
		viewContext.publishDtList(confluenceSettingsKey, confluenceSettings);
		viewContext.publishDtList(confluenceSettingsFilteredKey, confluenceSettings);
		return viewContext;
	}

	@PostMapping("/_deleteConfluenceSetting")
	public ViewContext deleteConfluenceSetting(final ViewContext viewContext,
			final UiMessageStack uiMessageStack,
			@ViewAttribute("bot") final Chatbot bot,
			@RequestParam("conSetId") final Long conSetId) {
		confluenceSettingServices.delete(conSetId);
		final DtList<ConfluenceSetting> confluenceSettings = confluenceSettingServices.findAllByBotId(bot.getBotId());
		viewContext.publishDtList(confluenceSettingsKey, confluenceSettings);
		viewContext.publishDtList(confluenceSettingsFilteredKey, confluenceSettings);
		return viewContext;
	}

	@PostMapping("/_saveJiraSetting")
	public ViewContext saveJiraSetting(final ViewContext viewContext,
			final UiMessageStack uiMessageStack,
			@ViewAttribute("bot") final Chatbot bot,
			@ViewAttribute("newJiraSetting") @Validate(JiraSettingNotEmptyValidator.class) final JiraSetting jiraSetting) {

		jiraSettingServices.save(jiraSetting);

		final DtList<JiraSetting> jiraSettings = jiraSettingServices.findAllByBotId(bot.getBotId());
		viewContext.publishDtList(jiraSettingsKey, jiraSettings);
		viewContext.publishDtList(jiraSettingsFilteredKey, jiraSettings);
		return viewContext;
	}

	@PostMapping("/_deleteJiraSetting")
	public ViewContext deleteJiraSetting(final ViewContext viewContext,
			final UiMessageStack uiMessageStack,
			@ViewAttribute("bot") final Chatbot bot,
			@RequestParam("jirSetId") final Long jirSetId) {
		jiraSettingServices.delete(jirSetId);
		final DtList<JiraSetting> jiraSettings = jiraSettingServices.findAllByBotId(bot.getBotId());
		viewContext.publishDtList(jiraSettingsKey, jiraSettings);
		viewContext.publishDtList(jiraSettingsFilteredKey, jiraSettings);
		return viewContext;
	}

	@PostMapping("_enableDisableJiraField")
	public ViewContext enabledDisableJiraField(final ViewContext viewContext,
			final UiMessageStack uiMessageStack,
			@ViewAttribute("bot") final Chatbot bot,
			@RequestParam("fieldKey") final String fieldKey,
			@RequestParam("enabled") final String enabled) {

		jiraFieldSettingServices.findByBotIdAndFieldName(bot.getBotId(), fieldKey).ifPresent(jiraFieldSetting -> {
			final boolean isEnabled = "true".equals(enabled);
			jiraFieldSetting.setEnabled(isEnabled);
			if (!isEnabled) {
				jiraFieldSetting.setMandatory(false);
			}
			jiraFieldSettingServices.save(jiraFieldSetting);
		});

		viewContext.publishDtList(jiraFieldSettingsKey, jiraFieldSettingServices.findAllByBotId(bot.getBotId()));
		return viewContext;
	}

	@PostMapping("_mandatoryJiraField")
	public ViewContext mandatoryJiraField(final ViewContext viewContext,
			final UiMessageStack uiMessageStack,
			@ViewAttribute("bot") final Chatbot bot,
			@RequestParam("fieldKey") final String fieldKey,
			@RequestParam("mandatory") final String mandatory) {

		jiraFieldSettingServices.findByBotIdAndFieldName(bot.getBotId(), fieldKey).ifPresent(jiraFieldSetting -> {
			jiraFieldSetting.setMandatory("true".equals(mandatory));
			jiraFieldSettingServices.save(jiraFieldSetting);
		});

		viewContext.publishDtList(jiraFieldSettingsKey, jiraFieldSettingServices.findAllByBotId(bot.getBotId()));
		return viewContext;
	}



	public static final class ConfluenceSettingNotEmptyValidator extends AbstractDtObjectValidator<ConfluenceSetting> {

		/** {@inheritDoc} */
		@Override
		protected void checkMonoFieldConstraints(final ConfluenceSetting confluenceSetting, final DtField dtField, final DtObjectErrors dtObjectErrors) {
			if (DtDefinitions.ConfluenceSettingFields.url.name().equals(dtField.getName())
					|| DtDefinitions.ConfluenceSettingFields.login.name().equals(dtField.getName())
					|| DtDefinitions.ConfluenceSettingFields.password.name().equals(dtField.getName())) {
				final String value = (String) dtField.getDataAccessor().getValue(confluenceSetting);
				if (value == null || value.trim().isEmpty()) {
					dtObjectErrors.addError(dtField.getName(), MessageText.of(ExtensionsMultilingualResources.MISSING_FIELD));
				}
			}
			if (DtDefinitions.ConfluenceSettingFields.numberOfResults.name().equals(dtField.getName())) {
				final Long value = (Long) dtField.getDataAccessor().getValue(confluenceSetting);
				if (value == null || value <= 0) {
					dtObjectErrors.addError(dtField.getName(), MessageText.of(ExtensionsMultilingualResources.VALUE_SHOULD_BE_GREATER_THAN_ZERO));
				}
			}
			if (DtDefinitions.ConfluenceSettingFields.nodId.name().equals(dtField.getName())) {
				final Long value = (Long) dtField.getDataAccessor().getValue(confluenceSetting);
				if (value == null) {
					dtObjectErrors.addError(dtField.getName(), MessageText.of(ExtensionsMultilingualResources.MISSING_FIELD));
				}
			}
		}
	}

	public static final class JiraSettingNotEmptyValidator extends AbstractDtObjectValidator<JiraSetting> {

		/** {@inheritDoc} */
		@Override
		protected void checkMonoFieldConstraints(final JiraSetting jiraSetting, final DtField dtField, final DtObjectErrors dtObjectErrors) {
			if (DtDefinitions.JiraSettingFields.url.name().equals(dtField.getName())
					|| DtDefinitions.JiraSettingFields.login.name().equals(dtField.getName())
					|| DtDefinitions.JiraSettingFields.password.name().equals(dtField.getName())
					|| DtDefinitions.JiraSettingFields.project.name().equals(dtField.getName())) {
				final String value = (String) dtField.getDataAccessor().getValue(jiraSetting);
				if (value == null || value.trim().isEmpty()) {
					dtObjectErrors.addError(dtField.getName(), MessageText.of(ExtensionsMultilingualResources.MISSING_FIELD));
				}
			}
			if (DtDefinitions.JiraSettingFields.nodId.name().equals(dtField.getName())) {
				final Long value = (Long) dtField.getDataAccessor().getValue(jiraSetting);
				if (value == null) {
					dtObjectErrors.addError(dtField.getName(), MessageText.of(ExtensionsMultilingualResources.MISSING_FIELD));
				}
			}
		}
	}

}
