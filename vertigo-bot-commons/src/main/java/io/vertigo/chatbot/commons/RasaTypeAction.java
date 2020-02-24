package io.vertigo.chatbot.commons;

public enum RasaTypeAction {
	OPEN(false),
	RESTART(false),
	MESSAGE(true),
	RESPONSE_INFO(true),
	BUTTON(true),
	RATING(false);

	private boolean isUserMessage;

	private RasaTypeAction(final boolean isUserMessage) {
		this.isUserMessage = isUserMessage;
	}

	/**
	 * @return the isUserMessage
	 */
	public boolean isUserMessage() {
		return isUserMessage;
	}

}
