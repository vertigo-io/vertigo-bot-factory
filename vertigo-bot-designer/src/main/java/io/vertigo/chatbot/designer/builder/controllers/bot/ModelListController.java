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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.time.LocalDate;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.commons.domain.SavedTraining;
import io.vertigo.chatbot.commons.domain.SavedTrainingCriteria;
import io.vertigo.chatbot.commons.domain.TrainerInfo;
import io.vertigo.chatbot.commons.domain.Training;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.designer.builder.services.SavedTrainingServices;
import io.vertigo.chatbot.designer.builder.services.TrainerInfoServices;
import io.vertigo.chatbot.designer.builder.services.TrainingServices;
import io.vertigo.chatbot.designer.builder.services.bot.ChatbotCustomConfigServices;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.engines.webservice.json.JsonEngine;
import io.vertigo.vega.webservice.validation.UiMessageStack;

import static io.vertigo.chatbot.designer.utils.ListUtils.listLimitReached;

@Controller
@RequestMapping("/bot/{botId}/models")
@Secured("BotUser")
public class ModelListController extends AbstractBotListEntityController<Training> {

	private static final ViewContextKey<TrainerInfo> trainerStateKey = ViewContextKey.of("trainerState");

	private static final ViewContextKey<Boolean> autoscrollKey = ViewContextKey.of("autoscroll");

	private static final ViewContextKey<Training> trainingListKey = ViewContextKey.of("trainingList");

	private static final ViewContextKey<ChatbotNode> nodeListKey = ViewContextKey.of("nodeList");

	private static final ViewContextKey<Training> trainingDisplayedKey = ViewContextKey.of("trainingDisplayed");

	private static final ViewContextKey<SavedTraining> newSavedTrainingKey = ViewContextKey.of("newSavedTraining");

	private static final ViewContextKey<SavedTraining> savedTrainingListKey = ViewContextKey.of("savedTrainingList");

    private static final ViewContextKey<Long> maxSavedTrainingsKey = ViewContextKey.of("maxSavedTrainings");

	private static final ViewContextKey<SavedTrainingCriteria> criteriaKey = ViewContextKey.of("criteria");


	@Inject
	private TrainingServices trainingServices;

	@Inject
	private TrainerInfoServices trainerInfoServices;

	@Inject
	private NodeServices nodeServices;

	@Inject
	private SavedTrainingServices savedTrainingServices;

	@Inject
	private JsonEngine jsonEngine;

