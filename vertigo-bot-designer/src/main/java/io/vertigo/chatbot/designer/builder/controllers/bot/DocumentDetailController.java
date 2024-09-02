package io.vertigo.chatbot.designer.builder.controllers.bot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Attachment;
import io.vertigo.chatbot.commons.domain.AttachmentTypeEnum;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotCustomConfig;
import io.vertigo.chatbot.designer.builder.services.DocumentaryResourceServices;
import io.vertigo.chatbot.designer.builder.services.DocumentaryResourceTypeServices;
import io.vertigo.chatbot.designer.builder.services.bot.AttachmentServices;
import io.vertigo.chatbot.designer.builder.services.bot.ChatbotCustomConfigServices;
import io.vertigo.chatbot.designer.domain.DocumentaryResource;
import io.vertigo.chatbot.designer.domain.DocumentaryResourceType;
import io.vertigo.chatbot.designer.domain.DocumentaryResourceTypeEnum;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.validation.UiMessageStack;

import static io.vertigo.chatbot.designer.utils.ListUtils.listLimitReached;

@Controller
@RequestMapping("/bot/{botId}/document")
@Secured("Chatbot$botContributor")
public class DocumentDetailController extends AbstractBotCreationController<DocumentaryResource> {

    private static final ViewContextKey<DocumentaryResource> documentaryResourceKey = ViewContextKey.of("documentaryResource");
    private static final ViewContextKey<DocumentaryResourceType> documentaryResourceTypeListKey = ViewContextKey.of("documentaryResourceTypeList");
    private static final ViewContextKey<Attachment> attachmentKey = ViewContextKey.of("attachment");
    private static final ViewContextKey<Long> maxSizeKey = ViewContextKey.of("maxSize");
    private static final ViewContextKey<Long> attachmentTotalSizeKey = ViewContextKey.of("attachmentTotalSize");
    private static final ViewContextKey<FileInfoURI> importAttachmentFileUri = ViewContextKey.of("importAttachmentFileUri");

    @Inject
    DocumentaryResourceServices documentaryResourceServices;

    @Inject
    DocumentaryResourceTypeServices documentaryResourceTypeServices;

    @Inject
    AttachmentServices attachmentServices;

    @Inject
    ChatbotCustomConfigServices chatbotCustomConfigServices;

    @GetMapping("/{dreId}")
    public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId, @PathVariable("dreId") final Long dreId) {
        final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);
        final ChatbotCustomConfig chatbotCustomConfig = chatbotCustomConfigServices.getChatbotCustomConfigByBotId(botId);
        final DocumentaryResource documentaryResource = documentaryResourceServices.getDocResById(dreId);
        final Attachment attachment = documentaryResource.getAttId() != null? attachmentServices.findById(documentaryResource.getAttId()) : new Attachment();

        viewContext.publishDto(documentaryResourceKey, documentaryResource);
        viewContext.publishDto(attachmentKey, attachment);
        viewContext.publishDtList(documentaryResourceTypeListKey, documentaryResourceTypeServices.getAllDocResTypes());
        viewContext.publishRef(maxSizeKey, chatbotCustomConfig.getTotalMaxAttachmentSize() != null ?
                chatbotCustomConfig.getTotalMaxAttachmentSize() : -1L);
        viewContext.publishRef(attachmentTotalSizeKey, computeAttachmentTotalSize(attachmentServices.findAllByBotId(botId)));
        viewContext.publishFileInfoURI(importAttachmentFileUri, null);

        super.initBreadCrums(viewContext, documentaryResource);
        listLimitReached(viewContext, uiMessageStack);
        toModeReadOnly();
    }

    @GetMapping("/new")
    public void getNewDocumentaryResource(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
        final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);
        final ChatbotCustomConfig chatbotCustomConfig = chatbotCustomConfigServices.getChatbotCustomConfigByBotId(botId);

        viewContext.publishDto(documentaryResourceKey, documentaryResourceServices.getNewdocumentaryResource(botId));
        viewContext.publishDto(attachmentKey, new Attachment());
        viewContext.publishDtList(documentaryResourceTypeListKey, documentaryResourceTypeServices.getAllDocResTypes());
        viewContext.publishRef(maxSizeKey, chatbotCustomConfig.getTotalMaxAttachmentSize() != null ?
                chatbotCustomConfig.getTotalMaxAttachmentSize() : -1L);
        viewContext.publishRef(attachmentTotalSizeKey, computeAttachmentTotalSize(attachmentServices.findAllByBotId(botId)));
        viewContext.publishFileInfoURI(importAttachmentFileUri, null);

        super.initEmptyBreadcrums(viewContext);
        listLimitReached(viewContext, uiMessageStack);
        toModeCreate();
    }


    @PostMapping("/_save")
    public String saveDocumentaryResource(final ViewContext viewContext,
                                          @ViewAttribute("bot") final Chatbot bot,
                                          @ViewAttribute("documentaryResource") final DocumentaryResource documentaryResource,
                                          @ViewAttribute("maxSize") final Long maxSize,
                                          @ViewAttribute("attachmentTotalSize") final Long attachmentTotalSize,
                                          @ViewAttribute("importAttachmentFileUri") final Optional<FileInfoURI> attachmentFile,
                                          @ViewAttribute("attachment") final Attachment attachment) {


        if(documentaryResource.getDreTypeCd().equals(DocumentaryResourceTypeEnum.TEXT.name())){
            documentaryResource.setUrl(null);
            documentaryResource.setAttId(null);
        }
        if(documentaryResource.getDreTypeCd().equals(DocumentaryResourceTypeEnum.URL.name())){
            documentaryResource.setText(null);
            documentaryResource.setAttId(null);
        }
        if(documentaryResource.getDreTypeCd().equals(DocumentaryResourceTypeEnum.FILE.name())) {
            attachment.setBotId(bot.getBotId());
            attachment.setAttTypeCd(AttachmentTypeEnum.DOCUMENT.name());
            Attachment savedAttachment = attachmentServices.save(bot, attachment, attachmentFile, maxSize, attachmentTotalSize);
            documentaryResource.setAttId(savedAttachment.getAttId());
            documentaryResource.setText(null);
            documentaryResource.setUrl(null);
        }
        documentaryResourceServices.saveDocumentaryResource(bot,documentaryResource);

        if (attachment.getAttId() != null && !documentaryResource.getDreTypeCd().equals(DocumentaryResourceTypeEnum.FILE.name())) {
            attachmentServices.delete(bot, attachment.getAttId());
        }
        return "redirect:/bot/" + bot.getBotId() + "/document/" + documentaryResource.getDreId();
    }

    @PostMapping("/_delete")
    public String deleteDocumentaryResource(final ViewContext viewContext,
                                            @ViewAttribute("bot") Chatbot bot,
                                            @ViewAttribute("documentaryResource") DocumentaryResource documentaryResource) {
        documentaryResourceServices.deleteDocumentaryResource(bot, documentaryResource);
        return "redirect:/bot/" + bot.getBotId() + "/documents/";
    }

    @PostMapping("/_edit")
    public void doEdit() {
        toModeEdit();
    }


    private Long computeAttachmentTotalSize(final DtList<Attachment> attachments) {
        return attachments.stream().mapToLong(Attachment::getLength).sum();
    }

    @Override
    protected String getBreadCrums(final DocumentaryResource object) {
        return object.getTitle();
    }
}
