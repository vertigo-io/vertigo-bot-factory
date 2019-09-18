package io.vertigo.chatbot.designer.builder.services;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;

import io.vertigo.chatbot.commons.dao.IntentDAO;
import io.vertigo.chatbot.commons.dao.IntentTrainingSentenceDAO;
import io.vertigo.chatbot.commons.dao.TrainingDAO;
import io.vertigo.chatbot.commons.dao.UtterTextDAO;
import io.vertigo.chatbot.commons.domain.BotExport;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.Intent;
import io.vertigo.chatbot.commons.domain.IntentTrainingSentence;
import io.vertigo.chatbot.commons.domain.RunnerInfo;
import io.vertigo.chatbot.commons.domain.SmallTalkExport;
import io.vertigo.chatbot.commons.domain.TrainerInfo;
import io.vertigo.chatbot.commons.domain.Training;
import io.vertigo.chatbot.commons.domain.UtterText;
import io.vertigo.chatbot.designer.builder.BuilderPAO;
import io.vertigo.chatbot.designer.commons.webservices.JaxrsProvider;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.component.Component;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.dynamo.domain.util.VCollectors;
import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.dynamo.impl.file.model.StreamFile;
import io.vertigo.lang.VSystemException;
import io.vertigo.lang.VUserException;

@Transactional
public class ExecutorBridgeServices implements Component {

	@Inject
	private DesignerServices designerServices;

	@Inject
	private IntentDAO intentDAO;

	@Inject
	private TrainingDAO trainingDAO;

	@Inject
	private BuilderPAO builderPAO;

	@Inject
	private IntentTrainingSentenceDAO intentTrainingSentenceDAO;

	@Inject
	private UtterTextDAO utterTextDAO;

	@Inject
	private JaxrsProvider jaxrsProvider;

	public void trainAgent(final Long botId) {
		final Long versionNumber = builderPAO.getNextModelNumber(botId);

		final Training training = new Training();
		training.setBotId(botId);
		training.setStartTime(Instant.now());
		training.setStatus("TRAINING");
		training.setVersionNumber(versionNumber);

		trainingDAO.save(training);


		final Map<String, Object> requestData = new HashMap<>();
		requestData.put("botExport", exportBot(botId));
		requestData.put("smallTalkExport", exportSmallTalk(botId));
		requestData.put("modelId", versionNumber);

		jaxrsProvider.getWebTarget().path("/api/chatbot/train")
		.request(MediaType.APPLICATION_JSON)
		.post(Entity.json(requestData));

	}

	public void stopAgent() {
		jaxrsProvider.getWebTarget().path("/api/chatbot/train")
		.request(MediaType.APPLICATION_JSON)
		.delete();

	}

	public TrainerInfo getTrainingState() {
		final Response response = jaxrsProvider.getWebTarget().path("/api/chatbot/trainStatus")
				.request(MediaType.APPLICATION_JSON)
				.get();

		if (response.getStatus() != 200) {
			final TrainerInfo trainerInfo = new TrainerInfo();
			trainerInfo.setName("Impossible d'accéder au service");
			return trainerInfo;
		}

		return response.readEntity(TrainerInfo.class);
	}

	public RunnerInfo getRunnerState() {
		final Response response =  jaxrsProvider.getWebTarget().path("/api/chatbot/runnerStatus")
				.request(MediaType.APPLICATION_JSON)
				.get();

		if (response.getStatus() != 200) {
			final RunnerInfo runnerInfo = new RunnerInfo();
			runnerInfo.setName("Impossible d'accéder au service");
			return runnerInfo;
		}

		return response.readEntity(RunnerInfo.class);
	}

	private BotExport exportBot(final Long botId) {
		final Chatbot bot = designerServices.getChatbotById(botId);
		final UtterText welcomeText = designerServices.getWelcomeTextByBot(bot);
		final UtterText defaultText = designerServices.getDefaultTextByBot(bot);

		final BotExport retour = new BotExport();
		retour.setBot(bot);
		retour.setWelcomeText(welcomeText);
		retour.setDefaultText(defaultText);
		return retour;
	}

	private DtList<SmallTalkExport> exportSmallTalk(final Long botId) {
		final DtList<Intent> intents = intentDAO.exportSmallTalk(botId);

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

		final DtList<SmallTalkExport> retour = new DtList<>(SmallTalkExport.class);
		for (final Intent intent : intents) {
			final SmallTalkExport newExport = new SmallTalkExport();
			newExport.setIntent(intent);
			newExport.setIntentTrainingSentences(trainingSentencesMap.get(intent.getIntId()));
			newExport.setUtterTexts(utterTextsMap.get(intent.getIntId()));

			retour.add(newExport);
		}

		return retour;
	}

	public VFile fetchModel(final Long id) {
		final Response response = jaxrsProvider.getWebTarget().path("/api/chatbot/model/"+id)
				.request(MediaType.APPLICATION_OCTET_STREAM)
				.get();

		response.bufferEntity();

		return new StreamFile(id + ".tar.gz", "", Instant.now(), -1, () -> response.readEntity(InputStream.class));
	}

	public void loadModel(final VFile model) {
		final StreamDataBodyPart bodyPart;
		try {
			bodyPart = new StreamDataBodyPart("model", model.createInputStream(), model.getFileName());
		} catch (final IOException e) {
			throw new VSystemException(e, "Impossible de lire le modèle");
		}

		final Response response;
		try (final FormDataMultiPart fdmp = new FormDataMultiPart();
				final MultiPart multiPart = fdmp.bodyPart(bodyPart);) {
			response = jaxrsProvider.getWebTarget().path("/api/chatbot/model")
					.request(MediaType.APPLICATION_JSON)
					.put(Entity.entity(multiPart, multiPart.getMediaType()));

		} catch (final IOException e) {
			throw new VSystemException(e, "Impossible de lire le modèle");
		}

		if (response.getStatus() != 204) {
			throw new VUserException("Impossible de charger le modèle");
		}
	}
}