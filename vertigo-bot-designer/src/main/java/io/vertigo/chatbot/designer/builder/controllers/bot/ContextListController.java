package io.vertigo.chatbot.designer.builder.controllers.bot;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ContextValue;
import io.vertigo.chatbot.designer.builder.services.bot.ContextValueServices;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.validation.UiMessageStack;

@Controller
@RequestMapping("/bot/{botId}/contextList")
@Secured("botAdm")
public class ContextListController extends AbstractBotListController<ContextValue> {

	private static final ViewContextKey<ContextValue> contextValuesKey = ViewContextKey.of("contextValues");

	@Inject
	private ContextValueServices contextValueServices;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
		final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);
		viewContext.publishDtList(contextValuesKey, contextValueServices.getAllContextValueByBotId(botId));

		super.initBreadCrums(viewContext, ContextValue.class);
	}

	@PostMapping("/createContextValue")
	@Secured("BotAdm")
	public String doCreateContextValue(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot) {
		final Long botId = bot.getBotId();
		return "redirect:/bot/" + botId + "/contextValue/new";
	}

}
