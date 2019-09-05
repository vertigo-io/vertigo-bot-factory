package io.vertigo.chatbot.designer.services;

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

import io.vertigo.chatbot.commons.JaxrsProvider;
import io.vertigo.chatbot.commons.dao.IntentDAO;
import io.vertigo.chatbot.commons.dao.IntentTrainingSentenceDAO;
import io.vertigo.chatbot.commons.dao.UtterTextDAO;
import io.vertigo.chatbot.commons.domain.ExecutorTrainingCallback;
import io.vertigo.chatbot.commons.domain.Intent;
import io.vertigo.chatbot.commons.domain.IntentTrainingSentence;
import io.vertigo.chatbot.commons.domain.SmallTalkExport;
import io.vertigo.chatbot.commons.domain.TrainerInfo;
import io.vertigo.chatbot.commons.domain.UtterText;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.component.Component;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.dynamo.domain.util.VCollectors;
import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.dynamo.impl.file.model.StreamFile;

@Transactional
public class ExecutorBridgeServices implements Component {

	@Inject
	private IntentDAO intentDAO;

	@Inject
	private IntentTrainingSentenceDAO intentTrainingSentenceDAO;

	@Inject
	private UtterTextDAO utterTextDAO;

	@Inject
	private JaxrsProvider jaxrsProvider;

	public void trainAgent() {
		final DtList<SmallTalkExport> export = exportSmallTalk();

		final Map<String, Object> requestData = new HashMap<>();
		requestData.put("export", export);
		requestData.put("id", 2L);
		//		final String json = jsonEngine.toJson(requestData);

		jaxrsProvider.getWebTarget().path("/api/chatbot/train")
		.request(MediaType.APPLICATION_JSON)
		.post(Entity.json(requestData));

	}

	public TrainerInfo getState() {
		return jaxrsProvider.getWebTarget().path("/api/chatbot/state")
				.request(MediaType.APPLICATION_JSON)
				.get(TrainerInfo.class);
	}

	private DtList<SmallTalkExport> exportSmallTalk() {
		final DtList<Intent> intents = intentDAO.exportSmallTalk();

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

	public void fetchModel(final ExecutorTrainingCallback callback) {
		final Response response = jaxrsProvider.getWebTarget().path("/api/chatbot/model/"+callback.getTrainedModelVersion())
				.request(MediaType.APPLICATION_OCTET_STREAM)
				.get();

		System.out.println(response.bufferEntity());

		final VFile file = new StreamFile(callback.getTrainedModelVersion()+".zip", "", Instant.now(), -1, () -> response.readEntity(InputStream.class));

		System.out.println(file);
	}
}