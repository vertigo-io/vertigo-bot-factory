package io.vertigo.chatbot.engine.plugins.bt.confluence.model.result;

public class ConfluenceSearchResponse extends AbstractConfluenceResponses {

	private ConfluenceSearchResult[] results;

	private Integer limit;

	private Integer size;

	public ConfluenceSearchResult[] getResults() {
		return results;
	}

	public void setResults(final ConfluenceSearchResult[] results) {
		this.results = results;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(final Integer limit) {
		this.limit = limit;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(final Integer size) {
		this.size = size;
	}
}
