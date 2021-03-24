package io.vertigo.ai.plugins.nlu.rasa.mda;

import java.util.List;

public final class RasaParsingResponse {

	private String text;
	private List<?> entities;
	private RasaIntentWithConfidence intent;
	private List<RasaIntentWithConfidence> intent_ranking;

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(final String text) {
		this.text = text;
	}

	/**
	 * @return the entities
	 */
	public List<?> getEntities() {
		return entities;
	}

	/**
	 * @param entities the entities to set
	 */
	public void setEntities(final List<?> entities) {
		this.entities = entities;
	}

	/**
	 * @return the intent
	 */
	public RasaIntentWithConfidence getIntent() {
		return intent;
	}

	/**
	 * @param intent the intent to set
	 */
	public void setIntent(final RasaIntentWithConfidence intent) {
		this.intent = intent;
	}

	/**
	 * @return the intent_ranking
	 */
	public List<RasaIntentWithConfidence> getIntent_ranking() {
		return intent_ranking;
	}

	/**
	 * @param intent_ranking the intent_ranking to set
	 */
	public void setIntent_ranking(final List<RasaIntentWithConfidence> intent_ranking) {
		this.intent_ranking = intent_ranking;
	}

}
