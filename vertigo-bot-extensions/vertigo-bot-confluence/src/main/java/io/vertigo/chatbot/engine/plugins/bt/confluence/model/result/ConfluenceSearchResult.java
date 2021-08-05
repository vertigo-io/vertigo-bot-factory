package io.vertigo.chatbot.engine.plugins.bt.confluence.model.result;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConfluenceSearchResult {

	@JsonProperty(value = "content")
	private ConfluenceSearchResultDetail detail;

	private String title;

	private String url;

	public ConfluenceSearchResultDetail getDetail() {
		return detail;
	}

	public void setDetail(final ConfluenceSearchResultDetail detail) {
		this.detail = detail;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}
}
