package io.vertigo.chatbot.executor.atlassian.model.confluence.result;

public class ConfluenceLinks {

	public ConfluenceLinks() {
	}

	//rest request
	private String self;

	private String base;

	private String context;

	public String getSelf() {
		return self;
	}

	public void setSelf(final String self) {
		this.self = self;
	}

	public String getBase() {
		return base;
	}

	public void setBase(final String base) {
		this.base = base;
	}

	public String getContext() {
		return context;
	}

	public void setContext(final String context) {
		this.context = context;
	}

}
