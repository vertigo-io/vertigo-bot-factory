package io.vertigo.chatbot.engine.plugins.bt.confluence.model.search;

public class SingleConfluenceSearch implements ConfluenceSearchObject {

	public SingleConfluenceSearch(final String key, final ConfluenceSearchOperator operator, final String value) {
		super();
		this.key = key;
		this.operator = operator;
		this.value = value;
	}

	private String key;

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

	public String getKey() {
		return key;
	}

	public void setKey(final String key) {
		this.key = key;
	}
}
