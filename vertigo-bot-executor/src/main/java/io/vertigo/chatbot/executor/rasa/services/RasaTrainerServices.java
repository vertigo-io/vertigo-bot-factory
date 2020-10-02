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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertigo.chatbot.commons.JaxrsProvider;
import io.vertigo.chatbot.commons.domain.BotExport;
import io.vertigo.chatbot.commons.domain.ExecutorTrainingCallback;
import io.vertigo.chatbot.commons.domain.SmallTalkExport;
import io.vertigo.chatbot.commons.domain.TrainerInfo;
import io.vertigo.chatbot.executor.rasa.bridge.TrainerRasaHandler;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.param.ParamManager;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datastore.filestore.model.VFile;

@Transactional
public class RasaTrainerServices implements Component, Activeable {

	protected static final Logger LOGGER = LogManager.getLogger("rasa");

	@Inject
	private TrainerRasaHandler trainerRasaHandler;

	@Inject
	private JaxrsProvider jaxrsProvider;

	@Inject
	private ParamManager paramManager;

	private WebTarget designerTarget;

	@Override
	public void start() {
		final String designerUrl = paramManager.getParam("DESIGNER_URL").getValueAsString();

		LOGGER.info("Using designerUrl {}", designerUrl);

		designerTarget = jaxrsProvider.getWebTarget(designerUrl);
	}

	@Override
	public void stop() {
		// Nothing
	}

	public void trainModel(final BotExport bot, final DtList<SmallTalkExport> smallTalkList, final Long trainingId, final Long modelId, final BigDecimal nluThreshold) {
		if (trainerRasaHandler.isTraining()) {
			throw new VUserException("Node already training a model.");
		}

		trainerRasaHandler.trainModel(bot, smallTalkList, modelId, nluThreshold, isSuccess -> {
			final ExecutorTrainingCallback executorTrainingCallback = new ExecutorTrainingCallback();
			executorTrainingCallback.setTrainingId(trainingId);
			executorTrainingCallback.setSuccess(isSuccess);
			executorTrainingCallback.setLog(trainerRasaHandler.getTrainingLog());
			executorTrainingCallback.setInfos(getTrainInfos());
			executorTrainingCallback.setWarnings(getTrainWarns());
			executorTrainingCallback.setApiKey(paramManager.getParam("API_KEY").getValue());

			final Map<String, Object> requestData = new HashMap<>();
			requestData.put("executorTrainingCallback", executorTrainingCallback);

			try {
				final Response response = designerTarget.path("/api/trainingCallback")
						.request(MediaType.APPLICATION_JSON)
						.post(Entity.json(requestData));

				if (response.getStatus() != 204) {
					LOGGER.error("Can't send trained model ! {}", response.getStatusInfo());
				}
			} catch (final Exception e) {
				LOGGER.error("Can't send trained model !", e);
			}
		});
	}

	private String getTrainInfos() {
		final long coreAccuracy = trainerRasaHandler.getCoreAccuracy();
		final long nluAccuracy = trainerRasaHandler.getNluAccuracy();

		final StringBuilder trainInfos = new StringBuilder();
		if (coreAccuracy != -1) {
			trainInfos.append("Core accuracy : ").append(coreAccuracy).append("%\n");
		}
		if (nluAccuracy != -1) {
			trainInfos.append("Nlu accuracy : ").append(nluAccuracy).append("%\n");
		}
		return trainInfos.toString();
	}

	private String getTrainWarns() {
		final long coreAccuracy = trainerRasaHandler.getCoreAccuracy();
		final long nluAccuracy = trainerRasaHandler.getNluAccuracy();

		final StringBuilder trainWarns = new StringBuilder();
		if (coreAccuracy != -1 && coreAccuracy < 90) {
			trainWarns.append("Warning, Core accuracy is low (").append(coreAccuracy).append("%), consider modify training parameters.\n");
		}
		if (nluAccuracy != -1 && nluAccuracy < 90) {
			trainWarns.append("Warning, NLU accuracy is low (").append(nluAccuracy).append("%), consider modify training parameters.\n");
		}
		return trainWarns.toString();
	}

	public TrainerInfo getTrainerState() {
		return trainerRasaHandler.getState();
	}

	public VFile getModel(final Long id) {
		return trainerRasaHandler.getModel(id);
	}

	@Deprecated
	public boolean delModel(final Long id) {
		// TODO faire un clean automatique des anciens modèles du répertoire (sauf le dernier pour conserver l'optim de rasa)
		return trainerRasaHandler.delModel(id);
	}

	public void stopTrain() {
		trainerRasaHandler.stopTrain();
	}

}
