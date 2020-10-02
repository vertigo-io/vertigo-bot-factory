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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.vertigo.chatbot.commons.domain.ResponseButton;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.util.StringUtil;
import io.vertigo.datamodel.structure.model.DtList;

public final class RasaUtterButton {

	private final String text;
	private String payload;
	private final Long intentId;

	static RasaUtterButton fromResponseButton(final ResponseButton button) {
		Assertion.check().isNotNull(button);
		return new RasaUtterButton(button.getText(), button.getSmtIdResponse());
	}

	static List<RasaUtterButton> fromResponseButtonList(final DtList<ResponseButton> buttons) {
		if (buttons == null) {
			return new ArrayList<>();
		}
		return buttons.stream()
				.map(RasaUtterButton::fromResponseButton)
				.collect(Collectors.toList());
	}

	private RasaUtterButton(final String text, final Long intentId) {
		Assertion.check()
		.isNotNull(text)
		.isNotNull(intentId);
		//--
		this.text = text;
		this.intentId = intentId;
	}

	public String getText() {
		return text;
	}

	public String getPayload() {
		return payload;
	}

	public void resolve(final List<RasaIntent> intents) {
		final RasaIntent target = intents.stream()
				.filter(i -> intentId.equals(i.getId()))
				.reduce((a, b) -> { // no more than 1 element
					throw new IllegalStateException("Multiple elements: " + a + ", " + b);
				})
				.orElseThrow(() -> new VUserException("Unknown intent ID " + intentId));

		payload = "/" + target.getCode();
	}

	public String getUtterTemplate() {
		Assertion.check().isFalse(StringUtil.isBlank(payload), "Button '" + text + "' not resolved");
		// ----

		return "        - title: \"" + text.replaceAll("[\\\"]", "\\\\\\\"") + "\"\r\n" +
				"          payload: \"" + payload + "\"\r\n";
	}
}
