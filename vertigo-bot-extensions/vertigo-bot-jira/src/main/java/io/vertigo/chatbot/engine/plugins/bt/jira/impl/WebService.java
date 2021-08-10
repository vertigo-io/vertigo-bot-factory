package io.vertigo.chatbot.engine.plugins.bt.jira.impl;

import java.net.http.HttpRequest;
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

	@Override
	public void start() {
		var paramManager = Node.getNode().getComponentSpace().resolve(ParamManager.class);
		url = paramManager.getParam("WEB_SERVICE").getValueAsString();
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

}
