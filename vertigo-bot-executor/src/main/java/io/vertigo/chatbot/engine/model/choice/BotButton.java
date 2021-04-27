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
package io.vertigo.chatbot.engine.model.choice;

import java.util.List;

import io.vertigo.core.lang.Assertion;

/**
 * Basic implementation of IBotChoice for simple buttons.
 *
 * @author skerdudou
 */
public final class BotButton implements IBotChoice {
	private final String label;
	private final String payload;

	public BotButton(final String label, final String payload) {
		this.label = label;
		this.payload = payload;
	}

	public static IBotChoice of(final String[] params) {
		Assertion.check().isTrue(params.length == 2, "BotButton need exactly 2 params");
		//--
		return new BotButton(params[0], params[1]);
	}

	/**
	 * @return the label
	 */
	@Override
	public String getLabel() {
		return label;
	}

	/**
	 * @return the payload
	 */
	@Override
	public String getPayload() {
		return payload;
	}

	@Override
	public String[] exportParams() {
		return List.of(label, payload).toArray(String[]::new);
	}

}
