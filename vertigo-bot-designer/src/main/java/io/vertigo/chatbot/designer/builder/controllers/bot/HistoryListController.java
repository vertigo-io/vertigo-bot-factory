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
import io.vertigo.vega.webservice.validation.UiMessageStack;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;
import java.time.LocalDate;

@Controller
@RequestMapping("/bot/{botId}/history")
@Secured("BotUser")
public class HistoryListController extends AbstractBotListController<History> {


	private static final ViewContextKey<History> historyListKey = ViewContextKey.of("historyList");
	private static final ViewContextKey<History> historyListFilteredKey = ViewContextKey.of("historyListFiltered");
	private static final ViewContextKey<HistoryCriteria> historyCriteriaKey = ViewContextKey.of("criteria");
	private static final ViewContextKey<HistoryAction> historyActionsKey = ViewContextKey.of("historyActions");

	@Inject
	private HistoryServices historyServices;

	@Inject
	private HistoryActionServices historyActionServices;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
		initCommonContext(viewContext, uiMessageStack, botId);
		DtList<History> historyDtList = historyServices.findAllByBotId(botId);
		viewContext.publishDtList(historyListKey, historyDtList);
		viewContext.publishDtList(historyListFilteredKey, historyDtList);
		HistoryCriteria criteria = new HistoryCriteria();
		criteria.setToDate(LocalDate.now());
		viewContext.publishDto(historyCriteriaKey, criteria);
		viewContext.publishDtList(historyActionsKey, historyActionServices.findAll());
		super.initBreadCrums(viewContext, History.class);
	}
}
