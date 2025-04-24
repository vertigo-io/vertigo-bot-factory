package io.vertigo.chatbot.designer.builder.controllers.bot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.questionanswer.QuestionAnswerCategory;
import io.vertigo.chatbot.commons.multilingual.utils.UtilsMultilingualResources;
import io.vertigo.chatbot.designer.builder.services.questionanswer.QuestionAnswerCategoryServices;
import io.vertigo.core.lang.VUserException;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.validation.UiMessageStack;

import static io.vertigo.chatbot.designer.utils.ListUtils.listLimitReached;

@Controller
@RequestMapping("/bot/{botId}/qacategories")
@Secured("BotUser")
public class QuestionAnswerCategoryListController extends AbstractBotListEntityController<QuestionAnswerCategory> {

    private static final ViewContextKey<QuestionAnswerCategory> queAnsCategoryListKey = ViewContextKey.of("queAnsCategoryList");
    private static final ViewContextKey<FileInfoURI> importCategoriesFileUri = ViewContextKey.of("importCategoriesFileUri");

    @Inject
    private QuestionAnswerCategoryServices questionAnswerCategoryServices;

    @GetMapping("/")
    @Secured("BotUser")
    public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
        final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);
        viewContext.publishDtList(queAnsCategoryListKey, questionAnswerCategoryServices.getAllQueAnsCatByBot(bot));
        viewContext.publishFileInfoURI(importCategoriesFileUri, null);
        super.initBreadCrums(viewContext, QuestionAnswerCategory.class);
        listLimitReached(viewContext, uiMessageStack);
        toModeReadOnly();
    }

    @PostMapping("/_exportCategories")
    public VFile exportCategories(final ViewContext viewContext, final UiMessageStack uiMessageStack, @ViewAttribute("bot") final Chatbot bot) {
        return questionAnswerCategoryServices.exportQueAnsCategories(bot, questionAnswerCategoryServices.getAllQueAnsCatByBot(bot));
    }

    @PostMapping("/_importCategories")
    public String doImportDictionary(final ViewContext viewContext,
                                     @ViewAttribute("bot") final Chatbot bot,
                                     @ViewAttribute("importCategoriesFileUri") final FileInfoURI importCategoriesFileUri) {

        if (importCategoriesFileUri == null) {
            throw new VUserException(UtilsMultilingualResources.IMPORT_FILE_MUST_NOT_BE_EMPTY);
        }
        questionAnswerCategoryServices.importQueAnsCategoriesFromCSVFile(bot, importCategoriesFileUri);

        return "redirect:/bot/" + bot.getBotId() + "/qacategories/";
    }
}

