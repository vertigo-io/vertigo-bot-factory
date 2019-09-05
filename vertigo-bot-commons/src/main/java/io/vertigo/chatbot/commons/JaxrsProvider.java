package io.vertigo.chatbot.commons;

import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import io.vertigo.core.component.Activeable;
import io.vertigo.core.component.Component;
import io.vertigo.vega.engines.webservice.json.JsonEngine;

public class JaxrsProvider implements Component, Activeable {

	private static JaxrsProvider me;

	@Inject
	private JsonEngine jsonEngine;


	@Override
	public void start() {
		me = this;
	}

	@Override
	public void stop() {
		// rien
	}

	public static JsonEngine getJsonEngine() {
		return me.jsonEngine;
	}

	private final WebTarget rasaExecutorTarget = ClientBuilder.newClient()
			.target("http://localhost:8080/vertigo-bot-executor")
			.register(GsonProvider.class);


	public WebTarget getWebTarget() {
		return rasaExecutorTarget;
	}
}
