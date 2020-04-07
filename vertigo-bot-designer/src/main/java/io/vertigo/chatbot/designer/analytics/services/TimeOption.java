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
package io.vertigo.chatbot.designer.analytics.services;

public enum TimeOption {
	DAY("1d", "1h"),
	WEEK("7d", "12h"),
	MONTH("30d", "1d"),
	YEAR("52w", "1w");
	private final String range;
	private final String grain;

	private TimeOption(final String range, final String grain) {
		this.range = range;
		this.grain = grain;
	}

	/**
	 * @return the range
	 */
	public String getRange() {
		return range;
	}
	/**
	 * @return the grain
	 */
	public String getGrain() {
		return grain;
	}
}