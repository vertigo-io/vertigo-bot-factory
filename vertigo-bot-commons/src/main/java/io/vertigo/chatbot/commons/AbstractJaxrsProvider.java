package io.vertigo.chatbot.commons;

import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.media.multipart.internal.MultiPartReaderServerSide;
import org.glassfish.jersey.media.multipart.internal.MultiPartWriter;

import io.vertigo.core.component.Activeable;
import io.vertigo.core.component.Component;
import io.vertigo.vega.engines.webservice.json.JsonEngine;

public abstract class AbstractJaxrsProvider implements Component, Activeable {

	private static AbstractJaxrsProvider me;

	@Inject
	private JsonEngine jsonEngine;


	public static JsonEngine getJsonEngine() {
		return me.jsonEngine;
	}

	private WebTarget rasaExecutorTarget;


	@Override
	public void start() {
		me = this;

		rasaExecutorTarget = ClientBuilder.newClient()
				.target(getTargetUrl())
				.register(GsonProvider.class)
				.register(MultiPartReaderServerSide.class)
				.register(MultiPartWriter.class);
	}

	@Override
	public void stop() {
		// rien
	}

	abstract protected String getTargetUrl();

	public WebTarget getWebTarget() {
		return rasaExecutorTarget;
	}
}
