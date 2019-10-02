package io.vertigo.chatbot.executor.rasa.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import io.vertigo.chatbot.commons.domain.BotExport;
import io.vertigo.chatbot.commons.domain.ExecutorTrainingCallback;
import io.vertigo.chatbot.commons.domain.NluTrainingSentence;
import io.vertigo.chatbot.commons.domain.SmallTalkExport;
import io.vertigo.chatbot.commons.domain.TrainerInfo;
import io.vertigo.chatbot.commons.domain.UtterText;
import io.vertigo.chatbot.executor.domain.RasaConfig;
import io.vertigo.chatbot.executor.rasa.DesignerJaxrsProvider;
import io.vertigo.chatbot.executor.rasa.bridge.TrainerRasaHandler;
import io.vertigo.chatbot.executor.rasa.config.RasaConfigBuilder;
import io.vertigo.core.component.Component;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.lang.VUserException;

public class RasaTrainerServices implements Component {

	@Inject
	private TrainerRasaHandler trainerRasaHandler;

	@Inject
	private DesignerJaxrsProvider designerJaxrsProvider;


	public void trainModel(final BotExport bot, final DtList<SmallTalkExport> smallTalkList, final Long trainingId, final Long modelId) {
		if (trainerRasaHandler.isTraining()) {
			throw new VUserException("Node already training a model.");
		}

		trainerRasaHandler.trainModel(generateRasaConfig(bot, smallTalkList), modelId, isSuccess -> {
			final ExecutorTrainingCallback executorTrainingCallback = new ExecutorTrainingCallback();
			executorTrainingCallback.setTrainingId(trainingId);
			executorTrainingCallback.setSuccess(isSuccess);
			executorTrainingCallback.setLog(trainerRasaHandler.getState().getLatestTrainingLog());

			final Map<String, Object> requestData = new HashMap<>();
			requestData.put("executorTrainingCallback", executorTrainingCallback);

			designerJaxrsProvider.getWebTarget().path("/trainingCallback")
			.request(MediaType.APPLICATION_JSON)
			.post(Entity.json(requestData));
		});
	}

	private RasaConfig generateRasaConfig(final BotExport bot, final DtList<SmallTalkExport> smallTalkList) {
		final String defaultText = bot.getDefaultText().getText();
		final String welcomeText = bot.getWelcomeText().getText();

		final RasaConfigBuilder rasaConfigBuilder = new RasaConfigBuilder(defaultText, welcomeText);


		for (final SmallTalkExport st : smallTalkList) {
			final List<String> utterTexts = st.getUtterTexts().stream()
					.map(UtterText::getText)
					.collect(Collectors.toList());

			final List<String> trainingSentences = st.getNluTrainingSentences().stream()
					.map(NluTrainingSentence::getText)
					.collect(Collectors.toList());

			rasaConfigBuilder.addSmallTalk(st.getSmallTalk().getTitle(), trainingSentences, utterTexts);
		}

		return rasaConfigBuilder.build();
	}

	public TrainerInfo getTrainerState() {
		return trainerRasaHandler.getState();
	}

	public VFile getModel(final Long id) {
		return trainerRasaHandler.getModel(id);
	}

	@Deprecated
	public boolean delModel(final Long id) {
		// TODO faire un clean automatique des anciens modèles du répertoire (sauf le dernier pour conserver l'optim de rasa)
		return trainerRasaHandler.delModel(id);
	}

	public void stopTrain() {
		trainerRasaHandler.stopTrain();
	}

}