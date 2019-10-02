package io.vertigo.chatbot.designer.builder.services;

import java.time.LocalDate;
import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.dao.ChatbotDAO;
import io.vertigo.chatbot.commons.dao.ChatbotNodeDAO;
import io.vertigo.chatbot.commons.dao.NluTrainingSentenceDAO;
import io.vertigo.chatbot.commons.dao.SmallTalkDAO;
import io.vertigo.chatbot.commons.dao.UtterTextDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.commons.domain.NluTrainingSentence;
import io.vertigo.chatbot.commons.domain.SmallTalk;
import io.vertigo.chatbot.commons.domain.UtterText;
import io.vertigo.chatbot.designer.commons.services.FileServices;
import io.vertigo.chatbot.domain.DtDefinitions.ChatbotNodeFields;
import io.vertigo.chatbot.domain.DtDefinitions.NluTrainingSentenceFields;
import io.vertigo.chatbot.domain.DtDefinitions.SmallTalkFields;
import io.vertigo.chatbot.domain.DtDefinitions.UtterTextFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.component.Component;
import io.vertigo.dynamo.criteria.Criterions;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.dynamo.domain.model.DtListState;
import io.vertigo.dynamo.domain.model.FileInfoURI;
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
	private SmallTalkDAO smallTalkDAO;

	@Inject
	private NluTrainingSentenceDAO nluTrainingSentenceDAO;

	@Inject
	private UtterTextDAO utterTextDAO;

	@Inject
	private ChatbotNodeDAO chatbotNodeDAO;

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
		return fileServices.getFile(bot.getFilIdAvatar());
	}

	public VFile getNoAvatar() {
		return fileManager.createFile(
				"noAvatar.png",
				"image/png",
				DesignerServices.class.getResource("/noAvatar.png"));
	}

	public UtterText getDefaultTextByBot(final Chatbot bot) {
		Assertion.checkNotNull(bot);
		// ---
		return utterTextDAO.get(bot.getUttIdDefault());
	}

	public UtterText getWelcomeTextByBot(final Chatbot bot) {
		Assertion.checkNotNull(bot);
		// ---
		return utterTextDAO.get(bot.getUttIdWelcome());
	}

	public Chatbot saveChatbot(final Chatbot chatbot, final Optional<FileInfoURI> personPictureFile, final UtterText defaultText, final UtterText welcomeText) {
		Assertion.checkNotNull(chatbot);
		Assertion.checkNotNull(defaultText);
		Assertion.checkNotNull(welcomeText);
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
		final Chatbot savedChatbot =  chatbotDAO.save(chatbot);

		// clean old avatar
		if (oldAvatar != null) {
			fileServices.deleteFile(oldAvatar);
		}

		return savedChatbot;
	}

	public SmallTalk getSmallTalkById(final Long movId) {
		Assertion.checkNotNull(movId);
		// ---
		return smallTalkDAO.get(movId);
	}

	public DtList<SmallTalk> getAllSmallTalksByBotId(final Long botId) {
		return smallTalkDAO.findAll(Criterions.isEqualTo(SmallTalkFields.botId, botId), DtListState.of(1000));
	}

	public SmallTalk getNewSmallTalk(final Long botId) {
		final SmallTalk smallTalk = new SmallTalk();
		smallTalk.setBotId(botId);
		smallTalk.setIsEnabled(true);
		return smallTalk;
	}

	public SmallTalk saveSmallTalk(final SmallTalk smallTalk, final DtList<NluTrainingSentence> nluTrainingSentences, final DtList<NluTrainingSentence> nluTrainingSentencesToDelete, final DtList<UtterText> utterTexts, final DtList<UtterText> utterTextsToDelete) {
		Assertion.checkNotNull(smallTalk);
		Assertion.checkNotNull(nluTrainingSentences);
		Assertion.checkNotNull(nluTrainingSentencesToDelete);
		Assertion.checkNotNull(utterTexts);
		Assertion.checkNotNull(utterTextsToDelete);
		// ---

		if (nluTrainingSentences.isEmpty()) {
			throw new VUserException("Il est nécessaire d'avoir au moins 1 texte d'exemple");
		}

		if (utterTexts.isEmpty()) {
			throw new VUserException("Il est nécessaire d'avoir au moins 1 texte de réponse");
		}

		final SmallTalk savedST = smallTalkDAO.save(smallTalk);

		// save nlu textes
		nluTrainingSentences.stream()
		.filter(nts -> nts.getNtsId() == null) // no edit, only new elements
		.peek(nts -> nts.setSmtId(savedST.getSmtId()))
		.forEach(nts -> nluTrainingSentenceDAO.save(nts));

		nluTrainingSentencesToDelete.stream()
		.filter(itt -> itt.getNtsId() != null)
		.forEach(itt -> nluTrainingSentenceDAO.delete(itt.getNtsId()));

		// save utter textes
		utterTexts.stream()
		.filter(utt -> utt.getUttId() == null) // no edit, only new elements
		.peek(utt -> utt.setSmtId(savedST.getSmtId()))
		.forEach(utt -> utterTextDAO.save(utt));

		utterTextsToDelete.stream()
		.filter(utt -> utt.getUttId() != null)
		.forEach(utt -> utterTextDAO.delete(utt.getUttId()));

		return savedST;
	}

	public void deleteSmallTalk(final SmallTalk smallTalk) {
		// delete sub elements
		for (final NluTrainingSentence its:getNluTrainingSentenceList(smallTalk)) {
			nluTrainingSentenceDAO.delete(its.getUID());
		}

		for (final UtterText ut:getUtterTextList(smallTalk)) {
			utterTextDAO.delete(ut.getUID());
		}

		// delete smallTalk
		smallTalkDAO.delete(smallTalk.getUID());
	}

	public DtList<NluTrainingSentence> getNluTrainingSentenceList(final SmallTalk smallTalk) {
		Assertion.checkNotNull(smallTalk);
		Assertion.checkNotNull(smallTalk.getSmtId());
		// ---

		return nluTrainingSentenceDAO.findAll(
				Criterions.isEqualTo(NluTrainingSentenceFields.smtId, smallTalk.getSmtId()),
				DtListState.of(1000, 0, NluTrainingSentenceFields.ntsId.name(), true));
	}

	public DtList<UtterText> getUtterTextList(final SmallTalk smallTalk) {
		Assertion.checkNotNull(smallTalk);
		Assertion.checkNotNull(smallTalk.getSmtId());
		// ---
		return utterTextDAO.findAll(
				Criterions.isEqualTo(UtterTextFields.smtId, smallTalk.getSmtId()),
				DtListState.of(1000, 0, UtterTextFields.uttId.name(), true));
	}

	public DtList<ChatbotNode> getAllNodesByBotId(final Long botId) {
		return chatbotNodeDAO.findAll(Criterions.isEqualTo(ChatbotNodeFields.botId, botId), DtListState.of(100));
	}

	public ChatbotNode getDevNodeByBotId(final Long botId) {
		return chatbotNodeDAO.find(
				Criterions.isEqualTo(ChatbotNodeFields.botId, botId)
				.and(Criterions.isEqualTo(ChatbotNodeFields.isDev, true)));
	}
}