package io.vertigo.chatbot.executor.atlassian.model.confluence.search;

import java.util.Map.Entry;

public class ConfluenceVisitor {

	private static final String BLANK_SPACE = " ";
	private static final String OR_SEPARATOR = " OR ";

	public String visitMultipleSearch(final MultipleConfluenceSearch multipleConfluenceSearch) {
		final StringBuilder builder = new StringBuilder();
		final String key = multipleConfluenceSearch.getKey();
		builder.append("( ");
		for (final Entry<String, ConfluenceSearchOperator> entry : multipleConfluenceSearch.getValue().entrySet()) {
			builder.append(writeSingleOperation(key, entry.getValue(), entry.getKey()));
			builder.append(OR_SEPARATOR);
		}
		//remove the last OR_SEPARATOR
		builder.setLength(builder.length() - 3);

		builder.append(" ) ");
		return builder.toString();
	}

	public String visitSingleSearch(final SingleConfluenceSearch singleConfluenceSearch) {
		return writeSingleOperation(singleConfluenceSearch.getKey(), singleConfluenceSearch.getOperator(), singleConfluenceSearch.getValue());
	}

	private String writeSingleOperation(final String key, final ConfluenceSearchOperator operator, final String value) {
		final StringBuilder builder = new StringBuilder();
		builder.append(key);
		addSeparator(builder);
		builder.append(operator.getValue());
		addSeparator(builder);
		builder.append(getAllStringForValue(value));
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
