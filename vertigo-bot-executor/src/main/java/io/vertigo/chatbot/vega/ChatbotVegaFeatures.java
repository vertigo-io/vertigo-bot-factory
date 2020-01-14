package io.vertigo.chatbot.vega;

import io.vertigo.app.config.Feature;
import io.vertigo.app.config.Features;
import io.vertigo.app.config.PluginConfig;
import io.vertigo.app.config.PluginConfigBuilder;
import io.vertigo.chatbot.vega.plugins.webservice.handler.ApiTokenWebServiceHandlerPlugin;
import io.vertigo.core.param.Param;
import io.vertigo.lang.Assertion;
import io.vertigo.util.ListBuilder;
import io.vertigo.vega.VegaFeatures;
import io.vertigo.vega.engines.webservice.json.GoogleJsonEngine;
import io.vertigo.vega.engines.webservice.json.JsonEngine;
import io.vertigo.vega.impl.webservice.WebServiceManagerImpl;
import io.vertigo.vega.impl.webservice.catalog.CatalogWebServices;
import io.vertigo.vega.impl.webservice.catalog.SwaggerWebServices;
import io.vertigo.vega.plugins.webservice.handler.AnalyticsWebServiceHandlerPlugin;
import io.vertigo.vega.plugins.webservice.handler.CorsAllowerWebServiceHandlerPlugin;
import io.vertigo.vega.plugins.webservice.handler.ExceptionWebServiceHandlerPlugin;
import io.vertigo.vega.plugins.webservice.handler.JsonConverterWebServiceHandlerPlugin;
import io.vertigo.vega.plugins.webservice.handler.RestfulServiceWebServiceHandlerPlugin;
import io.vertigo.vega.plugins.webservice.handler.ValidatorWebServiceHandlerPlugin;
import io.vertigo.vega.plugins.webservice.scanner.annotations.AnnotationsWebServiceScannerPlugin;
import io.vertigo.vega.plugins.webservice.webserver.sparkjava.SparkJavaServletFilterWebServerPlugin;
import io.vertigo.vega.webservice.WebServiceManager;

/**
 * modules Vega pour le chatbot.
 * @author skerdudou
 */
public final class ChatbotVegaFeatures extends Features<VegaFeatures> {

	private boolean webservicesEnabled;

	private Param[] jsonParams = new Param[0];
	private String myApiPrefix;
	private String myApiKey;
	private String myOriginCORSFilter;

	public ChatbotVegaFeatures() {
		super("vertigo-vega");
	}

	@Feature("webservices")
	public ChatbotVegaFeatures withWebServices() {
		webservicesEnabled = true;
		return this;
	}

	@Feature("webservices.apiPrefix")
	public ChatbotVegaFeatures withWebServicesApiPrefix(final Param... params) {
		Assertion.checkState(params.length == 1 && "apiPrefix".equals(params[0].getName()), "apiPrefix param should be provided ");
		myApiPrefix = params[0].getValue();
		return this;
	}

	@Feature("webservices.json")
	public ChatbotVegaFeatures withWebServicesJson(final Param... params) {
		jsonParams = params;
		return this;
	}

	@Feature("webservices.apiToken")
	public ChatbotVegaFeatures withApiToken(final Param... params) {
		Assertion.checkState(params.length == 1 && "apiKey".equals(params[0].getName()), "apiKey param should be provided ");
		myApiKey = params[0].getValue();
		return this;
	}

	@Feature("webservices.cors")
	public ChatbotVegaFeatures withWebServicesOriginCORSFilter(final Param... params) {
		Assertion.checkState(params.length == 1 && "originCORSFilter".equals(params[0].getName()), "originCORSFilter param should be provided ");
		myOriginCORSFilter = params[0].getValue();
		return this;
	}

	/** {@inheritDoc} */
	@Override
	protected void buildFeatures() {
		if (webservicesEnabled) {
			final PluginConfigBuilder corsAllowerPluginConfigBuilder = PluginConfig.builder(CorsAllowerWebServiceHandlerPlugin.class);
			if (myOriginCORSFilter != null) {
				corsAllowerPluginConfigBuilder.addParam(Param.of("originCORSFilter", myOriginCORSFilter));
			}

			getModuleConfigBuilder()
			.addComponent(WebServiceManager.class, WebServiceManagerImpl.class)
			.addPlugin(AnnotationsWebServiceScannerPlugin.class)
			.addComponent(SwaggerWebServices.class)
			.addComponent(CatalogWebServices.class)

			//-- Handlers plugins
			.addPlugin(ExceptionWebServiceHandlerPlugin.class)
			.addPlugin(corsAllowerPluginConfigBuilder.build())
			.addPlugin(AnalyticsWebServiceHandlerPlugin.class)
			.addPlugin(JsonConverterWebServiceHandlerPlugin.class);

			getModuleConfigBuilder()
			.addComponent(JsonEngine.class, GoogleJsonEngine.class, jsonParams);

			// Add API token management
			if (myApiKey != null) {
				getModuleConfigBuilder()
				.addPlugin(ApiTokenWebServiceHandlerPlugin.class,Param.of("apiKey", myApiKey));
			}

			final ListBuilder<Param> params = new ListBuilder<>();
			if (myApiPrefix != null) {
				params.add(Param.of("apiPrefix", myApiPrefix));
			}
			getModuleConfigBuilder().addPlugin(new PluginConfig(SparkJavaServletFilterWebServerPlugin.class, params.build()));

			getModuleConfigBuilder().addPlugin(ValidatorWebServiceHandlerPlugin.class)
			.addPlugin(RestfulServiceWebServiceHandlerPlugin.class);
		}
	}
}
