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
package io.vertigo.chatbot.analytics.rasa.model;

import java.math.BigDecimal;

public class RasaTrackerActionEvent extends AbstractRasaTrackerEvent {
	private String name;
	private String policy;
	private BigDecimal confidence;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}
	/**
	 * @return the policy
	 */
	public String getPolicy() {
		return policy;
	}
	/**
	 * @param policy the policy to set
	 */
	public void setPolicy(final String policy) {
		this.policy = policy;
	}
	/**
	 * @return the confidence
	 */
	public BigDecimal getConfidence() {
		return confidence;
	}
	/**
	 * @param confidence the confidence to set
	 */
	public void setConfidence(final BigDecimal confidence) {
		this.confidence = confidence;
	}

}
