package io.vertigo.chatbot.engine.plugins.bt.confluence.model.result;

public abstract class AbstractConfluenceResponses {

	private ConfluenceLinks _links;

	public ConfluenceLinks getLinks() {
		return _links;
	}

	public void setLinks(final ConfluenceLinks links) {
		this._links = links;
	}
}
