package io.vertigo.chatbot.engine.plugins.bt.confluence.model.result;

public final class ConfluenceSpace {

	private double id;

	private String key;

	private String name;

	public void setId(final double id) {
		this.id = id;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public double getId() {
		return id;
	}

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public ConfluenceSpace() {

	}

	public ConfluenceSpace(final double id, final String key, final String name) {
		super();
		this.id = id;
		this.key = key;
		this.name = name;
	}
}
