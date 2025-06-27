package io.vertigo.chatbot.executor.webservices;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.QuestionAnswerExport;
import io.vertigo.chatbot.executor.manager.ExecutorManager;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.vega.webservice.WebServices;
import io.vertigo.vega.webservice.stereotype.POST;
import io.vertigo.vega.webservice.stereotype.PathPrefix;

import java.util.Map;

@PathPrefix("/qanda")
public class QAndAWebService implements WebServices {

    @Inject
    private ExecutorManager executorManager;

    @POST("/getQuestionsAnswers")
    public DtList<QuestionAnswerExport> getQuestionAnswerList(final Map<String, String> context) {
        return executorManager.getQuestionAnswerList(context);
    }
}
