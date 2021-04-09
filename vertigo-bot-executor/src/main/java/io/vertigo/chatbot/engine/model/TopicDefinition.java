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
package io.vertigo.chatbot.engine.model;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import io.vertigo.ai.bb.BlackBoard;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.chatbot.engine.BotNodeProvider;
import io.vertigo.core.lang.Assertion;

/**
 * @author skerdudou
 */
public final class TopicDefinition {
	private final String code;
	private final Function<BlackBoard, BTNode> btRootProvider;

	private final List<String> trainingPhrases;
	private final Double nluThreshold;

	private TopicDefinition(final String code, final Function<BlackBoard, BTNode> btRootProvider, final List<String> trainingPhrases, final Double nluThreshold) {
		Assertion.check()
				.isNotBlank(code)
				.isNotNull(btRootProvider)
				.isNotNull(trainingPhrases)
				.isNotNull(nluThreshold);
		//--
		this.code = code;
		this.btRootProvider = btRootProvider;
		this.trainingPhrases = trainingPhrases;
		this.nluThreshold = nluThreshold;
	}

	public static TopicDefinition of(final String code, final Function<BlackBoard, BTNode> btRootProvider, final List<String> trainingPhrases, final Double nluThreshold) {
		return new TopicDefinition(code, btRootProvider, trainingPhrases, nluThreshold);
	}

	public static TopicDefinition of(final String code, final Function<BlackBoard, BTNode> btRootProvider) {
		return of(code, btRootProvider, Collections.emptyList(), 0.0);
	}

	public static TopicDefinition ofWithBotNodeProvider(final String code, final Function<BotNodeProvider, BTNode> botDef, final List<String> trainingPhrases, final Double nluThreshold) {
		return of(code, (final BlackBoard b) -> botDef.apply(new BotNodeProvider(b)), trainingPhrases, nluThreshold);
	}

	public static TopicDefinition ofWithBotNodeProvider(final String code, final Function<BotNodeProvider, BTNode> botDef) {
		return ofWithBotNodeProvider(code, botDef, Collections.emptyList(), 0.0);
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @return the btRoot
	 */
	public BTNode getBtRoot(final BlackBoard bb) {
		return btRootProvider.apply(bb);
	}

	/**
	 * @return the trainingPhrases
	 */
	public List<String> getTrainingPhrases() {
		return trainingPhrases;
	}

	/**
	 * @return the nluThreshold
	 */
	public Double getNluThreshold() {
		return nluThreshold;
	}

}
