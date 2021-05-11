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

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.BotExport;
import io.vertigo.chatbot.commons.domain.ExecutorConfiguration;
import io.vertigo.chatbot.commons.domain.RunnerInfo;
import io.vertigo.chatbot.commons.domain.TrainerInfo;
import io.vertigo.chatbot.executor.manager.ExecutorManager;
import io.vertigo.chatbot.vega.webservice.stereotype.RequireApiKey;
import io.vertigo.vega.webservice.WebServices;
import io.vertigo.vega.webservice.stereotype.GET;
import io.vertigo.vega.webservice.stereotype.InnerBodyParam;
import io.vertigo.vega.webservice.stereotype.PUT;
import io.vertigo.vega.webservice.stereotype.PathPrefix;

@RequireApiKey
@PathPrefix("/chatbot/admin")
public class AdminWebService implements WebServices {

	@Inject
	private ExecutorManager executorManager;

	//@Inject
	//private RasaRunnerServices runnerServices;
	//
	//@Inject
	//private RasaTrainerServices trainerServices;

	@GET("/")
	public Boolean checkAlive() {
		return true;
	}

	@PUT("/model")
	public void loadModel(@InnerBodyParam("botExport") final BotExport bot,
			@InnerBodyParam("executorConfig") final ExecutorConfiguration executorConfig) {
		executorManager.loadModel(bot, executorConfig);
	}

	@GET("/trainStatus")
	public TrainerInfo getTrainStatus() {
		//return trainerServices.getTrainerState();
		return new TrainerInfo();
	}

	@GET("/runnerStatus")
	public RunnerInfo getRunnerStatus() {
		//return runnerServices.getRunnerState();
		return new RunnerInfo();
	}

}
