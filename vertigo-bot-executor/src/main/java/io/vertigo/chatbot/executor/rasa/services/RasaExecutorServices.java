package io.vertigo.chatbot.executor.rasa.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.ExecutorState;
import io.vertigo.chatbot.commons.domain.IntentTrainingSentence;
import io.vertigo.chatbot.commons.domain.SmallTalkExport;
import io.vertigo.chatbot.commons.domain.UtterText;
import io.vertigo.chatbot.executor.domain.RasaConfig;
import io.vertigo.chatbot.executor.rasa.bridge.RasaHandler;
import io.vertigo.chatbot.executor.rasa.config.RasaConfigBuilder;
import io.vertigo.core.component.Component;
import io.vertigo.dynamo.domain.model.DtList;

public class RasaExecutorServices implements Component {
	
	@Inject
	private RasaHandler rasaHandler;
	
	public void trainModel(DtList<SmallTalkExport> data) {
		rasaHandler.trainModel(generateRasaConfig(data));
	}
	
	private RasaConfig generateRasaConfig(DtList<SmallTalkExport> data) {
		RasaConfigBuilder rasaConfigBuilder = new RasaConfigBuilder();
		
		for (SmallTalkExport st : data) {
			List<String> utterTexts = st.getUtterTexts().stream()
					.map(UtterText::getText)
					.collect(Collectors.toList());
			
			List<String> trainingSentences = st.getIntentTrainingSentences().stream()
					.map(IntentTrainingSentence::getText)
					.collect(Collectors.toList());
			
			rasaConfigBuilder.addSmallTalk(st.getIntent().getTitle(), trainingSentences, utterTexts);
		}
		
		return rasaConfigBuilder.build();
	}
	
	public ExecutorState getState() {
		return rasaHandler.getState();
	}
	
}