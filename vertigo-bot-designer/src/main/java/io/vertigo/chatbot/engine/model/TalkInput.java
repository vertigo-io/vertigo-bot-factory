package io.vertigo.chatbot.engine.model;

import java.util.Map;
import java.util.UUID;

public final class TalkInput {

	private UUID sender;
	private String message;
	private Map<String, Object> metadatas;

	public Map<String, Object> getMetadatas() {
		return metadatas;
	}

	public void setMetadatas(final Map<String, Object> metadatas) {
		this.metadatas = metadatas;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

	public UUID getSender() {
		return sender;
	}

	public void setSender(final UUID sender) {
		this.sender = sender;
	}

}
