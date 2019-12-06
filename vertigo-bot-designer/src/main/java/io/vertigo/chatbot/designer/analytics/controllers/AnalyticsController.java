package io.vertigo.chatbot.designer.analytics.controllers;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.vertigo.chatbot.designer.analytics.services.AnalyticsServices;
import io.vertigo.chatbot.designer.analytics.services.TimeOption;
import io.vertigo.database.timeseries.TimedDatas;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@RequestMapping("/analytics")
public class AnalyticsController extends AbstractVSpringMvcController {

	private static final ViewContextKey<TimedDatas> sessionStatsKey = ViewContextKey.of("sessionStats");
	private static final ViewContextKey<TimedDatas> requestsStatsKey = ViewContextKey.of("requestsStats");



	@Inject
	private AnalyticsServices analyticsServices;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext) {
		updateGraph(viewContext, TimeOption.DAY);

		toModeReadOnly();
	}

	@PostMapping("/_updateStats")
	public ViewContext doRemoveTraining(final ViewContext viewContext,
			@RequestParam("timeOption") final TimeOption timeOption
			) {

		updateGraph(viewContext, timeOption);

		return viewContext;
	}

	private void updateGraph(final ViewContext viewContext, final TimeOption timeOption) {
		viewContext.publishRef(sessionStatsKey, analyticsServices.getSessionsStats(timeOption));
		viewContext.publishRef(requestsStatsKey, analyticsServices.getRequestStats(timeOption));
	}


}