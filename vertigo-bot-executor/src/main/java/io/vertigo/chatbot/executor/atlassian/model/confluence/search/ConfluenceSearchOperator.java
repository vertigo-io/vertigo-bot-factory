package io.vertigo.chatbot.executor.atlassian.model.confluence.search;

public enum ConfluenceSearchOperator {

	EQUALS("="),

	LIKE("~");

	ConfluenceSearchOperator(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	private String value;
}
