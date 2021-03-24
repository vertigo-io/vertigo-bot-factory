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
package io.vertigo.chatbot.engine.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.ai.nlu.NluManager;
import io.vertigo.ai.nlu.VIntent;
import io.vertigo.ai.nlu.VRecognitionResult;
import io.vertigo.chatbot.engine.model.BotState;
import io.vertigo.chatbot.engine.model.TopicDefinition;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;

/**
 * Bot engine that handle user interactions through a Vertigo BlackBoard.
 * <br>
 * bb conventions :
 * <ul>
 * <li>/i/ : inputs</li>
 * <li>/o/ : outputs</li>
 * <li>/p/ : persisting informations</li>
 * <li>/v/ : volatile informations (local to a topic / BT)</li>
 * </ul>
 *
 * @author skerdudou
 */
public class BotEngine implements Component {

	private final NluManager nluManager;

	private Map<String, TopicDefinition> topicDefinitionMap;
	private Map<String, TopicDefinition> topicDefinitionTempMap;

	@Inject
	public BotEngine(final NluManager nluManager) {
		Assertion.check().isNotNull(nluManager);
		//--
		this.nluManager = nluManager;

		topicDefinitionMap = new HashMap<>();
	}

	/**
	 * Updates engine configuration.
	 * May take some time due to Nlu backend.
	 */
	public synchronized void updateConfig(final Iterable<TopicDefinition> newTopics) {
		final var nluTtrainingData = new HashMap<VIntent, List<String>>();
		for (final TopicDefinition t : newTopics) {
			nluTtrainingData.put(VIntent.of(t.getCode()), t.getTrainingPhrases()); // build NLU training data
			topicDefinitionTempMap.put(t.getCode(), t);
		}

		nluManager.train(nluTtrainingData); // the new NLU model is effectively running after this line

		// clean state
		topicDefinitionMap = topicDefinitionTempMap;
		topicDefinitionTempMap = null;
	}

	public void processMessage(final BotState state) {
	}

	private Optional<TopicDefinition> getTopicFromNlu(final String sentence) {
		final VRecognitionResult nluResponse = nluManager.recognize(sentence);
		// intents are sorted by decreasing accuracy
		for (final var intent : nluResponse.getIntentClassificationList()) {
			final var topic = getTopicByCode(intent.getIntent().getCode());
			if (intent.getAccuracy().compareTo(topic.getNluThreshold()) > 0) { // dont take if not accurate enough
				return Optional.of(topic);
			}
		}
		return Optional.empty();
	}

	private TopicDefinition getTopicByCode(final String code) {
		var topic = topicDefinitionMap.get(code);
		// if not found, search in temp space in case of parallelism issue (a call to NLU that return a new intent before the end of updateConfig method)
		topic = topic != null ? topic : topicDefinitionTempMap.get(code);

		Assertion.check().isNotNull(topic, "Topic '{0}' not found, is NLU backend up to date ?", code);
		return topic;
	}

}
