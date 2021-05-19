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
import java.util.Map;

public final class BotResponse {
	public enum BotStatus {
		Talking, Ended;
	}

	private final List<String> htmlTexts;
	private final List<BotButton> choices;
	private final Map<String, Object> metadatas;
	private final BotStatus status;

	BotResponse(final List<String> htmlTexts, final List<BotButton> choices, final Map<String, Object> metadatas, final BotStatus status) {
		this.htmlTexts = Collections.unmodifiableList(htmlTexts);
		this.choices = Collections.unmodifiableList(choices);
		this.metadatas = metadatas;
		this.status = status;
	}

	/**
	 * @return the htmlText
	 */
	public List<String> getHtmlTexts() {
		return htmlTexts;
	}

	/**
	 * @return the choices
	 */
	public List<BotButton> getChoices() {
		return choices;
	}

	/**
	 * @return the metadatas
	 */
	public Map<String, Object> getMetadatas() {
		return metadatas;
	}

	/**
	 * @return the status
	 */
	public BotStatus getStatus() {
		return status;
	}

}
