package io.vertigo.chatbot.executor.rasa;

import io.vertigo.chatbot.commons.AbstractJaxrsProvider;

public class JaxrsProvider extends AbstractJaxrsProvider {

	@Override
	protected String getTargetUrl() {
		return "http://localhost:5005/";
	}

}
