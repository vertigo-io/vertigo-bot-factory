package io.vertigo.chatbot.analytics;

import io.vertigo.app.config.discovery.ModuleDiscoveryFeatures;

public class RasaAnalyticsFeatures extends ModuleDiscoveryFeatures<RasaAnalyticsFeatures> { // nous étendons ModuleDiscoveryFeatures pour activer la découverte automatique

	public RasaAnalyticsFeatures() {
		super("ChatbotAnalytics"); // Nous donnons un nom signigiant à notre module métier
	}

	@Override
	protected void buildFeatures() {
		super.buildFeatures(); // découverte automatique de tous les composants

	}

	@Override
	protected String getPackageRoot() {
		return this.getClass().getPackage().getName(); // nous utilisons la localisation de la classe de manisfeste comme racine du module
	}

}