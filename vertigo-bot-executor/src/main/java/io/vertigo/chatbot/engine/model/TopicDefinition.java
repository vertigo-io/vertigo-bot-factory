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

import io.vertigo.ai.bt.BTNode;
import io.vertigo.core.lang.Assertion;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @author skerdudou
 */
public final class TopicDefinition {
	private final String code;
	private final Boolean unreachable;
	private final Function<List<Object>, BTNode> btRootProvider;

	private final List<String> trainingPhrases;
	private final Double nluThreshold;

	private TopicDefinition(final String code, final Function<List<Object>, BTNode> btRootProvider, final List<String> trainingPhrases, final Double nluThreshold, final Boolean unreachable) {
		Assertion.check()
				.isNotBlank(code)
				.isNotNull(btRootProvider)
				.isNotNull(trainingPhrases)
				.isNotNull(nluThreshold);
		//--
		this.code = code;
		this.unreachable = unreachable;
		this.btRootProvider = btRootProvider;
		this.trainingPhrases = trainingPhrases;
		this.nluThreshold = nluThreshold;
	}

	public static TopicDefinition of(final String code, final Function<List<Object>, BTNode> btRootProvider, final List<String> trainingPhrases, final Double nluThreshold, final Boolean unreachable) {
		return new TopicDefinition(code, btRootProvider, trainingPhrases, nluThreshold, unreachable);
	}

	public static TopicDefinition of(final String code, final Function<List<Object>, BTNode> btRootProvider) {
		return of(code, btRootProvider, Collections.emptyList(), 0.0, false);
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
	public BTNode getBtRoot(final List<Object> params) {
		return btRootProvider.apply(params);
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

	/**
	 * @return unreachable
	 */
	public Boolean getUnreachable() { return unreachable; }

}
