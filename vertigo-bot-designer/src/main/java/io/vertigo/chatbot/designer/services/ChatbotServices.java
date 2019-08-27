package io.vertigo.chatbot.designer.services;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.dao.IntentDAO;
import io.vertigo.chatbot.commons.dao.IntentTrainingSentenceDAO;
import io.vertigo.chatbot.commons.dao.UtterTextDAO;
import io.vertigo.chatbot.commons.domain.Intent;
import io.vertigo.chatbot.commons.domain.IntentTrainingSentence;
import io.vertigo.chatbot.commons.domain.UtterText;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.component.Component;
import io.vertigo.dynamo.criteria.Criterions;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.dynamo.domain.model.DtListState;
import io.vertigo.dynamo.domain.model.ListVAccessor;
import io.vertigo.lang.Assertion;
import io.vertigo.lang.VUserException;

@Transactional
public class ChatbotServices implements Component {

	@Inject
	private IntentDAO intentDAO;
	
	@Inject
	private IntentTrainingSentenceDAO intentTrainingSentenceDAO;
	
	@Inject
	private UtterTextDAO utterTextDAO;

	public Intent getIntentById(final Long movId) {
		Assertion.checkNotNull(movId);
		// ---
		return intentDAO.get(movId);
	}

	public DtList<Intent> getAllIntents() {
		return intentDAO.findAll(Criterions.alwaysTrue(), DtListState.of(100));
	}
	
	public Intent getNewIntent() {
		Intent intent = new Intent();
		intent.setIsSmallTalk(true);
		return intent;
	}

	public Intent save(final Intent intent, DtList<IntentTrainingSentence> intentTexts, DtList<IntentTrainingSentence> intentTextsToDelete, DtList<UtterText> utterTexts, DtList<UtterText> utterTextsToDelete) {
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
		ListVAccessor<IntentTrainingSentence> accessor = intent.intentTrainingSentence();
		if (!accessor.isLoaded()) {
			accessor.load();
		}
		
		return accessor.get();
	}

	public DtList<UtterText> getIntentUtterTextList(Intent intent) {
		Assertion.checkNotNull(intent);
		// ---
		// url
		ListVAccessor<UtterText> accessor = intent.utterText();
		if (!accessor.isLoaded()) {
			accessor.load();
		}
		
		return accessor.get();
	}
}