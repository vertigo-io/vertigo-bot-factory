package io.vertigo.chatbot.designer.builder.services.bot;

import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.ChatbotCustomConfigDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotCustomConfig;
import io.vertigo.chatbot.commons.domain.FontFamilyEnum;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;

import javax.inject.Inject;

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
            if ((oldChatbotCustomConfig.getDisableNlu() != null && !oldChatbotCustomConfig.getDisableNlu().equals(chatbotCustomConfig.getDisableNlu())) ||
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
                (oldChatbotCustomConfig.getBotMessageBackgroundColor() != null && !oldChatbotCustomConfig.getBotMessageBackgroundColor().equals(chatbotCustomConfig.getBotMessageBackgroundColor())) ||
                (oldChatbotCustomConfig.getBotMessageFontColor() != null && !oldChatbotCustomConfig.getBotMessageFontColor().equals(chatbotCustomConfig.getBotMessageFontColor())) ||
                (oldChatbotCustomConfig.getUserMessageFontColor() != null && !oldChatbotCustomConfig.getUserMessageFontColor().equals(chatbotCustomConfig.getUserMessageFontColor())) ||
                (oldChatbotCustomConfig.getFofCd() != null && !oldChatbotCustomConfig.getFofCd().equals(chatbotCustomConfig.getFofCd())) ||
                (oldChatbotCustomConfig.getFontColor() != null && !oldChatbotCustomConfig.getFontColor().equals(chatbotCustomConfig.getFontColor())) ||
                (oldChatbotCustomConfig.getUserMessageBackgroundColor() != null && !oldChatbotCustomConfig.getUserMessageBackgroundColor().equals(chatbotCustomConfig.getUserMessageBackgroundColor())) ||
                (oldChatbotCustomConfig.getChatbotDisplay() != null && !oldChatbotCustomConfig.getChatbotDisplay().equals(chatbotCustomConfig.getChatbotDisplay())) ||
                (oldChatbotCustomConfig.getQandaDisplay() != null && !oldChatbotCustomConfig.getQandaDisplay().equals(chatbotCustomConfig.getQandaDisplay())) ||
                (oldChatbotCustomConfig.getDocumentaryResourceDisplay() != null && !oldChatbotCustomConfig.getDocumentaryResourceDisplay().equals(chatbotCustomConfig.getDocumentaryResourceDisplay()));
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
        chatbotCustomConfig.setDisableNlu(false);
        chatbotCustomConfig.setBackgroundColor("#000091");
        chatbotCustomConfig.setFontColor("#ffffff");
        chatbotCustomConfig.setBotMessageBackgroundColor("#ffffff");
        chatbotCustomConfig.setBotMessageFontColor("#000000");
        chatbotCustomConfig.setUserMessageBackgroundColor("#E0E0E0");
        chatbotCustomConfig.setUserMessageFontColor("#000000");
        chatbotCustomConfig.setFofCd(FontFamilyEnum.ARIAL.name());
        chatbotCustomConfig.setTotalMaxAttachmentSize(-1L);
        chatbotCustomConfig.setChatbotDisplay(true);
        chatbotCustomConfig.setQandaDisplay(true);
        chatbotCustomConfig.setDocumentaryResourceDisplay(true);
        return chatbotCustomConfig;
    }

    public Long getChatbotMaxSavedTraining(Long botId) {
        ChatbotCustomConfig chatbotCustomConfig = getChatbotCustomConfigByBotId(botId);
        return chatbotCustomConfig.getMaxSavedTraining();
    }

    public void setChatbotMaxSavedTraining(Chatbot bot, Long maxSavedTraining) {
        ChatbotCustomConfig chatbotCustomConfig = getChatbotCustomConfigByBotId(bot.getBotId());
        chatbotCustomConfig.setMaxSavedTraining(maxSavedTraining);
        chatbotCustomConfigDAO.save(chatbotCustomConfig);
    }
}
