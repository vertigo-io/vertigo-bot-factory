package io.vertigo.chatbot.engine.plugins.bt.jira.impl;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.ProjectRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import io.vertigo.core.node.Node;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.param.ParamManager;

public class JiraServerService implements Component, IJiraService, Activeable {

	private ParamManager paramManager;

	private String baseJira;
	private String user;
	private String password;

	@Override
	public void start() {
		paramManager = Node.getNode().getComponentSpace().resolve(ParamManager.class);
		baseJira = paramManager.getParam("JIRA_URL").getValueAsString();
		user = paramManager.getParam("JIRA_USER").getValueAsString();
		password = paramManager.getParam("JIRA_PWD").getValueAsString();
	}

	@Override
	public void stop() {
		//do nothing
	}

	public BasicIssue createIssue(final List<String> jfFields, final List<String> versions) {
		final URI jiraServerUri = URI.create(baseJira);
		final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();

		final AuthenticationHandler auth = new BasicHttpAuthenticationHandler(user, password);
		final JiraRestClient restClient = factory.create(jiraServerUri, auth);
		final IssueRestClient issueClient = restClient.getIssueClient();

		try {
			final IssueInputBuilder iib = new IssueInputBuilder();

			iib.setProjectKey("CHATBOTPOC");
			iib.setIssueTypeId(10004L);
			iib.setSummary(jfFields.get(1).substring(0, Math.min(jfFields.get(1).length(), 49)));
			iib.setDescription(getDescriptionFromVersion(versions, "La référence du client est " + jfFields.get(0)));
			setScenario(iib, jfFields.get(1));
			setExpectedResult(iib, jfFields.get(2));
			setObtainedResult(iib, jfFields.get(3));
			setReproductibilityCode(iib, jfFields.get(4));
			setCriticityCode(iib, jfFields.get(5));
			final ArrayList<String> listComponents = new ArrayList<>();
			listComponents.add(jfFields.get(6));
			iib.setComponentsNames(listComponents);

			final IssueInput issue = iib.build();
			return issueClient.createIssue(issue).claim();

		} finally {
			try {
				restClient.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String getDescriptionFromVersion(final List<String> versions, final String refClient) {
		final StringBuilder builder = new StringBuilder();
		builder.append(refClient);
		builder.append("\n");
		builder.append("Numéro de version du back : ");
		builder.append(versions.get(0));
		builder.append("\n");
		builder.append("Numéro de version du front : ");
		builder.append(versions.get(1));
		builder.append("\n");
		builder.append("Numéro de version du paramétrage : ");
		builder.append(versions.get(2));
		return builder.toString();
	}

	@Override
	public String createIssueJiraCommand(final List<String> jfStrings, final List<String> versions) {
		final var createdIssue = createIssue(jfStrings, versions);
		return createLinkUrl(createdIssue.getKey());

	}

	private String createLinkUrl(final String key) {
		final String url = baseJira + "/browse/" + key;
		final var builder = new StringBuilder();
		builder.append("<a href=\"");
		builder.append(url);
		builder.append("\">");
		builder.append(key);
		builder.append("</a>");
		return builder.toString();
	}

	private void setScenario(final IssueInputBuilder iib, final String value) {
		iib.setFieldValue("customfield_10409", value);
	}

	private void setExpectedResult(final IssueInputBuilder iib, final String value) {
		iib.setFieldValue("customfield_10410", value);
	}

	private void setObtainedResult(final IssueInputBuilder iib, final String value) {
		iib.setFieldValue("customfield_10411", value);
	}

	private void setCriticityCode(final IssueInputBuilder iib, final String value) {
		final Map<String, Object> customField = new HashMap<>();
		customField.put("id", value);
		iib.setFieldValue("customfield_10412", new ComplexIssueInputFieldValue(customField));
	}

	private void setReproductibilityCode(final IssueInputBuilder iib, final String value) {
		final Map<String, Object> customField = new HashMap<>();
		customField.put("id", value);
		iib.setFieldValue("customfield_10413", new ComplexIssueInputFieldValue(customField));
	}

	public Project getProject() {
		final URI jiraServerUri = URI.create(baseJira);
		final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();

		final AuthenticationHandler auth = new BasicHttpAuthenticationHandler(user, password);
		final JiraRestClient restClient = factory.create(jiraServerUri, auth);
		final ProjectRestClient projectClient = restClient.getProjectClient();
		try {
			return projectClient.getProject("CHATBOTPOC").claim();
		} finally {

		}

	}

}
