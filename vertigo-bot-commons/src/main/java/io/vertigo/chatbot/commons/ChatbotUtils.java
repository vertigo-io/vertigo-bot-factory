package io.vertigo.chatbot.commons;

import java.time.Duration;
import java.time.Instant;

public class ChatbotUtils {
	/**
	 * Seconds per minute.
	 */
	static final int SECONDS_PER_MINUTE = 60;

	/**
	 * Minutes per hour.
	 */
	static final int MINUTES_PER_HOUR = 60;
	/**
	 * Seconds per hour.
	 */
	static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;

	/**
	 * Hours per day.
	 */
	static final int HOURS_PER_DAY = 24;
	/**
	 * Seconds per day.
	 */
	static final int SECONDS_PER_DAY = SECONDS_PER_HOUR * HOURS_PER_DAY;

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
}
