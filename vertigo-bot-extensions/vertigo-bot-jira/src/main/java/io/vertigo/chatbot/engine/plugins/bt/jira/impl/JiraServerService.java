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
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousHttpClientFactory;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.jira.rest.client.internal.async.DisposableHttpClient;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.inject.Inject;

import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.chatbot.commons.LogsUtils;
import io.vertigo.chatbot.commons.PasswordEncryptionServices;
import io.vertigo.chatbot.commons.domain.JiraFieldSettingExport;
import io.vertigo.chatbot.commons.domain.JiraSettingExport;
import io.vertigo.chatbot.engine.plugins.bt.jira.helper.CustomAsynchronousJiraRestClient;
import io.vertigo.chatbot.engine.plugins.bt.jira.helper.CustomServerInfo;
import io.vertigo.chatbot.engine.plugins.bt.jira.model.JiraField;
import io.vertigo.chatbot.executor.model.ExecutorGlobalConfig;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtList;

import static io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider.formatLink;

public class JiraServerService implements Component, IJiraService {

	private String baseJira;
	private String user;
	private String password;
	private String project;
	private Boolean isCloud;
	private JiraRestClient jiraRestClient;
	private DtList<JiraFieldSettingExport> jiraFieldSettingExports;
	private CustomAsynchronousJiraRestClient customAsynchronousUserRestClient;
	@Inject
	private PasswordEncryptionServices passwordEncryptionServices;


	public void refreshConfig(final ExecutorGlobalConfig config, StringBuilder logs) {
		LogsUtils.addLogs(logs, "Refreshing Jira settings ... ");
		final JiraSettingExport jiraSettingExport = config.getBot().getJiraSetting();
		final DtList<JiraFieldSettingExport> jiraFieldSettingExport = config.getBot().getJiraFieldSetting();
		isCloud = true;
		if (jiraSettingExport == null || jiraFieldSettingExport == null) {
			LogsUtils.logKO(logs);
			throw new VSystemException("Jira setting and Jira fields settings must be set for jira plugin to work...");
		} else {
			baseJira = jiraSettingExport.getUrl();
			user = jiraSettingExport.getLogin();
			password = passwordEncryptionServices.decryptPassword(jiraSettingExport.getPassword());
			project = jiraSettingExport.getProject();
			jiraRestClient = createJiraRestClient();
			customAsynchronousUserRestClient = createCustomUserRestClient();
			isCloud = checkIfIsCloud();
			jiraFieldSettingExports = jiraFieldSettingExport;
			LogsUtils.logOK(logs);
		}
	}

	public Boolean isCloud() {
		return isCloud;
	}

	public DtList<JiraFieldSettingExport> getJiraFieldSettingExports() {
		return jiraFieldSettingExports;
	}

	private JiraRestClient createJiraRestClient() {
		return new AsynchronousJiraRestClientFactory()
				.createWithBasicHttpAuthentication(URI.create(baseJira), user, password);
	}

	private CustomAsynchronousJiraRestClient createCustomUserRestClient() {
		DisposableHttpClient httpClient = new AsynchronousHttpClientFactory()
				.createClient(URI.create(baseJira), new BasicHttpAuthenticationHandler(user, password));
		return new CustomAsynchronousJiraRestClient(URI.create(baseJira), httpClient);
	}

	public BasicIssue createIssue(final BlackBoard bb, final List<JiraField> jfFields, final List<IJiraFieldService> fieldServices) {
		final IssueRestClient issueClient = jiraRestClient.getIssueClient();
		final IssueInputBuilder iib = new IssueInputBuilder();
		iib.setProjectKey(project);
		jfFields.forEach(jiraField -> {
			if (jiraField.getValue() != null) {
				fieldServices.forEach(fieldService -> {
					if (fieldService.supports(jiraField.getFieldType())) {
						fieldService.processTicket(bb, iib, jiraField);
					}
				});
			}
		});
		final IssueInput issue = iib.build();
		return issueClient.createIssue(issue).claim();
	}

	private boolean checkIfIsCloud() {
		try {
			CustomServerInfo customServerInfo = customAsynchronousUserRestClient.getServerInfo().claim();
			if (customServerInfo != null && customServerInfo.getDeploymentType() != null) {
				return customServerInfo.getDeploymentType().equalsIgnoreCase("cloud");
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
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
		if (isCloud) {
			return (List<User>) customAsynchronousUserRestClient.findUsers(username, project).claim();
		} else {
			return (List<User>) jiraRestClient.getUserClient().findUsers(username).claim();
		}
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
}
