package io.vertigo.chatbot.designer.builder.webservices;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.ExecutorTrainingCallback;
import io.vertigo.chatbot.designer.builder.services.TrainingServices;
import io.vertigo.vega.webservice.WebServices;
import io.vertigo.vega.webservice.stereotype.AnonymousAccessAllowed;
import io.vertigo.vega.webservice.stereotype.InnerBodyParam;
import io.vertigo.vega.webservice.stereotype.POST;
import io.vertigo.vega.webservice.stereotype.SessionLess;

public class TrainingWebService implements WebServices {

	@Inject
	private TrainingServices trainingServices;

	@AnonymousAccessAllowed
	@POST("/trainingCallback")
	@SessionLess
	public void trainingCallback(@InnerBodyParam("executorTrainingCallback") final ExecutorTrainingCallback callback) {
		trainingServices.trainingCallback(callback);
	}
}
