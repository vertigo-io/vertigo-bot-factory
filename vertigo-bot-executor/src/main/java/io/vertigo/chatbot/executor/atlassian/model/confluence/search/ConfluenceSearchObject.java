package io.vertigo.chatbot.executor.atlassian.model.confluence.search;

public abstract class ConfluenceSearchObject {

	public ConfluenceSearchObject() {
	}

	public abstract String accept(ConfluenceVisitor visitor);

}
