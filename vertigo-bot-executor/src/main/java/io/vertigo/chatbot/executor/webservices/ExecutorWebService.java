package io.vertigo.chatbot.executor.webservices;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import io.vertigo.chatbot.commons.domain.BotExport;
import io.vertigo.chatbot.commons.domain.RunnerInfo;
import io.vertigo.chatbot.commons.domain.SmallTalkExport;
import io.vertigo.chatbot.commons.domain.TrainerInfo;
import io.vertigo.chatbot.executor.model.IncomeMessage;
import io.vertigo.chatbot.executor.rasa.services.RasaRunnerServices;
import io.vertigo.chatbot.executor.rasa.services.RasaTrainerServices;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.lang.WrappedException;
import io.vertigo.vega.webservice.WebServices;
import io.vertigo.vega.webservice.stereotype.AnonymousAccessAllowed;
import io.vertigo.vega.webservice.stereotype.DELETE;
import io.vertigo.vega.webservice.stereotype.GET;
import io.vertigo.vega.webservice.stereotype.InnerBodyParam;
import io.vertigo.vega.webservice.stereotype.POST;
import io.vertigo.vega.webservice.stereotype.PUT;
import io.vertigo.vega.webservice.stereotype.PathParam;
import io.vertigo.vega.webservice.stereotype.PathPrefix;
import io.vertigo.vega.webservice.stereotype.QueryParam;
import io.vertigo.vega.webservice.stereotype.SessionLess;

@PathPrefix("/chatbot")
public class ExecutorWebService implements WebServices {

	@Inject
	private RasaRunnerServices runnerServices;

	@Inject
	private RasaTrainerServices trainerServices;

	@AnonymousAccessAllowed
	@POST("/train")
	@SessionLess
	public void train(@InnerBodyParam("botExport") final BotExport bot,
			@InnerBodyParam("smallTalkExport") final DtList<SmallTalkExport> smallTalkList,
			@InnerBodyParam("trainingId") final Long trainingId,
			@InnerBodyParam("modelId") final Long modelId) {
		trainerServices.trainModel(bot, smallTalkList, trainingId, modelId);
	}

	@AnonymousAccessAllowed
	@DELETE("/train")
	@SessionLess
	public void stopTrain() {
		trainerServices.stopTrain();
	}

	@AnonymousAccessAllowed
	@GET("/trainStatus")
	@SessionLess
	public TrainerInfo getTrainStatus() {
		return trainerServices.getTrainerState();
	}

	@AnonymousAccessAllowed
	@GET("/runnerStatus")
	@SessionLess
	public RunnerInfo getRunnerStatus() {
		return runnerServices.getRunnerState();
	}

	@AnonymousAccessAllowed
	@PUT("/model")
	@SessionLess
	public void putModel(@QueryParam("model") final VFile model) {
		runnerServices.loadModel(model);
	}

	@AnonymousAccessAllowed
	@GET("/model/{id}")
	@SessionLess
	public VFile getModel(@PathParam("id") final Long id) {
		return trainerServices.getModel(id);
	}

	@AnonymousAccessAllowed
	@DELETE("/model/{id}")
	@SessionLess
	public boolean delModel(@PathParam("id") final Long id) {
		return trainerServices.delModel(id);
	}

	@AnonymousAccessAllowed
	@POST("/talk")
	public void talk(final HttpServletResponse httpResponse, final IncomeMessage income) {
		final String response = runnerServices.callChatbot(income);
		doSendRawResponse(httpResponse, response);
	}

	private void doSendRawResponse(final HttpServletResponse httpResponse, final String response) {
		httpResponse.setContentType("application/json;charset=UTF-8");
		httpResponse.setContentLength(response.length());
		httpResponse.setHeader("Access-Control-Allow-Origin", "*"); // TODO : remove from here

		ServletOutputStream os;
		try {
			os = httpResponse.getOutputStream();
			os.write(response.getBytes(StandardCharsets.UTF_8));
			os.flush();
		} catch (final IOException e) {
			throw WrappedException.wrap(e);
		}
	}

}
