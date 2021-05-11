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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;

import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.JaxrsProvider;
import io.vertigo.chatbot.commons.dao.ChatbotNodeDAO;
import io.vertigo.chatbot.commons.dao.TrainingDAO;
import io.vertigo.chatbot.commons.domain.BotExport;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.commons.domain.ExecutorConfiguration;
import io.vertigo.chatbot.commons.domain.ExecutorTrainingCallback;
import io.vertigo.chatbot.commons.domain.RunnerInfo;
import io.vertigo.chatbot.commons.domain.TrainerInfo;
import io.vertigo.chatbot.commons.domain.Training;
import io.vertigo.chatbot.designer.builder.services.topic.export.BotExportServices;
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

	private static final String API_KEY = "apiKey";

	@Inject
	private BotExportServices botExportServices;

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

	public Training trainAgent(@SecuredOperation("botContributor") final Chatbot bot) {
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

		saveTraining(bot, training);

		final Map<String, Object> requestData = Map.of(
				"botExport", exportBot(bot),
				"trainingId", training.getTraId(),
				"modelId", versionNumber,
				"nluThreshold", training.getNluThreshold());

		final Response response = jaxrsProvider.getWebTarget(devNode.getUrl()).path("/api/chatbot/admin/train")
				.request(MediaType.APPLICATION_JSON_TYPE)
				.header(API_KEY, devNode.getApiKey())
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

	public void stopAgent(@SecuredOperation("botContributor") final Chatbot bot) {
		final ChatbotNode devNode = nodeServices.getDevNodeByBotId(bot.getBotId()).get();

		jaxrsProvider.getWebTarget(devNode.getUrl()).path("/api/chatbot/admin/train")
				.request(MediaType.APPLICATION_JSON)
				.header(API_KEY, devNode.getApiKey())
				.delete();

	}

	public TrainerInfo getTrainingState(@SecuredOperation("botContributor") final Chatbot bot) {
		final Optional<ChatbotNode> optDevNode = nodeServices.getDevNodeByBotId(bot.getBotId());

		if (!optDevNode.isPresent()) {
			final TrainerInfo trainerInfo = new TrainerInfo();
			trainerInfo.setName("No training node configured");
			return trainerInfo;
		}
		final ChatbotNode devNode = optDevNode.get();

		String error = null;
		TrainerInfo retour = null;
		try {
			final Response response = jaxrsProvider.getWebTarget(devNode.getUrl()).path("/api/chatbot/admin/trainStatus")
					.request(MediaType.APPLICATION_JSON)
					.header(API_KEY, devNode.getApiKey())
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

	public RunnerInfo getRunnerState(@SecuredOperation("botContributor") final Chatbot bot) {
		final Optional<ChatbotNode> optDevNode = nodeServices.getDevNodeByBotId(bot.getBotId());

		if (!optDevNode.isPresent()) {
			final RunnerInfo runnerInfo = new RunnerInfo();
			runnerInfo.setName("No training node configured");
			return runnerInfo;
		}
		final ChatbotNode devNode = optDevNode.get();

		String error = null;
		RunnerInfo retour = null;
		try {
			final Response response = jaxrsProvider.getWebTarget(devNode.getUrl()).path("/api/chatbot/admin/runnerStatus")
					.request(MediaType.APPLICATION_JSON)
					.header(API_KEY, devNode.getApiKey())
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

	private BotExport exportBot(@SecuredOperation("botContributor") final Chatbot bot) {
		return botExportServices.exportBot(bot);
	}

	public void loadModel(@SecuredOperation("botContributor") final Chatbot bot, final Long traId, final Long nodId) {
		Assertion.check()
				.isNotNull(traId)
				.isNotNull(nodId);

		final Training training = getTraining(bot, traId);
		final ChatbotNode node = nodeServices.getNodeByNodeId(bot, nodId);

		Assertion.check().isTrue(training.getBotId().equals(node.getBotId()), "Incoherent parameters");

		final VFile model = fileServices.getFile(training.getFilIdModel());

		doLoadModel(training, model, node);

		// update node-training link
		node.setTraId(traId);
		nodeServices.save(bot, node);
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
					.header(API_KEY, node.getApiKey())
					.put(Entity.entity(fdmp, fdmp.getMediaType()));

		} catch (final IOException e) {
			throw new VSystemException(e, "Impossible to read the model");
		}

		if (response.getStatus() != 204) {
			LOGGER.info("Impossible to load the model. {}", response.getStatusInfo());
			throw new VUserException("Impossible to load the model");
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

	public DtList<Training> getAllTrainings(@SecuredOperation("botVisitor") final Chatbot bot) {
		return trainingDAO.findAll(
				Criterions.isEqualTo(TrainingFields.botId, bot.getBotId()),
				DtListState.of(1000, 0, TrainingFields.versionNumber.name(), true));
	}

	public Training getTraining(@SecuredOperation("botVisitor") final Chatbot bot, final Long traId) {
		return trainingDAO.get(traId);
	}

	public Training saveTraining(@SecuredOperation("botContributor") final Chatbot bot, final Training training) {
		return trainingDAO.save(training);
	}

	public void removeTraining(@SecuredOperation("botContributor") final Chatbot bot, final Long traId) {
		trainingDAO.delete(traId);
	}

	public void trainingCallback(@SecuredOperation("botContributor") final Chatbot bot, final ExecutorTrainingCallback callback) {
		final Training training = getTraining(bot, callback.getTrainingId());
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

		saveTraining(bot, training);
	}

	private VFile fetchModel(final ChatbotNode node, final Long modelVersion) {
		final Response response = jaxrsProvider.getWebTarget(node.getUrl()).path("/api/chatbot/admin/model/" + modelVersion)
				.request(MediaType.APPLICATION_OCTET_STREAM)
				.header(API_KEY, node.getApiKey())
				.get();

		response.bufferEntity();

		return new StreamFile(modelVersion + ".tar.gz", response.getHeaderString("Content-Type"), Instant.now(), response.getLength(), () -> response.readEntity(InputStream.class));
	}

	public void removeAllTraining(@SecuredOperation("botAdm") final Chatbot bot) {
		final Long botId = bot.getBotId();
		final List<Long> filesId = trainingPAO.getAllTrainingFilIdsByBotId(botId);
		trainingPAO.removeTrainingByBotId(botId);
		trainingPAO.removeTrainingFileByFilIds(filesId);
	}

}
