package io.vertigo.chatbot.executor;

import io.vertigo.app.config.DefinitionProviderConfig;
import io.vertigo.app.config.discovery.ModuleDiscoveryFeatures;
import io.vertigo.dynamo.plugins.environment.DynamoDefinitionProvider;

public class RasaExecutorFeatures extends ModuleDiscoveryFeatures<RasaExecutorFeatures> { // nous étendons ModuleDiscoveryFeatures pour activer la découverte automatique

	public RasaExecutorFeatures() {
		super("ChatbotExecutor"); // Nous donnons un nom signigiant à notre module métier
	}

	@Override
	protected void buildFeatures() {
		super.buildFeatures(); // découverte automatique de tous les composants
		getModuleConfigBuilder()
		.addDefinitionProvider(DefinitionProviderConfig.builder(DynamoDefinitionProvider.class)
				.addDefinitionResource("kpr", "io/vertigo/chatbot/executor/run.kpr") // chargement de notre modèle de donnée
				.addDefinitionResource("classes", "io.vertigo.chatbot.domain.DtDefinitions")
				.build());

	}

	@Override
	protected String getPackageRoot() {
		return this.getClass().getPackage().getName(); // nous utilisons la localisation de la classe de manisfeste comme racine du module
	}

}