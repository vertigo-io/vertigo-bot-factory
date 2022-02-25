package io.vertigo.chatbot.engine.plugins.bt.jira.helper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import io.vertigo.core.lang.VSystemException;

public final class JiraHttpRequestHelper {

	public static final String API_URL = "/rest/api";
	public static final String SEARCH_URL = "/search";
	public static final String SPACE_URL = "/space";

	private JiraHttpRequestHelper() {
		//helper
	}

	private static HttpClient getBasicHttpClient() {
		return HttpClient.newBuilder().version(Version.HTTP_1_1).build();
	}

	public static HttpRequest createGetRequest(final String url, final Map<String, String> headers) {
		return createRequestBuilder(url, headers).GET().build();
	}

	public static HttpRequest createPostRequest(final String url, final Map<String, String> headers, final BodyPublisher publisher) {
		return createRequestBuilder(url, headers).POST(publisher).build();
	}

	public static HttpRequest createPutRequest(final String url, final Map<String, String> headers, final BodyPublisher publisher) {
		return createRequestBuilder(url, headers).PUT(publisher).build();
	}

	public static <T extends Object> HttpResponse<T> sendRequest(HttpClient client, final HttpRequest request, final BodyHandler<T> handler, final int successStatutCode) {
		HttpResponse<T> response;
		client = client != null ? client : getBasicHttpClient();
		try {
			response = client.send(request, handler);
			if (response.statusCode() != successStatutCode) {
				throw new VSystemException("Error during sending request : " + request.uri().toString());
			}
		} catch (IOException | InterruptedException e) {
			throw new VSystemException(e, "Error during sending request");
		}
		return response;
	}

	public static String encodeUrl(final String urlToEncode) {
		try {
			return URLEncoder.encode(urlToEncode, StandardCharsets.UTF_8.toString());
		} catch (final UnsupportedEncodingException e) {
			throw new VSystemException("can't encode {0}", urlToEncode);
		}
	}

	public static Map<String, String> getHeadersWithAuthorization(final String user, final String password) {
		final Map<String, String> result = new HashMap<>();
		result.put("Authorization", basicAuth(user, password));
		return result;
	}

	public static String basicAuth(final String username, final String password) {
		return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
	}

	private static Builder createRequestBuilder(final String url, final Map<String, String> headers) {
		final URI uri = URI.create(url);
		final var builder = HttpRequest.newBuilder().uri(uri);
		for (final Entry<String, String> entry : headers.entrySet()) {
			builder.setHeader(entry.getKey(), entry.getValue());
		}
		return builder;
	}

}
