package io.vertigo.chatbot.executor.rasa.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.ExecutorConfiguration;
import io.vertigo.chatbot.commons.domain.RunnerInfo;
import io.vertigo.chatbot.executor.manager.ExecutorConfigManager;
import io.vertigo.chatbot.executor.model.IncomeMessage;
import io.vertigo.chatbot.executor.rasa.bridge.RunnerRasaHandler;
import io.vertigo.chatbot.executor.rasa.model.RasaInputMessage;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.component.Activeable;
import io.vertigo.core.component.Component;
import io.vertigo.core.param.ParamManager;
import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.lang.VSystemException;
import io.vertigo.vega.engines.webservice.json.JsonEngine;

@Transactional
public class RasaRunnerServices implements Component, Activeable {

	@Inject
	private RunnerRasaHandler runnerRasaHandler;

	@Inject
	private JsonEngine jsonEngine;

	@Inject
	private ParamManager paramManager;

	@Inject
	private ExecutorConfigManager executorConfigManager;

	private String rasaURL;

	@Override
	public void start() {
		rasaURL = paramManager.getParam("rasaUrl").getValueAsString() + "/webhooks/rest/webhook";
	}

	@Override
	public void stop() {
		// Nothing
	}

	public RunnerInfo getRunnerState() {
		return runnerRasaHandler.getState();
	}

	public void loadModel(final VFile model, final ExecutorConfiguration config) {
		executorConfigManager.loadConfig(config);
		runnerRasaHandler.loadModel(model);
	}

	public String callChatbot(final IncomeMessage incomeMessage) {
		final RasaInputMessage rasaInputMessage = new RasaInputMessage();
		rasaInputMessage.setMessage(incomeMessage.getMessage());
		rasaInputMessage.setSender(incomeMessage.getSender()); // TODO : mécanisme a revoir, par ex ID de la session Tomcat


		return doCallRasa(rasaInputMessage);

	}

	private String doCallRasa(final RasaInputMessage rasaInputMessage) {
		HttpURLConnection con;
		try {
			con = (HttpURLConnection) new URL(rasaURL).openConnection();

			con.setRequestMethod("POST");
			con.setDoOutput(true);
			final OutputStream os = con.getOutputStream();
			os.write(jsonEngine.toJson(rasaInputMessage).getBytes(StandardCharsets.UTF_8));
			os.flush();
			os.close();

			final int responseCode = con.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) { // success
				String inputLine;
				final StringBuilder response = new StringBuilder();


				try (final InputStream is= con.getInputStream();
						final InputStreamReader isr = new InputStreamReader(con.getInputStream());
						final BufferedReader bin = new BufferedReader(isr);
						) {

					while ((inputLine = bin.readLine()) != null) {
						response.append(inputLine);
					}
				}

				return response.toString();
			}

			throw new VSystemException("POST request to RASA not worked (code {0})", responseCode);
		} catch (final IOException e) {
			throw new VSystemException(e, "POST request to Rasa not worked");
		}
	}

}