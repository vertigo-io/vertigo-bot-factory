package io.vertigo.chatbot.engine.plugins.bt.jira.impl;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.OptionalIterable;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Field;
import com.atlassian.jira.rest.client.api.domain.FieldType;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Priority;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Transition;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.chatbot.commons.domain.JiraFieldSettingExport;
import io.vertigo.chatbot.commons.domain.JiraSettingExport;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JiraField;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtList;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class JiraServerService implements Component, IJiraService {

	private String baseJira;
	private String user;
	private String password;
	private String project;
	private JiraRestClient jiraRestClient;
	private DtList<JiraFieldSettingExport> jiraFieldSettingExports;

	public void refreshConfig(JiraSettingExport jiraSettingExport, DtList<JiraFieldSettingExport> jiraFieldSettingExports) {
		this.baseJira = jiraSettingExport.getUrl();
		this.user = jiraSettingExport.getLogin();
		this.password = jiraSettingExport.getPassword();
		this.project = jiraSettingExport.getProject();
		jiraRestClient = createJiraRestClient();
		this.jiraFieldSettingExports = jiraFieldSettingExports;
	}

	public DtList<JiraFieldSettingExport> getJiraFieldSettingExports() {
		return jiraFieldSettingExports;
	}

	private JiraRestClient createJiraRestClient() {
		return new AsynchronousJiraRestClientFactory()
				.createWithBasicHttpAuthentication(URI.create(baseJira), this.user, this.password);
	}

	public BasicIssue createIssue(final BlackBoard bb, final List<JiraField> jfFields, List<IJiraFieldService> fieldServices) {
		final IssueRestClient issueClient = jiraRestClient.getIssueClient();
		final IssueInputBuilder iib = new IssueInputBuilder();
		iib.setProjectKey(project);
		jfFields.forEach(jiraField -> fieldServices.forEach(fieldService -> {
			if (fieldService.supports(jiraField.getFieldType())) {
				fieldService.processTicket(bb, iib, jiraField);
			}
		}));
		final IssueInput issue = iib.build();
		return issueClient.createIssue(issue).claim();
	}


	public List<String> getIssues(final String jqlSearch) {
		final SearchRestClient searchClient = jiraRestClient.getSearchClient();
		final SearchResult searchResult = searchClient.searchJql(jqlSearch).claim();
		return StreamSupport.stream(searchResult.getIssues().spliterator(), false)
				.map(x -> createLinkUrl(x.getKey()))
				.collect(Collectors.toList());
	}

	@Override
	public String createIssueJiraCommand(final BlackBoard bb, final List<JiraField> jiraFields, List<IJiraFieldService> fieldServices) {
		final var createdIssue = createIssue(bb, jiraFields, fieldServices);
		return createLinkUrl(createdIssue.getKey());

	}

	private String createLinkUrl(final String key) {
		final String url = baseJira + "/browse/" + key;
		final StringBuilder builder = new StringBuilder();
		builder.append("<a href=\"");
		builder.append(url);
		builder.append("\">");
		builder.append(key);
		builder.append("</a>");
		return builder.toString();
	}

	public Project getProject() {
		return jiraRestClient.getProjectClient().getProject(project).claim();
	}

	public OptionalIterable<IssueType> getIssueTypes() {
		return getProject().getIssueTypes();
	}

	public List<Version> getVersions() {
		return (List<Version>) getProject().getVersions();
	}

	public List<User> findUserByUsername(String username) {
		return (List<User>) jiraRestClient.getUserClient().findUsers(username).claim();
	}

	public List<Priority> getPriorities() {
		return (List<Priority>) jiraRestClient.getMetadataClient().getPriorities().claim();
	}

	public Issue getIssueByKey(String key) {
		return jiraRestClient.getIssueClient().getIssue(key).claim();
	}

	public List<Transition> getIssueTransitions(Issue issue) {
		return (List<Transition>) jiraRestClient.getIssueClient().getTransitions(issue).claim();
	}

	public List<Field> getCustomFields() {
		List<Field> allFields = (List<Field>) jiraRestClient.getMetadataClient().getFields().claim();
		return allFields.stream().filter(field -> field.getFieldType() == FieldType.CUSTOM).collect(Collectors.toList());
	}

	public Iterable<BasicComponent> getComponents() {
		return getProject().getComponents();
	}

}
