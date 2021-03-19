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
package io.vertigo.chatbot.designer.builder.services;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;

import io.vertigo.chatbot.commons.JaxrsProvider;
import io.vertigo.chatbot.commons.dao.ChatbotNodeDAO;
import io.vertigo.chatbot.commons.dao.TrainingDAO;
import io.vertigo.chatbot.commons.domain.BotExport;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.commons.domain.ExecutorConfiguration;
import io.vertigo.chatbot.commons.domain.ExecutorTrainingCallback;
import io.vertigo.chatbot.commons.domain.NluTrainingSentence;
import io.vertigo.chatbot.commons.domain.ResponseButton;
import io.vertigo.chatbot.commons.domain.RunnerInfo;
import io.vertigo.chatbot.commons.domain.SmallTalk;
import io.vertigo.chatbot.commons.domain.SmallTalkExport;
import io.vertigo.chatbot.commons.domain.TrainerInfo;
import io.vertigo.chatbot.commons.domain.Training;
import io.vertigo.chatbot.commons.domain.UtterText;
import io.vertigo.chatbot.designer.builder.training.TrainingPAO;
import io.vertigo.chatbot.designer.commons.services.FileServices;
import io.vertigo.chatbot.domain.DtDefinitions.ChatbotNodeFields;
import io.vertigo.chatbot.domain.DtDefinitions.TrainingFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.definitions.DtField;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.util.DtObjectUtil;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.datastore.impl.filestore.model.StreamFile;

@Transactional
public class TrainingServices implements Component {

	@Inject
	private SmallTalkServices smallTalkServices;

	@Inject
	private ResponsesButtonServices responsesButtonServices;

	@Inject
	private UtterTextServices utterTextServices;

	@Inject
	private FileServices fileServices;

	@Inject
	private TrainingDAO trainingDAO;

	@Inject
	private TrainingPAO trainingPAO;

	@Inject
	private ChatbotNodeDAO chatbotNodeDAO;

	@Inject
	private JaxrsProvider jaxrsProvider;

	@Inject
	private NodeServices nodeServices;

	private static final Logger LOGGER = LogManager.getLogger(TrainingServices.class);

	public Training trainAgent(final Chatbot bot) {
		final Long botId = bot.getBotId();
		trainingPAO.cleanOldTrainings(botId);

		final Long versionNumber = trainingPAO.getNextModelNumber(botId);

		final ChatbotNode devNode = nodeServices.getDevNodeByBotId(botId)
				.orElseThrow(() -> new VUserException("No training node configured"));

		final Training training = new Training();
		training.setBotId(botId);
		training.setStartTime(Instant.now());
		training.setStatus("TRAINING");
		training.setVersionNumber(versionNumber);
		training.setNluThreshold(BigDecimal.valueOf(0.6));

		saveTraining(training);

		final Map<String, Object> requestData = new HashMap<>();
		requestData.put("botExport", exportBot(bot));
		requestData.put("smallTalkExport", exportSmallTalk(botId));
		requestData.put("trainingId", training.getTraId());
		requestData.put("modelId", versionNumber);
		requestData.put("nluThreshold", training.getNluThreshold());

		final Response response = jaxrsProvider.getWebTarget(devNode.getUrl()).path("/api/chatbot/admin/train")
				.request(MediaType.APPLICATION_JSON_TYPE)
				.header("apiKey", devNode.getApiKey())
				.post(Entity.json(requestData));

		if (response.getStatus() != 204) {
			throw new VUserException(getMessageFromVUserResponse(response));
		}

		return training;
	}

	@SuppressWarnings("unchecked")
	private String getMessageFromVUserResponse(final Response response) {
		return ((List<String>) response.readEntity(Map.class).get("globalErrors")).get(0);
	}

	public void stopAgent(final Long botId) {
		final ChatbotNode devNode = nodeServices.getDevNodeByBotId(botId).get();

		jaxrsProvider.getWebTarget(devNode.getUrl()).path("/api/chatbot/admin/train")
				.request(MediaType.APPLICATION_JSON)
				.header("apiKey", devNode.getApiKey())
				.delete();

	}

