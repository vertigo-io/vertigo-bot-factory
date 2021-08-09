package io.vertigo.chatbot.engine.plugins.bt.jira.impl;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
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

	public BasicIssue createIssue(final List<String> jfFields) {
		final URI jiraServerUri = URI.create(baseJira);
		final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();

		final AuthenticationHandler auth = new BasicHttpAuthenticationHandler(user, password);
		final JiraRestClient restClient = factory.create(jiraServerUri, auth);
		final IssueRestClient issueClient = restClient.getIssueClient();

		try {
			final IssueInputBuilder iib = new IssueInputBuilder();

			iib.setProjectKey("CHATBOTPOC");
			iib.setIssueTypeId(10004L);
			iib.setSummary(jfFields.get(0).substring(0, Math.min(jfFields.get(0).length(), 49)));
			iib.setDescription("");
			setScenario(iib, jfFields.get(0));
			setExpectedResult(iib, jfFields.get(1));
			setObtainedResult(iib, jfFields.get(2));
			setReproductibilityCode(iib, jfFields.get(3));
			setCriticityCode(iib, jfFields.get(4));
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

	@Override
	public String createIssueJiraCommand(final List<String> jfStrings) {
		final var createdIssue = createIssue(jfStrings);
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
		final Map<String, Object> customField = new HashMap<String, Object>();
		customField.put("id", value);
		iib.setFieldValue("customfield_10412", new ComplexIssueInputFieldValue(customField));
	}

	private void setReproductibilityCode(final IssueInputBuilder iib, final String value) {
		final Map<String, Object> customField = new HashMap<String, Object>();
		customField.put("id", value);
		iib.setFieldValue("customfield_10413", new ComplexIssueInputFieldValue(customField));
	}

}
