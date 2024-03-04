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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;

public enum TimeOption {
	DAY(1, ChronoUnit.DAYS, "1h"), WEEK(7, ChronoUnit.DAYS, "1d"), MONTH(1, ChronoUnit.MONTHS, "1d"), YEAR(1, ChronoUnit.YEARS, "1mo");

	private final long range;
	private final TemporalUnit rangeUnit;
	private final String grain;

	TimeOption(final long range, final TemporalUnit rangeUnit, final String grain) {
		this.range = range;
		this.rangeUnit = rangeUnit;
		this.grain = grain;
	}

	public LocalDateTime getFromNow() {
		final LocalDateTime ldt = LocalDateTime.now();
		return getFrom(ldt);
	}

	@SuppressWarnings("unchecked") // Temporal method return same type
	public <T extends Temporal> T getFrom(final T toDate) {
		return (T) toDate.minus(range, rangeUnit);
	}

	/**
	 * @return the grain
	 */
	public String getGrain() {
		return grain;
	}
}
