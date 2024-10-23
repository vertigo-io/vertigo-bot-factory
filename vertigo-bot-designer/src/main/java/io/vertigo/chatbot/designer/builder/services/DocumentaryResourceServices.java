package io.vertigo.chatbot.designer.builder.services;

import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.domain.Attachment;
import io.vertigo.chatbot.commons.domain.AttachmentTypeEnum;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.builder.services.bot.AttachmentServices;
import io.vertigo.chatbot.designer.dao.DocumentaryResourceDAO;
import io.vertigo.chatbot.designer.domain.DocumentaryResource;
import io.vertigo.chatbot.designer.domain.DocumentaryResourceTypeEnum;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datastore.filestore.model.FileInfoURI;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
public class DocumentaryResourceServices implements Component {

    @Inject
    DocumentaryResourceDAO documentaryResourceDAO;

    @Inject
    AttachmentServices attachmentServices;

    public DocumentaryResource getDocResById(final Long  dreId) {
        return documentaryResourceDAO.get(dreId);
    }

    public DtList<DocumentaryResource> getAllDocResByBot(final Chatbot bot) {
        return documentaryResourceDAO.findAll(Criterions.isEqualTo(DtDefinitions.DocumentaryResourceFields.botId, bot.getBotId()), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
    }

    @Secured("BotUser")
    public void saveDocumentaryResource(@SecuredOperation("botAdm") final Chatbot bot, final DocumentaryResource documentaryResource, final Attachment attachment, final Optional<FileInfoURI> attachmentFile, final Long maxSize, final Long attachmentTotalSize) {
        DocumentaryResourceTypeEnum documentaryResourceTypeEnum = DocumentaryResourceTypeEnum.valueOf(documentaryResource.getDreTypeCd());

        switch (documentaryResourceTypeEnum) {
            case TEXT:
                documentaryResource.setUrl(null);
                documentaryResource.setAttId(null);
                break;
            case URL:
                documentaryResource.setText(null);
                documentaryResource.setAttId(null);
                break;
            case FILE:
                attachment.setBotId(bot.getBotId());
                attachment.setAttTypeCd(AttachmentTypeEnum.DOCUMENT.name());
                Attachment savedAttachment = attachmentServices.save(bot, attachment, attachmentFile, maxSize, attachmentTotalSize);
                documentaryResource.setAttId(savedAttachment.getAttId());
                documentaryResource.setText(null);
                documentaryResource.setUrl(null);
                break;
        }
        documentaryResourceDAO.save(documentaryResource);

        if (attachment.getAttId() != null && !documentaryResource.getDreTypeCd().equals(DocumentaryResourceTypeEnum.FILE.name())) {
            attachmentServices.delete(bot, attachment.getAttId());
        }
    }

    @Secured("BotUser")
    public void deleteDocumentaryResource(@SecuredOperation("botAdm") final Chatbot bot, final DocumentaryResource documentaryResource) {
        documentaryResourceDAO.delete(documentaryResource.getDreId());
        if(documentaryResource.getAttId() != null){
            attachmentServices.delete(bot, documentaryResource.getAttId());
        }
    }

    public DocumentaryResource getNewdocumentaryResource(final Long botId) {
        DocumentaryResource documentaryResource = new DocumentaryResource();
        documentaryResource.setBotId(botId);
        return documentaryResource;
    }
}
