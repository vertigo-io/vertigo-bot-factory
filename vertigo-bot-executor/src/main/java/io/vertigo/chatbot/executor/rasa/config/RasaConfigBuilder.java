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
package io.vertigo.chatbot.executor.rasa.config;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.vertigo.chatbot.commons.domain.ResponseButton;
import io.vertigo.chatbot.commons.domain.SmallTalk;
import io.vertigo.chatbot.executor.domain.RasaConfig;
import io.vertigo.dynamo.domain.model.DtList;

public class RasaConfigBuilder {

	private static final String NEW_LINE = "\r\n";

	private final BigDecimal nluThreshold;

	private final List<RasaAction> actions = new ArrayList<>();
	private final List<RasaIntent> intents = new ArrayList<>();

	public RasaConfigBuilder(final String fallbackText, final DtList<ResponseButton> fallbackButtons,
			final String welcomeText, final DtList<ResponseButton> welcomeButtons,
			final BigDecimal nluThreshold) {

		this.nluThreshold = nluThreshold;

		// add welcome
		final RasaAction actionWelcome = RasaAction.newStartUtter(welcomeText, RasaUtterButton.fromResponseButtonList(welcomeButtons));
		actions.add(actionWelcome);
		intents.add(RasaIntent.newStartIntent(actionWelcome));

		// add fallback
		actions.add(RasaAction.newFallbackUtter(fallbackText, RasaUtterButton.fromResponseButtonList(fallbackButtons)));
	}

	public RasaConfigBuilder addSmallTalk(final SmallTalk smallTalk, final List<String> nlus, final List<String> messages, final DtList<ResponseButton> buttons) {
		//		Assertion.checkState(!utterances.containsKey(name), "Le code de message {1} existe déjà", name);
		// ----
		final String name = smallTalk.getSmtId().toString() + '_' + smallTalk.getTitle();

		final RasaAction action = RasaAction.newStUtterance(name, messages, RasaUtterButton.fromResponseButtonList(buttons));
		final RasaIntent intent = RasaIntent.newSmallTalk(smallTalk.getSmtId(), name, nlus, action);

		actions.add(action);
		intents.add(intent);

		return this;
	}

	public RasaConfig build() {
		actions.forEach(a -> a.resolve(intents));

		final RasaConfig rasaConfig = new RasaConfig();

		rasaConfig.setDomain(doBuildDomain());
		rasaConfig.setNlu(doBuildNlu());
		rasaConfig.setStories(doBuildStories());
		rasaConfig.setConfig(doBuildConfig());

		return rasaConfig;
	}

	private String doBuildDomain() {
		final StringBuilder retour = new StringBuilder();

		retour.append("intents:");
		retour.append(NEW_LINE);

		for (final RasaIntent intent : intents) {
			retour.append(intent.getDomainDeclaration());
			retour.append(NEW_LINE);
		}
		retour.append(NEW_LINE);

		// ajout des actions
		retour.append("actions:");
		retour.append(NEW_LINE);

		for (final RasaAction action : actions) {
			retour.append("  - ");
			retour.append(action.getCode());
			retour.append(NEW_LINE);
		}
		retour.append(NEW_LINE);

		// ajout des templates
		retour.append("templates:");
		retour.append(NEW_LINE);

		for (final RasaAction action : actions) {
			if (action.isUtterance()) {
				retour.append(action.getUtterTemplate());
			}
		}

		return retour.toString();
	}

	private String doBuildNlu() {
		final StringBuilder retour = new StringBuilder();

		for (final RasaIntent intent : intents) {
			retour.append(intent.getNluDeclaration())
					.append(NEW_LINE);
		}

		return retour.toString();
	}

	private String doBuildStories() {
		// nothing usefull in this file, all is based on triggers for this version
		// but if we leave it empty, only NLU is trained by rasa, and mappingPolicy is not used :(

		//		return "";
		return "## start" + NEW_LINE +
				"* start" + NEW_LINE +
				"    - utter_start";
	}

	private String doBuildConfig() {
		return "language: fr_core_news_md" + NEW_LINE +
				"pipeline: supervised_embeddings" + NEW_LINE +
				NEW_LINE +
				"policies:" + NEW_LINE +
				"  - name: MappingPolicy" + NEW_LINE +
				"  - name: FallbackPolicy" + NEW_LINE +
				"    nlu_threshold: " + nluThreshold + NEW_LINE +
				"    core_threshold: 0.3" + NEW_LINE +
				"    fallback_action_name: 'action_default_fallback'" + NEW_LINE;
	}
}
