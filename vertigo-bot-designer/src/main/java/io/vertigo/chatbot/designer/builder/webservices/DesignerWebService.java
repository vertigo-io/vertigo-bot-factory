package io.vertigo.chatbot.designer.builder.webservices;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.ExecutorTrainingCallback;
import io.vertigo.chatbot.designer.builder.services.ExecutorBridgeServices;
import io.vertigo.vega.webservice.WebServices;
import io.vertigo.vega.webservice.stereotype.AnonymousAccessAllowed;
import io.vertigo.vega.webservice.stereotype.POST;
import io.vertigo.vega.webservice.stereotype.SessionLess;

public class DesignerWebService implements WebServices {

	@Inject
	private ExecutorBridgeServices executorBridgeServices;

	@AnonymousAccessAllowed
	@POST("/trainingCallback")
	@SessionLess
	public void trainingCallback(final ExecutorTrainingCallback callback) {
		if (callback.getSuccess()) {
			//			executorBridgeServices.fetchModel(callback);
		} else {
			// TODO
		}
	}
}
