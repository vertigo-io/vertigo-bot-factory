package io.vertigo.chatbot.designer.builder.services;

import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.LogsUtils;
import io.vertigo.chatbot.commons.domain.Attachment;
import io.vertigo.chatbot.commons.domain.AttachmentTypeEnum;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.DocumentaryResourceExport;
import io.vertigo.chatbot.commons.multilingual.attachment.AttachmentMultilingualResources;
import io.vertigo.chatbot.designer.builder.services.bot.AttachmentServices;
import io.vertigo.chatbot.designer.commons.services.DesignerFileServices;
import io.vertigo.chatbot.designer.dao.DocumentaryResourceDAO;
import io.vertigo.chatbot.designer.domain.DocumentaryResource;
import io.vertigo.chatbot.designer.domain.DocumentaryResourceContext;
import io.vertigo.chatbot.designer.domain.DocumentaryResourceTypeEnum;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.util.VCollectors;
import io.vertigo.vega.engines.webservice.json.JsonEngine;
import io.vertigo.datastore.filestore.model.FileInfoURI;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
public class DocumentaryResourceServices implements Component {

    @Inject
    private DocumentaryResourceDAO documentaryResourceDAO;

    @Inject
    private DocumentaryResourceContextServices documentaryResourceContextServices;

    @Inject
    private AttachmentServices attachmentServices;

    @Inject
    private JsonEngine jsonEngine;

    public DocumentaryResource getDocResById(final Long dreId) {
        return documentaryResourceDAO.get(dreId);
    }

    public DtList<DocumentaryResource> getAllDocResByBot(final Chatbot bot) {
        return documentaryResourceDAO.findAll(Criterions.isEqualTo(DtDefinitions.DocumentaryResourceFields.botId, bot.getBotId()), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
    }

    @Secured("BotUser")
    public void saveDocumentaryResource(@SecuredOperation("botAdm") final Chatbot bot, final DocumentaryResource documentaryResource, final Attachment attachment, final Optional<FileInfoURI> attachmentFile, final Long maxSize, final Long attachmentTotalSize) {
        DocumentaryResourceTypeEnum documentaryResourceTypeEnum = DocumentaryResourceTypeEnum.valueOf(documentaryResource.getDreTypeCd());

        switch (documentaryResourceTypeEnum) {
            case URL:
                documentaryResource.setAttId(null);
                break;
            case FILE:
                attachment.setBotId(bot.getBotId());
                attachment.setAttTypeCd(AttachmentTypeEnum.DOCUMENT.name());
                Attachment savedAttachment = attachmentServices.save(bot, attachment, attachmentFile, maxSize, attachmentTotalSize);
                documentaryResource.setAttId(savedAttachment.getAttId());
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
        documentaryResourceContextServices.deleteAllDocumentaryResourceContextByDreId(bot, documentaryResource.getDreId());
        documentaryResourceDAO.delete(documentaryResource.getDreId());
        if (documentaryResource.getAttId() != null) {
            attachmentServices.delete(bot, documentaryResource.getAttId());
        }
    }

    public void deleteAllDocumentaryResourceByBot(@SecuredOperation("botAdm") final Chatbot bot){
        getAllDocResByBot(bot).forEach(documentaryResource -> deleteDocumentaryResource(bot, documentaryResource));
    }

    public DocumentaryResource getNewdocumentaryResource(final Long botId) {
        DocumentaryResource documentaryResource = new DocumentaryResource();
        documentaryResource.setBotId(botId);
        return documentaryResource;
    }

    public String exportDocumentaryResourceByBot(@SecuredOperation("botAdm") final Chatbot bot, final StringBuilder logs) {
        LogsUtils.addLogs(logs, "Export documentary resources: ");
        try {
            return jsonEngine.toJson(getAllDocResByBot(bot).stream().map(documentaryResource -> {
                documentaryResource.attachment().load();

                final DocumentaryResourceExport documentaryResourceExport = new DocumentaryResourceExport();
                documentaryResourceExport.setTitle(documentaryResource.getTitle());
                documentaryResourceExport.setDreTypeCd(documentaryResource.getDreTypeCd());
                documentaryResourceExport.setUrl(documentaryResource.getUrl());
                if(documentaryResource.getAttId() != null) {
                    documentaryResourceExport.setAttId(documentaryResource.getAttId());
                    documentaryResourceExport.setFileName(documentaryResource.attachment().get().getLabel());
                }
                return documentaryResourceExport;
            }).collect(VCollectors.toDtList(DocumentaryResourceExport.class)));

        } catch (final Exception e) {
            LogsUtils.logKO(logs);
            LogsUtils.addLogs(logs, e);
            throw new VUserException(AttachmentMultilingualResources.EXPORT_UNEXPECTED_ERROR, e);
        }
  }

}
