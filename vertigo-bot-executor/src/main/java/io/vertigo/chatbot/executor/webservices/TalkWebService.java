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
package io.vertigo.chatbot.executor.webservices;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import io.vertigo.chatbot.engine.model.BotInput;
import io.vertigo.chatbot.engine.model.BotResponse;
import io.vertigo.chatbot.executor.manager.ExecutorManager;
import io.vertigo.chatbot.executor.model.IncomeRating;
import io.vertigo.core.lang.WrappedException;
import io.vertigo.core.param.Param;
import io.vertigo.core.param.ParamManager;
import io.vertigo.vega.webservice.WebServices;
import io.vertigo.vega.webservice.stereotype.POST;
import io.vertigo.vega.webservice.stereotype.PathParam;
import io.vertigo.vega.webservice.stereotype.PathPrefix;

@PathPrefix("/chatbot")
public class TalkWebService implements WebServices {

	@Inject
	private ParamManager paramManager;

	@Inject
	private ExecutorManager executorManager;

	@POST("/start")
	public BotResponse start(final BotInput input) {
		return executorManager.startNewConversation(input);
	}

	@POST("/talk/{sessionId}")
	public BotResponse talk(@PathParam("sessionId") final UUID sessionId, final BotInput input) {
		return executorManager.handleUserMessage(sessionId, input);
	}

	@POST("/rating")
	public void rate(final IncomeRating rating) {
		// todo
	}

	private void doSendRawResponse(final HttpServletResponse httpResponse, final String response) {

		httpResponse.setContentType("application/json;charset=UTF-8");
		httpResponse.setContentLength(response.length());

		final Optional<String> corsValue = paramManager.getOptionalParam("HTTP_CORS").map(Param::getValue); // TODO, passer en filtre
		if (corsValue.isPresent()) {
			httpResponse.setHeader("Access-Control-Allow-Origin", corsValue.get());
		}

		try (ServletOutputStream os = httpResponse.getOutputStream()) {
			os.write(response.getBytes(StandardCharsets.UTF_8));
			os.flush();
		} catch (final IOException e) {
			throw WrappedException.wrap(e);
		}
	}

}
