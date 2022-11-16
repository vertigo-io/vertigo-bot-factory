package io.vertigo.chatbot.designer.builder.services;

import java.util.Comparator;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.dao.ChatbotFormatDAO;
import io.vertigo.chatbot.commons.domain.ChatbotFormat;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.util.VCollectors;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

/**
 * @author cmarechal
 * @created 19/10/2022 - 09:56
 * @project vertigo-bot-factory
 */

@Transactional
public class ChatbotFormatServices implements Component {

    @Inject
    private ChatbotFormatDAO chatbotFormatDAO;

    public DtList<ChatbotFormat> findAll() {
        return chatbotFormatDAO.findAll(Criterions.alwaysTrue(), DtListState.of(MAX_ELEMENTS_PLUS_ONE))
                .stream().sorted(Comparator.comparing(ChatbotFormat::getSortOrder)).collect(VCollectors.toDtList(ChatbotFormat.class));
    }
}