	public TrainerInfo getTrainingState(final Long botId) {
		final Optional<ChatbotNode> optDevNode = nodeServices.getDevNodeByBotId(botId);

		if (!optDevNode.isPresent()) {
			final TrainerInfo trainerInfo = new TrainerInfo();
			trainerInfo.setName("No training node configured");
			return trainerInfo;
		}
		final ChatbotNode devNode = optDevNode.get();

		String error = null;
		Response response = null;
		TrainerInfo retour = null;

		try {
			response = jaxrsProvider.getWebTarget(devNode.getUrl()).path("/api/chatbot/admin/trainStatus")
					.request(MediaType.APPLICATION_JSON)
					.header("apiKey", devNode.getApiKey())
					.get();

			error = response.getStatus() != 200 ? "Code HTTP : " + response.getStatus() : null;

			if (error == null) {
				retour = response.readEntity(TrainerInfo.class);
			}
		} catch (final Exception e) {
			error = e.getLocalizedMessage();
			LOGGER.info("Impossible d'accéder au noeud.", e);
		}

		if (error != null) {
			final TrainerInfo trainerInfo = new TrainerInfo();
			trainerInfo.setName("Node unavailable");
			trainerInfo.setTrainingState(error);
			return trainerInfo;
		}

		return retour;
	}

	public RunnerInfo getRunnerState(final Long botId) {
		final Optional<ChatbotNode> optDevNode = nodeServices.getDevNodeByBotId(botId);

		if (!optDevNode.isPresent()) {
			final RunnerInfo runnerInfo = new RunnerInfo();
			runnerInfo.setName("No training node configured");
			return runnerInfo;
		}
		final ChatbotNode devNode = optDevNode.get();

		String error = null;
		Response response = null;
		RunnerInfo retour = null;

		try {
			response = jaxrsProvider.getWebTarget(devNode.getUrl()).path("/api/chatbot/admin/runnerStatus")
					.request(MediaType.APPLICATION_JSON)
					.header("apiKey", devNode.getApiKey())
					.get();

			error = response.getStatus() != 200 ? "Code HTTP : " + response.getStatus() : null;

			if (error == null) {
				retour = response.readEntity(RunnerInfo.class);
			}
		} catch (final Exception e) {
			error = e.getLocalizedMessage();
			LOGGER.info("Impossible d'accéder au noeud.", e);
		}

		if (error != null) {
			final RunnerInfo runnerInfo = new RunnerInfo();
			runnerInfo.setName("Node unavailable");
			runnerInfo.setState(error);
			return runnerInfo;
		}

		return retour;
	}

	private BotExport exportBot(final Chatbot bot) {
		final UtterText welcomeText = utterTextServices.getWelcomeTextByBot(bot);
		final UtterText defaultText = utterTextServices.getDefaultTextByBot(bot);
		final DtList<ResponseButton> welcomeButtons = responsesButtonServices.getWelcomeButtonsByBot(bot);
		final DtList<ResponseButton> defaultButtons = responsesButtonServices.getDefaultButtonsByBot(bot);

		final BotExport retour = new BotExport();
		retour.setBot(bot);
		retour.setWelcomeText(welcomeText);
		retour.setWelcomeButtons(welcomeButtons);
		retour.setFallbackText(defaultText);
		retour.setFallbackButtons(defaultButtons);
		return retour;
	}

	private DtList<SmallTalkExport> exportSmallTalk(final Long botId) {
		final DtList<SmallTalk> smallTalks = smallTalkServices.getAllActiveSmallTalksByBotId(botId);

		final List<Long> smallTalkIds = smallTalks.stream()
				.map(SmallTalk::getSmtId)
				.collect(Collectors.toList());

		//Create map for export
		final Map<Long, DtList<NluTrainingSentence>> trainingSentencesMap = smallTalkServices.exportSmallTalkRelativeTrainingSentence(smallTalkIds);
		final Map<Long, DtList<UtterText>> utterTextsMap = utterTextServices.exportSmallTalkRelativeUtter(smallTalkIds);
		final Map<Long, DtList<ResponseButton>> buttonsMap = responsesButtonServices.exportSmallTalkRelativeButtons(smallTalkIds);

		//create the smallTalkExport
		return smallTalkServices.exportSmallTalks(smallTalks, trainingSentencesMap, utterTextsMap, buttonsMap);
	}

	public void loadModel(final Long traId, final Long nodId) {
		Assertion.check()
				.isNotNull(traId)
				.isNotNull(nodId);

		final Training training = getTraining(traId);
		final ChatbotNode node = nodeServices.getNodeByNodeId(nodId);

		Assertion.check().isTrue(training.getBotId().equals(node.getBotId()), "Incohérence des paramètres");

		final VFile model = fileServices.getFile(training.getFilIdModel());

		doLoadModel(training, model, node);

		// update node-training link
		node.setTraId(traId);
		nodeServices.save(node);
	}

