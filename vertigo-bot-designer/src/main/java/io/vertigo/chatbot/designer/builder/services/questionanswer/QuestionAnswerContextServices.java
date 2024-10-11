package io.vertigo.chatbot.designer.builder.services.questionanswer;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.questionanswer.QuestionAnswerContextDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.questionanswer.QuestionAnswerContext;
import io.vertigo.chatbot.commons.domain.questionanswer.QuestionAnswerContextIhm;
import io.vertigo.chatbot.designer.builder.questionAnswerContext.QuestionAnswerContextPAO;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtList;

@Transactional
@Secured("BotUser")
public class QuestionAnswerContextServices implements Component {

    @Inject
    private QuestionAnswerContextDAO questionAnswerContextDAO;

    @Inject
    private QuestionAnswerContextPAO questionAnswerContextPAO;

    public DtList<QuestionAnswerContextIhm> getAllQuestionAnswerContextIhmByQaId(@SecuredOperation("botVisitor") final Chatbot bot, final long qaId) {
        return questionAnswerContextPAO.getAllQuestionAnswerContextIhmByQaId(qaId);
    }

    public void saveQuestionAnswerContext(@SecuredOperation("botContributor") final Chatbot bot, final QuestionAnswerContext questionAnswerContext) {
        questionAnswerContextDAO.save(questionAnswerContext);
    }

    public void deleteQuestionAnswerContextById(@SecuredOperation("botContributor") final Chatbot bot, final long qacId) {
        questionAnswerContextDAO.delete(qacId);
    }

    public void deleteAllQuestionAnswerContextByQaId(@SecuredOperation("botContributor") final Chatbot bot, final long qaId) {
        questionAnswerContextPAO.removeAllQuestionAnswerContextByQaId(qaId);
    }
}
