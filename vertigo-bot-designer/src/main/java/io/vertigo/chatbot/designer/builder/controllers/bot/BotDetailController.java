package io.vertigo.chatbot.designer.builder.controllers.bot;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.builder.services.DesignerServices;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@RequestMapping("/bot")
public class BotDetailController extends AbstractVSpringMvcController {

	@Inject
	private DesignerServices chatbotServices;

	@Inject
	private CommonBotDetailController commonBotDetailController;

	@GetMapping("/{botId}")
	public void initContext(final ViewContext viewContext, @PathVariable("botId") final Long botId) {
		commonBotDetailController.initCommonContext(viewContext, botId);

		toModeReadOnly();
	}

	@GetMapping("/new")
	public void initContext(final ViewContext viewContext) {
		commonBotDetailController.initEmptyCommonContext(viewContext);

		toModeCreate();
	}

	@PostMapping("/_edit")
	public void doEdit() {
		toModeEdit();
	}

	@PostMapping("/_save")
	public String doSave(@ViewAttribute("bot") final Chatbot bot) {
		final Chatbot savedChatbot = chatbotServices.saveChatbot(bot);

		return "redirect:/bot/" + savedChatbot.getBotId();
	}
}