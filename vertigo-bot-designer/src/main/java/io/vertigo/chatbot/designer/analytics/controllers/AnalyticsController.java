package io.vertigo.chatbot.designer.analytics.controllers;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.analytics.services.AnalyticsServices;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@RequestMapping("/analytics")
public class AnalyticsController extends AbstractVSpringMvcController {

	private static final ViewContextKey<Chatbot> botsKey = ViewContextKey.of("bots");

	@Inject
	private AnalyticsServices analyticsServices;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext) {
		analyticsServices.test();
		toModeReadOnly();
	}


}