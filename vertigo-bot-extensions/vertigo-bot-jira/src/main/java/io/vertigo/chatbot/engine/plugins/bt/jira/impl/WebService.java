package io.vertigo.chatbot.engine.plugins.bt.jira.impl;

import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertigo.chatbot.engine.plugins.bt.jira.helper.JiraHttpRequestHelper;
import io.vertigo.core.node.Node;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.param.ParamManager;

public class WebService implements Component, Activeable {

	private String url;
	private String urlTarifReduit;
	private String user;
	private String pwd;

	@Override
	public void start() {
		var paramManager = Node.getNode().getComponentSpace().resolve(ParamManager.class);
		url = paramManager.getParam("WEB_SERVICE").getValueAsString();
		urlTarifReduit = "http://comu-usi-batch-rec:8001/sigRest/v1/customers/33263038/summary";
		user = "Admin SIG";
		pwd = "test";
	}

	@Override
	public void stop() {
		//nothing to do
	}

	public List<String> getAllVersions() {
		final HttpRequest request = JiraHttpRequestHelper.createGetRequest(url, Map.of());
		final HttpResponse<String> response = JiraHttpRequestHelper.sendRequest(null, request, BodyHandlers.ofString(), 200);
		final String body = response.body();
		final List<String> result = new ArrayList<>();
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final JsonNode node = mapper.readTree(body);
			result.add(node.at("/back/version").asText());
			result.add(node.at("/front/version").asText());
			result.add(node.at("/database/param").asText());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return result;
	}

	public boolean getIsErrorTarifReduit(final String clientNumber) {
		Map<String, String> headers = Map.of("AuthorizationSIG", JiraHttpRequestHelper.basicAuth(user, pwd),
				"Content-type", "application/json");
		final String json = "{" +
				"\"searchLevel\": 1," +
				"\"searches\":" +
				"[{  \"holderPayerInd\":2," +
				"\"productCodes\": [5]," +
				"\"states\":[3]" +
				"}]" +

				"}";

		final HttpRequest request = JiraHttpRequestHelper.createPostRequest(urlTarifReduit, headers, BodyPublishers.ofString(json));
		final HttpResponse<String> response = JiraHttpRequestHelper.sendRequest(null, request, BodyHandlers.ofString(), 200);
		final String body = response.body();
		return testBody(body);
	}

	private boolean testBody(final String body) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final JsonNode results = mapper.readTree(body);
			for (JsonNode node : results) {
				final String test = node.at("/contract/currentProfile").asText();
				final String test2 = "juste pour l'info d'avant";
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return false;
	}

}
