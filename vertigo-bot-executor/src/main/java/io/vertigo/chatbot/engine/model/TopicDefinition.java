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

import java.util.List;

import io.vertigo.ai.bt.BTNode;
import io.vertigo.core.lang.Assertion;

/**
 * @author skerdudou
 */
public final class TopicDefinition {
	private final String code;
	private final BTNode btRoot;

	private final List<String> trainingPhrases;
	private final Double nluThreshold;

	public TopicDefinition(final String code, final BTNode btRoot, final List<String> trainingPhrases, final Double nluThreshold) {
		Assertion.check()
				.isNotBlank(code)
				.isNotNull(btRoot)
				.isNotNull(trainingPhrases)
				.isNotNull(nluThreshold);
		//--
		this.code = code;
		this.btRoot = btRoot;
		this.trainingPhrases = trainingPhrases;
		this.nluThreshold = nluThreshold;
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
	public BTNode getBtRoot() {
		return btRoot;
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
