package io.vertigo.chatbot.engine.plugins.bt.jira.helper;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.internal.async.AsynchronousUserRestClient;
import com.atlassian.jira.rest.client.internal.json.UsersJsonParser;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import io.atlassian.util.concurrent.Promise;

/**
 * @author cmarechal
 * @created 09/01/2024 - 14:38
 * @project vertigo-bot-factory
 */
public class CustomAsynchronousJiraRestClient extends AsynchronousUserRestClient {

    private final UsersJsonParser usersJsonParser = new UsersJsonParser();

    private final CustomServerInfoJsonParser customServerInfoJsonParser = new CustomServerInfoJsonParser();

    private static final String USER_URI_PREFIX = "user";
    private static final String SEARCH_URI_PREFIX = "assignable/multiProjectSearch";

    private static final String SERVER_INFO_RESOURCE = "/serverInfo";

    private static final String QUERY_ATTRIBUTE = "query";

    private static final String PROJECT_ATTRIBUTE = "projectKeys";

    private final URI uri;

    public CustomAsynchronousJiraRestClient(URI uri, HttpClient client) {
        super(uri, client);
        this.uri = UriBuilder.fromUri(uri).path("/rest/api/latest").build();;
    }

    public Promise<Iterable<User>> findUsers(String username, String project) {

        UriBuilder uriBuilder = UriBuilder.fromUri(uri).path(USER_URI_PREFIX).path(SEARCH_URI_PREFIX)
                .queryParam(QUERY_ATTRIBUTE, username).queryParam(PROJECT_ATTRIBUTE, project);

        final URI usersUri = uriBuilder.build();
        return getAndParse(usersUri, usersJsonParser);
    }

    public Promise<CustomServerInfo> getServerInfo() {
        final URI serverInfoUri = UriBuilder.fromUri(uri).path(SERVER_INFO_RESOURCE).build();
        return getAndParse(serverInfoUri, customServerInfoJsonParser);
    }
}
