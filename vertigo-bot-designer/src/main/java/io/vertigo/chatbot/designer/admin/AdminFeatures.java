package io.vertigo.chatbot.designer.admin;

import io.vertigo.app.config.discovery.ModuleDiscoveryFeatures;

public class AdminFeatures extends ModuleDiscoveryFeatures<AdminFeatures> { // nous étendons ModuleDiscoveryFeatures pour activer la découverte automatique

	public AdminFeatures() {
		super("ChatbotAdmin"); // Nous donnons un nom signifiant à notre module métier
	}


	@Override
	protected String getPackageRoot() {
		return this.getClass().getPackage().getName(); // nous utilisons la localisation de la classe de manisfeste comme racine du module
	}

}