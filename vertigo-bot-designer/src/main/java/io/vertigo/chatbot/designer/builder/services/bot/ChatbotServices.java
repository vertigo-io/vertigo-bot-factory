package io.vertigo.chatbot.designer.builder.services.bot;

import java.time.LocalDate;
import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.account.authorization.AuthorizationManager;
import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.authorization.GlobalAuthorizations;
import io.vertigo.chatbot.authorization.SecuredEntities.ChatbotOperations;
import io.vertigo.chatbot.commons.dao.ChatbotDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ResponseButton;
import io.vertigo.chatbot.commons.domain.UtterText;
import io.vertigo.chatbot.designer.builder.BuilderPAO;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.designer.builder.services.ResponsesButtonServices;
import io.vertigo.chatbot.designer.builder.services.SmallTalkServices;
import io.vertigo.chatbot.designer.builder.services.TrainingServices;
import io.vertigo.chatbot.designer.builder.services.UtterTextServices;
import io.vertigo.chatbot.designer.commons.services.FileServices;
import io.vertigo.chatbot.designer.utils.AuthorizationUtils;
import io.vertigo.chatbot.designer.utils.UserSessionUtils;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.datastore.impl.filestore.model.StreamFile;

@Transactional
@Secured("BotUser")
public class ChatbotServices implements Component {

	@Inject
	private BuilderPAO builderPAO;

	@Inject
	private ChatbotDAO chatbotDAO;

	@Inject
	private ResponsesButtonServices responsesButtonServices;

	@Inject
	private UtterTextServices utterTextServices;

	@Inject
	private SmallTalkServices smallTalkServices;

	@Inject
	private AuthorizationManager authorizationManager;

	@Inject
	private FileServices fileServices;

	@Inject
	private ChatbotProfilServices chatbotProfilServices;

	@Inject
	private TrainingServices trainingServices;

	@Inject
	private NodeServices nodeServices;

	public Chatbot saveChatbot(@SecuredOperation("botAdm") final Chatbot chatbot, final Optional<FileInfoURI> personPictureFile,
			final UtterText defaultText, final DtList<ResponseButton> defaultButtons,
			final UtterText welcomeText, final DtList<ResponseButton> welcomeButtons) {

		Assertion.check().isNotNull(chatbot);
		Assertion.check().isNotNull(defaultText);
		Assertion.check().isNotNull(defaultButtons);
		Assertion.check().isNotNull(welcomeText);
		Assertion.check().isNotNull(welcomeButtons);
		// ---

		// default text
		utterTextServices.save(defaultText);
		chatbot.setUttIdDefault(defaultText.getUttId());
		// welcome
		utterTextServices.save(welcomeText);
		chatbot.setUttIdWelcome(welcomeText.getUttId());

		// Avatar
		Long oldAvatar = null;
		if (personPictureFile.isPresent()) {
			oldAvatar = chatbot.getFilIdAvatar();
			final VFile fileTmp = fileServices.getFileTmp(personPictureFile.get());
			final FileInfoURI fileInfoUri = fileServices.saveFile(fileTmp);
			chatbot.setFilIdAvatar((Long) fileInfoUri.getKey());
		}

		// chatbot save
		chatbot.setStatus("OK");
		final Chatbot savedChatbot = chatbotDAO.save(chatbot);

		// clean old avatar
		if (oldAvatar != null) {
			fileServices.deleteFile(oldAvatar);
		}

		// clear old buttons
		responsesButtonServices.removeAllButtonsByBot(chatbot);
		// save new buttons
		responsesButtonServices.saveAllDefaultButtonsByBot(savedChatbot, defaultButtons);
		responsesButtonServices.saveAllWelcomeButtonsByBot(savedChatbot, welcomeButtons);

		return savedChatbot;
	}

	public Boolean deleteChatbot(@SecuredOperation("botAdm") final Chatbot bot) {

		// Delete avatar file
		if (bot.getFilIdAvatar() != null) {
			fileServices.delete(bot.getFilIdAvatar());
		}
		// Delete node
		nodeServices.deleteChatbotNodeByBot(bot);
		// Delete training and all media file
		trainingServices.removeAllTraining(bot);
		utterTextServices.removeAllUtterTextByBotId(bot);
		responsesButtonServices.removeAllButtonsByBot(bot);
		// Delete training, reponsetype and smallTalk
		smallTalkServices.removeAllNTSFromBot(bot);
		smallTalkServices.removeAllSmallTalkFromBot(bot);

		chatbotProfilServices.deleteAllProfilByBot(bot);
		chatbotDAO.delete(bot.getBotId());
		return true;
	}

	public DtList<Chatbot> getMySupervisedChatbots() {
		if (authorizationManager.hasAuthorization(GlobalAuthorizations.AtzSuperAdm)) {
			return getAllChatbots();
		}
		return chatbotDAO.getChatbotByPerId(UserSessionUtils.getLoggedPerson().getPerId());
	}

	@Secured("SuperAdm")
	public DtList<Chatbot> getAllChatbots() {
		return chatbotDAO.findAll(Criterions.alwaysTrue(), DtListState.of(100));
	}

	@Secured("SuperAdm")
	public Chatbot getNewChatbot() {
		final Chatbot newChatbot = new Chatbot();
		newChatbot.setCreationDate(LocalDate.now());

		return newChatbot;
	}

	public Chatbot getChatbotById(final Long botId) {
		Assertion.check().isNotNull(botId);
		// ---
		final Chatbot chatbot = chatbotDAO.get(botId);
		AuthorizationUtils.checkRights(chatbot, ChatbotOperations.botVisitor, "can't get the chatbot : not enough right");
		return chatbot;
	}

	public VFile getAvatar(@SecuredOperation("botVisitor") final Chatbot bot) {
		if (bot.getFilIdAvatar() == null) {
			return getNoAvatar();
		}
		return fileServices.getFile(bot.getFilIdAvatar());
	}

	public VFile getNoAvatar() {
		return StreamFile.of(
				"noAvatar.png",
				"image/png",
				ChatbotServices.class.getResource("/noAvatar.png"));
	}

}
