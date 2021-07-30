package io.vertigo.chatbot.engine.plugins.bt.confluence.model.search;

public interface ConfluenceSearchObject {

	public abstract String accept(ConfluenceVisitor visitor);

}
