package io.vertigo.chatbot.designer.builder.controllers.bot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.questionanswer.QuestionAnswer;
import io.vertigo.chatbot.commons.domain.questionanswer.QuestionAnswerCategory;
import io.vertigo.chatbot.commons.domain.questionanswer.QuestionAnswerIhm;
import io.vertigo.chatbot.commons.domain.questionanswer.SelectQueAnsCategory;
import io.vertigo.chatbot.commons.domain.topic.TopicCriteria;
import io.vertigo.chatbot.commons.multilingual.utils.UtilsMultilingualResources;
import io.vertigo.chatbot.designer.builder.services.questionanswer.QuestionAnswerCategoryServices;
import io.vertigo.chatbot.designer.builder.services.questionanswer.QuestionAnswerFileExportServices;
import io.vertigo.chatbot.designer.builder.services.questionanswer.QuestionAnswerServices;
import io.vertigo.core.lang.VUserException;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.validation.UiMessageStack;

import static io.vertigo.chatbot.designer.utils.ListUtils.listLimitReached;

@Controller
@RequestMapping("/bot/{botId}/questionsanswers")
@Secured("BotUser")
public class QuestionAnswerListController extends AbstractBotListEntityController<QuestionAnswer> {

    private static final ViewContextKey<QuestionAnswerIhm> questionAnswerIhmListKey = ViewContextKey.of("questionAnswerIhmList");
    private static final ViewContextKey<QuestionAnswerCategory> categoryListKey = ViewContextKey.of("categoryList");
    private static final ViewContextKey<FileInfoURI> importQuestionAnswerFileUri = ViewContextKey.of("importQuestionAnswerFileUri");
    private static final ViewContextKey<SelectQueAnsCategory> selectionCatListKey = ViewContextKey.of("selectionCatList");
    private static final ViewContextKey<TopicCriteria> criteriaKey = ViewContextKey.of("criteria");

    @Inject
    private QuestionAnswerServices questionAnswerServices;

    @Inject
    private QuestionAnswerFileExportServices questionAnswerFileExportServices;

    @Inject
    private QuestionAnswerCategoryServices queAnsCategoryServices;

    @GetMapping("/")
    @Secured("BotUser")
    public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
        final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);
        viewContext.publishDtList(questionAnswerIhmListKey, questionAnswerServices.getAllParsedQueAnsIhmByBot(bot));
        viewContext.publishDtList(categoryListKey, queAnsCategoryServices.getAllQueAnsCatByBot(bot));
        viewContext.publishFileInfoURI(importQuestionAnswerFileUri, null);
        viewContext.publishDto(selectionCatListKey, new SelectQueAnsCategory());
        viewContext.publishDto(criteriaKey, new TopicCriteria());

        super.initBreadCrums(viewContext, QuestionAnswer.class);
        listLimitReached(viewContext, uiMessageStack);
        toModeReadOnly();
    }

    @PostMapping("/_exportQuestionAnswer")
    @Secured("SuperAdm")
    public VFile doExportTopicFile(final ViewContext viewContext,
                                   @ViewAttribute("bot") final Chatbot bot,
                                   @ViewAttribute("selectionCatList") final SelectQueAnsCategory selectQueAnsCategory) {

        DtList<QuestionAnswerIhm> questionAnwserIhmList = questionAnswerServices.getAllQueAnsIhmByCatIdList(bot, selectQueAnsCategory.getQaCatId());
        return questionAnswerFileExportServices.exportQuestionAnswers(bot, questionAnwserIhmList);
    }

    @PostMapping("/_importQuestionAnswer")
    @Secured("SuperAdm")
    public String doImportQuestionAnswer(final ViewContext viewContext,
                                @ViewAttribute("bot") final Chatbot bot,
                                @ViewAttribute("importQuestionAnswerFileUri") final FileInfoURI importQuestionAnswerFile) {

        if (importQuestionAnswerFile == null) {
            throw new VUserException(UtilsMultilingualResources.IMPORT_FILE_MUST_NOT_BE_EMPTY);
        }
        questionAnswerFileExportServices.importQueAnsFromCSVFile(bot, importQuestionAnswerFile);

        return "redirect:/bot/" + bot.getBotId() + "/questionsanswers/";
    }
}