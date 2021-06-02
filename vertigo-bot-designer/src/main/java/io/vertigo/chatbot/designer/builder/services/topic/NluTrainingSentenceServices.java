package io.vertigo.chatbot.designer.builder.services.topic;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.dao.topic.NluTrainingSentenceDAO;
import io.vertigo.chatbot.commons.domain.topic.NluTrainingSentence;
import io.vertigo.chatbot.commons.domain.topic.TopicFileExport;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.util.StringUtil;
import io.vertigo.datamodel.structure.model.DtList;

@Transactional
public class NluTrainingSentenceServices implements Component {

	@Inject
	private NluTrainingSentenceDAO nluTrainingSentenceDAO;

	public NluTrainingSentence save(final NluTrainingSentence nluTrainingSentence) {
		return nluTrainingSentenceDAO.save(nluTrainingSentence);
	}

	public void deleteTypeTopic(final NluTrainingSentence nluTrainingSentence) {
		nluTrainingSentenceDAO.delete(nluTrainingSentence.getNtsId());
	}

	public void addTrainingSentense(final String newNluTrainingSentenceIn,
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

	public DtList<NluTrainingSentence> extractNlutsFromTfe(final TopicFileExport tfe) {
		final String[] listTrainingPhrases = tfe.getTrainingPhrases().split("\\|");

		final DtList<NluTrainingSentence> nluTrainingSentences = new DtList<NluTrainingSentence>(NluTrainingSentence.class);

		for (final String newNluTrainingSentence : listTrainingPhrases) {
			addTrainingSentense(newNluTrainingSentence, nluTrainingSentences);
		}
		return nluTrainingSentences;
	}
}
