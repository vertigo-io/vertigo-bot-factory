package io.vertigo.chatbot.executor.webservices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import io.vertigo.lang.VSystemException;
import io.vertigo.vega.webservice.WebServices;
import io.vertigo.vega.webservice.stereotype.AnonymousAccessAllowed;
import io.vertigo.vega.webservice.stereotype.POST;

public class ChatbotWebService implements WebServices {

	@AnonymousAccessAllowed
	@POST("/chatbot")
	public void chatbot(String requestParam, HttpServletResponse httpResponse) throws IOException {
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