    @Inject
    private ChatbotCustomConfigServices chatbotCustomConfigServices;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
		final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);

		viewContext.publishRef(autoscrollKey, Boolean.TRUE);

		refreshTrainerState(viewContext, bot, new TrainerInfo());
		refreshTrainings(viewContext, bot, uiMessageStack);
		viewContext.publishDto(trainingDisplayedKey, new Training());
		final SavedTraining newSavedTraining = new SavedTraining();
		newSavedTraining.setName("Model " + viewContext.readDto(AbstractBotController.trainingKey, uiMessageStack).getVersionNumber());
		viewContext.publishDto(newSavedTrainingKey, newSavedTraining);
		viewContext.publishDtList(savedTrainingListKey, savedTrainingServices.getAllSavedTrainingByBotId(botId));
		final SavedTrainingCriteria savedTrainingCriteria = new SavedTrainingCriteria();
		savedTrainingCriteria.setToDate(LocalDate.now());
		viewContext.publishDto(criteriaKey, savedTrainingCriteria);
        viewContext.publishRef(maxSavedTrainingsKey, chatbotCustomConfigServices.getChatbotMaxSavedTraining(botId));

		super.initBreadCrums(viewContext, Training.class);
		listLimitReached(viewContext, uiMessageStack);
		toModeReadOnly();
	}

	@PostMapping("/_refreshTrainer")
	public ViewContext refreshTrainerState(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot, @ViewAttribute("trainerState") final TrainerInfo trainerInfo) {
		final TrainerInfo state = trainerInfoServices.getTrainingState(bot, trainerInfo);
		viewContext.publishDto(trainerStateKey, state);

		return viewContext;
	}

	@PostMapping("/_refreshTrainings")
	public ViewContext refreshTrainings(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot, final UiMessageStack uiMessageStack) {
		viewContext.publishDtList(trainingListKey, trainingServices.getAllTrainings(bot));
		viewContext.publishDtList(nodeListKey, nodeServices.getNodesByBot(bot));
		final SavedTraining newSavedTraining = new SavedTraining();
		newSavedTraining.setName("Model " + viewContext.readDto(AbstractBotController.trainingKey, uiMessageStack).getVersionNumber());
		viewContext.publishDto(newSavedTrainingKey, newSavedTraining);
		listLimitReached(viewContext, uiMessageStack);
		return viewContext;
	}

	@PostMapping("/_refreshTrainingInfo")
	public ViewContext doUpdateTrainingInfo(final ViewContext viewContext) {

		viewContext.publishDto(trainerStateKey, new TrainerInfo());
		return viewContext;
	}

	@PostMapping("/_saveTraining")
	public ViewContext saveTraining(final ViewContext viewContext,
			final UiMessageStack uiMessageStack,
			@ViewAttribute("bot") final Chatbot bot,
			@ViewAttribute("training") final Training deployedTraining,
			@RequestParam("name") final String name,
                                    @RequestParam("description") final String description,
                                    @RequestParam("maxSavedTrainings") final Long maxSavedTrainings) {

		final SavedTraining newSavedTraining = new SavedTraining();
		newSavedTraining.setName(name);
		newSavedTraining.setDescription(description);
		newSavedTraining.setBotId(bot.getBotId());
		newSavedTraining.setCreationTime(Instant.now());
		newSavedTraining.setTraId(deployedTraining.getTraId());
		newSavedTraining.setBotExport(jsonEngine.toJson(trainingServices.exportBot(bot, new StringBuilder())));
        savedTrainingServices.save(bot, newSavedTraining, maxSavedTrainings);
		viewContext.publishDtList(savedTrainingListKey, savedTrainingServices.getAllSavedTrainingByBotId(bot.getBotId()));
		listLimitReached(viewContext, uiMessageStack);
		return viewContext;
	}

	@PostMapping("/_refreshSavedTraining")
	public ViewContext refreshSavedTraining(final ViewContext viewContext, final UiMessageStack uiMessageStack, @ViewAttribute("bot") final Chatbot bot) {
		viewContext.publishDtList(savedTrainingListKey, savedTrainingServices.getAllSavedTrainingByBotId(bot.getBotId()));
		listLimitReached(viewContext, uiMessageStack);
		return viewContext;
	}

	@PostMapping("/_deleteSavedTraining")
	public ViewContext deleteSavedTraining(final ViewContext viewContext, final UiMessageStack uiMessageStack,
			@ViewAttribute("bot") final Chatbot bot,
			@RequestParam("savedTraId") final Long savedTraId) {
		savedTrainingServices.delete(bot, savedTraId);
		viewContext.publishDtList(savedTrainingListKey, savedTrainingServices.getAllSavedTrainingByBotId(bot.getBotId()));
		listLimitReached(viewContext, uiMessageStack);
		return viewContext;
	}

	@PostMapping("/_deploySavedTraining")
	public ViewContext deploySavedTraining(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot,
			@RequestParam("savedTrainingIdToDeploy") final Long savedTrainingIdToDeploy,
			@RequestParam("nodeId") final Long nodeId) {
		trainingServices.deployTraining(bot, savedTrainingIdToDeploy, nodeId);
		viewContext.publishDto(trainerStateKey, trainerInfoServices.createTrainingState(bot));
		return viewContext;
	}

	@PostMapping("/_rollbackSavedTraining")
	public ViewContext rollbackSavedTraining(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot,
											 @RequestParam("savedTrainingIdToDeploy") final Long savedTrainingIdToDeploy) {
		savedTrainingServices.rollbackConfig(bot, savedTrainingIdToDeploy);
		return viewContext;
	}

	@PostMapping("/_filterSavedTraining")
	public ViewContext filterSavedTraining(
			final ViewContext viewContext, final UiMessageStack uiMessageStack,
			@ViewAttribute("bot") final Chatbot bot,
			@ViewAttribute("criteria") final SavedTrainingCriteria criteria) {

		viewContext.publishDtList(savedTrainingListKey, savedTrainingServices.filter(bot, criteria));
		listLimitReached(viewContext, uiMessageStack);
		return viewContext;
	}

    @PostMapping("/_saveMaxSavedTrainings")
    public ViewContext saveMaxSavedTrainings(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot,
                                             @RequestParam("maxSavedTrainings") final Long maxSavedTrainings) {

        chatbotCustomConfigServices.setChatbotMaxSavedTraining(bot, maxSavedTrainings);
        viewContext.publishRef(maxSavedTrainingsKey, maxSavedTrainings);
        return viewContext;
    }


}
