package io.vertigo.chatbot.designer.commons;

import io.vertigo.app.config.DefinitionProviderConfig;
import io.vertigo.app.config.discovery.ModuleDiscoveryFeatures;
import io.vertigo.chatbot.designer.boot.ChatbotMasterDataDefinitionProvider;
import io.vertigo.dynamo.plugins.environment.DynamoDefinitionProvider;

public class DesignerCommonsFeatures extends ModuleDiscoveryFeatures<DesignerCommonsFeatures> { // nous étendons ModuleDiscoveryFeatures pour activer la découverte automatique

	public DesignerCommonsFeatures() {
		super("ChatbotDesignerCommons"); // Nous donnons un nom signifiant à notre module métier
	}

	@Override
	protected void buildFeatures() {
		super.buildFeatures();
		getModuleConfigBuilder()
				.addDefinitionProvider(DefinitionProviderConfig.builder(DynamoDefinitionProvider.class)
						.addDefinitionResource("kpr", "io/vertigo/chatbot/designer/run.kpr") // chargement de notre modèle de donnée
						.addDefinitionResource("classes", "io.vertigo.chatbot.domain.DtDefinitions")
						.build())
				.addDefinitionProvider(ChatbotMasterDataDefinitionProvider.class);
	}

	@Override
	protected String getPackageRoot() {
		return this.getClass().getPackage().getName(); // nous utilisons la localisation de la classe de manisfeste comme racine du module
	}

}
