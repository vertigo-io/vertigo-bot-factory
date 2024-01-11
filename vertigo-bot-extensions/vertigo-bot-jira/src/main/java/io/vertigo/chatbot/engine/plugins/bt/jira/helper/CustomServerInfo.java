package io.vertigo.chatbot.engine.plugins.bt.jira.helper;

import com.atlassian.jira.rest.client.api.domain.ServerInfo;

import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

import java.net.URI;

/**
 * @author cmarechal
 * @created 09/01/2024 - 17:32
 * @project vertigo-bot-factory
 */
public class CustomServerInfo extends ServerInfo {

    private final String deploymentType;
    public CustomServerInfo(URI baseUri, String version, int buildNumber, DateTime buildDate, @Nullable DateTime serverTime, String scmInfo, String serverTitle, String deploymentType) {
        super(baseUri, version, buildNumber, buildDate, serverTime, scmInfo, serverTitle);
        this.deploymentType = deploymentType;
    }

    public String getDeploymentType() {
        return deploymentType;
    }
}
