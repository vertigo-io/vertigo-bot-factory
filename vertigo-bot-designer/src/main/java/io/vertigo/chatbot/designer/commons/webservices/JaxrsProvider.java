package io.vertigo.chatbot.designer.commons.webservices;

import io.vertigo.chatbot.commons.AbstractJaxrsProvider;

public class JaxrsProvider extends AbstractJaxrsProvider {

	@Override
	protected String getTargetUrl() {
		return "http://localhost:8080/vertigo-bot-executor";
	}

}
