package io.vertigo.chatbot.designer.builder.chatbot.services;

import java.util.List;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.dao.ChatbotDAO;
import io.vertigo.chatbot.commons.dao.MediaFileInfoDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.builder.BuilderPAO;
import io.vertigo.chatbot.designer.builder.chatbot.ChatbotPAO;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;

@Transactional
public class ChatbotServicesImpl implements Component {

	@Inject
	private BuilderPAO builderPAO;

	@Inject
	private ChatbotPAO chatbotPAO;

	@Inject
	private ChatbotDAO chatbotDAO;

	@Inject
	private MediaFileInfoDAO mediaFileInfoDAO;

	public Boolean deleteChatbot(Chatbot bot) {

		// Delete link with person
		chatbotPAO.removeAllChaPerRightByBotId(bot.getBotId());
		// Delete avatar file
		deleteFil(bot);
		// Delete node
		deleteChatbotNode(bot);
		// Delete training and all media file
		deleteTraining(bot);
		deleteUtterText(bot);
		deleteResponseButton(bot);
		// Delete training, reponsetype and smallTalk
		deleteSmallTalkCascade(bot);
		chatbotDAO.delete(bot.getBotId());
		return true;
	}

	private void deleteSmallTalkCascade(Chatbot bot) {
		Long botId = bot.getBotId();
		builderPAO.removeAllNluTrainingSentenceByBotId(botId);
		builderPAO.removeAllSmallTalkByBotId(botId);

	}

	private void deleteResponseButton(Chatbot bot) {
		builderPAO.removeAllButtonsByBotId(bot.getBotId());

	}

	private void deleteUtterText(Chatbot bot) {
		builderPAO.removeAllUtterTextByBotId(bot.getBotId());
	}

	private void deleteTraining(Chatbot bot) {
		Long botId = bot.getBotId();
		List<Long> filesId = builderPAO.getAllTrainingFilIdsByBotId(botId);
		builderPAO.removeTrainingByBotId(botId);
		builderPAO.removeTrainingFileByFilIds(filesId);
	}

	private void deleteChatbotNode(Chatbot bot) {
		builderPAO.removeChatbotNodeByBotId(bot.getBotId());
	}

	private void deleteFil(Chatbot bot) {
		if (bot.getFilIdAvatar() != null) {
			mediaFileInfoDAO.delete(bot.getFilIdAvatar());
		}
	}

}
