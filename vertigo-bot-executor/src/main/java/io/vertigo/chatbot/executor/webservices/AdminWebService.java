package io.vertigo.chatbot.executor.webservices;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.BotExport;
import io.vertigo.chatbot.commons.domain.ExecutorConfiguration;
import io.vertigo.chatbot.commons.domain.RunnerInfo;
import io.vertigo.chatbot.commons.domain.SmallTalkExport;
import io.vertigo.chatbot.commons.domain.TrainerInfo;
import io.vertigo.chatbot.executor.rasa.services.RasaRunnerServices;
import io.vertigo.chatbot.executor.rasa.services.RasaTrainerServices;
import io.vertigo.chatbot.vega.webservice.stereotype.RequireApiKey;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.vega.webservice.WebServices;
import io.vertigo.vega.webservice.stereotype.DELETE;
import io.vertigo.vega.webservice.stereotype.GET;
import io.vertigo.vega.webservice.stereotype.InnerBodyParam;
import io.vertigo.vega.webservice.stereotype.POST;
import io.vertigo.vega.webservice.stereotype.PUT;
import io.vertigo.vega.webservice.stereotype.PathParam;
import io.vertigo.vega.webservice.stereotype.PathPrefix;
import io.vertigo.vega.webservice.stereotype.QueryParam;

@RequireApiKey
@PathPrefix("/chatbot/admin")
public class AdminWebService implements WebServices {

	@Inject
	private RasaRunnerServices runnerServices;

	@Inject
	private RasaTrainerServices trainerServices;

	@GET("/")
	public Boolean checkAlive() {
		return true;
	}

	@POST("/train")
	public void train(@InnerBodyParam("botExport") final BotExport bot,
			@InnerBodyParam("smallTalkExport") final DtList<SmallTalkExport> smallTalkList,
			@InnerBodyParam("trainingId") final Long trainingId,
			@InnerBodyParam("modelId") final Long modelId) {
		trainerServices.trainModel(bot, smallTalkList, trainingId, modelId);
	}

	@DELETE("/train")
	public void stopTrain() {
		trainerServices.stopTrain();
	}

	@GET("/trainStatus")
	public TrainerInfo getTrainStatus() {
		return trainerServices.getTrainerState();
	}

	@GET("/runnerStatus")
	public RunnerInfo getRunnerStatus() {
		return runnerServices.getRunnerState();
	}

	@PUT("/model")
	public void putModel(@QueryParam("model") final VFile model, @QueryParam("config") final ExecutorConfiguration config) {
		runnerServices.loadModel(model, config);
	}

	@GET("/model/{id}")
	public VFile getModel(@PathParam("id") final Long id) {
		return trainerServices.getModel(id);
	}

	@DELETE("/model/{id}")
	public boolean delModel(@PathParam("id") final Long id) {
		return trainerServices.delModel(id);
	}

}
