package io.vertigo.chatbot.designer.analytics.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.component.Component;
import io.vertigo.database.timeseries.DataFilter;
import io.vertigo.database.timeseries.TimeFilter;
import io.vertigo.database.timeseries.TimeSeriesDataBaseManager;
import io.vertigo.database.timeseries.TimedDataSerie;
import io.vertigo.database.timeseries.TimedDatas;
import io.vertigo.lang.Assertion;

@Transactional
public class AnalyticsServices implements Component {

	@Inject
	private TimeSeriesDataBaseManager timeSeriesDataBaseManager;

	public TimedDatas getSessionsStats(final TimeOption timeOption) {
		final String now = '\'' + Instant.now().toString() + '\'';

		return timeSeriesDataBaseManager.getTimeSeries("chatbot-test", Arrays.asList("isTypeOpen:sum"),
				DataFilter.builder("chatbot").build(),
				TimeFilter.builder(now + " - " + timeOption.getRange(), now).withTimeDim(timeOption.getGrain()).build());

		// select count(distinct("sessionId")) from (select "name", "sessionId" from "chatbot-test"."autogen"."chatbot" where time > '2019-12-09T17:08:56.130Z' - 30m and time <'2019-12-09T17:08:56.130Z' and "isTypeOpen" = 0) group by time(1m)

	}

	public TimedDatas getRequestStats(final TimeOption timeOption) {
		final String now = '\'' + Instant.now().toString() + '\'';

		return timeSeriesDataBaseManager.getTimeSeries("chatbot-test", Arrays.asList("name:count", "isFallback:sum"),
				DataFilter.builder("chatbot").withAdditionalWhereClause("\"isTypeOpen\" = 0").build(),
				TimeFilter.builder(now + " - " + timeOption.getRange(), now).withTimeDim(timeOption.getGrain()).build());

	}

	private TimedDatas mergeTimedDatas(final TimedDatas data, final TimedDatas data2, final TimedDatas... otherDatas) {
		Assertion.checkNotNull(data);
		Assertion.checkNotNull(data2);

		final TimedDatas newTimedDatas = new TimedDatas(new ArrayList<>(), new ArrayList<>());

		// Juste recopy first TimedDatas
		newTimedDatas.getSeriesNames().addAll(data.getSeriesNames());
		for (final TimedDataSerie timedDataSerie : data.getTimedDataSeries()) {
			final Map<String, Object> newMapValues = new HashMap<>();
			newMapValues.putAll(timedDataSerie.getValues());

			final TimedDataSerie newTimedDataSerie = new TimedDataSerie(timedDataSerie.getTime(), newMapValues);

			newTimedDatas.getTimedDataSeries().add(newTimedDataSerie);
		}

		// add every other timedDatas with consistency check
		addToTimedDatas(newTimedDatas, data2);
		for (final TimedDatas otherData : otherDatas) {
			addToTimedDatas(newTimedDatas, otherData);
		}

		return newTimedDatas;
	}

	private void addToTimedDatas(final TimedDatas data, final TimedDatas otherData) {
		if (otherData.getSeriesNames().isEmpty()) {
			return; // no data, no merge
		}

		Assertion.checkArgument(data.getTimedDataSeries().size() == otherData.getTimedDataSeries().size(), "Series haven't the same size");
		Assertion.checkArgument(otherData.getSeriesNames().stream().noneMatch(name -> data.getSeriesNames().contains(name)), "Duplicated series");

		data.getSeriesNames().addAll(otherData.getSeriesNames());

		int i = 0;
		for (final TimedDataSerie timedDataSerie : otherData.getTimedDataSeries()) {
			final TimedDataSerie curTimedDataSerie = data.getTimedDataSeries().get(i);

			Assertion.checkState(timedDataSerie.getTime().equals(curTimedDataSerie.getTime()), "Series are not time synchronous");

			curTimedDataSerie.getValues().putAll(timedDataSerie.getValues());

			i++;
		}

	}
}