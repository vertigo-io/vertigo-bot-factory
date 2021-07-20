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
package io.vertigo.chatbot.designer.builder.controllers.bot;

import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.commons.domain.TrainerInfo;
import io.vertigo.chatbot.commons.domain.Training;
import io.vertigo.chatbot.commons.multilingual.model.ModelMultilingualResources;
import io.vertigo.chatbot.designer.builder.services.BotConversationServices;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.designer.builder.services.TrainerInfoServices;
import io.vertigo.chatbot.designer.builder.services.TrainingServices;
import io.vertigo.chatbot.designer.utils.BotConversationUtils;
import io.vertigo.chatbot.designer.utils.HttpRequestUtils;
import io.vertigo.chatbot.designer.utils.ObjectConvertionUtils;
import io.vertigo.chatbot.engine.model.BotResponse;
import io.vertigo.chatbot.engine.model.TalkInput;
import io.vertigo.core.lang.VUserException;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;

@Controller
@RequestMapping("/bot/{botId}/models")
@Secured("BotUser")
public class ModelListController extends AbstractBotController {

	private static final ViewContextKey<TrainerInfo> trainerStateKey = ViewContextKey.of("trainerState");

	private static final ViewContextKey<Boolean> autoscrollKey = ViewContextKey.of("autoscroll");

	private static final ViewContextKey<Training> trainingListKey = ViewContextKey.of("trainingList");

	private static final ViewContextKey<ChatbotNode> nodeListKey = ViewContextKey.of("nodeList");

	private static final ViewContextKey<Training> deployedTrainingKey = ViewContextKey.of("deployedTraining");

	@Inject
	private TrainingServices trainingServices;

	@Inject
	private TrainerInfoServices trainerInfoServices;

	@Inject
	private NodeServices nodeServices;

	@Inject
	private BotConversationServices botConversationServices;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, @PathVariable("botId") final Long botId) {
		final Chatbot bot = initCommonContext(viewContext, botId);

		viewContext.publishRef(autoscrollKey, Boolean.TRUE);

		refreshTrainerState(viewContext, bot, new TrainerInfo());
		refreshTrainings(viewContext, bot);
		final Optional<Training> deployedTrainingOpt = trainingServices.getDeployedTraining(bot);

		if (deployedTrainingOpt.isPresent()) {
			viewContext.publishDto(deployedTrainingKey, deployedTrainingOpt.get());
		} else {
			viewContext.publishDto(deployedTrainingKey, new Training());
		}
		toModeReadOnly();
	}

	@PostMapping("/_refreshTrainer")
	public ViewContext refreshTrainerState(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot, @ViewAttribute("trainerState") final TrainerInfo trainerInfo) {
		final TrainerInfo state = trainerInfoServices.getTrainingState(bot, trainerInfo);
		viewContext.publishDto(trainerStateKey, state);

		return viewContext;
	}

	@PostMapping("/_refreshTrainings")
	public ViewContext refreshTrainings(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot) {
		viewContext.publishDtList(trainingListKey, trainingServices.getAllTrainings(bot));

		viewContext.publishDtList(nodeListKey, nodeServices.getNodesByBot(bot));
		return viewContext;
	}

	@PostMapping("/_refreshTrainingInfo")
	public ViewContext doUpdateTrainingInfo(final ViewContext viewContext) {

		viewContext.publishDto(trainerStateKey, new TrainerInfo());
		return viewContext;
	}

	@PostMapping("/_train")
	public ViewContext doTrain(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot,
			@RequestParam("nodId") final Long nodId) {
		final Training deployedTraining = trainingServices.trainAgent(bot, nodId);
		viewContext.publishDto(trainerStateKey, trainerInfoServices.createTrainingState(bot));
		viewContext.publishDto(deployedTrainingKey, deployedTraining);
		return viewContext;
	}

	@PostMapping("/_talk")
	@ResponseBody
	public String talk(
			@ViewAttribute("nodeList") final DtList<ChatbotNode> nodeList,
			@RequestBody final String input) {

		final ChatbotNode devNode = nodeServices.getDevNodeFromList(nodeList);
		final TalkInput talkInput = ObjectConvertionUtils.jsonToObject(input, TalkInput.class);
		final String botInput = BotConversationUtils.createBotInput(talkInput);
		return HttpRequestUtils.postToUrl(devNode.getUrl() + "/api/chatbot/talk/" + talkInput.getSender(), botInput.getBytes());
	}

	@PostMapping("/_start")
	@ResponseBody
	public BotResponse start(
			final ViewContext viewContext,
			@ViewAttribute("nodeList") final DtList<ChatbotNode> nodeList) {

		final ChatbotNode devNode = nodeList.stream()
				.filter(ChatbotNode::getIsDev)
				.findFirst()
				.orElseThrow(() -> new VUserException(ModelMultilingualResources.MISSING_NODE_ERROR));
		final BodyPublisher publisher = BodyPublishers.ofByteArray(botConversationServices.createBotInput("").getBytes());
		final HttpRequest request = HttpRequestUtils.createPostRequest(devNode.getUrl() + "/api/chatbot/start", publisher);
		final HttpResponse<String> result = HttpRequestUtils.sendRequest(null, request, BodyHandlers.ofString(), 200);
		return botConversationServices.jsonToObject(result.body(), BotResponse.class);
	}

}
