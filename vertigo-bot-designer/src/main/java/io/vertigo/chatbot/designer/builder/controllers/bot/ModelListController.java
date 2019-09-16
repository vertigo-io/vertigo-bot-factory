package io.vertigo.chatbot.designer.builder.controllers.bot;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.vertigo.chatbot.commons.domain.RunnerInfo;
import io.vertigo.chatbot.commons.domain.TrainerInfo;
import io.vertigo.chatbot.designer.builder.services.ExecutorBridgeServices;
import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@RequestMapping("/bot/{botId}/models")
public class ModelListController extends AbstractVSpringMvcController {

	private static final ViewContextKey<RunnerInfo> runnerStateKey = ViewContextKey.of("runnerState");
	private static final ViewContextKey<TrainerInfo> trainerStateKey = ViewContextKey.of("trainerState");

	private static final ViewContextKey<Boolean> autoscrollKey = ViewContextKey.of("autoscroll");

	@Inject
	private ExecutorBridgeServices executorBridgeServices;

	@Inject
	private CommonBotDetailController commonBotDetailController;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, @PathVariable("botId") final Long botId) {
		commonBotDetailController.initCommonContext(viewContext, botId);

		viewContext.publishRef(autoscrollKey, Boolean.TRUE);

		refreshRunnerState(viewContext);
		refreshTrainerState(viewContext);

		toModeReadOnly();
	}


	@PostMapping("/_refreshRunner")
	public ViewContext refreshRunnerState(final ViewContext viewContext) {
		final RunnerInfo state = executorBridgeServices.getRunnerState();
		viewContext.publishDto(runnerStateKey, state);

		return viewContext;
	}

	@PostMapping("/_refreshTrainer")
	public ViewContext refreshTrainerState(final ViewContext viewContext) {
		final TrainerInfo state = executorBridgeServices.getTrainingState();
		viewContext.publishDto(trainerStateKey, state);

		return viewContext;
	}


	@PostMapping("/_train")
	public ViewContext doSave(final ViewContext viewContext) {
		executorBridgeServices.trainAgent();

		return viewContext;
	}


	@PostMapping("/_stop")
	public ViewContext doStop(final ViewContext viewContext) {
		executorBridgeServices.stopAgent();

		return viewContext;
	}

	@PostMapping("/_loadModel")
	public ViewContext doLoadModel(final ViewContext viewContext,
			@RequestParam("id") final Long id) {

		final VFile file = executorBridgeServices.fetchModel(id);
		executorBridgeServices.loadModel(file);

		return viewContext;
	}

}