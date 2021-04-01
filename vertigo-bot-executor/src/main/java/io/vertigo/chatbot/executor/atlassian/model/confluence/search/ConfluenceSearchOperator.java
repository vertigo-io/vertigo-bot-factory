package io.vertigo.chatbot.executor.atlassian.model.confluence.search;

public enum ConfluenceSearchOperator {

	EQUALS("="),

	CONTAINS("~"),

	AND("AND"),

	OR("OR");

	ConfluenceSearchOperator(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	private String value;
}
