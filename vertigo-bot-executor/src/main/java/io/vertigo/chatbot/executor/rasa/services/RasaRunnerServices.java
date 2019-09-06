package io.vertigo.chatbot.executor.rasa.services;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.RunnerInfo;
import io.vertigo.chatbot.executor.rasa.bridge.RunnerRasaHandler;
import io.vertigo.core.component.Component;
import io.vertigo.dynamo.file.model.VFile;

public class RasaRunnerServices implements Component {

	@Inject
	private RunnerRasaHandler runnerRasaHandler;

	public RunnerInfo getRunnerState() {
		return runnerRasaHandler.getState();
	}

	public void loadModel(final VFile model) {
		runnerRasaHandler.loadModel(model);
	}

}