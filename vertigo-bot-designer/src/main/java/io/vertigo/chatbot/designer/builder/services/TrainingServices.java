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

import io.vertigo.chatbot.commons.dao.ChatbotNodeDAO;
import io.vertigo.chatbot.commons.dao.NluTrainingSentenceDAO;
import io.vertigo.chatbot.commons.dao.TrainingDAO;
import io.vertigo.chatbot.commons.dao.UtterTextDAO;
import io.vertigo.chatbot.commons.domain.BotExport;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.commons.domain.ExecutorTrainingCallback;
import io.vertigo.chatbot.commons.domain.NluTrainingSentence;
import io.vertigo.chatbot.commons.domain.RunnerInfo;
import io.vertigo.chatbot.commons.domain.SmallTalk;
import io.vertigo.chatbot.commons.domain.SmallTalkExport;
import io.vertigo.chatbot.commons.domain.TrainerInfo;
import io.vertigo.chatbot.commons.domain.Training;
import io.vertigo.chatbot.commons.domain.UtterText;
import io.vertigo.chatbot.designer.builder.BuilderPAO;
import io.vertigo.chatbot.designer.commons.services.FileServices;
import io.vertigo.chatbot.designer.commons.webservices.ExecutorJaxrsProvider;
import io.vertigo.chatbot.domain.DtDefinitions.TrainingFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.component.Component;
import io.vertigo.dynamo.criteria.Criterions;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.dynamo.domain.model.DtListState;
import io.vertigo.dynamo.domain.model.FileInfoURI;
import io.vertigo.dynamo.domain.util.VCollectors;
import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.dynamo.impl.file.model.StreamFile;
import io.vertigo.lang.VSystemException;
import io.vertigo.lang.VUserException;

@Transactional
public class TrainingServices implements Component {

	@Inject
	private DesignerServices designerServices;

	@Inject
	private FileServices fileServices;

	@Inject
	private TrainingDAO trainingDAO;

	@Inject
	private BuilderPAO builderPAO;

	@Inject
	private NluTrainingSentenceDAO nluTrainingSentenceDAO;

	@Inject
	private UtterTextDAO utterTextDAO;

	@Inject
	private ChatbotNodeDAO chatbotNodeDAO;

	@Inject
	private ExecutorJaxrsProvider executorJaxrsProvider;

	public Training trainAgent(final Long botId) {
		builderPAO.cleanOldTrainings(botId);

		final Long versionNumber = builderPAO.getNextModelNumber(botId);

		final Training training = new Training();
		training.setBotId(botId);
		training.setStartTime(Instant.now());
		training.setStatus("TRAINING");
		training.setVersionNumber(versionNumber);

		saveTraining(training);


		final Map<String, Object> requestData = new HashMap<>();
		requestData.put("botExport", exportBot(botId));
		requestData.put("smallTalkExport", exportSmallTalk(botId));
		requestData.put("trainingId", training.getTraId());
		requestData.put("modelId", versionNumber);

		final Response response = executorJaxrsProvider.getWebTarget().path("/api/chatbot/train")
				.request(MediaType.APPLICATION_JSON)
				.post(Entity.json(requestData));

		if (response.getStatus() != 204) {
			throw new VUserException(getMessageFromVUserResponse(response));
		}

		return training;
	}


	@SuppressWarnings("unchecked")
	private String getMessageFromVUserResponse(final Response response) {
		return ((List<String>)response.readEntity(Map.class).get("globalErrors")).get(0);
	}


	public void stopAgent() {
		executorJaxrsProvider.getWebTarget().path("/api/chatbot/train")
		.request(MediaType.APPLICATION_JSON)
		.delete();

	}

