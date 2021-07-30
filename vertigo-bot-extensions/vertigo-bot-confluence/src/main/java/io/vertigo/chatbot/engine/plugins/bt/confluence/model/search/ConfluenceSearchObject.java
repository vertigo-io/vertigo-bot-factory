package io.vertigo.chatbot.engine.plugins.bt.confluence.model.search;

public abstract class ConfluenceSearchObject {

	protected ConfluenceSearchObject() {
	}

	public abstract String accept(ConfluenceVisitor visitor);

}
