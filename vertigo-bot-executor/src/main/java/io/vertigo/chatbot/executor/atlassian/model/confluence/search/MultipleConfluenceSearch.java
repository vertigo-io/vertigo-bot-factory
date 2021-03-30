package io.vertigo.chatbot.executor.atlassian.model.confluence.search;

import java.util.HashMap;
import java.util.Map;

public class MultipleConfluenceSearch extends ConfluenceSearchObject {

	private Map<String, ConfluenceSearchOperator> value = new HashMap<>();

	public MultipleConfluenceSearch(final String key, final Map<String, ConfluenceSearchOperator> value) {
		super(key);
		this.value = value;
	}

	public Map<String, ConfluenceSearchOperator> getValue() {
		return value;
	}

	public void setValue(final Map<String, ConfluenceSearchOperator> value) {
		this.value = value;
	}

	@Override
	public String accept(final ConfluenceVisitor visitor) {
		return visitor.visitMultipleSearch(this);
	}
}
