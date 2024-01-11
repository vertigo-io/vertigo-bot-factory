package io.vertigo.chatbot.engine.plugins.bt.jira.helper;

import com.atlassian.jira.rest.client.api.domain.ServerInfo;
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import com.atlassian.jira.rest.client.internal.json.JsonParseUtil;
import com.atlassian.jira.rest.client.internal.json.ServerInfoJsonParser;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;

import java.net.URI;

/**
 * @author cmarechal
 * @created 09/01/2024 - 17:34
 * @project vertigo-bot-factory
 */
public class CustomServerInfoJsonParser implements JsonObjectParser<CustomServerInfo> {
    @Override
    public CustomServerInfo parse(JSONObject json) throws JSONException {
        final URI baseUri = JsonParseUtil.parseURI(json.getString("baseUrl"));
        final String version = json.getString("version");
        final int buildNumber = json.getInt("buildNumber");
        final DateTime buildDate = JsonParseUtil.parseDateTime(json, "buildDate");
        final DateTime serverTime = JsonParseUtil.parseOptionalDateTime(json, "serverTime");
        final String scmInfo = json.getString("scmInfo");
        final String serverTitle = json.getString("serverTitle");
        final String deploymentType = json.getString("deploymentType");
        return new CustomServerInfo(baseUri, version, buildNumber, buildDate, serverTime, scmInfo, serverTitle, deploymentType);
    }
}
