package io.vertigo.chatbot.designer.builder.services;

import java.time.LocalDate;
import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.dao.ChatbotDAO;
import io.vertigo.chatbot.commons.dao.IntentDAO;
import io.vertigo.chatbot.commons.dao.IntentTrainingSentenceDAO;
import io.vertigo.chatbot.commons.dao.UtterTextDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.Intent;
import io.vertigo.chatbot.commons.domain.IntentTrainingSentence;
import io.vertigo.chatbot.commons.domain.UtterText;
import io.vertigo.chatbot.designer.commons.services.FileServices;
import io.vertigo.chatbot.domain.DtDefinitions.IntentFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.component.Component;
import io.vertigo.dynamo.criteria.Criterions;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.dynamo.domain.model.DtListState;
import io.vertigo.dynamo.domain.model.FileInfoURI;
import io.vertigo.dynamo.domain.model.ListVAccessor;
import io.vertigo.dynamo.file.FileManager;
import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.lang.Assertion;
import io.vertigo.lang.VUserException;

@Transactional
public class DesignerServices implements Component {

	@Inject
	private FileServices fileServices;

	@Inject
	private FileManager fileManager;

	@Inject
	private ChatbotDAO chatbotDAO;

	@Inject
	private IntentDAO intentDAO;

	@Inject
	private IntentTrainingSentenceDAO intentTrainingSentenceDAO;

	@Inject
	private UtterTextDAO utterTextDAO;

	public DtList<Chatbot> getAllChatbots() {
		return chatbotDAO.findAll(Criterions.alwaysTrue(), DtListState.of(100));
	}

	public Chatbot getNewChatbot() {
		final Chatbot newChatbot = new Chatbot();
		newChatbot.setCreationDate(LocalDate.now());

		return newChatbot;
	}

	public Chatbot getChatbotById(final Long botId) {
		Assertion.checkNotNull(botId);
		// ---
		return chatbotDAO.get(botId);
	}

	public VFile getAvatar(final Chatbot bot) {
		if (bot.getFilIdAvatar() == null) {
			return getNoAvatar();
		}
		return fileServices.getFile(fileServices.toStdFileInfoUri(bot.getFilIdAvatar()));
	}

	public UtterText getDefaultByBot(final Chatbot bot) {
		Assertion.checkNotNull(bot);
		// ---
		return utterTextDAO.get(bot.getUtxIdDefault());
	}

	public UtterText getWelcomeByBot(final Chatbot bot) {
		Assertion.checkNotNull(bot);
		// ---
		return utterTextDAO.get(bot.getUtxIdWelcome());
	}

	public VFile getNoAvatar() {
		return fileManager.createFile(
				"noAvatar.png",
				"image/png",
				DesignerServices.class.getResource("/noAvatar.png"));
	}

	public Chatbot saveChatbot(final Chatbot chatbot, final Optional<FileInfoURI> personPictureFile, final UtterText defaultText, final UtterText welcome) {
		Assertion.checkNotNull(chatbot);
		Assertion.checkNotNull(welcome);
		// ---

		// Avatar
		if (personPictureFile.isPresent()) {
			saveChatbotPicture(chatbot, personPictureFile.get());
		}

		// default text
		utterTextDAO.save(defaultText);
		chatbot.setUtxIdDefault(defaultText.getUtxId());

		// welcome
		utterTextDAO.save(welcome);
		chatbot.setUtxIdWelcome(welcome.getUtxId());

		// chatbot save
		chatbot.setStatus("OK");
		return chatbotDAO.save(chatbot);
	}

	private void saveChatbotPicture(final Chatbot chatbot, final FileInfoURI avatarTmp) {
		final Long oldAvatar = chatbot.getFilIdAvatar();
		final VFile fileTmp = fileServices.getFileTmp(avatarTmp);
		final FileInfoURI fileInfoUri = fileServices.saveFile(fileTmp);
		chatbot.setFilIdAvatar((Long) fileInfoUri.getKey());

		if (oldAvatar != null) {
			fileServices.deleteFile(oldAvatar);
		}
	}

	public Intent getIntentById(final Long movId) {
		Assertion.checkNotNull(movId);
		// ---
		return intentDAO.get(movId);
	}

	public DtList<Intent> getAllIntents(final Long botId) {
		return intentDAO.findAll(Criterions.isEqualTo(IntentFields.botId, botId), DtListState.of(1000));
	}

	public Intent getNewIntent(final Long botId) {
		final Intent intent = new Intent();
		intent.setBotId(botId);
		intent.setIsEnabled(true);
		intent.setIsSmallTalk(true);
		return intent;
	}

	public Intent saveIntent(final Intent intent, final DtList<IntentTrainingSentence> intentTexts, final DtList<IntentTrainingSentence> intentTextsToDelete, final DtList<UtterText> utterTexts, final DtList<UtterText> utterTextsToDelete) {
		Assertion.checkNotNull(intent);
		Assertion.checkNotNull(intentTexts);
		Assertion.checkNotNull(intentTextsToDelete);
		Assertion.checkNotNull(utterTexts);
		Assertion.checkNotNull(utterTextsToDelete);
		// ---

		if (intentTexts.isEmpty()) {
			throw new VUserException("Il est nécessaire d'avoir au moins 1 texte d'exemple");
		}

		if (utterTexts.isEmpty()) {
			throw new VUserException("Il est nécessaire d'avoir au moins 1 texte de réponse");
		}

		final Intent savedIntent = intentDAO.save(intent);

		// save intent textes
		intentTexts.stream()
		.filter(itt -> itt.getItsId() == null) // no edit, only new elements
		.peek(itt -> itt.setIntId(savedIntent.getIntId()))
		.forEach(itt -> intentTrainingSentenceDAO.save(itt));

		intentTextsToDelete.stream()
		.filter(itt -> itt.getItsId() != null)
		.forEach(itt -> intentTrainingSentenceDAO.delete(itt.getItsId()));

		// save utter textes
		utterTexts.stream()
		.filter(utx -> utx.getUtxId() == null) // no edit, only new elements
		.peek(utx -> utx.setIntId(savedIntent.getIntId()))
		.forEach(utx -> utterTextDAO.save(utx));

		utterTextsToDelete.stream()
		.filter(utx -> utx.getUtxId() != null)
		.forEach(utx -> utterTextDAO.delete(utx.getUtxId()));

		return savedIntent;
	}

	public DtList<IntentTrainingSentence> getIntentTrainingSentenceList(final Intent intent) {
		Assertion.checkNotNull(intent);
		// ---
		// url
		final ListVAccessor<IntentTrainingSentence> accessor = intent.intentTrainingSentence();
		if (!accessor.isLoaded()) {
			accessor.load();
		}

		return accessor.get();
	}

	public DtList<UtterText> getIntentUtterTextList(final Intent intent) {
		Assertion.checkNotNull(intent);
		// ---
		// url
		final ListVAccessor<UtterText> accessor = intent.utterText();
		if (!accessor.isLoaded()) {
			accessor.load();
		}

		return accessor.get();
	}
}