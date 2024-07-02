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
import io.vertigo.chatbot.commons.domain.questionanswer.QuestionAnswerIhm;
import io.vertigo.chatbot.designer.builder.services.questionanswer.QuestionAnswerCategoryServices;
import io.vertigo.chatbot.designer.builder.services.questionanswer.QuestionAnswerServices;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.validation.UiMessageStack;

import static io.vertigo.chatbot.designer.utils.ListUtils.listLimitReached;
import static java.lang.Long.parseLong;

@Controller
@RequestMapping("/bot/{botId}/qacategory")
@Secured("BotUser")
public class QuestionAnswerCategoryDetailController extends AbstractBotCreationController<QuestionAnswerCategory> {

    private static final ViewContextKey<QuestionAnswerCategory> categoryListKey = ViewContextKey.of("categoryList");
    private static final ViewContextKey<QuestionAnswerCategory> categoryKey = ViewContextKey.of("category");
    private static final ViewContextKey<QuestionAnswerIhm> queAnsIhmFromCategoryListKey = ViewContextKey.of("queAnsIhmFromCategoryList");
    private static final ViewContextKey<QuestionAnswerIhm> queAnsIhmExceptCategoryListKey = ViewContextKey.of("queAnsIhmExceptCategoryList");
    private static final ViewContextKey<String> queAnsIdKey = ViewContextKey.of("queAnsId");
    private static final ViewContextKey<String> newQueAnsCatIdKey = ViewContextKey.of("newQueAnsCatId");
    private static final ViewContextKey<String> queAnsListToUpdateKey = ViewContextKey.of("queAnsListToUpdate");
    private static final ViewContextKey<QuestionAnswerIhm> selectedQueAnsIhmListKey = ViewContextKey.of("selectedQueAnsIhmList");


    @Inject
    private QuestionAnswerCategoryServices questionAnswerCategoryServices;

    @Inject
    private QuestionAnswerServices questionAnswerServices;

