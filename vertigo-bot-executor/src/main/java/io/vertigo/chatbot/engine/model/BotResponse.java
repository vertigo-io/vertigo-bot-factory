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
import java.util.Map;

import io.vertigo.chatbot.engine.model.choice.IBotChoice;

public final class BotResponse {
	private String htmlText;
	private List<IBotChoice> choices;
	private Map<String, Object> metadatas;

	public BotResponse(final String htmlText, final List<IBotChoice> choices, final Map<String, Object> metadatas) {
		this.htmlText = htmlText;
		this.choices = choices;
		this.metadatas = metadatas;
	}

	/**
	 * @return the htmlText
	 */
	public String getHtmlText() {
		return htmlText;
	}

	/**
	 * @param htmlText the htmlText to set
	 */
	public void setHtmlText(final String htmlText) {
		this.htmlText = htmlText;
	}

	/**
	 * @return the choices
	 */
	public List<IBotChoice> getChoices() {
		return choices;
	}

	/**
	 * @param choices the choices to set
	 */
	public void setChoices(final List<IBotChoice> choices) {
		this.choices = choices;
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
