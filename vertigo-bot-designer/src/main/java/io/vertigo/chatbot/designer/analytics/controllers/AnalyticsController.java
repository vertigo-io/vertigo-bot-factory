package io.vertigo.chatbot.designer.analytics.controllers;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.analytics.services.AnalyticsServices;
import io.vertigo.chatbot.designer.analytics.services.TimeOption;
import io.vertigo.chatbot.designer.builder.services.DesignerServices;
import io.vertigo.chatbot.designer.domain.StatCriteria;
import io.vertigo.database.timeseries.TimedDatas;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@RequestMapping("/analytics")
public class AnalyticsController extends AbstractVSpringMvcController {

	private static final ViewContextKey<TimedDatas> sessionStatsKey = ViewContextKey.of("sessionStats");
	private static final ViewContextKey<TimedDatas> requestsStatsKey = ViewContextKey.of("requestsStats");

	private static final ViewContextKey<Chatbot> botsKey = ViewContextKey.of("bots");

	private static final ViewContextKey<StatCriteria> criteriaKey = ViewContextKey.of("criteria");

	@Inject
	private AnalyticsServices analyticsServices;

	@Inject
	private DesignerServices chatbotServices;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext) {
		viewContext.publishDtList(botsKey, chatbotServices.getAllChatbots());

		final StatCriteria statCriteria = new StatCriteria();
		statCriteria.setTimeOption(TimeOption.DAY.name());
		viewContext.publishDto(criteriaKey, statCriteria);

		updateGraph(viewContext, statCriteria);

		toModeReadOnly();
	}

	@PostMapping("/_updateStats")
	public ViewContext doUpdateStats(final ViewContext viewContext,
			@ViewAttribute("criteria") final StatCriteria criteria
			) {

		updateGraph(viewContext, criteria);

		return viewContext;
	}


	private void updateGraph(final ViewContext viewContext, final StatCriteria criteria) {
		viewContext.publishRef(sessionStatsKey, analyticsServices.getSessionsStats(criteria));
		viewContext.publishRef(requestsStatsKey, analyticsServices.getRequestStats(criteria));
	}


}