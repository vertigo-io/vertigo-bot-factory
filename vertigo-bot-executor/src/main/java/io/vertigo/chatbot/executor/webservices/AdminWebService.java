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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

import io.vertigo.ai.nlu.NluManager;
import io.vertigo.chatbot.commons.LogsUtils;
import io.vertigo.chatbot.commons.domain.AttachmentExport;
import io.vertigo.chatbot.commons.domain.BotExport;
import io.vertigo.chatbot.commons.domain.ExecutorConfiguration;
import io.vertigo.chatbot.commons.domain.RunnerHealthCheck;
import io.vertigo.chatbot.commons.domain.RunnerInfo;
import io.vertigo.chatbot.commons.domain.TrainerInfo;
import io.vertigo.chatbot.executor.manager.ExecutorManager;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.vega.webservice.WebServices;
import io.vertigo.vega.webservice.stereotype.GET;
import io.vertigo.vega.webservice.stereotype.InnerBodyParam;
import io.vertigo.vega.webservice.stereotype.PUT;
import io.vertigo.vega.webservice.stereotype.PathPrefix;
import io.vertigo.vega.webservice.stereotype.RequireApiKey;

@RequireApiKey
@PathPrefix("/chatbot/admin")
public class AdminWebService implements WebServices {

	@Inject
	private ExecutorManager executorManager;

	@Inject
	private NluManager nluManager;

	private static final Logger LOGGER = LogManager.getLogger(AdminWebService.class);

	@GET("/")
	public RunnerHealthCheck checkAlive() {
		final RunnerHealthCheck runnerHealthCheck = new RunnerHealthCheck();
		runnerHealthCheck.setAlive(true);
		runnerHealthCheck.setNlpReady(nluManager.isAlive(NluManager.DEFAULT_ENGINE_NAME));
		return runnerHealthCheck;
	}

	@PUT("/model")
	public String loadModel(@InnerBodyParam("botExport") final BotExport bot,
			@InnerBodyParam("attachmentsExport") final DtList<AttachmentExport> attachmentExports,
			@InnerBodyParam("executorConfig") final ExecutorConfiguration executorConfig) throws Exception {
		final StringBuilder logs = new StringBuilder();
		LogsUtils.breakLine(logs);
		LogsUtils.breakLine(logs);
		LogsUtils.addLogs(logs, "Executor logs");
		LogsUtils.breakLine(logs);
		try {
			executorManager.loadModel(bot, executorConfig, logs);
			executorManager.updateAttachments(attachmentExports);
		} catch (final Exception e) {
			LogsUtils.logKO(logs);
			LogsUtils.addLogs(logs, e);
			LOGGER.error("error", e);
			throw new VSystemException(logs.toString(), e);
		}
		return logs.toString();

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
