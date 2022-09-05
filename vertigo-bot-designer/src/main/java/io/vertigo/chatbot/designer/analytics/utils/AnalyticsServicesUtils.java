package io.vertigo.chatbot.designer.analytics.utils;

import io.vertigo.chatbot.designer.analytics.services.TimeOption;
import io.vertigo.chatbot.designer.domain.analytics.StatCriteria;
import io.vertigo.core.lang.Assertion;
import io.vertigo.database.timeseries.DataFilter;
import io.vertigo.database.timeseries.DataFilterBuilder;
import io.vertigo.database.timeseries.TimeFilter;
import io.vertigo.database.timeseries.TimeFilterBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public final class AnalyticsServicesUtils {

	public static final String MESSAGES_MSRMT = "chatbotmessages";
	public static final String CONVERSATION_MSRMT = "conversation";
	public static final String RATING_MSRMT = "rating";

	private AnalyticsServicesUtils() {
		//utils class
	}

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

}
