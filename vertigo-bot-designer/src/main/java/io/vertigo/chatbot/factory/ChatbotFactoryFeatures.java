package io.vertigo.chatbot.factory;

import io.vertigo.app.config.DefinitionProviderConfig;
import io.vertigo.app.config.discovery.ModuleDiscoveryFeatures;
import io.vertigo.dynamo.plugins.environment.DynamoDefinitionProvider;

public class ChatbotFactoryFeatures extends ModuleDiscoveryFeatures<ChatbotFactoryFeatures> { // nous étendons ModuleDiscoveryFeatures pour activer la découverte automatique

    public ChatbotFactoryFeatures() {
        super("ChatbotFactory"); // Nous donnons un nom signigiant à notre module métier
    }

    @Override
    protected void buildFeatures() {
        super.buildFeatures(); // découverte automatique de tous les composants
        getModuleConfigBuilder()
                .addDefinitionProvider(DefinitionProviderConfig.builder(DynamoDefinitionProvider.class)
                        .addDefinitionResource("kpr", "io/vertigo/chatbot/factory/run.kpr") // chargement de notre modèle de donnée
                        .build());

    }

    @Override
    protected String getPackageRoot() {
        return this.getClass().getPackage().getName(); // nous utilisons la localisation de la classe de manisfeste comme racine du module
    }

}