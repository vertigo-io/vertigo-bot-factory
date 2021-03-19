package io.vertigo.chatbot.designer.dao;

import io.vertigo.core.node.config.discovery.ModuleDiscoveryFeatures;

public class DAOFeatures extends ModuleDiscoveryFeatures<DAOFeatures> {

	public DAOFeatures() {
		super("ChatbotDAO"); // Nous donnons un nom signifiant à notre module métier
	}

	@Override
	protected String getPackageRoot() {
		return this.getClass().getPackage().getName(); // nous utilisons la localisation de la classe de manisfeste comme racine du module
	}

}
