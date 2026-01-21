package io.vertigo.chatbot.executor.webservices;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.QuestionAnswerExport;
import io.vertigo.chatbot.executor.manager.ExecutorManager;
import io.vertigo.chatbot.executor.model.IncomeQuestionAnswer;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.vega.webservice.WebServices;
import io.vertigo.vega.webservice.stereotype.POST;
import io.vertigo.vega.webservice.stereotype.PathParam;
import io.vertigo.vega.webservice.stereotype.PathPrefix;

import java.util.Map;
import java.util.UUID;

/**
 * Web service for questions and answers (FAQ).
 * Provides endpoints to retrieve Q&A based on context and track clicks.
 *
 * @author Chatbot Team
 */
@PathPrefix("/qanda")
public class QAndAWebService implements WebServices {

    @Inject
    private ExecutorManager executorManager;

    /**
     * Get questions and answers filtered by context
     *
     * @param context context map for filtering Q&A
     * @return list of questions and answers matching the context
     */
    @POST("/getQuestionsAnswers")
    public DtList<QuestionAnswerExport> getQuestionAnswerList(final Map<String, String> context) {
        return executorManager.getQuestionAnswerList(context);
    }

	/**
	 * Track a question/answer click for analytics
	 *
	 * @param sessionId session identifier
	 * @param incomeQuestionAnswer question/answer click information
	 */
	@POST("/stats/{sessionId}")
	public void stats(@PathParam("sessionId") final UUID sessionId,
					  final IncomeQuestionAnswer incomeQuestionAnswer) {
		executorManager.trackQuestionAnswerClick(
				sessionId,
				incomeQuestionAnswer.qaId(),
				incomeQuestionAnswer.question(),
				incomeQuestionAnswer.catLabel());
	}
}
