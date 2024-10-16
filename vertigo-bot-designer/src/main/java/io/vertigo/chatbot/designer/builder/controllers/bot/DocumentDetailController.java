package io.vertigo.chatbot.designer.builder.controllers.bot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Attachment;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotCustomConfig;
import io.vertigo.chatbot.commons.domain.ContextPossibleValue;
import io.vertigo.chatbot.commons.domain.ContextValue;
import io.vertigo.chatbot.designer.builder.services.DocumentaryResourceContextServices;
import io.vertigo.chatbot.designer.builder.services.DocumentaryResourceServices;
import io.vertigo.chatbot.designer.builder.services.DocumentaryResourceTypeServices;
import io.vertigo.chatbot.designer.builder.services.bot.AttachmentServices;
import io.vertigo.chatbot.designer.builder.services.bot.ChatbotCustomConfigServices;
import io.vertigo.chatbot.designer.builder.services.bot.ContextPossibleValueServices;
import io.vertigo.chatbot.designer.builder.services.bot.ContextValueServices;
import io.vertigo.chatbot.designer.domain.DocumentaryResource;
import io.vertigo.chatbot.designer.domain.DocumentaryResourceContext;
import io.vertigo.chatbot.designer.domain.DocumentaryResourceContextIhm;
import io.vertigo.chatbot.designer.domain.DocumentaryResourceType;
import io.vertigo.chatbot.designer.utils.AbstractChatbotDtObjectValidator;
import io.vertigo.datamodel.structure.definitions.DtField;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.stereotype.Validate;
import io.vertigo.vega.webservice.validation.DtObjectErrors;
import io.vertigo.vega.webservice.validation.UiMessageStack;

import static io.vertigo.chatbot.designer.utils.ListUtils.listLimitReached;

@Controller
@RequestMapping("/bot/{botId}/document")
@Secured("Chatbot$botContributor")
public class DocumentDetailController extends AbstractBotCreationController<DocumentaryResource> {

    private static final ViewContextKey<DocumentaryResource> documentaryResourceKey = ViewContextKey.of("documentaryResource");
    private static final ViewContextKey<DocumentaryResourceContextIhm> documentaryResourceContextIhmListKey = ViewContextKey.of("documentaryResourceContextIhmList");
    private static final ViewContextKey<DocumentaryResourceContext> newDocumentaryResourceContextKey = ViewContextKey.of("newDocumentaryResourceContext");
    private static final ViewContextKey<DocumentaryResourceType> documentaryResourceTypeListKey = ViewContextKey.of("documentaryResourceTypeList");
    private static final ViewContextKey<ContextValue> contextValueListKey = ViewContextKey.of("contextValueList");
    private static final ViewContextKey<ContextPossibleValue> contextPossibleValueListKey = ViewContextKey.of("contextPossibleValueList");
    private static final ViewContextKey<ContextPossibleValue> filteredContextPossibleValueListKey = ViewContextKey.of("filteredContextPossibleValueList");
    private static final ViewContextKey<Attachment> attachmentKey = ViewContextKey.of("attachment");
    private static final ViewContextKey<Long> maxSizeKey = ViewContextKey.of("maxSize");
    private static final ViewContextKey<Long> attachmentTotalSizeKey = ViewContextKey.of("attachmentTotalSize");
    private static final ViewContextKey<FileInfoURI> importAttachmentFileUri = ViewContextKey.of("importAttachmentFileUri");

    @Inject
    private DocumentaryResourceServices documentaryResourceServices;

    @Inject
    private DocumentaryResourceTypeServices documentaryResourceTypeServices;

    @Inject
    private AttachmentServices attachmentServices;

    @Inject
    private ChatbotCustomConfigServices chatbotCustomConfigServices;

    @Inject
    private ContextValueServices contextValueServices;

    @Inject
    private ContextPossibleValueServices contextPossibleValueServices;

    @Inject
    private DocumentaryResourceContextServices documentaryResourceContextServices;

