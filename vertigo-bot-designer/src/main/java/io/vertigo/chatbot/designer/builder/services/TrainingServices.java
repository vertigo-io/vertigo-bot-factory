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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.JaxrsProvider;
import io.vertigo.chatbot.commons.LogsUtils;
import io.vertigo.chatbot.commons.dao.TrainingDAO;
import io.vertigo.chatbot.commons.domain.AttachmentExport;
import io.vertigo.chatbot.commons.domain.BotExport;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotCustomConfigExport;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.commons.domain.ExecutorConfiguration;
import io.vertigo.chatbot.commons.domain.RunnerHealthCheck;
import io.vertigo.chatbot.commons.domain.SavedTraining;
import io.vertigo.chatbot.commons.domain.Training;
import io.vertigo.chatbot.commons.domain.TrainingStatusEnum;
import io.vertigo.chatbot.commons.multilingual.model.ModelMultilingualResources;
import io.vertigo.chatbot.designer.builder.services.topic.export.BotExportServices;
import io.vertigo.chatbot.designer.builder.training.TrainingPAO;
import io.vertigo.chatbot.designer.commons.services.DesignerFileServices;
import io.vertigo.chatbot.designer.domain.History;
import io.vertigo.chatbot.designer.domain.HistoryActionEnum;
import io.vertigo.chatbot.designer.utils.HttpRequestUtils;
import io.vertigo.chatbot.designer.utils.ObjectConvertionUtils;
import io.vertigo.chatbot.domain.DtDefinitions.TrainingFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.param.Param;
import io.vertigo.core.param.ParamManager;
import io.vertigo.datamodel.criteria.Criteria;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.definitions.DtDefinition;
import io.vertigo.datamodel.structure.definitions.DtField;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.util.DtObjectUtil;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.vega.engines.webservice.json.JsonEngine;


@Transactional
public class TrainingServices implements Component, IRecordable<Training>, Activeable {

	private static final String API_KEY = "apiKey";

	public static final int MAX_TRAINING_ELEMENTS = 50;

	@Inject
	private AsynchronousServices asynchronousServices;

	@Inject
	private BotExportServices botExportServices;

	@Inject
	private TrainingDAO trainingDAO;

	@Inject
	private TrainingPAO trainingPAO;

	@Inject
	private JaxrsProvider jaxrsProvider;

	@Inject
	private NodeServices nodeServices;

	@Inject
	private DesignerFileServices designerFileServices;

	@Inject
	private SavedTrainingServices savedTrainingServices;

	@Inject
	private JsonEngine jsonEngine;

	@Inject
	private HistoryServices historyServices;

	@Inject
	private ParamManager paramManager;

	private static final Logger LOGGER = LogManager.getLogger(TrainingServices.class);

	private static final String URL_MODEL = "/api/chatbot/admin/model";

	private static final String URL_PING = "/api/chatbot/admin/";

	private HttpClient httpClient;

	private int runnerRequestTimeOut;

	@Override
	public void start() {
		final boolean useSSL = paramManager.getOptionalParam("USE_SSL")
				.orElse(Param.of("USE_SSL", "true")).getValueAsBoolean();
		runnerRequestTimeOut = paramManager.getOptionalParam("RUNNER_REQUEST_TIMEOUT")
				.orElse(Param.of("RUNNER_REQUEST_TIMEOUT", "120")).getValueAsInt();
		if (!useSSL) {
			httpClient = HttpRequestUtils.createHttpClientWithoutSSL();
		}
	}

	@Override
	public void stop() {

	}

