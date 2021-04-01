package io.vertigo.chatbot.executor.atlassian.model.confluence.search;

public class ConfluenceVisitor {

	private static final String BLANK_SPACE = " ";

	public String visitMultipleSearch(final MultipleConfluenceSearch multipleConfluenceSearch) {
		final StringBuilder builder = new StringBuilder();
		builder.append("( ");
		builder.append(multipleConfluenceSearch.getFirstObject().accept(this));
		addSeparator(builder);
		builder.append(multipleConfluenceSearch.getOperator().getValue());
		addSeparator(builder);
		builder.append(multipleConfluenceSearch.getSecondObject().accept(this));
		builder.append(" ) ");
		return builder.toString();
	}

	public String visitSingleSearch(final SingleConfluenceSearch singleConfluenceSearch) {
		final StringBuilder builder = new StringBuilder();
		builder.append(singleConfluenceSearch.getKey());
		addSeparator(builder);
		builder.append(singleConfluenceSearch.getOperator().getValue());
		addSeparator(builder);
		builder.append(getAllStringForValue(singleConfluenceSearch.getValue()));
		return builder.toString();
	}

	/**
	 * Return "value" for rest request if value is a phrase
	 *
	 * @param value the value to test
	 * @return "value" if a phrase else value
	 */
	private String getAllStringForValue(final String value) {
		return value.split(" ").length == 1 ? value : "\"" + value + "\"";
	}

	private void addSeparator(final StringBuilder builder) {
		builder.append(BLANK_SPACE);
	}
}
