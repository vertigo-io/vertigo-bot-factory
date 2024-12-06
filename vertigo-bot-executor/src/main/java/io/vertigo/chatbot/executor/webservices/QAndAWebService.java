package io.vertigo.chatbot.executor.webservices;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.QuestionAnswerExport;
import io.vertigo.chatbot.executor.manager.ExecutorManager;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.vega.webservice.WebServices;
import io.vertigo.vega.webservice.stereotype.GET;
import io.vertigo.vega.webservice.stereotype.PathPrefix;

@PathPrefix("/qanda")
public class QAndAWebService implements WebServices {

    @Inject
    private ExecutorManager executorManager;

    @GET("/getQuestionsAnswers")
    public DtList<QuestionAnswerExport> getQuestionAnswerList() {
        return executorManager.getQuestionAnswerList();
    }
}
