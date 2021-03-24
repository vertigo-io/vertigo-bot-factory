package io.vertigo.ai;

/**
 * vertigo - simple java starter
 *
 * Copyright (C) 2013-2019, Vertigo.io, KleeGroup, direction.technique@kleegroup.com (http://www.kleegroup.com)
 * KleeGroup, Centre d'affaire la Boursidiere - BP 159 - 92357 Le Plessis Robinson Cedex - France
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

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertigo.ai.nlu.NluManager;
import io.vertigo.ai.nlu.VIntent;
import io.vertigo.core.node.AutoCloseableNode;
import io.vertigo.core.node.component.di.DIInjector;
import io.vertigo.core.node.config.BootConfig;
import io.vertigo.core.node.config.NodeConfig;
import io.vertigo.core.param.Param;
import io.vertigo.core.plugins.resource.classpath.ClassPathResourceResolverPlugin;
import io.vertigo.core.plugins.resource.url.URLResourceResolverPlugin;

/**
 * Test of the NLU manager.
 *
 * @author skerdudou
 */
public final class NluAiTest {

	private AutoCloseableNode node;

	@Inject
	private NluManager nluManager;

	private void defaultIntentCorpus() {
		final var intentMeteo = VIntent.of("meteo", "afficher la météo");
		nluManager.registerIntent(intentMeteo);
		nluManager.addTrainingPhrase(intentMeteo, "quel temps fait-il demain ?");
		nluManager.addTrainingPhrase(intentMeteo, "donne moi la météo");
		nluManager.addTrainingPhrase(intentMeteo, "C'est quoi la météo pour demain ?");
		nluManager.addTrainingPhrase(intentMeteo, "il fait beau demain ?");
		nluManager.addTrainingPhrase(intentMeteo, "va t il pleuvoir dans les prochains jours ?");

		final var intentTrain = VIntent.of("train", "prendre le train");
		nluManager.registerIntent(intentTrain);
		nluManager.addTrainingPhrase(intentTrain, "je voudrais prendre le train");
		nluManager.addTrainingPhrase(intentTrain, "réserver billet de train");
		nluManager.addTrainingPhrase(intentTrain, "réserve-moi un ticket de train");
		nluManager.addTrainingPhrase(intentTrain, "je veux un billet de train");

		final var intentBlague = VIntent.of("blague", "Ah ah ah !");
		nluManager.registerIntent(intentBlague);
		nluManager.addTrainingPhrase(intentBlague, "raconte moi une blague");
		nluManager.addTrainingPhrase(intentBlague, "donne moi une blague");
		nluManager.addTrainingPhrase(intentBlague, "fais moi rire");
		nluManager.addTrainingPhrase(intentBlague, "t'a pas une blague pour moi ?");
		nluManager.addTrainingPhrase(intentBlague, "je veux une blague");
		nluManager.addTrainingPhrase(intentBlague, "je voudrais une blague");

		nluManager.trainAll();
	}

	@Test
	public void testNlu() {
		defaultIntentCorpus();

		var result = nluManager.recognize("je veux rire");
		Assertions.assertFalse(result.getIntentClassificationList().isEmpty());
		Assertions.assertEquals("blague", result.getIntentClassificationList().get(0).getIntent().getCode());

		result = nluManager.recognize("j'ai un train a prendre");
		Assertions.assertFalse(result.getIntentClassificationList().isEmpty());
		Assertions.assertEquals("train", result.getIntentClassificationList().get(0).getIntent().getCode());

		result = nluManager.recognize("quel est la météo demain");
		Assertions.assertEquals("meteo", result.getIntentClassificationList().get(0).getIntent().getCode());
		Assertions.assertEquals(true, result.getIntentClassificationList().get(0).getAccuracy() > 0.4D);
	}

	@BeforeEach
	public final void setUp() {
		node = new AutoCloseableNode(buildNodeConfig());
		DIInjector.injectMembers(this, node.getComponentSpace());
	}

	@AfterEach
	public final void tearDown() {
		if (node != null) {
			node.close();
		}
	}

	private NodeConfig buildNodeConfig() {
		return NodeConfig.builder()
				.withBoot(BootConfig.builder()
						.withLocales("fr_FR")
						.addPlugin(ClassPathResourceResolverPlugin.class)
						.addPlugin(URLResourceResolverPlugin.class)
						.build())
				.addModule(new AiFeatures()
						.withNLU()
						.withRasaNLU(Param.of("rasaUrl", "http://localhost:5005"))
						.build())
				.build();
	}

}
