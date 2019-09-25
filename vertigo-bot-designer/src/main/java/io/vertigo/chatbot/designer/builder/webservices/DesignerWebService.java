package io.vertigo.chatbot.designer.builder.webservices;

import java.time.Instant;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.ExecutorTrainingCallback;
import io.vertigo.chatbot.commons.domain.Training;
import io.vertigo.chatbot.designer.builder.services.DesignerServices;
import io.vertigo.chatbot.designer.builder.services.ExecutorBridgeServices;
import io.vertigo.chatbot.designer.commons.services.FileServices;
import io.vertigo.dynamo.domain.model.FileInfoURI;
import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.vega.webservice.WebServices;
import io.vertigo.vega.webservice.stereotype.AnonymousAccessAllowed;
import io.vertigo.vega.webservice.stereotype.InnerBodyParam;
import io.vertigo.vega.webservice.stereotype.POST;
import io.vertigo.vega.webservice.stereotype.SessionLess;

public class DesignerWebService implements WebServices {

	@Inject
	private ExecutorBridgeServices executorBridgeServices;

	@Inject
	private DesignerServices designerServices;

	@Inject
	private FileServices fileServices;

	@AnonymousAccessAllowed
	@POST("/trainingCallback")
	@SessionLess
	public void trainingCallback(@InnerBodyParam("executorTrainingCallback") final ExecutorTrainingCallback callback) {
		final Training training = designerServices.getTraining(callback.getTrainingId());

		if (Boolean.TRUE.equals(callback.getSuccess())) {
			final VFile model = executorBridgeServices.fetchModel(training.getVersionNumber());
			final FileInfoURI fileInfoUri = fileServices.saveFile(model);
			training.setFilIdModel((Long) fileInfoUri.getKey());

			training.setStatus("OK");
		} else {
			training.setStatus("KO");
		}

		training.setEndTime(Instant.now());
		designerServices.saveTraining(training);
	}
}
