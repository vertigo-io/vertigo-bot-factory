package io.vertigo.chatbot.engine.model;

import java.util.UUID;

public final class TalkInput {

	private UUID sender;
	private String message;
	private boolean isButton;

	public boolean isButton() {
		return isButton;
	}

	public void setButton(final boolean isButton) {
		this.isButton = isButton;
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
