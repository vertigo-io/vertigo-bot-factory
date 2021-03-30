package io.vertigo.chatbot.executor.atlassian.model.confluence.result;

public class ConfluenceSpaceResponse extends AbstractConfluenceResponses {

	private ConfluenceSpace[] results;

	private Integer size;

	private Integer limit;

	public ConfluenceSpaceResponse() {

	}

	public ConfluenceSpace[] getResults() {
		return results;
	}

	public void setResults(final ConfluenceSpace[] results) {
		this.results = results;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(final Integer size) {
		this.size = size;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(final Integer limit) {
		this.limit = limit;
	}

}
