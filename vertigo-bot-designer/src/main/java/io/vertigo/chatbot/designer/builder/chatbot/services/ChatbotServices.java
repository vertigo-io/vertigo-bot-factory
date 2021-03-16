package io.vertigo.chatbot.designer.builder.chatbot.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.account.authorization.AuthorizationManager;
import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.authorization.GlobalAuthorizations;
import io.vertigo.chatbot.authorization.SecuredEntities.ChatbotOperations;
import io.vertigo.chatbot.commons.dao.ChatbotDAO;
import io.vertigo.chatbot.commons.dao.MediaFileInfoDAO;
import io.vertigo.chatbot.commons.dao.ResponseButtonDAO;
import io.vertigo.chatbot.commons.dao.UtterTextDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ResponseButton;
import io.vertigo.chatbot.commons.domain.UtterText;
import io.vertigo.chatbot.designer.builder.BuilderPAO;
import io.vertigo.chatbot.designer.builder.services.DesignerServices;
import io.vertigo.chatbot.designer.builder.services.bot.ChatbotProfilServices;
import io.vertigo.chatbot.designer.commons.services.FileServices;
import io.vertigo.chatbot.designer.commons.utils.AuthorizationUtils;
import io.vertigo.chatbot.designer.commons.utils.UserSessionUtils;
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
@Secured("AdmBot")
public class ChatbotServices implements Component {

	@Inject
	private BuilderPAO builderPAO;

	@Inject
	private ChatbotDAO chatbotDAO;

	@Inject
	private ResponseButtonDAO responseButtonDAO;

	@Inject
	private UtterTextDAO utterTextDAO;

	@Inject
	private MediaFileInfoDAO mediaFileInfoDAO;

	@Inject
	private AuthorizationManager authorizationManager;

	@Inject
	private UserSessionUtils userSessionUtils;

	@Inject
	private FileServices fileServices;

	@Inject
	private ChatbotProfilServices chatbotProfilServices;

	public Chatbot saveChatbot(@SecuredOperation("admFct") final Chatbot chatbot, final Optional<FileInfoURI> personPictureFile,
			final UtterText defaultText, final DtList<ResponseButton> defaultButtons,
			final UtterText welcomeText, final DtList<ResponseButton> welcomeButtons) {

		Assertion.check().isNotNull(chatbot);
		Assertion.check().isNotNull(defaultText);
		Assertion.check().isNotNull(defaultButtons);
		Assertion.check().isNotNull(welcomeText);
		Assertion.check().isNotNull(welcomeButtons);
		// ---

		// default text
		utterTextDAO.save(defaultText);
		chatbot.setUttIdDefault(defaultText.getUttId());

		// welcome
		utterTextDAO.save(welcomeText);
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
		builderPAO.removeAllButtonsByBotId(chatbot.getBotId());

		// save new buttons
		for (final ResponseButton btn : defaultButtons) {
			btn.setBtnId(null); // force creation
			btn.setBotIdDefault(chatbot.getBotId());
			responseButtonDAO.save(btn);
		}

		for (final ResponseButton btn : welcomeButtons) {
			btn.setBtnId(null); // force creation
			btn.setBotIdWelcome(chatbot.getBotId());
			responseButtonDAO.save(btn);
		}

		return savedChatbot;
	}

	public Boolean deleteChatbot(@SecuredOperation("admFct") final Chatbot bot) {

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

		chatbotProfilServices.deleteAllProfilByBot(bot);
		chatbotDAO.delete(bot.getBotId());
		return true;
	}

	private void deleteSmallTalkCascade(final Chatbot bot) {
		final Long botId = bot.getBotId();
		builderPAO.removeAllNluTrainingSentenceByBotId(botId);
		builderPAO.removeAllSmallTalkByBotId(botId);

	}

	private void deleteResponseButton(final Chatbot bot) {
		builderPAO.removeAllButtonsByBotId(bot.getBotId());

	}

	private void deleteUtterText(final Chatbot bot) {
		builderPAO.removeAllUtterTextByBotId(bot.getBotId());
	}

	private void deleteTraining(final Chatbot bot) {
		final Long botId = bot.getBotId();
		final List<Long> filesId = builderPAO.getAllTrainingFilIdsByBotId(botId);
		builderPAO.removeTrainingByBotId(botId);
		builderPAO.removeTrainingFileByFilIds(filesId);
	}

	private void deleteChatbotNode(final Chatbot bot) {
		builderPAO.removeChatbotNodeByBotId(bot.getBotId());
	}

	private void deleteFil(final Chatbot bot) {
		if (bot.getFilIdAvatar() != null) {
			mediaFileInfoDAO.delete(bot.getFilIdAvatar());
		}
	}

	public DtList<Chatbot> getMySupervisedChatbots() {
		if (authorizationManager.hasAuthorization(GlobalAuthorizations.AtzSuperAdmBot)) {
			return getAllChatbots();
		}
		return chatbotDAO.getChatbotByPerId(userSessionUtils.getLoggedPerson().getPerId());
	}

	@Secured("SuperAdmBot")
	public DtList<Chatbot> getAllChatbots() {
		return chatbotDAO.findAll(Criterions.alwaysTrue(), DtListState.of(100));
	}

	@Secured("SuperAdmBot")
	public Chatbot getNewChatbot() {
		final Chatbot newChatbot = new Chatbot();
		newChatbot.setCreationDate(LocalDate.now());

		return newChatbot;
	}

	public Chatbot getChatbotById(final Long botId) {
		Assertion.check().isNotNull(botId);
		// ---
		final Chatbot chatbot = chatbotDAO.get(botId);
		AuthorizationUtils.checkRights(chatbot, ChatbotOperations.visiteur, "can't get the chatbot : not enough right");
		return chatbot;
	}

	public VFile getAvatar(final Chatbot bot) {
		if (bot.getFilIdAvatar() == null) {
			return getNoAvatar();
		}
		return fileServices.getFile(bot.getFilIdAvatar());
	}

	public VFile getNoAvatar() {
		return StreamFile.of(
				"noAvatar.png",
				"image/png",
				DesignerServices.class.getResource("/noAvatar.png"));
	}

}
