package io.vertigo.chatbot.designer.services;

import javax.inject.Inject;

import io.vertigo.chatbot.designer.dao.IntentDAO;
import io.vertigo.chatbot.designer.dao.IntentTextDAO;
import io.vertigo.chatbot.designer.domain.Intent;
import io.vertigo.chatbot.designer.domain.IntentText;
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
	private IntentTextDAO intentTextDAO;

	public Intent getIntentById(final Long movId) {
		Assertion.checkNotNull(movId);
		// ---
		return intentDAO.get(movId);
	}

	public DtList<Intent> getAllIntents() {
		return intentDAO.findAll(Criterions.alwaysTrue(), DtListState.of(100));
	}

	public Intent save(final Intent intent, DtList<IntentText> intentTexts, DtList<IntentText> intentTextsToDelete) {
		Assertion.checkNotNull(intent);
		Assertion.checkNotNull(intentTexts);
		Assertion.checkNotNull(intentTextsToDelete);
		// ---
		
		if (intentTexts.isEmpty()) {
			throw new VUserException("Il est nécessaire d'avoir au moins 1 texte d'exemple");
		}
		
		final Intent savedIntent = intentDAO.save(intent);
		
		// save intent textes
		intentTexts.stream()
			.filter(itt -> itt.getIttId() == null) // no edit, only new elements
//			.peek(itt -> itt.setIntId(savedIntent.getIntId()))
			.forEach(itt -> intentTextDAO.save(itt));
		
		intentTextsToDelete.stream()
			.forEach(itt -> intentTextDAO.delete(itt.getIttId()));
		
		return savedIntent;
	}

	public DtList<IntentText> getIntentTextList(final Intent intent) {
		Assertion.checkNotNull(intent);
		// ---
		// url
		ListVAccessor<IntentText> accessor = intent.intentText();
		if (!accessor.isLoaded()) {
			accessor.load();
		}
		
		return accessor.get();
	}
}