package io.vertigo.chatbot.commons;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.media.multipart.internal.MultiPartReaderServerSide;
import org.glassfish.jersey.media.multipart.internal.MultiPartWriter;

import io.vertigo.core.component.Component;

public class JaxrsProvider implements Component {

	public WebTarget getWebTarget(final String targetUrl) {
		return ClientBuilder.newClient()
				.target(targetUrl)
				.register(GsonProvider.class)
				.register(MultiPartReaderServerSide.class)
				.register(MultiPartWriter.class);
	}
}
