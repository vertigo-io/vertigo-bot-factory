package io.vertigo.chatbot.designer.builder.services.questionanswer;

import org.jsoup.Jsoup;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.questionanswer.QuestionAnswerDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.questionanswer.QuestionAnswer;
import io.vertigo.chatbot.commons.domain.questionanswer.QuestionAnswerIhm;
import io.vertigo.chatbot.designer.builder.questionAnswer.QuestionAnswerPAO;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.util.VCollectors;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;
import static java.lang.Long.parseLong;

@Transactional
public class QuestionAnswerServices implements Component {


    @Inject
    private QuestionAnswerDAO questionAnswerDAO;

    @Inject
    private QuestionAnswerPAO questionAnswerPAO;

    public DtList<QuestionAnswer> getAllQueAnsByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
        return questionAnswerDAO.findAll(Criterions.isEqualTo(DtDefinitions.QuestionAnswerFields.botId, bot.getBotId()), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
    }

    public DtList<QuestionAnswer> getAllQueAnsByCatId(@SecuredOperation("botVisitor") final Chatbot bot, final Long categoryId) {
        return getAllQueAnsByBot(bot).stream().filter(queAns -> Objects.equals(queAns.getQaCatId(), categoryId)).collect(VCollectors.toDtList(QuestionAnswer.class));
    }

    public DtList<QuestionAnswerIhm> getAllQueAnsIhmByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
        return sortQueAnsIhmByCat(bot,questionAnswerPAO.getAllQuestionAnswerIhmFromBot(bot.getBotId()));
    }


    public DtList<QuestionAnswerIhm> getAllParsedQueAnsIhmByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
        DtList<QuestionAnswerIhm> questionAnswerIhmList = sortQueAnsIhmByCat(bot,questionAnswerPAO.getAllQuestionAnswerIhmFromBot(bot.getBotId()));
        return parseQuestionAnswer(bot, questionAnswerIhmList);
    }

    public DtList<QuestionAnswerIhm> getAllQueAnsIhmByBotIdExceptACategory(final Chatbot bot, final Long categoryId) {
        DtList<QuestionAnswerIhm> allQueAnsIhm = getAllQueAnsIhmByBot(bot);
        DtList<QuestionAnswerIhm> filteredQueAnsIhmList = allQueAnsIhm.stream().filter(queAnsIhm -> !queAnsIhm.getCatId().equals(categoryId)).collect(VCollectors.toDtList(QuestionAnswerIhm.class));
        return sortQueAnsIhmByCat(bot,parseQuestionAnswer(bot, filteredQueAnsIhmList));
    }

    public QuestionAnswer getQueAnsById(@SecuredOperation("botVisitor") final Chatbot bot , final Long questionAnswerId) {
        return questionAnswerDAO.get(questionAnswerId);
    }

    public QuestionAnswerIhm getQueAnsIhmById(@SecuredOperation("botVisitor") final Chatbot bot, final Long questionAnswerId) {
        return questionAnswerPAO.getQuestionAnswerIhmById(questionAnswerId);
    }

    public DtList<QuestionAnswerIhm> getQueAnsIhmByCatId(@SecuredOperation("botVisitor") final Chatbot bot, final Long categoryId) {
        DtList<QuestionAnswerIhm> questionAnswerIhms = questionAnswerPAO.getAllQuestionAnswerIhmFromBot(bot.getBotId());
        questionAnswerIhms = questionAnswerIhms.stream().filter(queAns -> Objects.equals(queAns.getCatId(), categoryId)).collect(VCollectors.toDtList(QuestionAnswerIhm.class));
        return parseQuestionAnswer(bot, questionAnswerIhms);
    }

    public DtList<QuestionAnswerIhm> parseQuestionAnswer(@SecuredOperation("botVisitor") final Chatbot bot, final DtList<QuestionAnswerIhm> questionAnswerIhmList) {
        questionAnswerIhmList.forEach(questionAnswerIhm -> {
            questionAnswerIhm.setQuestion(Jsoup.parse(questionAnswerIhm.getQuestion()).text());
            questionAnswerIhm.setAnswer(Jsoup.parse(questionAnswerIhm.getAnswer()).text());
        });
        return questionAnswerIhmList;
    }

    public void deleteQueAnsById(@SecuredOperation("botContributor") final Chatbot bot, final Long questionAnswerId) {
        questionAnswerDAO.delete(questionAnswerId);
    }

    public void saveQuestionAnswer(@SecuredOperation("botContributor") final Chatbot bot, final QuestionAnswer questionAnswer) {
        questionAnswerDAO.save(questionAnswer);
    }

    public void saveCategoryChange(@SecuredOperation("botContributor") final Chatbot bot, Long queAnsId, final Long queAnsCategoryId) {
        QuestionAnswer questionAnswer = questionAnswerDAO.get(queAnsId);
        questionAnswer.setQaCatId(queAnsCategoryId);
        questionAnswerDAO.update(questionAnswer);
    }

    public void saveQueAnsCategoryChangesFromTopIdsString(@SecuredOperation("botContributor") final Chatbot bot, String queAnsIdsString, final Long queAnsCategoryId) {
        List<String> queAnsIdList = Arrays.asList(queAnsIdsString.split(","));
        queAnsIdList.forEach(queAnsId -> saveCategoryChange(bot, parseLong(queAnsId), queAnsCategoryId));
    }

    public DtList<QuestionAnswerIhm> sortQueAnsIhmByCat(@SecuredOperation("botVisitor") final Chatbot bot, final DtList<QuestionAnswerIhm> questionAnswerIhmList) {
        return questionAnswerIhmList.stream()
                .sorted(Comparator.comparing(QuestionAnswerIhm::getCatId)
                        .thenComparing(QuestionAnswerIhm::getQuestion))
                .collect(VCollectors.toDtList(QuestionAnswerIhm.class));
    }

    public QuestionAnswerIhm getNewQueAns(@SecuredOperation("botContributor") final Chatbot bot) {
        final QuestionAnswerIhm questionAnswerIhm = new QuestionAnswerIhm();
        questionAnswerIhm.setIsEnabled(true);
        return questionAnswerIhm;
    }

    public QuestionAnswer saveQueAnsFromIhm(@SecuredOperation("botContributor") final Chatbot bot, final QuestionAnswerIhm questionAnswerIhm) {
        QuestionAnswer questionAnswer = new  QuestionAnswer();
        questionAnswer.setQaId(questionAnswerIhm.getQaId());
        questionAnswer.setQuestion(questionAnswerIhm.getQuestion());
        questionAnswer.setAnswer(questionAnswerIhm.getAnswer());
        questionAnswer.setIsEnabled(questionAnswerIhm.getIsEnabled());
        questionAnswer.setQaCatId(questionAnswerIhm.getCatId());
        questionAnswer.setBotId(bot.getBotId());
        return questionAnswerDAO.save(questionAnswer);
    }




}
