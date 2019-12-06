package io.vertigo.chatbot.analytics.rasa.model;

import java.time.Instant;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import io.vertigo.chatbot.analytics.rasa.util.GsonRasaTimestampDeserializer;

public abstract class AbstractRasaTrackerEvent {

	@SerializedName("sender_id")
	private String senderId;

	@JsonAdapter(GsonRasaTimestampDeserializer.class)
	private Instant timestamp;

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
	public Instant getTimestamp() {
		return timestamp;
	}
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(final Instant timestamp) {
		this.timestamp = timestamp;
	}

}
