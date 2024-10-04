package io.vertigo.chatbot.designer.builder.services;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.dao.DocumentaryResourceContextDAO;
import io.vertigo.chatbot.designer.domain.DocumentaryResourceContext;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
@Secured("BotUser")
public class DocumentaryResourceContextServices implements Component {

    @Inject
    private DocumentaryResourceContextDAO documentaryResourceContextDAO;

    public DtList<DocumentaryResourceContext> getAllDocumentaryResourceContextBydDreId(@SecuredOperation("botVisitor") final Chatbot bot, final long dreId) {
        return documentaryResourceContextDAO.findAll(Criterions.isEqualTo(DtDefinitions.DocumentaryResourceContextFields.dreId, dreId), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
    }
}
