package io.vertigo.chatbot.designer.commons;

import io.vertigo.app.config.discovery.ModuleDiscoveryFeatures;

public class DesignerCommonsFeatures extends ModuleDiscoveryFeatures<DesignerCommonsFeatures> { // nous étendons ModuleDiscoveryFeatures pour activer la découverte automatique

	public DesignerCommonsFeatures() {
		super("ChatbotDesignerCommons"); // Nous donnons un nom signigiant à notre module métier
	}

	@Override
	protected String getPackageRoot() {
		return this.getClass().getPackage().getName(); // nous utilisons la localisation de la classe de manisfeste comme racine du module
	}

}