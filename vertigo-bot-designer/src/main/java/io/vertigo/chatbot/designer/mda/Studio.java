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
package io.vertigo.chatbot.designer.mda;

import io.vertigo.app.AutoCloseableApp;
import io.vertigo.app.config.DefinitionProviderConfig;
import io.vertigo.app.config.ModuleConfig;
import io.vertigo.app.config.NodeConfig;
import io.vertigo.commons.CommonsFeatures;
import io.vertigo.core.param.Param;
import io.vertigo.core.plugins.resource.classpath.ClassPathResourceResolverPlugin;
import io.vertigo.dynamo.DynamoFeatures;
import io.vertigo.dynamo.plugins.environment.DynamoDefinitionProvider;
import io.vertigo.studio.StudioFeatures;
import io.vertigo.studio.mda.MdaManager;

public class Studio {

	// Méthode d'initialisation de la configuration de Studio
	private static NodeConfig buildNodeConfig() {
		return NodeConfig.builder() // Création d'un conteneur pour la configuration
				.beginBoot() // Debut de configuration du boot de l'application qui permet de spécifier les
				// plugins des composants natifs (ParamManager et ResourcesManager)
				.addPlugin(ClassPathResourceResolverPlugin.class) // Initialisation du resolveur de ressources via le
				// classpath
				.endBoot() // Le démarrage de vertigo-studio est terminé
				.addModule(new CommonsFeatures().build()) // Configuration des fonctions communes de Vertigo
				.addModule(new DynamoFeatures().build()) // Configuration des fonctions d'accès aux données
				//				.addModule(new ChatbotCommonsFeatures().build())
				// ----Definitions
				.addModule(ModuleConfig.builder("ressources") // Ajout des ressources pour la génération des classes
						// Java
						.addDefinitionProvider(DefinitionProviderConfig.builder(DynamoDefinitionProvider.class)
								.addDefinitionResource("kpr", "io/vertigo/chatbot/commons/gen.kpr") // chargement des ksp communs
								.addDefinitionResource("kpr", "io/vertigo/chatbot/designer/gen.kpr").build())
						.build())
				// ---StudioFeature
				.addModule(new StudioFeatures() // Configuration du moteur vertigo-Studio
						.withMasterData()
						.withMda(Param.of("projectPackageName", "io.vertigo.chatbot"))
						.withJavaDomainGenerator(Param.of("generateDtResources", "false"))
						.withTaskGenerator()
						.withFileGenerator()
						.withSqlDomainGenerator(
								Param.of("targetSubDir", "javagen/sqlgen"),
								Param.of("baseCible", "Postgres"),
								Param.of("generateDrop", "true"),
								Param.of("generateMasterData", "true"))
						.withJsonMasterDataValuesProvider(
								Param.of("fileName", "io/vertigo/chatbot/commons/masterDataValues.json"))
						.build())
				.build();

	}

	// Méthode main à lancer pour générer les éléments du projet à partir du modèle
	public static void main(final String[] args) {
		// Création de l'application vertigo-studio avec la configuration ci-dessus
		try (final AutoCloseableApp app = new AutoCloseableApp(buildNodeConfig())) {
			final MdaManager mdaManager = app.getComponentSpace().resolve(MdaManager.class);
			// -----
			// Nettoyage des générations précédentes
			mdaManager.clean();
			// Lancement de la génération
			mdaManager.generate().displayResultMessage(System.out);
		}
	}

}
