package io.vertigo.ai.plugins.nlu.rasa.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.vertigo.ai.plugins.nlu.rasa.mda.MessageToRecognize;
import io.vertigo.ai.plugins.nlu.rasa.mda.RasaParsingResponse;
import io.vertigo.core.lang.VSystemException;

public final class RasaHttpSenderUtil {

	private static final String RASA_MODEL = "/model";
	private static final String RASA_TRAIN = "/train";
	private static final String RASA_PARSE = "/parse";

	private RasaHttpSenderUtil() {
		// util
	}

	public static String launchTraining(final String rasaUrl, final Map<String, Object> map) {
		final byte[] output = FileIOUtil.getYamlByteArrayFromMap(map);

		final HttpClient client = getBasicHttpClient();
		final String urlRequest = rasaUrl + RASA_MODEL + RASA_TRAIN;

		final HttpRequest request = createPostRequest(urlRequest, Map.of("Content-Type", "application/x-yaml"), BodyPublishers.ofByteArray(output));
		final HttpResponse<InputStream> response = sendRequest(client, request, BodyHandlers.ofInputStream(), 200);
		return response.headers().map().get("filename").get(0);
	}

	public static void putModel(final String rasaUrl, final String filename) {
		//Create object to send
		final ObjectMapper mapper = FileIOUtil.createCustomObjectMapper();
		final ObjectNode node = FileIOUtil.createNode(mapper, "model_file", "models/" + filename);
		final String json = FileIOUtil.getJsonStringFromObject(mapper, node);

		//send request
		final HttpClient client = getBasicHttpClient();
		final HttpRequest request = createPutRequest(rasaUrl + RASA_MODEL, Map.of("Content-Type", "application/json"), BodyPublishers.ofString(json));
		sendRequest(client, request, BodyHandlers.ofString(), HttpServletResponse.SC_NO_CONTENT);
	}

	public static RasaParsingResponse getIntentFromRasa(final String rasaUrl, final MessageToRecognize message) {
		final ObjectMapper mapper = FileIOUtil.createCustomObjectMapper();
		final String json = FileIOUtil.getJsonStringFromObject(mapper, message);

		final String urlRequest = rasaUrl + RASA_MODEL + RASA_PARSE;
		final HttpClient client = getBasicHttpClient();
		final HttpRequest request = createPostRequest(urlRequest, Map.of("Content-Type", "application/json"), BodyPublishers.ofString(json));
		final HttpResponse<String> response = sendRequest(client, request, BodyHandlers.ofString(), 200);
		return FileIOUtil.getObjectFromJson(mapper, response.body(), RasaParsingResponse.class);
	}

	/************** Request Part *********/
	private static HttpClient getBasicHttpClient() {
		return HttpClient.newBuilder().version(Version.HTTP_1_1).build();
	}

	private static Builder createRequestBuilder(final String url, final Map<String, String> headers) {
		final Builder builder = HttpRequest.newBuilder().uri(URI.create(url));
		for (final Entry<String, String> entry : headers.entrySet()) {
			builder.setHeader(entry.getKey(), entry.getValue());
		}
		return builder;
	}

	private static HttpRequest createPostRequest(final String url, final Map<String, String> headers, final BodyPublisher publisher) {
		return createRequestBuilder(url, headers).POST(publisher).build();
	}

	private static HttpRequest createPutRequest(final String url, final Map<String, String> headers, final BodyPublisher publisher) {
		return createRequestBuilder(url, headers).PUT(publisher).build();
	}

	private static <T extends Object> HttpResponse<T> sendRequest(final HttpClient client, final HttpRequest request, final BodyHandler<T> handler, final int successStatutCode) {
		HttpResponse<T> response;
		try {
			response = client.send(request, handler);
			if (response.statusCode() != successStatutCode) {
				throw new VSystemException("Error while sending request to '{0}'. Expected HTTP code '{1}' but was '{2}'.", request.uri().toString(), successStatutCode, response.statusCode());
			}
		} catch (IOException | InterruptedException e) {
			throw new VSystemException(e, "Error while sending request to '{0}'", request.uri().toString());
		}
		return response;
	}

}
