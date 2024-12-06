package io.vertigo.chatbot.designer.builder.controllers.bot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotCustomConfig;
import io.vertigo.chatbot.commons.domain.questionanswer.QuestionAnswer;
import io.vertigo.chatbot.commons.domain.questionanswer.QuestionAnswerCategory;
import io.vertigo.chatbot.commons.domain.questionanswer.QuestionAnswerIhm;
import io.vertigo.chatbot.designer.builder.services.bot.ChatbotCustomConfigServices;
import io.vertigo.chatbot.designer.builder.services.questionanswer.QuestionAnswerCategoryServices;
import io.vertigo.chatbot.designer.builder.services.questionanswer.QuestionAnswerServices;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.validation.UiMessageStack;

import static io.vertigo.chatbot.designer.utils.ListUtils.listLimitReached;

@Controller
@RequestMapping("/bot/{botId}/questionsanswers/detail")
@Secured("BotUser")
public class QuestionAnswerDetailController extends AbstractBotCreationController<QuestionAnswer> {

    private static final ViewContextKey<QuestionAnswerIhm> questionAnswerIhmKey = ViewContextKey.of("questionAnswerIhm");
    private static final ViewContextKey<QuestionAnswerCategory> categoryListKey = ViewContextKey.of("categoryList");
    private static final ViewContextKey<ChatbotCustomConfig> chatbotCustomConfigKey = ViewContextKey.of("chatbotCustomConfig");

    @Inject
    private QuestionAnswerServices questionAnswerServices;

    @Inject
    private QuestionAnswerCategoryServices questionAnswerCategoryServices;

    @Inject
    private ChatbotCustomConfigServices chatbotCustomConfigServices;

    @GetMapping("/{qaId}")
    public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId,
                            @PathVariable("qaId") final Long questionAnswerId) {

        final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);
        final QuestionAnswer questionAnswer = questionAnswerServices.getQueAnsById(bot, questionAnswerId);
        final QuestionAnswerIhm questionAnswerIhm = questionAnswerServices.getQueAnsIhmById(bot, questionAnswerId);
        final DtList<QuestionAnswerCategory> questionAnswerCategories = questionAnswerCategoryServices.getAllQueAnsCatByBot(bot);
        final ChatbotCustomConfig chatbotCustomConfig = chatbotCustomConfigServices.getChatbotCustomConfigByBotId(bot.getBotId());

        viewContext.publishDto(chatbotCustomConfigKey, chatbotCustomConfig);
        viewContext.publishDto(questionAnswerIhmKey, questionAnswerIhm);
        viewContext.publishDtList(categoryListKey, questionAnswerCategories);
        super.initBreadCrums(viewContext, questionAnswer);
        listLimitReached(viewContext, uiMessageStack);
        toModeReadOnly();
    }

    @GetMapping("/new")
    public void getNewQuestionAnswer(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
        final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);
        final QuestionAnswerIhm questionAnswerIhm = questionAnswerServices.getNewQueAns(bot);
        final DtList<QuestionAnswerCategory> questionAnswerCategories = questionAnswerCategoryServices.getAllQueAnsCatByBot(bot);
        final ChatbotCustomConfig chatbotCustomConfig = chatbotCustomConfigServices.getChatbotCustomConfigByBotId(bot.getBotId());

        viewContext.publishDto(chatbotCustomConfigKey, chatbotCustomConfig);
        viewContext.publishDto(questionAnswerIhmKey, questionAnswerIhm);
        viewContext.publishDtList(categoryListKey, questionAnswerCategories);

        super.initEmptyBreadcrums(viewContext);
        listLimitReached(viewContext, uiMessageStack);
        toModeCreate();
    }


    @Override
    protected String getBreadCrums(final QuestionAnswer object) {
        return object.getQuestion();
    }

    @PostMapping("/_save")
    public String doSave(@ViewAttribute("bot") final Chatbot bot, @ViewAttribute("questionAnswerIhm") final QuestionAnswerIhm questionAnswerIhm) {
        QuestionAnswer newQuestionAnswer = questionAnswerServices.saveQueAnsFromIhm(bot, questionAnswerIhm);
        return "redirect:/bot/" + bot.getBotId() + "/questionsanswers/detail/" + newQuestionAnswer.getQaId();
    }

    @PostMapping("/_delete")
    public String doDelete(@ViewAttribute("bot") final Chatbot bot, @ViewAttribute("questionAnswerIhm") final QuestionAnswerIhm questionAnswerIhm) {
        questionAnswerServices.deleteQueAnsById(bot, questionAnswerIhm.getQaId());
        return "redirect:/bot/" + bot.getBotId() + "/questionsanswers/";
    }

    @PostMapping("/_edit")
    public void doEdit() {
        toModeEdit();
    }
}