	public Training trainAgent(@SecuredOperation("botContributor") final Chatbot bot, final Long nodId) {
		final StringBuilder logs = new StringBuilder("new Training");
		LogsUtils.breakLine(logs);

		final Long botId = bot.getBotId();

		trainingPAO.cleanOldTrainings(botId);

		final ChatbotNode devNode = nodeServices.getDevNodeByBotId(botId)
				.orElseThrow(() -> new VUserException(ModelMultilingualResources.MISSING_NODE_ERROR));

		//Set training

		final Training training = createTraining(bot);
		saveTraining(bot, training);
		LogsUtils.addLogs(logs, "Bot export :");
		LogsUtils.breakLine(logs);
		final BotExport botExport = exportBot(bot, logs);
		botExportServices.exportConfluenceSetting(botId, devNode.getNodId()).ifPresent(botExport::setConfluenceSetting);
		botExportServices.exportJiraSetting(botId, devNode.getNodId()).ifPresent(botExport::setJiraSetting);
		final DtList<AttachmentExport> attachmentExports = botExportServices.exportBotAttachments(bot, logs);
		LogsUtils.addLogs(logs, "Bot export ");
		LogsUtils.logOK(logs);

		trainNode(bot, training, devNode, logs, botExport, attachmentExports);

		return training;
	}

	private void trainNode(final Chatbot bot, final Training training, final ChatbotNode node, final StringBuilder logs, final BotExport botExport, final DtList<AttachmentExport> attachmentExports) {
		try {
			LogsUtils.addLogs(logs, "Executor configuration... ");
			final ExecutorConfiguration execConfig = getExecutorConfig(bot, training, node);
			LogsUtils.logOK(logs);

			LogsUtils.addLogs(logs, "Bot export :");
			LogsUtils.breakLine(logs);
			final Map<String, Object> requestData = new HashMap<String, Object>();
			requestData.put("botExport", botExport);
			requestData.put("attachmentsExport", attachmentExports);
			requestData.put("executorConfig", execConfig);

			tryPing(node, training, logs);

			LogsUtils.addLogs(logs, "Call executor training (", node.getUrl(), ") :");
			LogsUtils.breakLine(logs);
			final BodyPublisher publisher = BodyPublishers.ofString(ObjectConvertionUtils.objectToJson(requestData));
			final Map<String, String> headers = Map.of(API_KEY, node.getApiKey(),
					"Content-type", "application/json");
			final HttpRequest request = HttpRequestUtils.createPutRequest(node.getUrl() + URL_MODEL, runnerRequestTimeOut, headers, publisher);
			HttpRequestUtils.sendAsyncRequest(httpClient, request, BodyHandlers.ofString())
					.thenApply(response -> {
						return handleResponse(response, training, node, bot, logs);
					});
			LogsUtils.addLogs(logs, "Call training OK, training in progress...");
		}  catch (final Exception e) {
			LogsUtils.logKO(logs);
			LogsUtils.addLogs(logs, e.getMessage());
			LOGGER.error("error", e);
			training.setWarnings(e.getMessage());
			training.setEndTime(Instant.now());
			training.setStrCd(TrainingStatusEnum.KO.name());
		} finally {
			training.setLog(logs.toString());
			saveTraining(bot, training);
		}
		record(bot, training, HistoryActionEnum.ADDED);
	}

	@Secured("BotUser")
	public void deployTraining(@SecuredOperation("botAdm") final Chatbot bot, final Long savedTrainingId, final Long nodeId) {
		final SavedTraining savedTraining = savedTrainingServices.getById(savedTrainingId);
		final Training training = getTraining(bot, savedTraining.getTraId());
		final ChatbotNode node = nodeServices.getNodeByNodeId(bot, nodeId);
		updateTraining(training);
		saveTraining(bot, training);
		final StringBuilder logs = new StringBuilder("Starting deployment of training " + training.getTraId() + " on node " + node.getName() + " ...");
		LogsUtils.breakLine(logs);
		try {
			final BotExport botExport = jsonEngine.fromJson(savedTraining.getBotExport(), BotExport.class);
			botExportServices.exportConfluenceSetting(bot.getBotId(), nodeId).ifPresent(botExport::setConfluenceSetting);
			botExportServices.exportJiraSetting(bot.getBotId(), nodeId).ifPresent(botExport::setJiraSetting);
			final DtList<AttachmentExport> attachmentExports = botExportServices.exportBotAttachments(bot, logs);
			trainNode(bot, training, node, logs, botExport, attachmentExports);
		} catch (final Exception e) {
			LogsUtils.logKO(logs);
			LogsUtils.addLogs(logs, e.getMessage());
			LOGGER.error("error", e);
		}
	}

