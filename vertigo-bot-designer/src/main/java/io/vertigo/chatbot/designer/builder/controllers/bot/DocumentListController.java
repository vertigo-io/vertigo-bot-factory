package io.vertigo.chatbot.designer.builder.controllers.bot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.builder.services.DocumentaryResourceServices;
import io.vertigo.chatbot.designer.builder.services.DocumentaryResourceTypeServices;
import io.vertigo.chatbot.designer.domain.DocumentaryResource;
import io.vertigo.chatbot.designer.domain.DocumentaryResourceType;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.vega.webservice.validation.UiMessageStack;

import static io.vertigo.chatbot.designer.utils.ListUtils.listLimitReached;

@Controller
@RequestMapping("/bot/{botId}/documents")
@Secured("Chatbot$botContributor")
public class DocumentListController extends AbstractBotListEntityController<DocumentaryResource>{

    public static final ViewContextKey<DocumentaryResource> documentaryResourceListKey = ViewContextKey.of("documentaryResourceList");
    public static final ViewContextKey<DocumentaryResourceType> documentaryResourceTypeListKey = ViewContextKey.of("documentaryResourceTypeList");

    @Inject
    private DocumentaryResourceServices documentaryResourceServices;

    @Inject
    private DocumentaryResourceTypeServices documentaryResourceTypeServices;

    @GetMapping("/")
    public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
        final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);

        viewContext.publishDtList(documentaryResourceListKey, documentaryResourceServices.getAllDocResByBot(bot));
        viewContext.publishDtList(documentaryResourceTypeListKey, documentaryResourceTypeServices.getAllDocResTypes());

        super.initBreadCrums(viewContext, DocumentaryResource.class);
        listLimitReached(viewContext, uiMessageStack);
    }
}
