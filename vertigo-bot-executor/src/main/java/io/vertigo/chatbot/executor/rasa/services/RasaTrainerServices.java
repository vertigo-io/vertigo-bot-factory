package io.vertigo.chatbot.executor.rasa.services;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import io.vertigo.chatbot.commons.JaxrsProvider;
import io.vertigo.chatbot.commons.domain.BotExport;
import io.vertigo.chatbot.commons.domain.ExecutorTrainingCallback;
import io.vertigo.chatbot.commons.domain.SmallTalkExport;
import io.vertigo.chatbot.commons.domain.TrainerInfo;
import io.vertigo.chatbot.executor.rasa.bridge.TrainerRasaHandler;
import io.vertigo.core.component.Activeable;
import io.vertigo.core.component.Component;
import io.vertigo.core.param.ParamManager;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.lang.VUserException;

public class RasaTrainerServices implements Component, Activeable {

	@Inject
	private TrainerRasaHandler trainerRasaHandler;

	@Inject
	private JaxrsProvider jaxrsProvider;

	@Inject
	private ParamManager paramManager;

	private WebTarget designerTarget;


	@Override
	public void start() {
		designerTarget = jaxrsProvider.getWebTarget(paramManager.getParam("designerUrl") + "/api/");
	}

	@Override
	public void stop() {
		// Nothing
	}

	public void trainModel(final BotExport bot, final DtList<SmallTalkExport> smallTalkList, final Long trainingId, final Long modelId) {
		if (trainerRasaHandler.isTraining()) {
			throw new VUserException("Node already training a model.");
		}

		trainerRasaHandler.trainModel(bot, smallTalkList, modelId, isSuccess -> {
			final ExecutorTrainingCallback executorTrainingCallback = new ExecutorTrainingCallback();
			executorTrainingCallback.setTrainingId(trainingId);
			executorTrainingCallback.setSuccess(isSuccess);
			executorTrainingCallback.setLog(trainerRasaHandler.getTrainingLog());
			executorTrainingCallback.setInfos(getTrainInfos());
			executorTrainingCallback.setWarnings(getTrainWarns());

			final Map<String, Object> requestData = new HashMap<>();
			requestData.put("executorTrainingCallback", executorTrainingCallback);

			designerTarget.path("/trainingCallback")
			.request(MediaType.APPLICATION_JSON)
			.post(Entity.json(requestData));
		});
	}

	private String getTrainInfos() {
		final long coreAccuacy = trainerRasaHandler.getCoreAccuacy();
		final long nluAccuacy = trainerRasaHandler.getNluAccuacy();

		final StringBuilder retour = new StringBuilder();
		if (coreAccuacy != -1) {
			retour.append("Core accuacy : ");
			retour.append(coreAccuacy);
			retour.append("%\n");
		}
		if (nluAccuacy != -1) {
			retour.append("Nlu accuacy : ");
			retour.append(nluAccuacy);
			retour.append("%\n");
		}
		return retour.toString();
	}

	private String getTrainWarns() {
		final long coreAccuacy = trainerRasaHandler.getCoreAccuacy();
		final long nluAccuacy = trainerRasaHandler.getNluAccuacy();

		final StringBuilder retour = new StringBuilder();
		if (coreAccuacy != -1 && coreAccuacy < 90) {
			retour.append("Warning, Core accuacy is low (");
			retour.append(coreAccuacy);
			retour.append("%), consider modify training parameters.\n");
		}
		if (nluAccuacy != -1 && nluAccuacy < 90) {
			retour.append("Warning, NLU accuacy is low (");
			retour.append(nluAccuacy);
			retour.append("%), consider modify training parameters.\n");
		}
		return retour.toString();
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