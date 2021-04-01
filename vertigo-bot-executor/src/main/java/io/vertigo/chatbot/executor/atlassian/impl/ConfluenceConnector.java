package io.vertigo.chatbot.executor.atlassian.impl;

import java.util.Map;

import io.vertigo.chatbot.executor.atlassian.model.confluence.result.ConfluenceSearchResponse;
import io.vertigo.chatbot.executor.atlassian.model.confluence.result.ConfluenceSpace;
import io.vertigo.chatbot.executor.atlassian.model.confluence.result.ConfluenceSpaceResponse;
import io.vertigo.chatbot.executor.atlassian.model.confluence.search.ConfluenceSearchObject;

public interface ConfluenceConnector {

	abstract ConfluenceSearchResponse searchOnConfluence(final Map<String, String> params, final Map<String, String> headers, ConfluenceSearchObject filter);

	abstract ConfluenceSpaceResponse searchAllSpaceOnConfluence(final Map<String, String> headers);

	abstract ConfluenceSpace searchSpaceOnClonfluence(final Map<String, String> headers, final String spaceKey);
}
