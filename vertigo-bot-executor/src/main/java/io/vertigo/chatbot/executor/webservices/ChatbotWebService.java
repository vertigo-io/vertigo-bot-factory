package io.vertigo.chatbot.executor.webservices;

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

import io.vertigo.chatbot.commons.domain.IntentExport;
import io.vertigo.chatbot.executor.services.ChatbotServices;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.lang.VSystemException;
import io.vertigo.vega.webservice.WebServices;
import io.vertigo.vega.webservice.stereotype.AnonymousAccessAllowed;
import io.vertigo.vega.webservice.stereotype.GET;
import io.vertigo.vega.webservice.stereotype.POST;
import io.vertigo.vega.webservice.stereotype.PathPrefix;
import io.vertigo.vega.webservice.stereotype.SessionLess;

@PathPrefix("/chatbot")
public class ChatbotWebService implements WebServices {

	@Inject
	private ChatbotServices chatbotServices;
	
	@AnonymousAccessAllowed
	@POST("/train")
	@SessionLess
	public boolean train(DtList<IntentExport> data) {
		System.out.println(data);
//		chatbotServices.trainModel();
		
		return true;
	}
	
	@AnonymousAccessAllowed
	@GET("/trainLog")
	@SessionLess
	public String trainLog() {
		return chatbotServices.getTrainingLog();
	}
	
	@AnonymousAccessAllowed
	@POST("/talk")
	@SessionLess
	public void talk(String requestParam, HttpServletResponse httpResponse) throws IOException {
		String response = callRasa(requestParam);

		doSendRawResponse(httpResponse, response);
	}

	private String callRasa(String param) throws IOException {
		String rasaURL = "http://localhost:5005/webhooks/rest/webhook";
		HttpURLConnection con = (HttpURLConnection) new URL(rasaURL).openConnection();

		con.setRequestMethod("POST");
		con.setDoOutput(true);
		OutputStream os = con.getOutputStream();
		os.write(param.getBytes(StandardCharsets.UTF_8));
		os.flush();
		os.close();

		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			return response.toString();
		}
		
		throw new VSystemException("POST request not worked (code {0})", responseCode);
	}

	private void doSendRawResponse(HttpServletResponse httpResponse, String response) throws IOException {
		httpResponse.setContentType("application/json;charset=UTF-8");
		httpResponse.setContentLength(response.length());
		httpResponse.setHeader("Access-Control-Allow-Origin", "*"); // TODO : remove from here

		ServletOutputStream os = httpResponse.getOutputStream();
		os.write(response.getBytes(StandardCharsets.UTF_8));
		os.flush();
	}

}
