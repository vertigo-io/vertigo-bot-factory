package io.vertigo.chatbot.designer.builder.services;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.builder.documentaryResourceContext.DocumentaryResourceContextPAO;
import io.vertigo.chatbot.designer.dao.DocumentaryResourceContextDAO;
import io.vertigo.chatbot.designer.domain.DocumentaryResourceContext;
import io.vertigo.chatbot.designer.domain.DocumentaryResourceContextIhm;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.locale.LocaleManager;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

@Transactional
@Secured("BotUser")
public class DocumentaryResourceContextServices implements Component {

    @Inject
    private DocumentaryResourceContextDAO documentaryResourceContextDAO;

    @Inject
    private DocumentaryResourceContextPAO documentaryResourceContextPAO;

    @Inject
    private LocaleManager localeManager;

    public DtList<DocumentaryResourceContextIhm> getAllDocumentaryResourceContextIhmByDreId(@SecuredOperation("botVisitor") final Chatbot bot, final long dreId) {
        return documentaryResourceContextPAO.getAllDocumentaryResourceContextIhmByDreId(dreId, localeManager.getCurrentLocale().toString());
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

    public void deleteAllDocumentaryResourceContextByCvaId(@SecuredOperation("botContributor") final Chatbot bot, final long cvaId) {
        documentaryResourceContextPAO.removeAllDocumentaryResourceContextByCvaId(cvaId);
    }

    public void setAllDocumentaryResourceContextCpvIdToNullByCpvId(@SecuredOperation("botContributor") final Chatbot bot, final long cpvId){
        documentaryResourceContextDAO.findAll(Criterions.isEqualTo(DtDefinitions.DocumentaryResourceContextFields.cpvId, cpvId), DtListState.of(null))
                .forEach(documentaryResourceContext -> {
                    documentaryResourceContext.setCpvId(null);
                    saveDocumentaryResourceContext(bot, documentaryResourceContext);
                });
    }
}
