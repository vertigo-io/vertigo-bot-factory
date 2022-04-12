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

import io.vertigo.chatbot.engine.model.BotInput;
import io.vertigo.chatbot.engine.model.BotResponse;
import io.vertigo.chatbot.executor.manager.ExecutorManager;
import io.vertigo.chatbot.executor.model.IncomeRating;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.vega.webservice.WebServices;
import io.vertigo.vega.webservice.stereotype.GET;
import io.vertigo.vega.webservice.stereotype.POST;
import io.vertigo.vega.webservice.stereotype.PathParam;
import io.vertigo.vega.webservice.stereotype.PathPrefix;
import io.vertigo.vega.webservice.stereotype.QueryParam;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

@PathPrefix("/chatbot")
public class TalkWebService implements WebServices {

	@Inject
	private ExecutorManager executorManager;

	@POST("/start")
	public BotResponse start(final BotInput input) {
		return executorManager.startNewConversation(input);
	}

	@POST("/context")
	public Map<String, String> getContext() {
		return executorManager.getContext();
	}

	@POST("/talk/{sessionId}")
	public BotResponse talk(@PathParam("sessionId") final UUID sessionId, final BotInput input) {
		return executorManager.handleUserMessage(sessionId, input);
	}

	@POST("/rating")
	public void rate(final IncomeRating rating) {
		executorManager.rate(rating);
	}

	@GET("/getAttachment")
	public VFile getAttachment(@QueryParam("label") final String label) {
		return  executorManager.getAttachment(label);
	}

}
