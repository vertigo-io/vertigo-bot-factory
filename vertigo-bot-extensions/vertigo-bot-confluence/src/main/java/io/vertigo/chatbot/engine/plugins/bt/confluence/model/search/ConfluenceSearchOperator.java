package io.vertigo.chatbot.engine.plugins.bt.confluence.model.search;

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
