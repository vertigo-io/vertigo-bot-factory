package io.vertigo.chatbot.designer.builder.controllers.bot;

import java.util.Optional;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.vertigo.chatbot.authorization.SecuredEntities.ChatbotAuthorizations;
import io.vertigo.chatbot.authorization.SecuredEntities.ChatbotOperations;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.commons.multilingual.bot.BotMultilingualResources;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.designer.builder.services.bot.ChatbotServices;
import io.vertigo.chatbot.designer.commons.controllers.AbstractDesignerController;
import io.vertigo.chatbot.designer.utils.AuthorizationUtils;
import io.vertigo.core.locale.LocaleManager;
import io.vertigo.core.locale.MessageText;
import io.vertigo.datamodel.structure.model.Entity;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.vega.webservice.validation.UiMessageStack;

public abstract class AbstractBotController<O extends Entity> extends AbstractDesignerController {

	@Inject
	private ChatbotServices chatbotServices;

	@Inject
	private NodeServices nodeServices;

	@Inject
	private LocaleManager localeManager;

	private static final ViewContextKey<Chatbot> botKey = ViewContextKey.of("bot");
	private static final ViewContextKey<String> localeKey = ViewContextKey.of("locale");
	protected static final ViewContextKey<String> breadCrumsKey = ViewContextKey.of("breadCrums");

	protected Chatbot initCommonContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, final Long botId) {
		final Chatbot chatbot = chatbotServices.getChatbotById(botId);
		viewContext.publishDto(botKey, chatbot);
		viewContext.publishRef(localeKey, localeManager.getCurrentLocale().toString());
		addKeyConceptSecurityToContext(chatbot, ChatbotAuthorizations.values());
		this.nodeMessageDisplay(chatbot, uiMessageStack);
		return chatbot;
	}

	protected void initBreadCrums(final ViewContext viewContext, final O object) {
		viewContext.publishRef(breadCrumsKey, getBreadCrums(object));
	}

	protected abstract String getBreadCrums(final O object);

	protected void initEmptyCommonContext(final ViewContext viewContext) {
		viewContext.publishDto(botKey, chatbotServices.getNewChatbot());
		viewContext.publishRef(localeKey, localeManager.getCurrentLocale().toString());
	}

	@GetMapping("/{botId}/avatar")
	public VFile getAvatar(@PathVariable("botId") final Long botId) {
		return chatbotServices.getAvatar(chatbotServices.getChatbotById(botId));
	}

	@GetMapping("/avatar")
	public VFile getAvatar() {
		return chatbotServices.getNoAvatar();
	}

	public void nodeMessageDisplay(final Chatbot chatbot, final UiMessageStack uiMessageStack) {
		if (AuthorizationUtils.isAuthorized(chatbot, ChatbotOperations.botAdm)) {
			final Optional<ChatbotNode> devNode = nodeServices.getDevNodeByBotId(chatbot.getBotId());
			if (devNode.isEmpty() || !devNode.get().getIsUpToDate()) {
				uiMessageStack.info(MessageText.of(BotMultilingualResources.NODE_NOT_UP_TO_DATE).getDisplay());
			}
		}
	}
}