	private void doLoadModel(final Training training, final VFile model, final ChatbotNode node) {
		final ExecutorConfiguration config = new ExecutorConfiguration();
		config.setBotId(node.getBotId());
		config.setNodId(node.getNodId());
		config.setTraId(training.getTraId());
		config.setModelName(training.getVersionNumber().toString());
		config.setNluThreshold(training.getNluThreshold());

		final Response response;
		try (final FormDataMultiPart fdmp = new FormDataMultiPart()) {
			final StreamDataBodyPart modelBodyPart = new StreamDataBodyPart("model", model.createInputStream(), model.getFileName());
			fdmp.bodyPart(modelBodyPart);

			addObjectToMultipart(fdmp, "config", config);

			response = jaxrsProvider.getWebTarget(node.getUrl()).path("/api/chatbot/admin/model")
					.request(MediaType.APPLICATION_JSON)
					.header("apiKey", node.getApiKey())
					.put(Entity.entity(fdmp, fdmp.getMediaType()));

		} catch (final IOException e) {
			throw new VSystemException(e, "Impossible de lire le modèle");
		}

		if (response.getStatus() != 204) {
			LOGGER.info("Impossible de charger le modèle. {}", response.getStatusInfo());
			throw new VUserException("Impossible de charger le modèle");
		}
	}

	private void addObjectToMultipart(final FormDataMultiPart fdmp, final String name, final DtObject dto) {
		final DtDefinition def = DtObjectUtil.findDtDefinition(dto);

		for (final DtField field : def.getFields()) {
			final Object value = field.getDataAccessor().getValue(dto);

			if (value != null) {
				// TODO: date handling ?
				fdmp.field(name + '.' + field.getName(), value.toString());
			}
		}
	}

	public DtList<Training> getAllTrainings(final Long botId) {
		return trainingDAO.findAll(
				Criterions.isEqualTo(TrainingFields.botId, botId),
				DtListState.of(1000, 0, TrainingFields.versionNumber.name(), true));
	}

	public Training getTraining(final Long traId) {
		return trainingDAO.get(traId);
	}

	public Training saveTraining(final Training training) {
		return trainingDAO.save(training);
	}

	public void removeTraining(final Long traId) {
		trainingDAO.delete(traId);
	}

	public void trainingCallback(final ExecutorTrainingCallback callback) {
		final Training training = getTraining(callback.getTrainingId());

		// final ChatbotNode node = designerServices.getDevNodeByBotId(training.getBotId()).get(); // FIXME : can't be used because of security
		final ChatbotNode node = chatbotNodeDAO.findOptional(
				Criterions.isEqualTo(ChatbotNodeFields.botId, training.getBotId())
						.and(Criterions.isEqualTo(ChatbotNodeFields.isDev, true)))
				.get();

		Assertion.check().isTrue(node.getApiKey().equals(callback.getApiKey()), "Access denied");

		// TODO : Limiter au dernier en cours en mode "trop tard, je refuse ton callback" ?

		if (Boolean.TRUE.equals(callback.getSuccess())) {
			final VFile model = fetchModel(node, training.getVersionNumber());
			final FileInfoURI fileInfoUri = fileServices.saveFile(model);
			training.setFilIdModel((Long) fileInfoUri.getKey());

			training.setStatus("OK");
		} else {
			training.setStatus("KO");
		}

		training.setLog(callback.getLog());
		training.setInfos(callback.getInfos());
		training.setWarnings(callback.getWarnings());
		training.setEndTime(Instant.now());

		saveTraining(training);
	}

	private VFile fetchModel(final ChatbotNode node, final Long modelVersion) {
		final Response response = jaxrsProvider.getWebTarget(node.getUrl()).path("/api/chatbot/admin/model/" + modelVersion)
				.request(MediaType.APPLICATION_OCTET_STREAM)
				.header("apiKey", node.getApiKey())
				.get();

		response.bufferEntity();

		return new StreamFile(modelVersion + ".tar.gz", response.getHeaderString("Content-Type"), Instant.now(), response.getLength(), () -> response.readEntity(InputStream.class));
	}

	public void removeAllTraining(final Chatbot bot) {
		final Long botId = bot.getBotId();
		final List<Long> filesId = trainingPAO.getAllTrainingFilIdsByBotId(botId);
		trainingPAO.removeTrainingByBotId(botId);
		trainingPAO.removeTrainingFileByFilIds(filesId);
	}

}
