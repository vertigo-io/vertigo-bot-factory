package io.vertigo.chatbot.executor.model;

public class IncomeMessage {
	private String message;
	private String sender;

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(final String message) {
		this.message = message;
	}
	/**
	 * @return the sender
	 */
	public String getSender() {
		return sender;
	}
	/**
	 * @param sender the sender to set
	 */
	public void setSender(final String sender) {
		this.sender = sender;
	}


}
