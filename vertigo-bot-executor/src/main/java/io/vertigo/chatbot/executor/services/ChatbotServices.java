package io.vertigo.chatbot.executor.services;

import javax.inject.Inject;

import io.vertigo.chatbot.executor.rasa.RasaHandler;
import io.vertigo.core.component.Component;

public class ChatbotServices implements Component {

	@Inject
	private RasaHandler rasaHandler;
	
	public void trainModel() {
		rasaHandler.trainModel();
	}

	public String getTrainingLog() {
		return rasaHandler.getTrainingLog();
	}
	
}