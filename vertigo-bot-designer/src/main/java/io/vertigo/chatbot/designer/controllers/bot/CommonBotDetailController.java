package io.vertigo.chatbot.designer.controllers.bot;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.services.DesignerServices;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@RequestMapping("/bot/{botId}/")
public class CommonBotDetailController extends AbstractVSpringMvcController {

	private static final ViewContextKey<Chatbot> botKey = ViewContextKey.of("bot");

	@Inject
	private DesignerServices designerServices;

	public void initCommonContext(final ViewContext viewContext, final Long botId) {
		viewContext.publishDto(botKey, designerServices.getChatbotById(botId));
	}


}