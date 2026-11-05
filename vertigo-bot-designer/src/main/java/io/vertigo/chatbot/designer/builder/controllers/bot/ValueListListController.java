package io.vertigo.chatbot.designer.builder.controllers.bot;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.JiraValueList;
import io.vertigo.chatbot.commons.domain.JiraValueListItem;
import io.vertigo.chatbot.designer.builder.services.JiraValueListServices;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.vega.webservice.validation.UiMessageStack;

import static io.vertigo.chatbot.designer.utils.ListUtils.listLimitReached;

@Controller
@RequestMapping("/bot/{botId}/valueLists")
@Secured("BotUser")
public class ValueListListController extends AbstractBotListEntityController<JiraValueList> {

	private static final ViewContextKey<JiraValueList> jiraValueListsKey = ViewContextKey.of("jiraValueLists");
	private static final ViewContextKey<JiraValueListItem> jiraValueListItemsKey = ViewContextKey.of("jiraValueListItems");

	@Inject
	private JiraValueListServices jiraValueListServices;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
		final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);
		viewContext.publishDtList(jiraValueListsKey, jiraValueListServices.findAllByBotId(bot));
		viewContext.publishDtList(jiraValueListItemsKey, jiraValueListServices.findAllItemsByBot(bot));

		super.initBreadCrums(viewContext, JiraValueList.class);
		listLimitReached(viewContext, uiMessageStack);
	}
}
