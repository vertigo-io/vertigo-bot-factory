package io.vertigo.chatbot.commons;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.media.multipart.internal.MultiPartReaderServerSide;
import org.glassfish.jersey.media.multipart.internal.MultiPartWriter;

import io.vertigo.core.component.Activeable;
import io.vertigo.core.component.Component;

public abstract class AbstractJaxrsProvider implements Component, Activeable {

	private WebTarget rasaExecutorTarget;


	@Override
	public void start() {
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
