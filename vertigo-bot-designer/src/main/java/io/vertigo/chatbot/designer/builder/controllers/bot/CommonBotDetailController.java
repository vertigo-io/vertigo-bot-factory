package io.vertigo.chatbot.designer.builder.controllers.bot;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.builder.services.DesignerServices;
import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;

@Controller
@RequestMapping("/bot/")
public class CommonBotDetailController {

	@Inject
	private DesignerServices designerServices;

	private static final ViewContextKey<Chatbot> botKey = ViewContextKey.of("bot");

	public Chatbot initCommonContext(final ViewContext viewContext, final Long botId) {
		final Chatbot chatbot = designerServices.getChatbotById(botId);
		viewContext.publishDto(botKey, chatbot);

		return chatbot;
	}

	public void initEmptyCommonContext(final ViewContext viewContext) {
		viewContext.publishDto(botKey, designerServices.getNewChatbot());
	}

	@GetMapping("/{botId}/avatar")
	public VFile getAvatar(@PathVariable("botId") final Long botId) {
		return designerServices.getAvatar(designerServices.getChatbotById(botId));
	}

	@GetMapping("/avatar")
	public VFile getAvatar() {
		return designerServices.getNoAvatar();
	}

}