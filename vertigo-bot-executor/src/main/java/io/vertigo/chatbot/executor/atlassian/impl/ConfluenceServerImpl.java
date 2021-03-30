package io.vertigo.chatbot.executor.atlassian.impl;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertigo.chatbot.executor.atlassian.helper.ConfluenceHttpRequestHelper;
import io.vertigo.chatbot.executor.atlassian.helper.JsonHelper;
import io.vertigo.chatbot.executor.atlassian.model.confluence.result.ConfluenceSearchResponse;
import io.vertigo.chatbot.executor.atlassian.model.confluence.result.ConfluenceSpace;
import io.vertigo.chatbot.executor.atlassian.model.confluence.result.ConfluenceSpaceResponse;
import io.vertigo.chatbot.executor.atlassian.model.confluence.search.ConfluenceSearchObject;
import io.vertigo.chatbot.executor.atlassian.model.confluence.search.ConfluenceVisitor;
import io.vertigo.core.node.component.Component;

public class ConfluenceServerImpl implements ConfluenceConnector, Component {

	@Override
	public ConfluenceSearchResponse searchOnConfluence(final Map<String, String> params, final Map<String, String> headers, final List<ConfluenceSearchObject> filter) {
		final String searchArgs = createSearchArgs(filter);
		params.put("cql", searchArgs);
		final HttpRequest request = ConfluenceHttpRequestHelper.createGetRequest(ConfluenceHttpRequestHelper.BASE_URL + "/search", headers, params);
		final HttpResponse<String> response = ConfluenceHttpRequestHelper.sendRequest(null, request, BodyHandlers.ofString(), 200);
		final ObjectMapper mapper = JsonHelper.createCustomObjectMapper();
		return JsonHelper.getObjectFromJson(mapper, response.body(), ConfluenceSearchResponse.class);
	}

	private String createSearchArgs(final List<ConfluenceSearchObject> filter) {
		final StringBuilder builder = new StringBuilder();
		final ConfluenceVisitor visitor = new ConfluenceVisitor();
		for (final ConfluenceSearchObject object : filter) {
			builder.append(object.accept(visitor));
			builder.append(" AND ");
		}
		builder.setLength(builder.length() - 4);
		return ConfluenceHttpRequestHelper.encodeUrl(builder.toString());
	}

	@Override
	public ConfluenceSpaceResponse searchAllSpaceOnConfluence(final Map<String, String> headers) {
		final HttpRequest request = ConfluenceHttpRequestHelper.createGetRequest(ConfluenceHttpRequestHelper.BASE_URL + "/space", headers, null);
		final HttpResponse<String> response = ConfluenceHttpRequestHelper.sendRequest(null, request, BodyHandlers.ofString(), 200);
		final ObjectMapper mapper = JsonHelper.createCustomObjectMapper();
		return JsonHelper.getObjectFromJson(mapper, response.body(), ConfluenceSpaceResponse.class);
	}

	@Override
	public ConfluenceSpace searchSpaceOnClonfluence(final Map<String, String> headers, final String spaceKey) {
		final HttpRequest request = ConfluenceHttpRequestHelper.createGetRequest(ConfluenceHttpRequestHelper.BASE_URL + "/space/" + spaceKey, headers, null);
		final HttpResponse<String> response = ConfluenceHttpRequestHelper.sendRequest(null, request, BodyHandlers.ofString(), 200);
		final ObjectMapper mapper = JsonHelper.createCustomObjectMapper();
		return JsonHelper.getObjectFromJson(mapper, response.body(), ConfluenceSpace.class);
	}

}
