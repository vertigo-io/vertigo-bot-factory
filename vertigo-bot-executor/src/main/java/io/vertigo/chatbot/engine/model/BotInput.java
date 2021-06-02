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
import java.util.Map;

public final class BotInput {

	private String message;
	private Map<String, Object> metadatas;

	public BotInput() {
		metadatas = Collections.emptyMap();
	}

	public BotInput(final String message) {
		this.message = message;
		metadatas = Collections.emptyMap();
	}

	public BotInput(final Map<String, Object> metadatas) {
		this.metadatas = metadatas;
	}

	public BotInput(final String message, final Map<String, Object> metadatas) {
		this.message = message;
		this.metadatas = metadatas;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(final String message) {
		this.message = message;
	}

	/**
	 * @return the metadatas
	 */
	public Map<String, Object> getMetadatas() {
		return metadatas;
	}

	/**
	 * @param metadatas the metadatas to set
	 */
	public void setMetadatas(final Map<String, Object> metadatas) {
		this.metadatas = metadatas;
	}

}
