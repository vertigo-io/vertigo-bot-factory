/**
 * vertigo - simple java starter
 *
 * Copyright (C) 2020, Vertigo.io, team@vertigo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import io.vertigo.commons.analytics.AnalyticsManager;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.component.Component;
import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.lang.VSystemException;
import io.vertigo.vega.engines.webservice.json.JsonEngine;

@Transactional
public class RasaRunnerServices implements Component {

	@Inject
	private RunnerRasaHandler runnerRasaHandler;

	@Inject
	private JsonEngine jsonEngine;

	@Inject
	private ExecutorConfigManager executorConfigManager;

	@Inject
	private AnalyticsManager analyticsManager;

	public RunnerInfo getRunnerState() {
		return runnerRasaHandler.getState();
	}

	public void loadModel(final VFile model, final ExecutorConfiguration config) {
		analyticsManager.trace(
				"chatbotevents",
				"loadModel",
				tracer -> {
					tracer
					.addTag("type", "loadModel")
					.addTag("botId", String.valueOf(config.getBotId()))
					.addTag("nodId", String.valueOf(config.getNodId()))
					.addTag("traId", String.valueOf(config.getTraId()))
					.addTag("modelName", config.getModelName())
					.setMeasure("nluThreshold", config.getNluThreshold().doubleValue());

					executorConfigManager.saveConfig(config);
					runnerRasaHandler.loadModel(model);
				});
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
			con = (HttpURLConnection) new URL(runnerRasaHandler.getRasaURL() + "/webhooks/rest/webhook").openConnection();

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