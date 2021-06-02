package io.vertigo.chatbot.designer.builder.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import io.vertigo.chatbot.engine.model.BotInput;
import io.vertigo.chatbot.engine.model.TalkInput;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.component.Component;
import io.vertigo.vega.engines.webservice.json.JsonEngine;

public class BotConversationServices implements Component {

	@Inject
	private JsonEngine jsonEngine;

	public String createBotInput(final String message, final Map<String, Object> metadatas) {
		final BotInput input = new BotInput(message, metadatas);
		return objectToJson(input);

	}

	public String createBotInput(final TalkInput talkInput) {
		String message = talkInput.getMessage();
		if (talkInput.isButton()) {
			return objectToJson(new BotInput(Map.of("payload", message)));
		}
		return createBotInput(message);
	}

	public String objectToJson(final Object input) {
		return jsonEngine.toJson(input);
	}

	public <D> D jsonToObject(final String json, final Type type) {
		return jsonEngine.fromJson(json, type);
	}

	public <D> D objectFromByteArray(final byte[] object) {
		ByteArrayInputStream bis = new ByteArrayInputStream(object);

		try (ObjectInput in = new ObjectInputStream(bis);) {
			return (D) in.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new VSystemException("error");
		}

	}

	public String createBotInput(final String message) {
		BotInput input = new BotInput(message);
		return objectToJson(input);
	}

	private HttpClient getBasicHttpClient() {
		return HttpClient.newBuilder().version(Version.HTTP_1_1).build();
	}

	private Builder createRequestBuilder(final String url, final Map<String, String> headers) {
		final URI uri = URI.create(url);
		final Builder builder = HttpRequest.newBuilder().uri(uri);
		if (!headers.isEmpty()) {
			for (final Entry<String, String> entry : headers.entrySet()) {
				builder.setHeader(entry.getKey(), entry.getValue());
			}
		}
		return builder;
	}

	public HttpRequest createPostRequest(final String url, final BodyPublisher publisher) {
		return createPostRequest(url, Map.of(), publisher);
	}

	public HttpRequest createPostRequest(final String url, final Map<String, String> headers, final BodyPublisher publisher) {
		return createRequestBuilder(url, headers).POST(publisher).build();
	}

	public HttpRequest createPutRequest(final String url, final Map<String, String> headers, final BodyPublisher publisher) {
		return createRequestBuilder(url, headers).PUT(publisher).build();
	}

	public <T extends Object> HttpResponse<T> sendRequest(HttpClient client, final HttpRequest request, final BodyHandler<T> handler, final int successStatutCode) {
		HttpResponse<T> response;
		client = client != null ? client : getBasicHttpClient();
		try {
			response = client.send(request, handler);
			if (response.statusCode() != successStatutCode) {
				throw new VSystemException("Error during sending request : " + request.uri().toString());
			}
		} catch (IOException | InterruptedException e) {
			throw new VSystemException(e.getMessage());
		}
		return response;
	}

	public <T extends Object> CompletableFuture<HttpResponse<T>> sendAsyncRequest(HttpClient client, final HttpRequest request, final BodyHandler<T> handler) {
		client = client != null ? client : getBasicHttpClient();
		return client.sendAsync(request, handler);
	}

}
