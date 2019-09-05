package io.vertigo.chatbot.executor.rasa.bridge;

import io.vertigo.chatbot.commons.domain.RunnerInfo;
import io.vertigo.core.component.Activeable;
import io.vertigo.core.component.Component;

public class RunnerRasaHandler extends AbstractRasaHandler implements Component, Activeable {

	private Process rasaProcess;

	@Override
	public void start() {
		LOGGER.info("Lancement de Rasa");

		rasaProcess = execRasa("run", "--enable-api");
	}

	@Override
	public void stop() {
		// stop child process

		if (rasaProcess.isAlive()) {
			rasaProcess.destroyForcibly();
		}
	}


	public RunnerInfo getState() {
		final RunnerInfo retour = new RunnerInfo();

		retour.setName("Rasa node");
		retour.setState(rasaProcess.isAlive() ? "Ok" : "Ko");
		retour.setLoadedModelVersion(-1L);

		return retour;
	}
}
