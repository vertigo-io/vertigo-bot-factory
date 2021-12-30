package io.vertigo.chatbot.designer.builder.controllers.bot;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.designer.builder.services.HistoryServices;
import io.vertigo.chatbot.designer.domain.History;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.vega.webservice.validation.UiMessageStack;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;

@Controller
@RequestMapping("/bot/{botId}/history")
@Secured("BotUser")
public class HistoryListController extends AbstractBotListController<History> {


	private static final ViewContextKey<History> historyListKey = ViewContextKey.of("historyList");

	@Inject
	private HistoryServices historyServices;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
		initCommonContext(viewContext, uiMessageStack, botId);
		viewContext.publishDtList(historyListKey,historyServices.findAllByBotId(botId));
		super.initBreadCrums(viewContext, History.class);
	}
}
