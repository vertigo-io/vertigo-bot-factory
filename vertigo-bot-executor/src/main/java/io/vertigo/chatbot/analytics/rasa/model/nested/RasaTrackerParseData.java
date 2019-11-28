package io.vertigo.chatbot.analytics.rasa.model.nested;

import com.google.gson.annotations.SerializedName;

public class RasaTrackerParseData {

	private RasaTrackerIntent intent;

	private RasaTrackerEntity[] entities;

	@SerializedName("intent_ranking")
	private RasaTrackerIntent[] intentRanking;

	private String text;

	/**
	 * @return the intent
	 */
	public RasaTrackerIntent getIntent() {
		return intent;
	}

	/**
	 * @param intent the intent to set
	 */
	public void setIntent(final RasaTrackerIntent intent) {
		this.intent = intent;
	}

	/**
	 * @return the entities
	 */
	public RasaTrackerEntity[] getEntities() {
		return entities;
	}

	/**
	 * @param entities the entities to set
	 */
	public void setEntities(final RasaTrackerEntity[] entities) {
		this.entities = entities;
	}

	/**
	 * @return the intentRanking
	 */
	public RasaTrackerIntent[] getIntentRanking() {
		return intentRanking;
	}

	/**
	 * @param intentRanking the intentRanking to set
	 */
	public void setIntentRanking(final RasaTrackerIntent[] intentRanking) {
		this.intentRanking = intentRanking;
	}

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


}
