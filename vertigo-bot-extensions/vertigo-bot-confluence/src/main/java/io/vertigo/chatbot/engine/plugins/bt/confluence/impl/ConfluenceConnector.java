package io.vertigo.chatbot.engine.plugins.bt.confluence.impl;

import java.util.Map;

import io.vertigo.chatbot.engine.plugins.bt.confluence.model.result.ConfluenceSearchResponse;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.result.ConfluenceSpace;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.result.ConfluenceSpaceResponse;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.search.ConfluenceSearchObject;

public interface ConfluenceConnector {

	abstract ConfluenceSearchResponse searchOnConfluence(final Map<String, String> params, final Map<String, String> headers, ConfluenceSearchObject filter);

	abstract ConfluenceSpaceResponse searchAllSpaceOnConfluence(final Map<String, String> headers);

	abstract ConfluenceSpace searchSpaceOnClonfluence(final Map<String, String> headers, final String spaceKey);
}
