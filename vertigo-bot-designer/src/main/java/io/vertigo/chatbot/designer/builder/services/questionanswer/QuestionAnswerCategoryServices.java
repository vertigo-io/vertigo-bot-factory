package io.vertigo.chatbot.designer.builder.services.questionanswer;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.questionanswer.QuestionAnswerCategoryDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.questionanswer.QuestionAnswer;
import io.vertigo.chatbot.commons.domain.questionanswer.QuestionAnswerCategory;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
public class QuestionAnswerCategoryServices implements Component {

    @Inject
    private QuestionAnswerCategoryDAO questionAnswerCategoryDAO;

    @Inject
    private QuestionAnswerServices questionAnswerServices;

    public DtList<QuestionAnswerCategory> getAllQueAnsCatByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
        return questionAnswerCategoryDAO.findAll(Criterions.isEqualTo(DtDefinitions.QuestionAnswerCategoryFields.botId, bot.getBotId()), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
    }

    public QuestionAnswerCategory getQueAnsCategoryById(@SecuredOperation("botVisitor") final Chatbot bot, final Long categoryId) {
        return questionAnswerCategoryDAO.get(categoryId);
    }

    public void saveCategory(@SecuredOperation("botVisitor") final Chatbot bot, final QuestionAnswerCategory questionAnswerCategory) {
        questionAnswerCategoryDAO.save(questionAnswerCategory);
    }


    public void deleteCategory(@SecuredOperation("botVisitor") final Chatbot bot, final Long categoryId) {
        for (final QuestionAnswer questionAnswer : questionAnswerServices.getAllQueAnsByCatId(bot, categoryId)) {
            questionAnswerServices.deleteQueAnsById(bot,questionAnswer.getQaId());
        }
        questionAnswerCategoryDAO.delete(categoryId);
    }

    public QuestionAnswerCategory getNewQueAnsCategory(@SecuredOperation("botAdm") final Chatbot bot) {
        final QuestionAnswerCategory category = new QuestionAnswerCategory();
        category.setBotId(bot.getBotId());
        category.setIsEnabled(true);
        return category;
    }


}
