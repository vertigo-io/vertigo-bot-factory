package io.vertigo.chatbot.designer.services;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.dao.ChatbotDAO;
import io.vertigo.chatbot.commons.dao.IntentDAO;
import io.vertigo.chatbot.commons.dao.IntentTrainingSentenceDAO;
import io.vertigo.chatbot.commons.dao.UtterTextDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.Intent;
import io.vertigo.chatbot.commons.domain.IntentTrainingSentence;
import io.vertigo.chatbot.commons.domain.UtterText;
import io.vertigo.chatbot.domain.DtDefinitions.IntentFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.component.Component;
import io.vertigo.dynamo.criteria.Criterions;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.dynamo.domain.model.DtListState;
import io.vertigo.dynamo.domain.model.ListVAccessor;
import io.vertigo.lang.Assertion;
import io.vertigo.lang.VUserException;

@Transactional
public class DesignerServices implements Component {

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

	public Chatbot getChatbotById(final Long botId) {
		Assertion.checkNotNull(botId);
		// ---
		return chatbotDAO.get(botId);
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

	public Intent save(final Intent intent, final DtList<IntentTrainingSentence> intentTexts, final DtList<IntentTrainingSentence> intentTextsToDelete, final DtList<UtterText> utterTexts, final DtList<UtterText> utterTextsToDelete) {
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