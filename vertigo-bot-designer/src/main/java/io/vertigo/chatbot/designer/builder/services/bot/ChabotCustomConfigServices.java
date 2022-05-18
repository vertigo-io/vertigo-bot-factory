package io.vertigo.chatbot.designer.builder.services.bot;

import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.ChatbotCustomConfigDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotCustomConfig;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.vega.engines.webservice.json.JsonEngine;

import javax.inject.Inject;

@Transactional
public class ChabotCustomConfigServices implements Component {

	@Inject
	private ChatbotCustomConfigDAO chatbotCustomConfigDAO;

	@Inject
	private JsonEngine jsonEngine;

	@Inject
	private NodeServices nodeServices;

	public ChatbotCustomConfig findCustomConfigById(final Long id) {
		return chatbotCustomConfigDAO.get(id);
	}

	public ChatbotCustomConfig save(@SecuredOperation("botAdm") final Chatbot bot, final ChatbotCustomConfig chatbotCustomConfig) {
		if (chatbotCustomConfig.getCccId() != null) {
			final ChatbotCustomConfig oldChatbotCustomConfig = chatbotCustomConfigDAO.get(chatbotCustomConfig.getCccId());
			/*if (oldChatbotCustomConfig.getRating() != chatbotCustomConfig.getRating() ||
					(oldChatbotCustomConfig.getRatingMessage() != null && !oldChatbotCustomConfig.getRatingMessage().equals(chatbotCustomConfig.getRatingMessage()))) {
				nodeServices.updateNodes(bot);
			}*/
		} else {
			nodeServices.updateNodes(bot);
		}
		chatbotCustomConfig.setBotId(bot.getBotId());
		return chatbotCustomConfigDAO.save(chatbotCustomConfig);
	}

	public void deleteChatbotCustomConfig(@SecuredOperation("botAdm") final Chatbot bot) {
		final ChatbotCustomConfig chatbotCustomConfig = getChatbotCustomConfigByBotId(bot.getBotId());
		if (chatbotCustomConfig.getCccId() != null) {
			chatbotCustomConfigDAO.delete(chatbotCustomConfig.getCccId());
		}
	}

	public ChatbotCustomConfig getChatbotCustomConfigByBotId(final Long botId) {
		return chatbotCustomConfigDAO.findOptional(Criterions.isEqualTo(DtDefinitions.ChatbotCustomConfigFields.botId, botId)).orElseGet(() -> {
			final ChatbotCustomConfig chatbotCustomConfig = getDefaultChatbotCustomConfig();
			chatbotCustomConfig.setBotId(botId);
			return chatbotCustomConfig;
		});
	}

	public ChatbotCustomConfig getDefaultChatbotCustomConfig() {
		final ChatbotCustomConfig chatbotCustomConfig = new ChatbotCustomConfig();
		chatbotCustomConfig.setReinitializationButton(false);
		return chatbotCustomConfig;
	}
}