    @GetMapping("/{qaCatId}")
    public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId,
                            @PathVariable("qaCatId") final Long qaCatId) {
        final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);
        final QuestionAnswerCategory queAnsCategory = questionAnswerCategoryServices.getQueAnsCategoryById(bot, qaCatId);
        final DtList<QuestionAnswerCategory> queAnsCategoryAllList = questionAnswerCategoryServices.getAllQueAnsCatByBot(bot);
        final DtList<QuestionAnswerIhm> questionAnswerIhmFromCategoryList = questionAnswerServices.getQueAnsIhmByCatId(bot, qaCatId);
        final DtList<QuestionAnswerIhm> questionAnswerIhmExceptCatList = questionAnswerServices.getAllQueAnsIhmByBotIdExceptACategory(bot, qaCatId);


        viewContext.publishDtList(categoryListKey, queAnsCategoryAllList);
        viewContext.publishDto(categoryKey, queAnsCategory);
        viewContext.publishDtList(queAnsIhmFromCategoryListKey, questionAnswerIhmFromCategoryList);
        viewContext.publishDtList(queAnsIhmExceptCategoryListKey, questionAnswerIhmExceptCatList);
        viewContext.publishRef(queAnsIdKey, "");
        viewContext.publishRef(newQueAnsCatIdKey, "");
        viewContext.publishRef(queAnsListToUpdateKey, "");
        viewContext.publishDtList(selectedQueAnsIhmListKey, new DtList<>(QuestionAnswerIhm.class));

        super.initBreadCrums(viewContext, queAnsCategory);
        listLimitReached(viewContext, uiMessageStack);
        toModeReadOnly();
    }

    @GetMapping("/new")
    public void getNewCategory(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
        final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);
        final QuestionAnswerCategory queAnsCategory = questionAnswerCategoryServices.getNewQueAnsCategory(bot);
        final DtList<QuestionAnswerCategory> queAnsCategoryAllList = questionAnswerCategoryServices.getAllQueAnsCatByBot(bot);
        final DtList<QuestionAnswerIhm> questionAnswerIhmExceptCatList = questionAnswerServices.getAllQueAnsIhmByBot(bot);

        viewContext.publishDtList(categoryListKey, queAnsCategoryAllList);
        viewContext.publishDto(categoryKey, queAnsCategory);
        viewContext.publishDtList(queAnsIhmFromCategoryListKey, new DtList<>(QuestionAnswerIhm.class));
        viewContext.publishDtList(queAnsIhmExceptCategoryListKey, questionAnswerIhmExceptCatList);
        viewContext.publishRef(queAnsIdKey, "");
        viewContext.publishRef(newQueAnsCatIdKey, "");
        viewContext.publishRef(queAnsListToUpdateKey, "");
        viewContext.publishDtList(selectedQueAnsIhmListKey, new DtList<>(QuestionAnswerIhm.class));

        super.initEmptyBreadcrums(viewContext);
        listLimitReached(viewContext, uiMessageStack);
        toModeCreate();
    }

    @PostMapping("/_saveQueAnsCategoryChange")
    public ViewContext saveQueAnsCategoryChange(final ViewContext viewContext,
                                               final UiMessageStack uiMessageStack,
                                               @ViewAttribute("bot") final Chatbot bot,
                                               @ViewAttribute("category") final QuestionAnswerCategory category,
                                               @ViewAttribute("queAnsId") final String queAnsId,
                                               @ViewAttribute("newQueAnsCatId") final String newQueAnsCatId) {


        questionAnswerServices.saveCategoryChange(bot, parseLong(queAnsId), parseLong(newQueAnsCatId));

        final DtList<QuestionAnswerIhm> questionAnswerIhmFromCategoryList = questionAnswerServices.getQueAnsIhmByCatId(bot, category.getQaCatId());
        final DtList<QuestionAnswerIhm> questionAnswerIhmExceptCatList = questionAnswerServices.getAllQueAnsIhmByBotIdExceptACategory(bot, category.getQaCatId());

        viewContext.publishDtList(queAnsIhmFromCategoryListKey, questionAnswerIhmFromCategoryList);
        viewContext.publishDtList(queAnsIhmExceptCategoryListKey, questionAnswerIhmExceptCatList);
        viewContext.publishRef(queAnsIdKey, "");
        viewContext.publishRef(newQueAnsCatIdKey, "");

        return viewContext;
    }

    @PostMapping("/_saveAddingQuestionsAnswers")
    public ViewContext saveAddingQuestionsAnswers(final ViewContext viewContext,
                                        final UiMessageStack uiMessageStack,
                                        @ViewAttribute("bot") final Chatbot bot,
                                        @ViewAttribute("category") final QuestionAnswerCategory category,
                                        @ViewAttribute("queAnsListToUpdate") final String queAnsListToUpdate) {


        questionAnswerServices.saveQueAnsCategoryChangesFromTopIdsString(bot, queAnsListToUpdate, category.getQaCatId());

        final DtList<QuestionAnswerIhm> questionAnswerIhmFromCategoryList = questionAnswerServices.getQueAnsIhmByCatId(bot, category.getQaCatId());
        final DtList<QuestionAnswerIhm> questionAnswerIhmExceptCatList = questionAnswerServices.getAllQueAnsIhmByBotIdExceptACategory(bot, category.getQaCatId());


        viewContext.publishDtList(queAnsIhmFromCategoryListKey, questionAnswerIhmFromCategoryList);
        viewContext.publishDtList(queAnsIhmExceptCategoryListKey, questionAnswerIhmExceptCatList);

        return viewContext;
    }

    @PostMapping("/_save")
    public String doSave(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot, @ViewAttribute("category") final QuestionAnswerCategory category) {
        questionAnswerCategoryServices.saveCategory(bot, category);
        return "redirect:/bot/" + category.getBotId() + "/qacategory/" + category.getQaCatId();
    }

    @PostMapping("/_delete")
    public String doDelete(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot, @ViewAttribute("category") final QuestionAnswerCategory category) {
        questionAnswerCategoryServices.deleteCategory(bot, category.getQaCatId());
        return "redirect:/bot/" + bot.getBotId() + "/qacategories/";
    }

    @PostMapping("/_edit")
    public void doEdit() {
        toModeEdit();
    }

    @Override
    protected String getBreadCrums(QuestionAnswerCategory object) {
        return object.getLabel();
    }
}
