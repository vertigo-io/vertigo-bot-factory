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

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.vertigo.chatbot.commons.ChatbotUtils;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.commons.domain.RunnerInfo;
import io.vertigo.chatbot.commons.domain.TrainerInfo;
import io.vertigo.chatbot.commons.domain.Training;
import io.vertigo.chatbot.designer.builder.services.DesignerServices;
import io.vertigo.chatbot.designer.builder.services.TrainingServices;
import io.vertigo.core.lang.VUserException;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@RequestMapping("/bot/{botId}/models")
public class ModelListController extends AbstractVSpringMvcController {

	private static final ViewContextKey<RunnerInfo> runnerStateKey = ViewContextKey.of("runnerState");
	private static final ViewContextKey<TrainerInfo> trainerStateKey = ViewContextKey.of("trainerState");

	private static final ViewContextKey<Boolean> autoscrollKey = ViewContextKey.of("autoscroll");

	private static final ViewContextKey<Training> trainingListKey = ViewContextKey.of("trainingList");

	private static final ViewContextKey<ChatbotNode> nodeListKey = ViewContextKey.of("nodeList");

	@Inject
	private DesignerServices designerServices;

	@Inject
	private TrainingServices trainingServices;

	@Inject
	private CommonBotDetailController commonBotDetailController;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, @PathVariable("botId") final Long botId) {
		final Chatbot bot = commonBotDetailController.initCommonContext(viewContext, botId);

		viewContext.publishRef(autoscrollKey, Boolean.TRUE);

		refreshRunnerState(viewContext, bot);
		refreshTrainerState(viewContext, bot);
		refreshTrainings(viewContext, bot);

		toModeReadOnly();
	}

	@PostMapping("/_refreshRunner")
	public ViewContext refreshRunnerState(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot) {
		final RunnerInfo state = trainingServices.getRunnerState(bot.getBotId());
		viewContext.publishDto(runnerStateKey, state);

		return viewContext;
	}

	@PostMapping("/_refreshTrainer")
	public ViewContext refreshTrainerState(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot) {
		final TrainerInfo state = trainingServices.getTrainingState(bot.getBotId());
		viewContext.publishDto(trainerStateKey, state);

		return viewContext;
	}

	@PostMapping("/_refreshTrainings")
	public ViewContext refreshTrainings(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot) {
		viewContext.publishDtList(trainingListKey, trainingServices.getAllTrainings(bot.getBotId()));

		viewContext.publishDtList(nodeListKey, designerServices.getAllNodesByBotId(bot.getBotId()));

		return viewContext;
	}

	@PostMapping("/_removeTraining")
	public ViewContext doRemoveTraining(final ViewContext viewContext,
			@RequestParam("traId") final Long traId,
			@ViewAttribute("bot") final Chatbot bot) {

		trainingServices.removeTraining(traId);

		refreshTrainings(viewContext, bot);

		return viewContext;
	}

	@PostMapping("/_train")
	public ViewContext doTrain(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot) {
		trainingServices.trainAgent(bot.getBotId());

		return viewContext;
	}

	@PostMapping("/_stop")
	public ViewContext doStop(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot) {
		trainingServices.stopAgent(bot.getBotId());

		return viewContext;
	}

	@PostMapping("/_loadTraining")
	public ViewContext doLoadTraining(final ViewContext viewContext,
			@RequestParam("traId") final Long traId,
			@RequestParam("nodId") final Long nodId,
			@ViewAttribute("bot") final Chatbot bot) {

		trainingServices.loadModel(traId, nodId);

		refreshRunnerState(viewContext, bot);
		refreshTrainings(viewContext, bot);

		return viewContext;
	}

	@PostMapping("/_talk")
	@ResponseBody
	public String talk(
			@ViewAttribute("nodeList") final DtList<ChatbotNode> nodeList,
			@RequestBody final byte[] input) {

		final ChatbotNode devNode = nodeList.stream()
				.filter(ChatbotNode::getIsDev)
				.findFirst()
				.orElseThrow(() -> new VUserException("No training node configured"));

		return ChatbotUtils.postToUrl(devNode.getUrl() + "/api/chatbot/talk", input);
	}

}
