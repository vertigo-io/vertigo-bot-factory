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

import java.util.List;

import io.vertigo.chatbot.executor.rasa.util.StringUtils;
import io.vertigo.lang.Assertion;

public final class RasaIntent {

	private static final String NEW_LINE = "\r\n";

	private final Long id;
	private final String code;
	private final List<String> nlus;
	private final RasaAction trigger;

	public static RasaIntent ofSmallTalk(final Long id, final String name, final List<String> nlus, final RasaAction trigger) {
		Assertion.checkNotNull(trigger);
		Assertion.checkArgument(trigger.isSmallTalk(), "Trigger must be a small talk action");
		// ----
		return new RasaIntent(id, "st_" + StringUtils.labelToCode(name), nlus, trigger);
	}

	public static RasaIntent ofGenericIntent(final Long id, final String name, final List<String> nlus) {
		return new RasaIntent(id, "int_" + name, nlus, null);
	}

	public static RasaIntent ofStartIntent(final RasaAction trigger) {
		Assertion.checkNotNull(trigger);
		//--
		return new RasaIntent(null, "start", null, trigger);
	}

	private RasaIntent(final Long id, final String code, final List<String> nlus, final RasaAction trigger) {
		this.id = id;
		this.code = code;
		this.nlus = nlus;
		this.trigger = trigger;
	}

	public Long getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public List<String> getNlus() {
		return nlus;
	}

	public RasaAction getTrigger() {
		return trigger;
	}

	public String getNluDeclaration() {
		if (nlus == null || nlus.isEmpty()) {
			// no NLU
			return null;
		}

		final StringBuilder nluDef = new StringBuilder()
				.append("## intent:").append(code).append(NEW_LINE);

		for (final String question : nlus) {
			nluDef.append("- ").append(question).append(NEW_LINE);
		}

		nluDef.append(NEW_LINE);

		return nluDef.toString();
	}

	public String getDomainDeclaration() {
		String retour = "  - " + code;

		if (trigger != null) {
			retour += ":" + NEW_LINE + "     triggers: " + trigger.getCode();
		}

		return retour;
	}
}
