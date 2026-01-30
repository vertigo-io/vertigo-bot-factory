package io.vertigo.chatbot.designer.analytics.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import io.vertigo.chatbot.designer.analytics.services.TimeOption;
import io.vertigo.chatbot.designer.domain.analytics.StatCriteria;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.util.StringUtil;
import io.vertigo.database.timeseries.DataFilter;
import io.vertigo.database.timeseries.DataFilterBuilder;
import io.vertigo.database.timeseries.TimeFilter;
import io.vertigo.database.timeseries.TimeFilterBuilder;
import io.vertigo.database.timeseries.TimedDataSerie;

/**
 * Utility class for analytics services.
 * Provides helper methods for building filters and extracting data from time series.
 *
 * @author Chatbot Team
 */
public final class AnalyticsServicesUtils {

	private static final Pattern FLUX_REGEX_SPECIAL_CHARS =
			Pattern.compile("([.\\\\*+?^${}\\[\\]()|/])");

	public static final String MESSAGES_MSRMT = "chatbotmessages";
	public static final String MESSAGES_STAT_MSRMT = "chatbotmessages_stat";
	public static final String CONVERSATION_MSRMT = "conversation";
	public static final String CONVERSATION_STAT_MSRMT = "conversation_stat";
	public static final String RATING_MSRMT = "rating";
	public static final String DOCUMENTARY_RESOURCE_MSRMT = "documentaryresource";
	public static final String DOCUMENTARY_RESOURCE_STAT_MSRMT = "documentaryresource_stat";
	public static final String QUESTION_ANSWER_MSRMT = "questionanswer";
	public static final String QUESTION_ANSWER_STAT_MSRMT = "questionanswer_stat";

	private AnalyticsServicesUtils() {
		//utils class
	}

	/**
	 * Build a data filter from criteria and measurement name
	 *
	 * @param criteria stat criteria containing bot and node filters
	 * @param measurement measurement name
	 * @return data filter builder
	 */
	public static DataFilterBuilder getDataFilter(final StatCriteria criteria, final String measurement) {
		final DataFilterBuilder dataFilterBuilder = DataFilter.builder(measurement);
		if (criteria.getBotId() != null) {
			dataFilterBuilder.addFilter("botId", criteria.getBotId().toString());
			if (criteria.getNodId() != null) {
				dataFilterBuilder.addFilter("nodId", criteria.getNodId().toString());
			}
		}
		return dataFilterBuilder;
	}

	/**
	 * Build a bot and node filter map from criteria
	 *
	 * @param criteria stat criteria containing bot and node IDs
	 * @return map of column filters
	 */
	public static Map<String, String> getBotNodFilter(final StatCriteria criteria) {
		final Map<String, String> ret = new HashMap<>();
		if (criteria.getBotId() != null) {
			ret.put("botId", '"' + criteria.getBotId().toString() + '"');
			if (criteria.getNodId() != null) {
				ret.put("nodId", '"' + criteria.getNodId().toString() + '"');
			}
		}

		return ret;
	}

	/**
	 * Build a time filter from criteria
	 *
	 * @param criteria stat criteria containing time range and options
	 * @return time filter for InfluxDB queries
	 */
	public static TimeFilter getTimeFilter(final StatCriteria criteria) {
		Assertion.check()
				.isFalse(criteria.getFromDate() != null && criteria.getFromInstant() != null, "Time criteria must not be from date AND instant")
				.isFalse(criteria.getToDate() != null && criteria.getToInstant() != null, "Time criteria must not be to date AND instant");

		final TimeOption timeOption = criteria.getTimeOption() == null ? null : TimeOption.valueOf(criteria.getTimeOption());
		final LocalDateTime toDate;
		if (criteria.getToInstant() != null) {
			toDate = LocalDateTime.ofInstant(criteria.getToInstant(), ZoneOffset.UTC);
		} else if (criteria.getToDate() != null) {
			toDate = atEndOfDay(criteria.getToDate());
		} else {
			toDate = atEndOfDay(LocalDate.now());
		}

		final LocalDateTime fromDate;
		if (criteria.getFromInstant() != null) {
			fromDate = LocalDateTime.ofInstant(criteria.getFromInstant(), ZoneOffset.UTC);
		} else if (criteria.getFromDate() != null) {
			fromDate = criteria.getFromDate().atStartOfDay();
		} else if (timeOption != null) {
			fromDate = timeOption.getFrom(toDate);
		} else {
			fromDate = null;
		}

		final TimeFilterBuilder timeFilterBuilder = TimeFilter.builder(
				fromDate == null ? "0" : (fromDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + 'Z'),
				toDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + 'Z');
		if (timeOption != null) {
			timeFilterBuilder.withTimeDim(timeOption.getGrain());
		}

		return timeFilterBuilder.build();
	}

	private static LocalDateTime atEndOfDay(final LocalDate date) {
		return date.plus(1, ChronoUnit.DAYS).atStartOfDay();
	}

	/**
	 * Extract a long value from a timed data serie
	 *
	 * @param it timed data serie
	 * @param name field name
	 * @param orElse default value if field is null or empty
	 * @return extracted long value or default
	 */
	public static Long getLongValue(final TimedDataSerie it, final String name, final Long orElse) {
		final var val = it.getValues().get(name);
		if (val == null) {
			return orElse;
		}
		if (val instanceof String) {
			if (StringUtil.isBlank((String) val)) {
				return null;
			}
			return Long.parseLong((String) val);
		}
		return (Long) val;
	}

	/**
	 * Escape special regex characters for safe use in Flux regex patterns.
	 * This prevents regex injection attacks when user input is used in InfluxDB queries.
	 * Escaped characters: * + ? ^ $ { } [ ] ( ) | \ / .
	 *
	 * @param input the string to escape
	 * @return the escaped string safe for use in Flux regex patterns, or empty string if input is null
	 */
	public static String escapeFluxRegex(final String input) {
		if (input == null) {
			return "";
		}
		return FLUX_REGEX_SPECIAL_CHARS.matcher(input).replaceAll("\\\\$1");
	}

}
