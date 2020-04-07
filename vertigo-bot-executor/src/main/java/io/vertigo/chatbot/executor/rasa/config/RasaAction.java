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

public class RasaAction {

	private static final String NEW_LINE = "\r\n";

	private final String code;
	private final boolean isUtterance;
	private final boolean isSmallTalk;
	private final List<String> texts;

	public static RasaAction newStUtterance(final String name, final List<String> texts) {
		return new RasaAction("utter_st_" + StringUtils.labelToCode(name), true, true, texts);
	}

	public static RasaAction newUtterance(final String name, final List<String> texts) {
		return new RasaAction("utter_cu_" + StringUtils.labelToCode(name), true, false, texts);
	}

	public static RasaAction newCustomAction(final String name) {
		return new RasaAction("action_cu_" + name, false, false, null);
	}

	private RasaAction(final String code, final boolean isUtterance, final boolean isSmallTalk, final List<String> texts) {
		this.code = code;
		this.isUtterance = isUtterance;
		this.isSmallTalk = isSmallTalk;
		this.texts = texts;
	}

	public String getCode() {
		return code;
	}

	public boolean isUtterance() {
		return isUtterance;
	}

	public boolean isSmallTalk() {
		return isSmallTalk;
	}

	public List<String> getTexts() {
		return texts;
	}

	public String getUtterTemplate() {
		Assertion.checkState(isUtterance, "Cette action n'est pas un utter");
		// ----

		final StringBuilder template = new StringBuilder();

		template.append("  ").append(code).append(':').append(NEW_LINE);

		for (final String answer : texts) {
			template.append("    - text: \"");
			template.append(answer.replaceAll("[\\r\\n]+", "<br>").replaceAll("[\\\"]", "\\\\\\\""));
			template.append('"').append(NEW_LINE);
		}

		template.append(NEW_LINE);

		return template.toString();
	}
}
