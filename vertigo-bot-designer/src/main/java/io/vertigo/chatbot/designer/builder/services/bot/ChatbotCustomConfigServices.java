package io.vertigo.chatbot.designer.builder.services.bot;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.ChatbotCustomConfigDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotCustomConfig;
import io.vertigo.chatbot.commons.domain.ChatbotFormatEnum;
import io.vertigo.chatbot.commons.domain.FontFamilyEnum;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;

@Transactional
public class ChatbotCustomConfigServices implements Component {

	@Inject
	private ChatbotCustomConfigDAO chatbotCustomConfigDAO;

	@Inject
	private NodeServices nodeServices;

	public ChatbotCustomConfig findCustomConfigById(final Long id) {
		return chatbotCustomConfigDAO.get(id);
	}

	public ChatbotCustomConfig save(@SecuredOperation("botAdm") final Chatbot bot, final ChatbotCustomConfig chatbotCustomConfig) {
		if (chatbotCustomConfig.getCccId() != null) {
			final ChatbotCustomConfig oldChatbotCustomConfig = chatbotCustomConfigDAO.get(chatbotCustomConfig.getCccId());
			if ((oldChatbotCustomConfig.getCftCd() != null && !oldChatbotCustomConfig.getCftCd().equals(chatbotCustomConfig.getCftCd())) ||
					(oldChatbotCustomConfig.getReinitializationButton() != null && !oldChatbotCustomConfig.getReinitializationButton().equals(chatbotCustomConfig.getReinitializationButton())) ||
					checkIfChatbotStyleChanged(oldChatbotCustomConfig, chatbotCustomConfig) ||
					checkIfEmailOrAttachmentSettingsChanged(oldChatbotCustomConfig, chatbotCustomConfig)) {
				nodeServices.updateNodes(bot);
			}
		} else {
			nodeServices.updateNodes(bot);
		}
		chatbotCustomConfig.setBotId(bot.getBotId());
		return chatbotCustomConfigDAO.save(chatbotCustomConfig);
	}

	private static boolean checkIfChatbotStyleChanged(final ChatbotCustomConfig oldChatbotCustomConfig, final ChatbotCustomConfig chatbotCustomConfig) {
		return (oldChatbotCustomConfig.getDisplayAvatar() != null && !oldChatbotCustomConfig.getDisplayAvatar().equals(chatbotCustomConfig.getDisplayAvatar())) ||
		(oldChatbotCustomConfig.getBackgroundColor() != null && !oldChatbotCustomConfig.getBackgroundColor().equals(chatbotCustomConfig.getBackgroundColor())) ||
		(oldChatbotCustomConfig.getFontColor() != null && !oldChatbotCustomConfig.getFontColor().equals(chatbotCustomConfig.getFontColor())) ||
		(oldChatbotCustomConfig.getFofCd()!= null && !oldChatbotCustomConfig.getFofCd().equals(chatbotCustomConfig.getFofCd()));
	}

	private static boolean checkIfEmailOrAttachmentSettingsChanged(final ChatbotCustomConfig oldChatbotCustomConfig, final ChatbotCustomConfig chatbotCustomConfig) {
		return (oldChatbotCustomConfig.getBotEmailAddress() != null && !oldChatbotCustomConfig.getBotEmailAddress().equals(chatbotCustomConfig.getBotEmailAddress())) ||
		(oldChatbotCustomConfig.getTotalMaxAttachmentSize() != null && !oldChatbotCustomConfig.getTotalMaxAttachmentSize().equals(chatbotCustomConfig.getTotalMaxAttachmentSize()));
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
		chatbotCustomConfig.setReinitializationButton(true);
		chatbotCustomConfig.setDisplayAvatar(true);
		chatbotCustomConfig.setCftCd(ChatbotFormatEnum.CLASSIC.name());
		chatbotCustomConfig.setBackgroundColor("#cdcdcd");
		chatbotCustomConfig.setFontColor("#000000");
		chatbotCustomConfig.setFofCd(FontFamilyEnum.ARIAL.name());
		chatbotCustomConfig.setTotalMaxAttachmentSize(-1L);
		return chatbotCustomConfig;
	}
}
