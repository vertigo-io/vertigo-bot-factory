package io.vertigo.chatbot.designer.analytics.utils;

import java.time.Instant;

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

	public static TimeFilter getTimeFilter(final StatCriteria criteria) {
		final TimeOption timeOption = TimeOption.valueOf(criteria.getTimeOption());
		final String toDate;
		if (criteria.getToDate() == null) {
			toDate = '\'' + Instant.now().toString() + '\'';
		} else {
			toDate = '\'' + criteria.getToDate().toString() + "T23:59:59.999999999Z" + '\'';
		}

		final String fromDate;
		if (criteria.getFromDate() == null) {
			fromDate = toDate + " - " + timeOption.getRange();
		} else {
			fromDate = '\'' + criteria.getFromDate().toString() + "T00:00:00.000000000Z" + '\'';
		}

		return TimeFilter.builder(fromDate, toDate).withTimeDim(timeOption.getGrain()).build();

	}

}
