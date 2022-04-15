package io.vertigo.chatbot.designer.analytics.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import io.vertigo.chatbot.designer.analytics.services.TimeOption;
import io.vertigo.chatbot.designer.domain.analytics.StatCriteria;
import io.vertigo.database.timeseries.DataFilter;
import io.vertigo.database.timeseries.DataFilterBuilder;
import io.vertigo.database.timeseries.TimeFilter;

public final class AnalyticsServicesUtils {

	public static final String MESSAGES_MSRMT = "chatbotmessages";
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
		final TimeOption timeOption = TimeOption.valueOf(criteria.getTimeOption());
		final LocalDateTime toDate;
		if (criteria.getToDate() == null) {
			toDate = atEndOfDay(LocalDate.now());
		} else {
			toDate = atEndOfDay(criteria.getToDate());
		}

		final LocalDateTime fromDate;
		if (criteria.getFromDate() == null) {
			fromDate = timeOption.getFrom(toDate);
		} else {
			fromDate = criteria.getFromDate().atStartOfDay();
		}

		return TimeFilter.builder(fromDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + 'Z', toDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + 'Z').withTimeDim(timeOption.getGrain()).build();
	}

	private static LocalDateTime atEndOfDay(final LocalDate date) {
		return date.plus(1, ChronoUnit.DAYS).atStartOfDay();
	}

}
