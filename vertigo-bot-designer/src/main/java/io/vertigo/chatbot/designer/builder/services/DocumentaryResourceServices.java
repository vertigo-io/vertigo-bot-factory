package io.vertigo.chatbot.designer.builder.services;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.dao.DocumentaryResourceDAO;
import io.vertigo.chatbot.designer.domain.DocumentaryResource;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

@Transactional
public class DocumentaryResourceServices implements Component {

    @Inject
    DocumentaryResourceDAO documentaryResourceDAO;

    public DocumentaryResource getDocResById(final Long  dreId) {
        return documentaryResourceDAO.get(dreId);
    }

    public DtList<DocumentaryResource> getAllDocResByBot(final Chatbot bot) {
        return documentaryResourceDAO.findAll(Criterions.isEqualTo(DtDefinitions.DocumentaryResourceFields.botId, bot.getBotId()), DtListState.of(null));
    }
}
