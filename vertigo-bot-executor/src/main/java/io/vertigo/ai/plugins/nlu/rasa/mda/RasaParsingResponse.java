package io.vertigo.ai.plugins.nlu.rasa.mda;

import java.util.List;

public class RasaParsingResponse {

	public String getText() {
		return text;
	}

	public void setText(final String text) {
		this.text = text;
	}

	public Object[] getEntities() {
		return entities;
	}

	public void setEntities(final Object[] entities) {
		this.entities = entities;
	}

	public RasaIntentWithConfidence getIntent() {
		return intent;
	}

	public void setIntent(final RasaIntentWithConfidence intent) {
		this.intent = intent;
	}

	public List<RasaIntentWithConfidence> getIntent_ranking() {
		return intent_ranking;
	}

	public void setIntent_ranking(final List<RasaIntentWithConfidence> intent_ranking) {
		this.intent_ranking = intent_ranking;
	}

	private String text;

	private Object[] entities;

	private RasaIntentWithConfidence intent;

	private List<RasaIntentWithConfidence> intent_ranking;

}
