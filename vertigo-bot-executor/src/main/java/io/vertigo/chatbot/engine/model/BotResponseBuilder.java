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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vertigo.chatbot.engine.model.BotResponse.BotStatus;
import io.vertigo.chatbot.engine.model.choice.IBotChoice;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.Builder;

public final class BotResponseBuilder implements Builder<BotResponse> {
	private final List<String> htmlTexts;
	private final List<IBotChoice> choices;
	private final Map<String, Object> metadatas;
	private final BotStatus status;

	public BotResponseBuilder(final BotStatus status) {
		Assertion.check()
				.isNotNull(status);
		//--
		htmlTexts = new ArrayList<>();
		choices = new ArrayList<>();
		metadatas = new HashMap<>();
		this.status = status;
	}

	public BotResponseBuilder addMessage(final String message) {
		Assertion.check()
				.isNotBlank(message);
		//-----
		htmlTexts.add(message);
		return this;
	}

	public BotResponseBuilder addAllMessages(final Iterable<String> messages) {
		Assertion.check()
				.isNotNull(messages);
		//-----
		messages.forEach(this::addMessage);
		return this;
	}

	public BotResponseBuilder addChoice(final IBotChoice choice) {
		Assertion.check()
				.isNotNull(choice);
		//-----
		choices.add(choice);
		return this;
	}

	public BotResponseBuilder addAllChoices(final Iterable<IBotChoice> choices) {
		Assertion.check()
				.isNotNull(choices);
		//-----
		choices.forEach(this::addChoice);
		return this;
	}

	public BotResponseBuilder addMetadata(final String key, final Object value) {
		Assertion.check()
				.isNotNull(key)
				.isNotNull(value)
				.isFalse(metadatas.containsKey(key), "Metadata {0} already exists", key);
		//-----
		metadatas.put(key, value);
		return this;
	}

	public BotResponseBuilder addAllChoices(final Map<String, Object> newMetadatas) {
		Assertion.check()
				.isNotNull(newMetadatas);
		//-----
		newMetadatas.forEach(this::addMetadata);
		return this;
	}

	@Override
	public BotResponse build() {
		return new BotResponse(htmlTexts, choices, metadatas, status);
	}

}
