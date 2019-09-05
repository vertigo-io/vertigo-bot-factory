package io.vertigo.chatbot.executor.rasa.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.IntentTrainingSentence;
import io.vertigo.chatbot.commons.domain.SmallTalkExport;
import io.vertigo.chatbot.commons.domain.TrainerInfo;
import io.vertigo.chatbot.commons.domain.UtterText;
import io.vertigo.chatbot.executor.domain.RasaConfig;
import io.vertigo.chatbot.executor.rasa.bridge.RunnerRasaHandler;
import io.vertigo.chatbot.executor.rasa.bridge.TrainerRasaHandler;
import io.vertigo.chatbot.executor.rasa.config.RasaConfigBuilder;
import io.vertigo.core.component.Component;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.lang.VUserException;

public class RasaExecutorServices implements Component {

	@Inject
	private TrainerRasaHandler trainerRasaHandler;

	@Inject
	private RunnerRasaHandler runnerRasaHandler;

	public void trainModel(final DtList<SmallTalkExport> data, final Long id) {
		if (getState().getTrainingInProgress()) {
			throw new VUserException("Un entrainement est déjà en cours sur ce noeud");
		}

		trainerRasaHandler.trainModel(generateRasaConfig(data), id);
	}

	private RasaConfig generateRasaConfig(final DtList<SmallTalkExport> data) {
		final RasaConfigBuilder rasaConfigBuilder = new RasaConfigBuilder();

		for (final SmallTalkExport st : data) {
			final List<String> utterTexts = st.getUtterTexts().stream()
					.map(UtterText::getText)
					.collect(Collectors.toList());

			final List<String> trainingSentences = st.getIntentTrainingSentences().stream()
					.map(IntentTrainingSentence::getText)
					.collect(Collectors.toList());

			rasaConfigBuilder.addSmallTalk(st.getIntent().getTitle(), trainingSentences, utterTexts);
		}

		return rasaConfigBuilder.build();
	}

	public TrainerInfo getState() {
		return trainerRasaHandler.getState();
	}

	public VFile getModel(final Long id) {
		return trainerRasaHandler.getModel(id);
	}

	public boolean delModel(final Long id) {
		return trainerRasaHandler.delModel(id);
	}

}