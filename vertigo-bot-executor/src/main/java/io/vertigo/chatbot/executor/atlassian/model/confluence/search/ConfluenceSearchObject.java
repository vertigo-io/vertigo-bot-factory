package io.vertigo.chatbot.executor.atlassian.model.confluence.search;

public abstract class ConfluenceSearchObject {

	public ConfluenceSearchObject(final String key) {
		super();
		this.key = key;
	}

	private String key;

	public String getKey() {
		return key;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public abstract String accept(ConfluenceVisitor visitor);

}