	private void tryPing(final ChatbotNode node, final Training training, final StringBuilder logs) {
		final RunnerHealthCheck runnerHealthCheck = tryPing(node);
		if (!runnerHealthCheck.getAlive()) {
			LogsUtils.logKO(logs);
			LogsUtils.addLogs(logs, node.getUrl(), " cannot be used to train the model.");
			training.setLog(logs.toString());
			throw new VSystemException(node.getUrl() + " cannot be used to train the model.");
		}
	}

	public RunnerHealthCheck tryPing (final ChatbotNode node) {
		final Map<String, String> headers = Map.of(API_KEY, node.getApiKey(),
				"Content-type", "application/json");
		final HttpRequest requestPing = HttpRequestUtils.createGetRequest(node.getUrl() + URL_PING, headers);
		try {
			final HttpResponse<String> responsePing = HttpRequestUtils.sendRequest(httpClient, requestPing, BodyHandlers.ofString(), 200);
			return jsonEngine.fromJson(responsePing.body(), RunnerHealthCheck.class);
		}
		catch (final Exception e) {
			final RunnerHealthCheck runnerHealthCheck = new RunnerHealthCheck();
			runnerHealthCheck.setAlive(false);
			runnerHealthCheck.setNlpReady(false);
			return runnerHealthCheck;
		}
	}

	public <T> String handleResponse(final HttpResponse<T> response, final Training training, final ChatbotNode node, final Chatbot bot, final StringBuilder logs) {
		training.setEndTime(Instant.now());
		if (HttpRequestUtils.isResponseOk(response, 200)) {

			training.setStrCd(TrainingStatusEnum.OK.name());
			node.setTraId(training.getTraId());
			node.setIsUpToDate(true);
			asynchronousServices.saveNodeWithoutAuthorizations(node);
			LogsUtils.logOK(logs);
			LogsUtils.addLogs(logs, response.body());

		} else {
			training.setStrCd(TrainingStatusEnum.KO.name());
			LogsUtils.logKO(logs);
			errorTreatment(response, logs, training);

		}
		training.setLog(logs.toString());
		asynchronousServices.saveTrainingWithoutAuthorizations(training);
		return "response handled";
	}

	private <T> void errorTreatment(final HttpResponse<T> response, final StringBuilder logs, Training training) {
		if (!HttpRequestUtils.isResponseKo(response, 404, 405)) {
			errorJsonTreatment(response, logs, training);
		} else {
			LogsUtils.addLogs(logs, response.body());
		}
	}

	private <T> void errorJsonTreatment(final HttpResponse<T> response, final StringBuilder logs, Training training) {
		final ObjectMapper mapper = new ObjectMapper();
		JsonNode root = null;
		try {
			root = mapper.readTree(response.body().toString());
			final String responseString = root.get("globalErrors").get(0).toString();
			final String warningString =  responseString.substring(1, responseString.length() - 1);
			training.setWarnings(warningString);
			LogsUtils.addLogs(logs, warningString);

		} catch (final JsonProcessingException e) {
			LOGGER.info("error on deserialization");
			LogsUtils.addLogs(logs, e);
			e.printStackTrace();
		}
	}

	private Training createTraining(final Chatbot bot) {
		final Long botId = bot.getBotId();
		final Long versionNumber = trainingPAO.getNextModelNumber(botId);
		final Training training = new Training();
		training.setBotId(botId);
		training.setStartTime(Instant.now());
		training.setStrCd(TrainingStatusEnum.TRAINING.name());
		training.setVersionNumber(versionNumber);
		training.setNluThreshold(BigDecimal.valueOf(0.6));
		return training;
	}

	private Training updateTraining(final Training training) {
		training.setStartTime(Instant.now());
		training.setStrCd(TrainingStatusEnum.TRAINING.name());
		return training;
	}

