package io.vertigo.chatbot.designer.analytics;

import io.vertigo.app.config.discovery.ModuleDiscoveryFeatures;

public class AnalyticsFeatures extends ModuleDiscoveryFeatures<AnalyticsFeatures> { // nous étendons ModuleDiscoveryFeatures pour activer la découverte automatique

	public AnalyticsFeatures() {
		super("ChatbotAnalytics"); // Nous donnons un nom signifiant à notre module métier
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