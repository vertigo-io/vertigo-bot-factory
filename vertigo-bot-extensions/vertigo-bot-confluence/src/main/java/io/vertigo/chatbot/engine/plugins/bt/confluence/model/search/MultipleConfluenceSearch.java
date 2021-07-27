package io.vertigo.chatbot.engine.plugins.bt.confluence.model.search;

public class MultipleConfluenceSearch extends ConfluenceSearchObject {

	private ConfluenceSearchObject firstObject;

	private ConfluenceSearchObject secondObject;

	private ConfluenceSearchOperator operator;

	public MultipleConfluenceSearch(final ConfluenceSearchObject firstObject, final ConfluenceSearchObject secondObject, final ConfluenceSearchOperator operator) {
		super();
		this.firstObject = firstObject;
		this.secondObject = secondObject;
		this.operator = operator;
	}

	public ConfluenceSearchObject getFirstObject() {
		return firstObject;
	}

	public void setFirstObject(final ConfluenceSearchObject firstObject) {
		this.firstObject = firstObject;
	}

	public ConfluenceSearchObject getSecondObject() {
		return secondObject;
	}

	public void setSecondObject(final ConfluenceSearchObject secondObject) {
		this.secondObject = secondObject;
	}

	public ConfluenceSearchOperator getOperator() {
		return operator;
	}

	public void setOperator(final ConfluenceSearchOperator operator) {
		this.operator = operator;
	}

	@Override
	public String accept(final ConfluenceVisitor visitor) {
		return visitor.visitMultipleSearch(this);
	}
}