	private ExecutorConfiguration getExecutorConfig(final Chatbot bot, final Training training, final ChatbotNode node) {
		final Long botId = training.getBotId();

		final ExecutorConfiguration result = new ExecutorConfiguration();
		result.setBotId(botId);
		result.setNodId(node.getNodId());
		result.setTraId(training.getTraId());
		result.setModelName("model " + training.getVersionNumber());
		result.setNluThreshold(training.getNluThreshold());
		if (bot.getFilIdAvatar() != null) {
			result.setAvatar(designerFileServices.getFileAsBase64(bot.getFilIdAvatar()));
		}
		final ChatbotCustomConfigExport chatbotCustomConfigExport = botExportServices.exportChatbotCustomSettings(botId);
		result.setCustomConfig(jsonEngine.toJson(chatbotCustomConfigExport));
		return result;
	}

	public Optional<Training> getCurrentTraining(final Chatbot bot) {
		return trainingDAO.getCurrentTrainingByBotId(bot.getBotId()).stream().findFirst();
	}

	public Optional<Training> getDeployedTraining(final Chatbot bot) {
		return trainingDAO.getDeployedTrainingByBotId(bot.getBotId());
	}

	public BotExport exportBot(@SecuredOperation("botContributor") final Chatbot bot, final StringBuilder logs) {
		return botExportServices.exportBot(bot, logs);
	}

	public void loadModel(@SecuredOperation("botContributor") final Chatbot bot, final Long traId, final Long nodId) {
		Assertion.check()
				.isNotNull(traId)
				.isNotNull(nodId);

		final Training training = getTraining(bot, traId);
		final ChatbotNode node = nodeServices.getNodeByNodeId(bot, nodId);

		Assertion.check().isTrue(training.getBotId().equals(node.getBotId()), "Incoherent parameters");

		final VFile model = designerFileServices.getFile(training.getFilIdModel());

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

			response = jaxrsProvider.getWebTarget(node.getUrl()).path(URL_MODEL)
					.request(MediaType.APPLICATION_JSON)
					.header(API_KEY, node.getApiKey())
					.put(Entity.entity(fdmp, fdmp.getMediaType()));

		} catch (final IOException e) {
			LOGGER.info("error during loading model");
			throw new VSystemException(e, "error during loading model");
		}

		if (response.getStatus() != 204) {
			LOGGER.info("Impossible to load the model. {}", response.getStatusInfo());
			throw new VUserException(ModelMultilingualResources.LOAD_MODEL_ERROR);
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
				DtListState.of(MAX_TRAINING_ELEMENTS, 0, TrainingFields.versionNumber.name(), true));
	}

	public Training getTraining(@SecuredOperation("botVisitor") final Chatbot bot, final Long traId) {
		return trainingDAO.get(traId);
	}

	public Optional<Training> getTrainingByTraIdAndBotId(final Long botId, final Long traId) {
		final Criteria<Training> criteria = Criterions.isEqualTo(TrainingFields.botId, botId).and(Criterions.isEqualTo(TrainingFields.traId, traId));
		return trainingDAO.findOptional(criteria);
	}

	public Training saveTraining(@SecuredOperation("botContributor") final Chatbot bot, final Training training) {
		return trainingDAO.save(training);
	}

	public void removeTraining(@SecuredOperation("botContributor") final Chatbot bot, final Long traId) {
		trainingDAO.delete(traId);
	}

	public void removeAllTraining(@SecuredOperation("botAdm") final Chatbot bot) {
		final Long botId = bot.getBotId();
		final List<Long> filesId = trainingPAO.getAllTrainingFilIdsByBotId(botId);
		trainingPAO.removeTrainingByBotId(botId);
		trainingPAO.removeTrainingFileByFilIds(filesId);
	}

	public Instant getInstantEndDisplay(final Long botId, final Long traId) {
		final Optional<Training> optionalTraining = getTrainingByTraIdAndBotId(botId, traId);
		return optionalTraining.isPresent() ? optionalTraining.get().getEndTime() : null;
	}

	@Override
	public History record(final Chatbot bot, final Training training, final HistoryActionEnum action) {
		return historyServices.record(bot, action, training.getClass().getSimpleName(), "Version " + training.getVersionNumber());
	}
}
