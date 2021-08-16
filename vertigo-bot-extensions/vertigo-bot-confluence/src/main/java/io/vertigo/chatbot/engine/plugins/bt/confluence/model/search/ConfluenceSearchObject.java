package io.vertigo.chatbot.engine.plugins.bt.confluence.model.search;

public interface ConfluenceSearchObject {

	String accept(ConfluenceVisitor visitor);

}
