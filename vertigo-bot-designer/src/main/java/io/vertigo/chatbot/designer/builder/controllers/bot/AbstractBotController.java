package io.vertigo.chatbot.designer.builder.controllers.bot;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.vertigo.chatbot.authorization.SecuredEntities.ChatbotAuthorizations;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.builder.services.bot.ChatbotServices;
import io.vertigo.chatbot.designer.commons.controllers.AbstractDesignerController;
import io.vertigo.core.locale.LocaleManager;
import io.vertigo.datamodel.structure.model.Entity;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;

public abstract class AbstractBotController<O extends Entity> extends AbstractDesignerController {

	@Inject
	private ChatbotServices chatbotServices;

	@Inject
	private LocaleManager localeManager;

	private static final ViewContextKey<Chatbot> botKey = ViewContextKey.of("bot");
	private static final ViewContextKey<String> localeKey = ViewContextKey.of("locale");
	protected static final ViewContextKey<String> breadCrumsKey = ViewContextKey.of("breadCrums");

	protected Chatbot initCommonContext(final ViewContext viewContext, final Long botId) {
		final Chatbot chatbot = chatbotServices.getChatbotById(botId);
		viewContext.publishDto(botKey, chatbot);
		viewContext.publishRef(localeKey, localeManager.getCurrentLocale().toString());
		addKeyConceptSecurityToContext(chatbot, ChatbotAuthorizations.values());

		return chatbot;
	}

	protected void initBreadCrums(final ViewContext viewContext, final O object) {
		viewContext.publishRef(breadCrumsKey, getBreadCrums(object));
	}

	protected abstract String getBreadCrums(final O object);

	protected void initEmptyCommonContext(final ViewContext viewContext) {
		viewContext.publishDto(botKey, chatbotServices.getNewChatbot());
	}

	@GetMapping("/{botId}/avatar")
	public VFile getAvatar(@PathVariable("botId") final Long botId) {
		return chatbotServices.getAvatar(chatbotServices.getChatbotById(botId));
	}

	@GetMapping("/avatar")
	public VFile getAvatar() {
		return chatbotServices.getNoAvatar();
	}
}
