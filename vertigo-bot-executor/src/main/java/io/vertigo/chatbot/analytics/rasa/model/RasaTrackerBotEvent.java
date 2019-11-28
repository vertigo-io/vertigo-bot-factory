package io.vertigo.chatbot.analytics.rasa.model;

import java.util.Map;

public class RasaTrackerBotEvent extends AbstractRasaTrackerEvent {

	private String text;
	private Map<String, Object> data;
	// "data": {"elements": null, "quick_replies": null, "buttons": null, "attachment": null, "image": null, "custom": null}

	private Map<String, Object> metadata;
	// "metadata": {}

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
	 * @return the data
	 */
	public Map<String, Object> getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(final Map<String, Object> data) {
		this.data = data;
	}
	/**
	 * @return the metadata
	 */
	public Map<String, Object> getMetadata() {
		return metadata;
	}
	/**
	 * @param metadata the metadata to set
	 */
	public void setMetadata(final Map<String, Object> metadata) {
		this.metadata = metadata;
	}

}
