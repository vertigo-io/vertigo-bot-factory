package io.vertigo.chatbot.designer.builder.controllers.bot;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.designer.builder.services.HistoryActionServices;
import io.vertigo.chatbot.designer.builder.services.HistoryServices;
import io.vertigo.chatbot.designer.domain.History;
import io.vertigo.chatbot.designer.domain.HistoryAction;
import io.vertigo.chatbot.designer.domain.HistoryCriteria;
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

import javax.inject.Inject;
import java.time.LocalDate;

import static io.vertigo.chatbot.designer.utils.ListUtils.listLimitReached;

@Controller
@RequestMapping("/bot/{botId}/history")
@Secured("BotUser")
public class HistoryListController extends AbstractBotListEntityController<History> {

	private static final ViewContextKey<History> historyListKey = ViewContextKey.of("historyList");
	private static final ViewContextKey<HistoryCriteria> historyCriteriaKey = ViewContextKey.of("criteria");
	private static final ViewContextKey<HistoryAction> historyActionsKey = ViewContextKey.of("historyActions");

	@Inject
	private HistoryServices historyServices;

	@Inject
	private HistoryActionServices historyActionServices;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
		initCommonContext(viewContext, uiMessageStack, botId);
		final HistoryCriteria criteria = new HistoryCriteria();
		criteria.setToDate(LocalDate.now());
		viewContext.publishDto(historyCriteriaKey, criteria);
		final DtList<History> historyDtList = historyServices.findByCriteria(botId, criteria);
		viewContext.publishDtList(historyListKey, historyDtList);
		viewContext.publishDtList(historyActionsKey, historyActionServices.findAll());
		listLimitReached(viewContext, uiMessageStack);
		super.initBreadCrums(viewContext, History.class);
	}

	@PostMapping("/_filterHistory")
	public ViewContext filterHistory(final ViewContext viewContext, final UiMessageStack uiMessageStack, @ViewAttribute("criteria") final HistoryCriteria criteria) {
		viewContext.publishDtList(historyListKey, historyServices.findByCriteria(getBotId(viewContext), criteria));
		listLimitReached(viewContext, uiMessageStack);
		return viewContext;
	}
}
