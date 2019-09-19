package io.vertigo.chatbot.executor.rasa.webservices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import io.vertigo.chatbot.commons.domain.BotExport;
import io.vertigo.chatbot.commons.domain.RunnerInfo;
import io.vertigo.chatbot.commons.domain.SmallTalkExport;
import io.vertigo.chatbot.commons.domain.TrainerInfo;
import io.vertigo.chatbot.executor.rasa.services.RasaRunnerServices;
import io.vertigo.chatbot.executor.rasa.services.RasaTrainerServices;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.lang.VSystemException;
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
public class RasaExecutorWebService implements WebServices {

	@Inject
	private RasaRunnerServices rasaRunnerServices;

	@Inject
	private RasaTrainerServices rasaTrainerServices;

	@AnonymousAccessAllowed
	@POST("/train")
	@SessionLess
	public void train(@InnerBodyParam("botExport") final BotExport bot,
			@InnerBodyParam("smallTalkExport") final DtList<SmallTalkExport> smallTalkList,
			@InnerBodyParam("trainingId") final Long trainingId,
			@InnerBodyParam("modelId") final Long modelId) {
		rasaTrainerServices.trainModel(bot, smallTalkList, trainingId, modelId);
	}

	@AnonymousAccessAllowed
	@DELETE("/train")
	@SessionLess
	public void stopTrain() {
		rasaTrainerServices.stopTrain();
	}

	@AnonymousAccessAllowed
	@GET("/trainStatus")
	@SessionLess
	public TrainerInfo getTrainStatus() {
		return rasaTrainerServices.getTrainerState();
	}

	@AnonymousAccessAllowed
	@GET("/runnerStatus")
	@SessionLess
	public RunnerInfo getRunnerStatus() {
		return rasaRunnerServices.getRunnerState();
	}

	@AnonymousAccessAllowed
	@PUT("/model")
	@SessionLess
	public void putModel(@QueryParam("model") final VFile model) {
		rasaRunnerServices.loadModel(model);
	}

	@AnonymousAccessAllowed
	@GET("/model/{id}")
	@SessionLess
	public VFile getModel(@PathParam("id") final Long id) {
		return rasaTrainerServices.getModel(id);
	}

	@AnonymousAccessAllowed
	@DELETE("/model/{id}")
	@SessionLess
	public boolean delModel(@PathParam("id") final Long id) {
		return rasaTrainerServices.delModel(id);
	}

	@AnonymousAccessAllowed
	@POST("/talk")
	public void talk(final String requestParam, final HttpServletResponse httpResponse) throws IOException {
		final String response = callRasa(requestParam);

		doSendRawResponse(httpResponse, response);
	}

	private String callRasa(final String param) throws IOException {
		final String rasaURL = "http://localhost:5005/webhooks/rest/webhook";
		final HttpURLConnection con = (HttpURLConnection) new URL(rasaURL).openConnection();

		con.setRequestMethod("POST");
		con.setDoOutput(true);
		final OutputStream os = con.getOutputStream();
		os.write(param.getBytes(StandardCharsets.UTF_8));
		os.flush();
		os.close();

		final int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			final StringBuilder response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			return response.toString();
		}

		throw new VSystemException("POST request not worked (code {0})", responseCode);
	}

	private void doSendRawResponse(final HttpServletResponse httpResponse, final String response) throws IOException {
		httpResponse.setContentType("application/json;charset=UTF-8");
		httpResponse.setContentLength(response.length());
		httpResponse.setHeader("Access-Control-Allow-Origin", "*"); // TODO : remove from here

		final ServletOutputStream os = httpResponse.getOutputStream();
		os.write(response.getBytes(StandardCharsets.UTF_8));
		os.flush();
	}

}
