package io.vertigo.chatbot.executor.atlassian.model.confluence.result;

public abstract class AbstractConfluenceResponses {

	private ConfluenceLinks links;

	public ConfluenceLinks getLinks() {
		return links;
	}

	public void setLinks(final ConfluenceLinks links) {
		this.links = links;
	}
}
