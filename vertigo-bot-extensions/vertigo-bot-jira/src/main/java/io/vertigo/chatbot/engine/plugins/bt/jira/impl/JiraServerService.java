package io.vertigo.chatbot.engine.plugins.bt.jira.impl;

import java.io.IOException;
import java.net.URI;

import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;

public class JiraServerService implements Component, IJiraService, Activeable {

	private final String baseJira = "";
	private final String user = "";
	private final String password = "";

	@Override
	public void start() {
		//nothing
	}

	@Override
	public void stop() {
		//do nothing
	}

	public BasicIssue createRequestJira() {
		URI jiraServerUri = URI.create(baseJira);
		AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();

		AuthenticationHandler auth = new BasicHttpAuthenticationHandler(user, password);
		JiraRestClient restClient = factory.create(jiraServerUri, auth);
		IssueRestClient issueClient = restClient.getIssueClient();

		try {
			IssueInputBuilder iib = new IssueInputBuilder();
			iib.setProjectKey("CHATBOTPOC");
			iib.setSummary("Test depuis service");
			iib.setIssueTypeId(10204L);
			iib.setDescription("Test de la description");

			IssueInput issue = iib.build();
			return issueClient.createIssue(issue).claim();

		} finally {
			try {
				restClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
