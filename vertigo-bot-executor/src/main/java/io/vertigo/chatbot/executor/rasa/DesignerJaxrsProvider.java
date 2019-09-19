package io.vertigo.chatbot.executor.rasa;

import io.vertigo.chatbot.commons.AbstractJaxrsProvider;

public class DesignerJaxrsProvider extends AbstractJaxrsProvider {

	@Override
	protected String getTargetUrl() {
		return "http://localhost:8080/vertigo-bot-designer/api/";
	}

}
