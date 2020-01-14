package io.vertigo.chatbot.executor.webservices;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import io.vertigo.chatbot.executor.model.IncomeMessage;
import io.vertigo.chatbot.executor.rasa.services.RasaRunnerServices;
import io.vertigo.lang.WrappedException;
import io.vertigo.vega.webservice.WebServices;
import io.vertigo.vega.webservice.stereotype.POST;
import io.vertigo.vega.webservice.stereotype.PathPrefix;

@PathPrefix("/chatbot")
public class TalkWebService implements WebServices {

	@Inject
	private RasaRunnerServices runnerServices;

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
