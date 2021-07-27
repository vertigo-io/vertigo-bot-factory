package io.vertigo.chatbot.engine.plugins.bt.confluence.model.result;

public abstract class AbstractConfluenceResponses {

	private ConfluenceLinks links;

	public ConfluenceLinks getLinks() {
		return links;
	}

	public void setLinks(final ConfluenceLinks links) {
		this.links = links;
	}
}
