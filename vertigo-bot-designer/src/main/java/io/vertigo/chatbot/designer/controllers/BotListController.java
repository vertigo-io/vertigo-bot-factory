package io.vertigo.chatbot.designer.controllers;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.services.DesignerServices;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@RequestMapping("/bots")
public class BotListController extends AbstractVSpringMvcController {

	private static final ViewContextKey<Chatbot> botsKey = ViewContextKey.of("bots");

	@Inject
	private DesignerServices chatbotServices;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext) {
		viewContext.publishDtList(botsKey, chatbotServices.getAllChatbots());
		toModeReadOnly();
	}


}