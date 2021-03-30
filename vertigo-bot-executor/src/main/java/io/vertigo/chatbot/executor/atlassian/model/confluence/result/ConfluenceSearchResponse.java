package io.vertigo.chatbot.executor.atlassian.model.confluence.result;

public class ConfluenceSearchResponse extends AbstractConfluenceResponses {

	private ConfluenceSearchResult[] results;

	private Integer limit;

	private Integer size;

	public ConfluenceSearchResponse() {
		super();
	}

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
