package io.vertigo.chatbot.designer.builder.services;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.builder.documentaryResourceContext.DocumentaryResourceContextPAO;
import io.vertigo.chatbot.designer.dao.DocumentaryResourceContextDAO;
import io.vertigo.chatbot.designer.domain.DocumentaryResourceContext;
import io.vertigo.chatbot.designer.domain.DocumentaryResourceContextIhm;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtList;

@Transactional
@Secured("BotUser")
public class DocumentaryResourceContextServices implements Component {

    @Inject
    private DocumentaryResourceContextDAO documentaryResourceContextDAO;

    @Inject
    private DocumentaryResourceContextPAO documentaryResourceContextPAO;

    public DtList<DocumentaryResourceContextIhm> getAllDocumentaryResourceContextIhmByDreId(@SecuredOperation("botVisitor") final Chatbot bot, final long dreId) {
        return documentaryResourceContextPAO.getAllDocumentaryResourceContextIhmByDreId(dreId);
    }

    public void saveDocumentaryResourceContext(@SecuredOperation("botContributor") final Chatbot bot, final DocumentaryResourceContext documentaryResourceContext) {
        documentaryResourceContextDAO.save(documentaryResourceContext);
    }

    public void deleteDocumentaryResourceContextById(@SecuredOperation("botContributor") final Chatbot bot, final long drcId) {
        documentaryResourceContextDAO.delete(drcId);
    }

    public void deleteAllDocumentaryResourceContextByDreId(@SecuredOperation("botContributor") final Chatbot bot, final long dreId) {
        documentaryResourceContextPAO.removeAllDocumentaryResourceContextByDreId(dreId);
    }
}
