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
package io.vertigo.chatbot.commons;

@Deprecated
public enum RasaTypeAction {
	OPEN(false), RESTART(false), MESSAGE(true), RESPONSE_INFO(true), BUTTON(true), RATING(false);

	private boolean isUserMessage;

	private RasaTypeAction(final boolean isUserMessage) {
		this.isUserMessage = isUserMessage;
	}

	/**
	 * @return the isUserMessage
	 */
	public boolean isUserMessage() {
		return isUserMessage;
	}

}
