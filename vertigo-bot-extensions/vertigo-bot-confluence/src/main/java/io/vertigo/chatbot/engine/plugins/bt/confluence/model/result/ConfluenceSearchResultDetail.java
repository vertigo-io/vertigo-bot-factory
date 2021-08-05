package io.vertigo.chatbot.engine.plugins.bt.confluence.model.result;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConfluenceSearchResultDetail {

	private String id;

	private String type;

	private String status;

	private String title;

	@JsonProperty(value = "_links")
	private ConfluenceSearchLinks links;

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public ConfluenceSearchLinks getLinks() {
		return links;
	}

	public void setLinks(final ConfluenceSearchLinks links) {
		this.links = links;
	}

}
