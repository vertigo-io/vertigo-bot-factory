package io.vertigo.chatbot.engine.plugins.bt.confluence.impl;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertigo.chatbot.engine.plugins.bt.confluence.helper.ConfluenceHttpRequestHelper;
import io.vertigo.chatbot.engine.plugins.bt.confluence.helper.JsonHelper;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.result.ConfluenceSearchResponse;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.result.ConfluenceSpace;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.result.ConfluenceSpaceResponse;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.search.ConfluenceSearchObject;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.search.ConfluenceVisitor;
import io.vertigo.core.node.component.Component;

public class ConfluenceServerImpl implements ConfluenceConnector, Component {

	@Override
	public ConfluenceSearchResponse searchOnConfluence(final Map<String, String> params, final Map<String, String> headers, final ConfluenceSearchObject filter) {
		final String searchArgs = createSearchArgs(filter);
		params.put("cql", searchArgs);
		final HttpRequest request = ConfluenceHttpRequestHelper.createGetRequest(ConfluenceHttpRequestHelper.BASE_URL + "/search", headers, params);
		final HttpResponse<String> response = ConfluenceHttpRequestHelper.sendRequest(null, request, BodyHandlers.ofString(), 200);
		final ObjectMapper mapper = JsonHelper.createCustomObjectMapper();
		return JsonHelper.getObjectFromJson(mapper, response.body(), ConfluenceSearchResponse.class);
	}

	private String createSearchArgs(final ConfluenceSearchObject filter) {
		final StringBuilder builder = new StringBuilder();
		final ConfluenceVisitor visitor = new ConfluenceVisitor();
		builder.append(filter.accept(visitor));
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
