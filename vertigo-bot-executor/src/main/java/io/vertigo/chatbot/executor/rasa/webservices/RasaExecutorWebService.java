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

import io.vertigo.chatbot.commons.domain.SmallTalkExport;
import io.vertigo.chatbot.commons.domain.TrainerInfo;
import io.vertigo.chatbot.executor.rasa.services.RasaExecutorServices;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.lang.VSystemException;
import io.vertigo.vega.webservice.WebServices;
import io.vertigo.vega.webservice.stereotype.AnonymousAccessAllowed;
import io.vertigo.vega.webservice.stereotype.DELETE;
import io.vertigo.vega.webservice.stereotype.GET;
import io.vertigo.vega.webservice.stereotype.InnerBodyParam;
import io.vertigo.vega.webservice.stereotype.POST;
import io.vertigo.vega.webservice.stereotype.PathPrefix;
import io.vertigo.vega.webservice.stereotype.SessionLess;

@PathPrefix("/chatbot")
public class RasaExecutorWebService implements WebServices {

	@Inject
	private RasaExecutorServices rasaExecutorServices;

	@AnonymousAccessAllowed
	@POST("/train")
	@SessionLess
	public void train(@InnerBodyParam("export") final DtList<SmallTalkExport> data,
			@InnerBodyParam("id") final Long id) {
		rasaExecutorServices.trainModel(data, id);
	}

	@AnonymousAccessAllowed
	@GET("/state")
	@SessionLess
	public TrainerInfo getState() {
		return rasaExecutorServices.getState();
	}

	@AnonymousAccessAllowed
	@GET("/model/{}")
	@SessionLess
	public VFile getModel(final Long id) {
		return rasaExecutorServices.getModel(id);
	}

	@AnonymousAccessAllowed
	@DELETE("/model/{}")
	@SessionLess
	public boolean delModel(final Long id) {
		return rasaExecutorServices.delModel(id);
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
