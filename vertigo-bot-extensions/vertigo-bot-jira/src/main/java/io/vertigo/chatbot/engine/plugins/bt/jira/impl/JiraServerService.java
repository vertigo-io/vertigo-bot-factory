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

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.chatbot.commons.domain.JiraFieldSettingExport;
import io.vertigo.chatbot.commons.domain.JiraSettingExport;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JiraField;
import io.vertigo.chatbot.executor.model.ExecutorGlobalConfig;
import io.vertigo.chatbot.executor.services.PasswordDecryptionServices;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.Node;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtList;

import static io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider.formatLink;

public class JiraServerService implements Component, IJiraService, Activeable {

	private String baseJira;
	private String user;
	private String password;
	private String project;
	private JiraRestClient jiraRestClient;
	private DtList<JiraFieldSettingExport> jiraFieldSettingExports;

	private PasswordDecryptionServices passwordDecryptionServices;

	@Override
	public void start() {
		passwordDecryptionServices = Node.getNode().getComponentSpace().resolve(PasswordDecryptionServices.class);
	}


	public void refreshConfig(final ExecutorGlobalConfig config) {
		final JiraSettingExport jiraSettingExport = config.getBot().getJiraSetting();
		final DtList<JiraFieldSettingExport> jiraFieldSettingExport = config.getBot().getJiraFieldSetting();
		if (jiraSettingExport == null || jiraFieldSettingExport == null) {
			throw new VSystemException("Jira setting and Jira fields settings must be set for jira plugin to work...");
		} else {
			baseJira = jiraSettingExport.getUrl();
			user = jiraSettingExport.getLogin();
			password = passwordDecryptionServices.decryptPassword(jiraSettingExport.getPassword());
			project = jiraSettingExport.getProject();
			jiraRestClient = createJiraRestClient();
			jiraFieldSettingExports = jiraFieldSettingExport;
		}
	}

	public DtList<JiraFieldSettingExport> getJiraFieldSettingExports() {
		return jiraFieldSettingExports;
	}

	private JiraRestClient createJiraRestClient() {
		return new AsynchronousJiraRestClientFactory()
				.createWithBasicHttpAuthentication(URI.create(baseJira), user, password);
	}

	public BasicIssue createIssue(final BlackBoard bb, final List<JiraField> jfFields, final List<IJiraFieldService> fieldServices) {
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
	public String createIssueJiraCommand(final BlackBoard bb, final List<JiraField> jiraFields, final List<IJiraFieldService> fieldServices) {
		final var createdIssue = createIssue(bb, jiraFields, fieldServices);
		return createLinkUrl(createdIssue.getKey());

	}

	private String createLinkUrl(final String key) {
		final String url = baseJira + "/browse/" + key;
		return formatLink(url, true);
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

	public List<User> findUserByUsername(final String username) {
		return (List<User>) jiraRestClient.getUserClient().findUsers(username).claim();
	}

	public List<Priority> getPriorities() {
		return (List<Priority>) jiraRestClient.getMetadataClient().getPriorities().claim();
	}

	public Issue getIssueByKey(final String key) {
		return jiraRestClient.getIssueClient().getIssue(key).claim();
	}

	public List<Transition> getIssueTransitions(final Issue issue) {
		return (List<Transition>) jiraRestClient.getIssueClient().getTransitions(issue).claim();
	}

	public List<Field> getCustomFields() {
		final List<Field> allFields = (List<Field>) jiraRestClient.getMetadataClient().getFields().claim();
		return allFields.stream().filter(field -> field.getFieldType() == FieldType.CUSTOM).collect(Collectors.toList());
	}

	public Iterable<BasicComponent> getComponents() {
		return getProject().getComponents();
	}

	@Override
	public void stop() {

	}
}
