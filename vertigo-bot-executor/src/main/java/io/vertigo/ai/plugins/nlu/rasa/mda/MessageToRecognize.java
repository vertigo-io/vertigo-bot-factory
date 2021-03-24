package io.vertigo.ai.plugins.nlu.rasa.mda;

public class MessageToRecognize {

	public MessageToRecognize(final String text) {
		super();
		this.text = text;
	}

	private String text;

	private String message_id;

	public String getText() {
		return text;
	}

	public void setText(final String text) {
		this.text = text;
	}

	public String getMessage_id() {
		return message_id;
	}

	public void setMessage_id(final String message_id) {
		this.message_id = message_id;
	}
}
