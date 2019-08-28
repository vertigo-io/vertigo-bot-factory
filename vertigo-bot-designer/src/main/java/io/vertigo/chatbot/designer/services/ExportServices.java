package io.vertigo.chatbot.designer.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import io.vertigo.chatbot.commons.dao.IntentDAO;
import io.vertigo.chatbot.commons.dao.IntentTrainingSentenceDAO;
import io.vertigo.chatbot.commons.dao.UtterTextDAO;
import io.vertigo.chatbot.commons.domain.Intent;
import io.vertigo.chatbot.commons.domain.IntentTrainingSentence;
import io.vertigo.chatbot.commons.domain.SmallTalkExport;
import io.vertigo.chatbot.commons.domain.UtterText;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.component.Component;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.dynamo.domain.util.VCollectors;
import io.vertigo.vega.engines.webservice.json.JsonEngine;

@Transactional
public class ExportServices implements Component {

	@Inject
	private IntentDAO intentDAO;
	
	@Inject
	private IntentTrainingSentenceDAO intentTrainingSentenceDAO;
	
	@Inject
	private UtterTextDAO utterTextDAO;
	
    @Inject
    private JsonEngine jsonEngine;

	public void trainAgent() {
		DtList<SmallTalkExport> export = exportSmallTalk();
		String json = jsonEngine.toJson(export);
		
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:8080/vertigo-bot-executor");
		Boolean response = target.path("/api/chatbot/train")
			.request(MediaType.APPLICATION_JSON)
			.post(Entity.json(json), Boolean.class);
		
		System.out.println(response);
	}

	private DtList<SmallTalkExport> exportSmallTalk() {
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
}