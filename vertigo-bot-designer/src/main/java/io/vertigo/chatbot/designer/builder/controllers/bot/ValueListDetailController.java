package io.vertigo.chatbot.designer.builder.controllers.bot;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.reflect.TypeToken;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.JiraValueList;
import io.vertigo.chatbot.commons.domain.JiraValueListItem;
import io.vertigo.chatbot.commons.multilingual.extensions.ExtensionsMultilingualResources;
import io.vertigo.chatbot.designer.builder.services.JiraValueListServices;
import io.vertigo.chatbot.designer.utils.AbstractChatbotDtObjectValidator;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.core.locale.LocaleMessageText;
import io.vertigo.datamodel.data.definitions.DataField;
import io.vertigo.datamodel.data.definitions.DataFieldName;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.engines.webservice.json.JsonEngine;
import io.vertigo.vega.webservice.stereotype.Validate;
import io.vertigo.vega.webservice.validation.DtObjectErrors;
import io.vertigo.vega.webservice.validation.UiMessageStack;

@Controller
@RequestMapping("/bot/{botId}/valueList")
@Secured("BotUser")
public class ValueListDetailController extends AbstractBotCreationController<JiraValueList> {

	private static final ViewContextKey<JiraValueList> jiraValueListKey = ViewContextKey.of("jiraValueList");
	private static final ViewContextKey<JiraValueListItem> jiraValueListItemsKey = ViewContextKey.of("jiraValueListItems");
	private static final ViewContextKey<JiraValueListItem> newJiraValueListItemKey = ViewContextKey.of("newJiraValueListItem");

	@Inject
	private JiraValueListServices jiraValueListServices;

	@Inject
	private JsonEngine jsonEngine;

	@GetMapping("/{jirValListId}")
	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId,
			@PathVariable("jirValListId") final Long jirValListId) {
		final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);
		final JiraValueList valueList = jiraValueListServices.findOptionalByIdAndBot(jirValListId, bot)
				.orElseThrow(() -> new SecurityException("Access denied: value list does not belong to this bot"));

		viewContext.publishDto(jiraValueListKey, valueList);
		viewContext.publishDtList(jiraValueListItemsKey, jiraValueListServices.findItems(valueList));
		viewContext.publishDto(newJiraValueListItemKey, new JiraValueListItem());

		super.initBreadCrums(viewContext, valueList);
		toModeReadOnly();
	}

	@GetMapping("/new")
	public void getNewValueList(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
		final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);
		final JiraValueList valueList = new JiraValueList();
		valueList.setBotId(bot.getBotId());

		viewContext.publishDto(jiraValueListKey, valueList);
		viewContext.publishDtList(jiraValueListItemsKey, new DtList<>(JiraValueListItem.class));
		viewContext.publishDto(newJiraValueListItemKey, new JiraValueListItem());

		super.initEmptyBreadcrums(viewContext);
		toModeCreate();
	}

	@PostMapping("/_edit")
	public void doEdit() {
		toModeEdit();
	}

	@PostMapping("/_save")
	public ViewContext saveValueList(final ViewContext viewContext,
			final UiMessageStack uiMessageStack,
			@ViewAttribute("bot") final Chatbot bot,
			@ViewAttribute("jiraValueList") @Validate(JiraValueListValidator.class) final JiraValueList jiraValueList,
			@RequestParam("items") final String itemsJson) {
		final List<JiraValueListItem> items = jsonEngine.fromJson(itemsJson, new TypeToken<List<JiraValueListItem>>() {
		}.getType(), Set.of(), Set.of());
		viewContext.publishDto(jiraValueListKey, jiraValueListServices.save(bot, jiraValueList, items));
		return viewContext;
	}

	@PostMapping("/_saveValueListItem")
	public ViewContext saveValueListItem(final ViewContext viewContext,
			@ViewAttribute("bot") final Chatbot bot,
			@ViewAttribute("jiraValueList") final JiraValueList jiraValueList,
			@ViewAttribute("newJiraValueListItem") @Validate(JiraValueListItemValidator.class) final JiraValueListItem jiraValueListItem) {
		jiraValueListServices.saveItem(bot, jiraValueList.getJirValListId(), jiraValueListItem);
		viewContext.publishDtList(jiraValueListItemsKey, jiraValueListServices.findItems(jiraValueList));
		return viewContext;
	}

	@PostMapping("/_deleteValueListItem")
	public ViewContext deleteValueListItem(final ViewContext viewContext,
			@ViewAttribute("bot") final Chatbot bot,
			@ViewAttribute("jiraValueList") final JiraValueList jiraValueList,
			@RequestParam("jirValItemId") final Long jirValItemId) {
		jiraValueListServices.deleteItem(bot, jiraValueList.getJirValListId(), jirValItemId);
		viewContext.publishDtList(jiraValueListItemsKey, jiraValueListServices.findItems(jiraValueList));
		return viewContext;
	}

	@PostMapping("/_delete")
	public String deleteValueList(final ViewContext viewContext,
			@ViewAttribute("bot") final Chatbot bot,
			@ViewAttribute("jiraValueList") final JiraValueList jiraValueList) {
		jiraValueListServices.delete(bot, jiraValueList.getJirValListId());
		return "redirect:/bot/" + bot.getBotId() + "/valueLists/";
	}

	@Override
	protected String getBreadCrums(final JiraValueList object) {
		return object.getLabel();
	}

	public static final class JiraValueListItemValidator extends AbstractChatbotDtObjectValidator<JiraValueListItem> {

		@Override
		protected List<DataFieldName<JiraValueListItem>> getFieldsToNullCheck() {
			return List.of(DtDefinitions.JiraValueListItemFields.code,
					DtDefinitions.JiraValueListItemFields.label);
		}
	}

	public static final class JiraValueListValidator extends AbstractChatbotDtObjectValidator<JiraValueList> {

		@Override
		protected void checkMonoFieldConstraints(final JiraValueList valueList, final DataField dtField, final DtObjectErrors dtObjectErrors) {
			super.checkMonoFieldConstraints(valueList, dtField, dtObjectErrors);
			final String fieldName = dtField.name();
			if (DtDefinitions.JiraValueListFields.code.name().equals(fieldName)
					|| DtDefinitions.JiraValueListFields.label.name().equals(fieldName)) {
				final String value = (String) dtField.getDataAccessor().getValue(valueList);
				if (value == null || value.trim().isEmpty()) {
					dtObjectErrors.addError(fieldName, LocaleMessageText.of(ExtensionsMultilingualResources.MISSING_FIELD));
				}
			}
		}
	}
}
