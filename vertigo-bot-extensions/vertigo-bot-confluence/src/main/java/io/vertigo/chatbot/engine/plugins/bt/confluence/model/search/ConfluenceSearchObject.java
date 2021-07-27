package io.vertigo.chatbot.engine.plugins.bt.confluence.model.search;

public abstract class ConfluenceSearchObject {

	public ConfluenceSearchObject() {
	}

	public abstract String accept(ConfluenceVisitor visitor);

}
