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
