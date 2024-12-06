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

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;

import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DataObject;
import io.vertigo.datamodel.data.util.VCollectors;
import io.vertigo.vega.engines.webservice.json.AbstractUiListModifiable;
import io.vertigo.vega.webservice.model.UiObject;
import io.vertigo.vega.webservice.validation.UiMessageStack;

public class ChatbotUtils {
	/**
	 * Seconds per minute.
	 */
	private static final int SECONDS_PER_MINUTE = 60;

	/**
	 * Minutes per hour.
	 */
	private static final int MINUTES_PER_HOUR = 60;
	/**
	 * Seconds per hour.
	 */
	private static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;

	/**
	 * Hours per day.
	 */
	private static final int HOURS_PER_DAY = 24;
	/**
	 * Seconds per day.
	 */
	private static final int SECONDS_PER_DAY = SECONDS_PER_HOUR * HOURS_PER_DAY;

	/**
	 * Encryption algo
	 */
	public static final String ENCRYPTION_ALGO = "PBKDF2WithHmacSHA256";

	public static final int MAX_UPLOAD_SIZE = 5 * 1024 * 1024;

	private ChatbotUtils() {
		// classe utilitaire
	}

	/**
	 * Compute human readable duration between two instants. If end is null, Instant.now() is used.
	 * If start is null, return "-".
	 *
	 * @param start Start of interval
	 * @param end End of interval
	 * @return Human readable string
	 */
	public static String durationBetween(final Instant start, final Instant end) {
		if (start == null) {
			return "-";
		}

		final Instant resolvedEnd = end == null ? Instant.now() : end;

		final long secNumber = Duration.between(start, resolvedEnd).getSeconds();

		final long days = secNumber / SECONDS_PER_DAY;
		final long hours = (secNumber % SECONDS_PER_DAY) / SECONDS_PER_HOUR;
		final long minutes = (secNumber % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE;
		final long seconds = (secNumber % SECONDS_PER_MINUTE);

		final StringBuilder durationString = new StringBuilder();

		addDuration(durationString, days, 'd');
		addDuration(durationString, hours, 'h');
		addDuration(durationString, minutes, 'm');
		addDuration(durationString, seconds, 's');

		return durationString.toString();
	}

	private static void addDuration(final StringBuilder builder, final long count, final char unit) {
		if (count > 0 || builder.length() > 0) {
			if (builder.length() > 0) {
				builder.append(' ');
			}
			builder.append(count);
			builder.append(unit);
		}
	}

	public static <D extends DataObject> DtList<D> getRawDtList(final AbstractUiListModifiable<D> uiList, final UiMessageStack uiMessageStack) {
		return uiList.stream()
				.filter(UiObject::isModified)
				.map(uiObject -> uiObject.mergeAndCheckInput(Collections.singletonList((a, b, c) -> {
					// rien
				}), uiMessageStack))
				.collect(VCollectors.toDtList(uiList.getDtDefinition()));
	}

}
