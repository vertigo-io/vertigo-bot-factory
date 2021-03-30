package io.vertigo.chatbot.executor.atlassian.model.confluence.result;

public class ConfluenceSearchResult {

	private double id;

	private String type;

	private String status;

	private String title;

	public double getId() {
		return id;
	}

	public void setId(final double id) {
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

}
