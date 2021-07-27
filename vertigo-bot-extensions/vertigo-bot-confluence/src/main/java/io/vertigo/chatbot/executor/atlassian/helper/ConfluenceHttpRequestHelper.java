package io.vertigo.chatbot.executor.atlassian.helper;

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
import java.util.Map;
import java.util.Map.Entry;

import io.vertigo.core.lang.VSystemException;

public class ConfluenceHttpRequestHelper {

	public static final String BASE_URL = "https://preprod-jira-temp.kleegroup.com/confluence/rest/api";

	private static HttpClient getBasicHttpClient() {
		return HttpClient.newBuilder().version(Version.HTTP_1_1).build();
	}

	private static Builder createRequestBuilder(final String url, final Map<String, String> headers, final Map<String, String> params) {
		final URI uri = params != null ? createURIWithParams(url, params) : URI.create(url);
		final Builder builder = HttpRequest.newBuilder().uri(uri);
		for (final Entry<String, String> entry : headers.entrySet()) {
			builder.setHeader(entry.getKey(), entry.getValue());
		}
		return builder;
	}

	private static URI createURIWithParams(final String url, final Map<String, String> params) {
		final StringBuilder builder = new StringBuilder();
		builder.append(url);
		builder.append("?");
		for (final Entry<String, String> entry : params.entrySet()) {
			builder.append(entry.getKey());
			builder.append("=");
			builder.append(entry.getValue());
			builder.append("&");
		}

		builder.setLength(builder.length() - 1);
		return URI.create(builder.toString());
	}

	public static HttpRequest createGetRequest(final String url, final Map<String, String> headers, final Map<String, String> params) {
		return createRequestBuilder(url, headers, params).GET().build();
	}

	public static HttpRequest createPostRequest(final String url, final Map<String, String> headers, final BodyPublisher publisher) {
		return createRequestBuilder(url, headers, null).POST(publisher).build();
	}

	public static HttpRequest createPutRequest(final String url, final Map<String, String> headers, final BodyPublisher publisher) {
		return createRequestBuilder(url, headers, null).PUT(publisher).build();
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
			throw new VSystemException(e.getMessage());
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
}
