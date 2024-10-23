package io.vertigo.chatbot.designer.builder.controllers.bot;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ContextPossibleValue;
import io.vertigo.chatbot.commons.domain.ContextValue;
import io.vertigo.chatbot.commons.domain.topic.TypeTopic;
import io.vertigo.chatbot.designer.builder.services.bot.ContextEnvironmentServices;
import io.vertigo.chatbot.designer.builder.services.bot.ContextEnvironmentValueServices;
import io.vertigo.chatbot.designer.builder.services.bot.ContextPossibleValueServices;
import io.vertigo.chatbot.designer.builder.services.bot.ContextTypeOperatorServices;
import io.vertigo.chatbot.designer.builder.services.bot.ContextValueServices;
import io.vertigo.chatbot.designer.domain.ContextEnvironment;
import io.vertigo.chatbot.designer.domain.ContextEnvironmentIhm;
import io.vertigo.chatbot.designer.domain.ContextEnvironmentValue;
import io.vertigo.chatbot.designer.domain.ContextEnvironmentValueIhm;
import io.vertigo.chatbot.designer.domain.TypeOperator;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.validation.UiMessageStack;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Optional;

import javax.inject.Inject;

import static io.vertigo.chatbot.designer.utils.ListUtils.listLimitReached;

@Controller
@RequestMapping("/bot/{botId}/contextList")
@Secured("Chatbot$botAdm")
public class ContextListController extends AbstractBotListEntityController<ContextValue> {

	private static final ViewContextKey<ContextValue> contextValuesKey = ViewContextKey.of("contextValues");
	private static final ViewContextKey<ContextEnvironmentIhm> contextEnvironmentsKey = ViewContextKey.of("contextEnvironments");
	private	static final ViewContextKey<ContextPossibleValue> contextPossibleValueskey = ViewContextKey.of("contextPossibleValues");
	private static final ViewContextKey<ContextEnvironment> newContextEnvironmentKey = ViewContextKey.of("newContextEnvironment");
	private static final ViewContextKey<ContextEnvironmentValue> newContextEnvironmentValueKey = ViewContextKey.of("newContextEnvironmentValue");
	private static final ViewContextKey<TypeOperator> typeOperators = ViewContextKey.of("typeOperators");

	@Inject
	private ContextValueServices contextValueServices;

	@Inject
	private ContextPossibleValueServices contextPossibleValueServices;

	@Inject
	private ContextTypeOperatorServices contextTypeOperatorServices;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
		final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);
		viewContext.publishDtList(contextValuesKey, contextValueServices.getAllContextValueByBotId(botId));
		viewContext.publishDtList(contextPossibleValueskey, contextPossibleValueServices.getAllContextPossibleValuesByBot(bot));
		viewContext.publishDto(newContextEnvironmentKey, new ContextEnvironment());
		viewContext.publishDto(newContextEnvironmentValueKey, new ContextEnvironmentValue());
		viewContext.publishDtList(typeOperators, contextTypeOperatorServices.getAllTypeOperators(bot));

		super.initBreadCrums(viewContext, ContextValue.class);
		listLimitReached(viewContext, uiMessageStack);
	}

	@PostMapping("/createContextValue")
	@Secured("Chatbot$botAdm")
	public String doCreateContextValue(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot) {
		final Long botId = bot.getBotId();
		return "redirect:/bot/" + botId + "/contextValue/new";
	}

	@PostMapping("/_saveContextEnvironment")
	public ViewContext saveContextEnvironment(final ViewContext viewContext,
										  final UiMessageStack uiMessageStack,
										  @ViewAttribute("bot") final Chatbot bot,
										  @RequestParam("cenvIdOpt") final Optional<Long> cenvIdOpt,
										  @RequestParam("label") final String label) {
		boolean creation = cenvIdOpt.isEmpty();
		final ContextEnvironment contextEnvironment = creation ? new ContextEnvironment() : contextEnvironmentServices.findById(cenvIdOpt.get());
		contextEnvironment.setLabel(label);
		contextEnvironment.setBotId(bot.getBotId());
		contextEnvironment.setLabel(label);
		contextEnvironmentServices.save(bot, contextEnvironment, creation);
		viewContext.publishDtList(contextEnvironmentsKey, contextEnvironmentServices.getContextEnvironmentIhmByBot(bot.getBotId()));
		return viewContext;
	}

	@PostMapping("/_deleteContextEnvironment")
	public ViewContext deleteContextValue(final ViewContext viewContext,
									 final UiMessageStack uiMessageStack,
									 @ViewAttribute("bot") final Chatbot bot,
									 @RequestParam("cenvId") final Long cenvId) {
		contextEnvironmentServices.deleteContextEnvironment(bot, cenvId);
		viewContext.publishDtList(contextEnvironmentsKey, contextEnvironmentServices.getContextEnvironmentIhmByBot(bot.getBotId()));
		return viewContext;
	}

	@PostMapping("/_saveContextEnvironmentValue")
	public ViewContext saveContextEnvironmentValue(final ViewContext viewContext,
											  final UiMessageStack uiMessageStack,
											  @ViewAttribute("bot") final Chatbot bot,
											  @ViewAttribute("newContextEnvironmentValue") final ContextEnvironmentValue contextEnvironmentValue) {

		contextEnvironmentServices.saveContextEnvironmentValue(bot, contextEnvironmentValue);
		viewContext.publishDtList(contextEnvironmentsKey, contextEnvironmentServices.getContextEnvironmentIhmByBot(bot.getBotId()));
		return viewContext;
	}

}
