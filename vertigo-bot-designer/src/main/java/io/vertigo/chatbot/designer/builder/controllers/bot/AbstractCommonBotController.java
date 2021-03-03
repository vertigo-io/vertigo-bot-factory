package io.vertigo.chatbot.designer.builder.controllers.bot;

import java.util.List;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.vertigo.account.authorization.AuthorizationManager;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.builder.services.DesignerServices;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

public class AbstractCommonBotController extends AbstractVSpringMvcController {

	@Inject
	private DesignerServices designerServices;
	@Inject
	private AuthorizationManager authorizationManager;

	private static final ViewContextKey<String[]> chatBotAuthorizedOperationsKey = ViewContextKey.of("chatBotAuthorizedOperations");
	private static final ViewContextKey<Chatbot> botKey = ViewContextKey.of("bot");

	public Chatbot initCommonContext(final ViewContext viewContext, final Long botId) {
		final Chatbot chatbot = designerServices.getChatbotById(botId);
		viewContext.publishDto(botKey, chatbot);
		final List<String> authorizedOperations = authorizationManager.getAuthorizedOperations(chatbot);
		viewContext.publishRef(chatBotAuthorizedOperationsKey, authorizedOperations.toArray(new String[authorizedOperations.size()]));
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