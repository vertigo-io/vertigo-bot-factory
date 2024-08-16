package io.vertigo.chatbot.engine.plugins.bt.confluence.helper;

import io.vertigo.chatbot.engine.plugins.bt.confluence.model.search.ConfluenceSearchObject;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.search.ConfluenceSearchOperator;
import io.vertigo.chatbot.engine.plugins.bt.confluence.model.search.ConfluenceVisitor;
import io.vertigo.core.lang.VSystemException;

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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class ConfluenceHttpRequestHelper {

    public static final String API_URL = "/rest/api";
    public static final String SEARCH_URL = "/search";
    public static final String SPACE_URL = "/space";
    public static final String CONTENT_URL = "/content/";

    public static final String PAGE_BODY = "?expand=body.view";
    private static final String ONLY_PAGE = " type=page";
    private static final String SPACE = "space=";


    private ConfluenceHttpRequestHelper() {
        //helper
    }

    private static HttpClient getBasicHttpClient() {
        return HttpClient.newBuilder().version(Version.HTTP_1_1).build();
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

    public static String createSearchArgs(final ConfluenceSearchObject filter, final boolean onlyPage, List<String> spaces) {
        final var builder = new StringBuilder();
        final var visitor = new ConfluenceVisitor();
        builder.append(filter.accept(visitor));

        if (spaces != null && !spaces.isEmpty()) {
            builder.append(ConfluenceSearchOperator.AND);
            builder.append(" (");
            spaces.forEach(space -> {
                builder.append(SPACE).append(space);
                builder.append(" ").append(ConfluenceSearchOperator.OR).append(" ");
            });
            // deleting the last " OR " after the last space
            builder.setLength(builder.length() - 4);
            builder.append(") ");
        }

        if (onlyPage) {
            builder.append(ConfluenceSearchOperator.AND);
            builder.append(ONLY_PAGE);
        }
        return ConfluenceHttpRequestHelper.encodeUrl(builder.toString());
    }

    private static String basicAuth(final String username, final String password) {
        return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    private static Builder createRequestBuilder(final String url, final Map<String, String> headers, final Map<String, String> params) {
        final URI uri = params != null ? createURIWithParams(url, params) : URI.create(url);
        final var builder = HttpRequest.newBuilder().uri(uri);
        for (final Entry<String, String> entry : headers.entrySet()) {
            builder.setHeader(entry.getKey(), entry.getValue());
        }
        return builder;
    }

    private static URI createURIWithParams(final String url, final Map<String, String> params) {
        final var builder = new StringBuilder();
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

}
