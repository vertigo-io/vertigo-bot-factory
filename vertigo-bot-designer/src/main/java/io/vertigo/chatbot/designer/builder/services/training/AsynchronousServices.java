package io.vertigo.chatbot.designer.builder.services.training;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.dao.ChatbotNodeDAO;
import io.vertigo.chatbot.commons.dao.TrainingDAO;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.commons.domain.Training;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;

//Use this service for asynchronous call
//Don't check authorizations for saving objects
@Transactional
public class AsynchronousServices implements Component {

	@Inject
	private ChatbotNodeDAO chatbotNodeDAO;

	@Inject
	private TrainingDAO trainingDAO;

	public ChatbotNode saveNodeWithoutAuthorizations(final ChatbotNode node) {
		return chatbotNodeDAO.save(node);
	}

	public Training saveTrainingWithoutAuthorizations(final Training training) {
		return trainingDAO.save(training);
	}
}
