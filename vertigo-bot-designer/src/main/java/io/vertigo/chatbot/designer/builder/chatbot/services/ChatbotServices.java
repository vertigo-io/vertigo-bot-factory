package io.vertigo.chatbot.designer.builder.chatbot.services;

import java.util.List;

import javax.inject.Inject;

import io.vertigo.account.authorization.AuthorizationManager;
import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.authorization.GlobalAuthorizations;
import io.vertigo.chatbot.commons.dao.ChatbotDAO;
import io.vertigo.chatbot.commons.dao.MediaFileInfoDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.admin.services.LoginServices;
import io.vertigo.chatbot.designer.admin.services.bot.ChatbotProfilServices;
import io.vertigo.chatbot.designer.builder.BuilderPAO;
import io.vertigo.chatbot.designer.builder.chatbot.ChatbotPAO;
import io.vertigo.chatbot.designer.domain.admin.ProfilPerChatbot;
import io.vertigo.chatbot.domain.DtDefinitions.ChatbotFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.util.VCollectors;

@Transactional
public class ChatbotServices implements Component {

	@Inject
	private BuilderPAO builderPAO;

	@Inject
	private ChatbotPAO chatbotPAO;

	@Inject
	private ChatbotDAO chatbotDAO;

	@Inject
	private MediaFileInfoDAO mediaFileInfoDAO;

	@Inject
	private AuthorizationManager authorizationManager;

	@Inject
	private ChatbotProfilServices chatbotProfilesServices;

	@Inject
	private LoginServices loginServices;

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

	public DtList<Chatbot> getMySupervisedChatbots() {
		if (authorizationManager.hasAuthorization(GlobalAuthorizations.AtzSuperAdmBot)) {
			return getAllChatbots();
		}

		final DtList<ProfilPerChatbot> profils = this.chatbotProfilesServices.getProfilByPerId(loginServices.getLoggedPerson().getPerId());
		final DtList<Chatbot> chatbots = profils.stream().map(x -> {
			x.chatbot().load();
			return x.chatbot().get();
		}).collect(VCollectors.toDtList(Chatbot.class));
		return chatbots;
	}

	@Secured("SuperAdmBot")
	public DtList<Chatbot> getAllChatbots() {
		return chatbotDAO.findAll(Criterions.alwaysTrue(), DtListState.of(100));
	}

	public Chatbot getChatbotByBotId(Long botId) {
		return this.chatbotDAO.find(Criterions.isEqualTo(ChatbotFields.botId, botId));
	}
}
