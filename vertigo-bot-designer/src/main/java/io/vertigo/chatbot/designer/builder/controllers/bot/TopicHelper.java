package io.vertigo.chatbot.designer.builder.controllers.bot;

import io.vertigo.chatbot.commons.domain.topic.NluTrainingSentence;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.util.StringUtil;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;

public class TopicHelper {

	private static final ViewContextKey<String> newNluTrainingSentenceKey = ViewContextKey.of("newNluTrainingSentence");
	private static final ViewContextKey<NluTrainingSentence> nluTrainingSentencesKey = ViewContextKey
			.of("nluTrainingSentences");
	private static final ViewContextKey<NluTrainingSentence> nluTrainingSentencesToDeleteKey = ViewContextKey
			.of("nluTrainingSentencesToDelete");

	public static ViewContext doAddTrainingSentence(final ViewContext viewContext,
			final String newNluTrainingSentenceIn,
			final DtList<NluTrainingSentence> nluTrainingSentences) {

		addTrainingSentense(newNluTrainingSentenceIn, nluTrainingSentences);

		viewContext.publishDtListModifiable(nluTrainingSentencesKey, nluTrainingSentences);
		viewContext.publishRef(newNluTrainingSentenceKey, "");

		return viewContext;
	}

	public static void addTrainingSentense(final String newNluTrainingSentenceIn,
			final DtList<NluTrainingSentence> nluTrainingSentences) {
		if (StringUtil.isBlank(newNluTrainingSentenceIn)) {
			return;
		}

		final String newNluTrainingSentence = newNluTrainingSentenceIn.trim();

		final boolean exists = nluTrainingSentences.stream()
				.anyMatch(its -> its.getText().equalsIgnoreCase(newNluTrainingSentence));
		if (exists) {
			throw new VUserException("This sentense already exists");
		}

		final NluTrainingSentence newText = new NluTrainingSentence();
		newText.setText(newNluTrainingSentence);

		nluTrainingSentences.add(newText);
	}

}
