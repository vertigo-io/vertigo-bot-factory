package io.vertigo.chatbot.engine.plugins.bt.jira.model;

public class JsmServiceDesk {

	private String id;
	private Long projectId;
	private String projectKey;
	private String projectName;
	private String name;

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(final Long projectId) {
		this.projectId = projectId;
	}

	public String getProjectKey() {
		return projectKey;
	}

	public void setProjectKey(final String projectKey) {
		this.projectKey = projectKey;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(final String projectName) {
		this.projectName = projectName;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}
}
