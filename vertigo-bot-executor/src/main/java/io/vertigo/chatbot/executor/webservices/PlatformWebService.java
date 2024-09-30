package io.vertigo.chatbot.executor.webservices;

import com.google.gson.JsonElement;

import javax.inject.Inject;

import io.vertigo.chatbot.executor.manager.ExecutorManager;
import io.vertigo.vega.webservice.WebServices;
import io.vertigo.vega.webservice.stereotype.GET;
import io.vertigo.vega.webservice.stereotype.PathPrefix;

@PathPrefix("/platform-config")
public class PlatformWebService implements WebServices {

    @Inject
    private ExecutorManager executorManager;

    @GET("/getCustomConfig")
    public JsonElement getCustomConfig() {
        return executorManager.getCustomConfig();
    }
}
