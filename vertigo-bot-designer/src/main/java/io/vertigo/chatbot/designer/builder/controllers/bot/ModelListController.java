package io.vertigo.chatbot.designer.builder.controllers.bot;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.commons.domain.RunnerInfo;
import io.vertigo.chatbot.commons.domain.TrainerInfo;
import io.vertigo.chatbot.commons.domain.Training;
import io.vertigo.chatbot.designer.builder.services.DesignerServices;
import io.vertigo.chatbot.designer.builder.services.TrainingServices;
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

		refreshRunnerState(viewContext);
		refreshTrainerState(viewContext);
		refreshTrainings(viewContext, bot);

		toModeReadOnly();
	}


	@PostMapping("/_refreshRunner")
	public ViewContext refreshRunnerState(final ViewContext viewContext) {
		final RunnerInfo state = trainingServices.getRunnerState();
		viewContext.publishDto(runnerStateKey, state);

		return viewContext;
	}

	@PostMapping("/_refreshTrainer")
	public ViewContext refreshTrainerState(final ViewContext viewContext) {
		final TrainerInfo state = trainingServices.getTrainingState();
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
			@ViewAttribute("bot") final Chatbot bot
			) {

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
	public ViewContext doStop(final ViewContext viewContext) {
		trainingServices.stopAgent();

		return viewContext;
	}

	@PostMapping("/_loadTraining")
	public ViewContext doLoadTraining(final ViewContext viewContext,
			@RequestParam("traId") final Long traId,
			@ViewAttribute("bot") final Chatbot bot) {

		trainingServices.loadModel(traId);

		refreshRunnerState(viewContext);
		refreshTrainings(viewContext, bot);

		return viewContext;
	}


}