    @GetMapping("/{dreId}")
    public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId, @PathVariable("dreId") final Long dreId) {
        final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);
        final ChatbotCustomConfig chatbotCustomConfig = chatbotCustomConfigServices.getChatbotCustomConfigByBotId(botId);
        final DocumentaryResource documentaryResource = documentaryResourceServices.getDocResById(dreId);
        final DtList<DocumentaryResourceContextIhm> documentaryResourceContexts = documentaryResourceContextServices.getAllDocumentaryResourceContextIhmByDreId(bot, dreId);
        final DtList<ContextValue> contextValues = contextValueServices.getAllContextValueByBotId(botId);
        final DtList<ContextPossibleValue> contextPossibleValues = contextPossibleValueServices.getAllContextPossibleValuesByBot(bot);
        final Attachment attachment = documentaryResource.getAttId() != null? attachmentServices.findById(documentaryResource.getAttId()) : new Attachment();

        viewContext.publishDto(documentaryResourceKey, documentaryResource);
        viewContext.publishDtList(documentaryResourceContextIhmListKey, documentaryResourceContexts);
        viewContext.publishDto(newDocumentaryResourceContextKey, new DocumentaryResourceContext());
        viewContext.publishDtList(contextValueListKey, contextValues);
        viewContext.publishDtList(contextPossibleValueListKey, contextPossibleValues);
        viewContext.publishDtList(filteredContextPossibleValueListKey, new DtList<>(ContextPossibleValue.class));
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
        final DtList<ContextValue> contextValues = contextValueServices.getAllContextValueByBotId(botId);
        final DtList<ContextPossibleValue> contextPossibleValues = contextPossibleValueServices.getAllContextPossibleValuesByBot(bot);

        viewContext.publishDto(documentaryResourceKey, documentaryResourceServices.getNewdocumentaryResource(botId));
        viewContext.publishDtList(documentaryResourceContextIhmListKey, new DtList<>(DocumentaryResourceContextIhm.class));
        viewContext.publishDto(newDocumentaryResourceContextKey, new DocumentaryResourceContext());
        viewContext.publishDtList(contextValueListKey, contextValues);
        viewContext.publishDtList(contextPossibleValueListKey, contextPossibleValues);
        viewContext.publishDtList(filteredContextPossibleValueListKey, new DtList<>(ContextPossibleValue.class));
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

        documentaryResourceServices.saveDocumentaryResource(bot, documentaryResource, attachment, attachmentFile, maxSize, attachmentTotalSize);

        return "redirect:/bot/" + bot.getBotId() + "/document/" + documentaryResource.getDreId();
    }

    @PostMapping("/_saveDocumentaryResourceContext")
    public ViewContext saveDocumentaryResourceContext(final ViewContext viewContext,
                                          @ViewAttribute("bot") final Chatbot bot,
                                          @ViewAttribute("newDocumentaryResourceContext")  @Validate(DocumentDetailController.DocumentaryResourceContextNotEmptyValidator.class) final DocumentaryResourceContext documentaryResourceContext) {

        documentaryResourceContextServices.saveDocumentaryResourceContext(bot, documentaryResourceContext);
        viewContext.publishDto(newDocumentaryResourceContextKey, new DocumentaryResourceContext());
        viewContext.publishDtList(documentaryResourceContextIhmListKey, documentaryResourceContextServices.getAllDocumentaryResourceContextIhmByDreId(bot, documentaryResourceContext.getDreId()));
        return viewContext;
    }

    @PostMapping("/_delete")
    public String deleteDocumentaryResource(final ViewContext viewContext,
                                            @ViewAttribute("bot") Chatbot bot,
                                            @ViewAttribute("documentaryResource") DocumentaryResource documentaryResource) {
        documentaryResourceServices.deleteDocumentaryResource(bot, documentaryResource);
        return "redirect:/bot/" + bot.getBotId() + "/documents/";
    }

    @PostMapping("/_deleteDocumentaryResourceContext")
    public ViewContext deleteDocumentaryResourceContext(final ViewContext viewContext,
                                                  @ViewAttribute("bot") final Chatbot bot,
                                                  @ViewAttribute("documentaryResource") final DocumentaryResource documentaryResource,
                                                  @RequestParam("drcId") final Long drcId) {

        documentaryResourceContextServices.deleteDocumentaryResourceContextById(bot, drcId);

        viewContext.publishDtList(documentaryResourceContextIhmListKey, documentaryResourceContextServices.getAllDocumentaryResourceContextIhmByDreId(bot, documentaryResource.getDreId()));
        return viewContext;
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

    public static final class DocumentaryResourceContextNotEmptyValidator extends AbstractChatbotDtObjectValidator<DocumentaryResourceContext> {
        /**
         * {@inheritDoc}
         */
        @Override
        protected void checkMonoFieldConstraints(final DocumentaryResourceContext documentaryResourceContext, final DtField dtField, final DtObjectErrors dtObjectErrors) {
            super.checkMonoFieldConstraints(documentaryResourceContext, dtField, dtObjectErrors);
        }
    }
}
