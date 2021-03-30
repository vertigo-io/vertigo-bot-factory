package io.vertigo.chatbot.executor.atlassian.model.confluence.search;

public class SingleConfluenceSearch extends ConfluenceSearchObject {

	public SingleConfluenceSearch(final String key, final ConfluenceSearchOperator operator, final String value) {
		super(key);
		this.operator = operator;
		this.value = value;
	}

	private ConfluenceSearchOperator operator;

	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	public String accept(final ConfluenceVisitor visitor) {
		return visitor.visitSingleSearch(this);
	}

	public ConfluenceSearchOperator getOperator() {
		return operator;
	}

	public void setOperator(final ConfluenceSearchOperator operator) {
		this.operator = operator;
	}
}