	public TrainerInfo getTrainingState() {
		final Response response = executorJaxrsProvider.getWebTarget().path("/api/chatbot/trainStatus")
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
		final Response response =  executorJaxrsProvider.getWebTarget().path("/api/chatbot/runnerStatus")
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
		final DtList<SmallTalk> smallTalks = designerServices.getAllSmallTalksByBotId(botId);

		final List<Long> smallTalkIds = smallTalks.stream()
				.map(SmallTalk::getSmtId)
				.collect(Collectors.toList());

		final Map<Long, DtList<NluTrainingSentence>> trainingSentencesMap = nluTrainingSentenceDAO.exportSmallTalkRelativeTrainingSentence(smallTalkIds)
				.stream()
				.collect(Collectors.groupingBy(NluTrainingSentence::getSmtId,
						VCollectors.toDtList(NluTrainingSentence.class)));

		final Map<Long, DtList<UtterText>> utterTextsMap = utterTextDAO.exportSmallTalkRelativeUtter(smallTalkIds)
				.stream()
				.collect(Collectors.groupingBy(UtterText::getSmtId,
						VCollectors.toDtList(UtterText.class)));

		final DtList<SmallTalkExport> retour = new DtList<>(SmallTalkExport.class);
		for (final SmallTalk smallTalk : smallTalks) {
			final SmallTalkExport newExport = new SmallTalkExport();
			newExport.setSmallTalk(smallTalk);
			newExport.setNluTrainingSentences(trainingSentencesMap.get(smallTalk.getSmtId()));
			newExport.setUtterTexts(utterTextsMap.get(smallTalk.getSmtId()));

			retour.add(newExport);
		}

		return retour;
	}

	public void loadModel(final Long traId) {
		final Training training = getTraining(traId);

		final VFile model = fileServices.getFile(training.getFilIdModel());

		doLoadModel(model);

		// update node-training link
		final ChatbotNode node = designerServices.getDevNodeByBotId(training.getBotId());
		node.setTraId(traId);
		chatbotNodeDAO.save(node);
	}

	private void doLoadModel(final VFile model) {
		final StreamDataBodyPart bodyPart;
		try {
			bodyPart = new StreamDataBodyPart("model", model.createInputStream(), model.getFileName());
		} catch (final IOException e) {
			throw new VSystemException(e, "Impossible de lire le modèle");
		}

		final Response response;
		try (final FormDataMultiPart fdmp = new FormDataMultiPart();
				final MultiPart multiPart = fdmp.bodyPart(bodyPart);) {
			response = executorJaxrsProvider.getWebTarget().path("/api/chatbot/model")
					.request(MediaType.APPLICATION_JSON)
					.put(Entity.entity(multiPart, multiPart.getMediaType()));

		} catch (final IOException e) {
			throw new VSystemException(e, "Impossible de lire le modèle");
		}

		if (response.getStatus() != 204) {
			throw new VUserException("Impossible de charger le modèle");
		}
	}

	public DtList<Training> getAllTrainings(final Long botId) {
		return trainingDAO.findAll(
				Criterions.isEqualTo(TrainingFields.botId, botId),
				DtListState.of(1000, 0, TrainingFields.versionNumber.name(), true)
				);
	}

	public Training getTraining(final Long traId) {
		return trainingDAO.get(traId);
	}

	public Training saveTraining(final Training training) {
		return trainingDAO.save(training);
	}

	public void removeTraining(final Long traId) {
		trainingDAO.delete(traId);
	}

	public void trainingCallback(final ExecutorTrainingCallback callback) {
		final Training training = getTraining(callback.getTrainingId());

		// TODO : Limiter au dernier en cours en mode "trop tard, je refuse ton callback" ?

		if (Boolean.TRUE.equals(callback.getSuccess())) {
			final VFile model = fetchModel(training.getVersionNumber());
			final FileInfoURI fileInfoUri = fileServices.saveFile(model);
			training.setFilIdModel((Long) fileInfoUri.getKey());

			training.setStatus("OK");
		} else {
			training.setStatus("KO");
		}

		training.setLog(callback.getLog());
		training.setInfos(callback.getInfos());
		training.setWarnings(callback.getWarnings());
		training.setEndTime(Instant.now());

		saveTraining(training);
	}


	private VFile fetchModel(final Long id) {
		final Response response = executorJaxrsProvider.getWebTarget().path("/api/chatbot/model/"+id)
				.request(MediaType.APPLICATION_OCTET_STREAM)
				.get();

		response.bufferEntity();

		return new StreamFile(id + ".tar.gz", response.getHeaderString("Content-Type"), Instant.now(), response.getLength(), () -> response.readEntity(InputStream.class));
	}

}