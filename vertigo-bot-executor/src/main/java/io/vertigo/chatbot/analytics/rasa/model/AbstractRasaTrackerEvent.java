package io.vertigo.chatbot.analytics.rasa.model;

import com.google.gson.annotations.SerializedName;

public abstract class AbstractRasaTrackerEvent {

	@SerializedName("sender_id")
	private String senderId;
	private String timestamp;

	/**
	 * @return the senderId
	 */
	public String getSenderId() {
		return senderId;
	}
	/**
	 * @param senderId the senderId to set
	 */
	public void setSenderId(final String senderId) {
		this.senderId = senderId;
	}
	/**
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(final String timestamp) {
		this.timestamp = timestamp;
	}

}
