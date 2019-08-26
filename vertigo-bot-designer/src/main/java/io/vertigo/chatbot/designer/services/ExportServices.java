package io.vertigo.chatbot.designer.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertigo.chatbot.designer.dao.IntentDAO;
import io.vertigo.chatbot.designer.dao.IntentTrainingSentenceDAO;
import io.vertigo.chatbot.designer.dao.UtterTextDAO;
import io.vertigo.chatbot.designer.domain.Intent;
import io.vertigo.chatbot.designer.domain.IntentExport;
import io.vertigo.chatbot.designer.domain.IntentTrainingSentence;
import io.vertigo.chatbot.designer.domain.UtterText;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.component.Component;
import io.vertigo.dynamo.criteria.Criterions;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.dynamo.domain.model.DtListState;
import io.vertigo.dynamo.domain.model.ListVAccessor;
import io.vertigo.dynamo.domain.util.VCollectors;
import io.vertigo.lang.Assertion;
import io.vertigo.lang.VUserException;

@Transactional
public class ExportServices implements Component {

	@Inject
	private IntentDAO intentDAO;
	
	@Inject
	private IntentTrainingSentenceDAO intentTrainingSentenceDAO;
	
	@Inject
	private UtterTextDAO utterTextDAO;

	public DtList<IntentExport> exportSmallTalk() {
		DtList<Intent> intents = intentDAO.exportSmallTalk();
		
		final List<Long> intentIds = intents.stream()
				.map(Intent::getIntId)
				.collect(Collectors.toList());
		
		final Map<Long, DtList<IntentTrainingSentence>> trainingSentencesMap = intentTrainingSentenceDAO.exportSmallTalkRelativeTrainingSentence(intentIds)
				.stream()
				.collect(Collectors.groupingBy(IntentTrainingSentence::getIntId,
											   VCollectors.toDtList(IntentTrainingSentence.class)));
		
		final Map<Long, DtList<UtterText>> utterTextsMap = utterTextDAO.exportSmallTalkRelativeUtter(intentIds)
				.stream()
				.collect(Collectors.groupingBy(UtterText::getIntId,
						   					   VCollectors.toDtList(UtterText.class)));
		
		final DtList<IntentExport> retour = new DtList<>(IntentExport.class);
		for (final Intent intent : intents) {
			final IntentExport newExport = new IntentExport();
			newExport.setIntent(intent);
			newExport.setIntentTrainingSentences(trainingSentencesMap.get(intent.getIntId()));
			newExport.setUtterTexts(utterTextsMap.get(intent.getIntId()));
			
			retour.add(newExport);
		}
		
		return retour;
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