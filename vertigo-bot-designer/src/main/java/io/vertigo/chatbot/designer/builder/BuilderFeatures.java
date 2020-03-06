package io.vertigo.chatbot.designer.builder;

import io.vertigo.app.config.discovery.ModuleDiscoveryFeatures;

public class BuilderFeatures extends ModuleDiscoveryFeatures<BuilderFeatures> { // nous étendons ModuleDiscoveryFeatures pour activer la découverte automatique

	public BuilderFeatures() {
		super("ChatbotDesigner"); // Nous donnons un nom signifiant à notre module métier
	}

	@Override
	protected String getPackageRoot() {
		return this.getClass().getPackage().getName(); // nous utilisons la localisation de la classe de manisfeste comme racine du module
	}

}
