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
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.vega.engines.webservice.json.JsonEngine;

import javax.inject.Inject;
import java.util.Map;

@Transactional
public class ChabotCustomConfigServices implements Component {

    @Inject
    private ChatbotCustomConfigDAO chatbotCustomConfigDAO;

    @Inject
    private JsonEngine jsonEngine;

    @Inject
    private NodeServices nodeServices;

    public static final String RATING_KEY = "rating";
    public static final String RATING_MESSAGE = "ratingMessage";

    public ChatbotCustomConfig findCustomConfigById(final Long id) {
        return chatbotCustomConfigDAO.get(id);
    }

    public ChatbotCustomConfig save(@SecuredOperation("botAdm") final Chatbot bot, ChatbotCustomConfig chatbotCustomConfig) {
        if (chatbotCustomConfig.getCccId() != null) {
            ChatbotCustomConfig oldChatbotCustomConfig = chatbotCustomConfigDAO.get(chatbotCustomConfig.getCccId());
            if (!oldChatbotCustomConfig.getValue().equals(chatbotCustomConfig.getValue())) {
                nodeServices.updateNodes(bot);
            }
        } else {
            nodeServices.updateNodes(bot);
        }
        return chatbotCustomConfigDAO.save(chatbotCustomConfig);
    }

    public void deleteChatbotCustomConfig(@SecuredOperation("botAdm") final Chatbot bot, final Long id) {
        nodeServices.updateNodes(bot);
        chatbotCustomConfigDAO.delete(id);
    }

    public ChatbotCustomConfig getChatbotCustomConfigByBotId(final Long botId) {
        return chatbotCustomConfigDAO.findOptional(Criterions.isEqualTo(DtDefinitions.ChatbotCustomConfigFields.botId, botId)).orElse(null);
    }

    public Map<String, Object> getChatbotCustomConfigMapByBotId(ChatbotCustomConfig chatbotCustomConfig) {
        return jsonEngine.fromJson(chatbotCustomConfig.getValue(), Map.class);
    }

    public String getChatbotCustomConfigJsonString(Map<String, Object> chatbotCustomConfigMap) {
        return jsonEngine.toJson(chatbotCustomConfigMap);
    }
}
