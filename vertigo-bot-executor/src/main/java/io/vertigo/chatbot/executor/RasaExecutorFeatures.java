/**
 * vertigo - simple java starter
 *
 * Copyright (C) 2020, Vertigo.io, team@vertigo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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