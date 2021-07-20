package io.vertigo.chatbot.designer.analytics.services;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.chatbot.designer.domain.StatCriteria;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.param.ParamManager;
import io.vertigo.database.timeseries.DataFilter;
import io.vertigo.database.timeseries.DataFilterBuilder;
import io.vertigo.database.timeseries.TabularDatas;
import io.vertigo.database.timeseries.TimeFilter;
import io.vertigo.database.timeseries.TimeSeriesManager;
import io.vertigo.database.timeseries.TimedDatas;

/**
 * Use for search in influxdb
 * All the request need at least a statCriteria
 *
 * @author vbaillet
 */
@Transactional
public class TimeSerieServices implements Component, Activeable {

	private String influxDbName;

	@Inject
	private TimeSeriesManager timeSeriesManager;

	@Inject
	private ParamManager paramManager;

	@Override
	public void start() {
		influxDbName = paramManager.getParam("boot.ANALYTICA_DBNAME").getValueAsString();

	}

	@Override
	public void stop() {
		//Nothing

	}

	/**
	 * Get all the sessions of users
	 *
	 * @param criteria statcriteria
	 * @return sum of sessions
	 */
	public TimedDatas getSessionsStats(final StatCriteria criteria) {
		return timeSeriesManager.getTimeSeries(influxDbName, Arrays.asList("isSessionStart:sum"),
				getDataFilter(criteria).build(),
				getTimeFilter(criteria));
	}

	/**
	 * get all nlu messages and fallback messages
	 *
	 * @param criteria statcriteria
	 * @return timeDatas with messages sum and fallbacksum
	 */
	public TimedDatas getRequestStats(final StatCriteria criteria) {
		return timeSeriesManager.getTimeSeries(influxDbName, Arrays.asList("name:count", "isFallback:sum", "isNlu:sum"),
				getDataFilter(criteria).withAdditionalWhereClause("isUserMessage = 1").build(),
				getTimeFilter(criteria));

	}

	/**
	 * Get all messages unrecognized
	 *
	 * @param criteria statscriteria
	 * @return all the messages unreconized
	 */
	public TimedDatas getSentenceDetails(final StatCriteria criteria) {
		return timeSeriesManager.getFlatTabularTimedData(influxDbName, Arrays.asList("text", "name", "confidence"),
				getDataFilter(criteria).withAdditionalWhereClause("isFallback = 1").build(),
				getTimeFilter(criteria),
				Optional.empty());
	}

	/**
	 * Get all top intent with no restrictions
	 * Add additional where clause if we want to get a specific top number
	 *
	 * @param criteria
	 * @return all topIntents
	 */
	public TabularDatas getAllTopIntents(final StatCriteria criteria) {
		return timeSeriesManager.getTabularData(influxDbName, Arrays.asList("name:count"),
				getDataFilter(criteria)
						.withAdditionalWhereClause("isNlu = 1")
						//No technical issues must be get
						.withAdditionalWhereClause("isTechnical = 0")
						.build(),
				getTimeFilter(criteria),
				"name");

	}

	/**
	 * Get the sentences recognized for a specific intentRasa
	 *
	 * @param criteria
	 * @param intentRasa
	 * @return all the sentences recognized
	 */
	public TimedDatas getKnowSentence(final StatCriteria criteria, final String intentRasa) {
		return timeSeriesManager.getFlatTabularTimedData(influxDbName, Arrays.asList("text", "name", "confidence"),
				getDataFilter(criteria)
						.addFilter("name", intentRasa)
						.withAdditionalWhereClause("isNlu = 1")
						.build(),
				getTimeFilter(criteria),
				Optional.of(5000L));
	}

	/**
	 * Get all topics already used in test bot
	 *
	 * @param criteria
	 * @return all topics used at least one time
	 */
	public TimedDatas getTopicsStats(final StatCriteria criteria) {
		return timeSeriesManager.getFlatTabularTimedData(influxDbName, Arrays.asList("name"),
				getDataFilter(criteria).build(),
				getTimeFilter(criteria),
				Optional.empty());
	}

	private static DataFilterBuilder getDataFilter(final StatCriteria criteria) {
		final DataFilterBuilder dataFilterBuilder = DataFilter.builder("chatbotmessages");
		if (criteria.getBotId() != null) {
			dataFilterBuilder.addFilter("botId", criteria.getBotId().toString());
			if (criteria.getNodId() != null) {
				dataFilterBuilder.addFilter("nodId", criteria.getNodId().toString());
			}
		}
		return dataFilterBuilder;
	}

	private static TimeFilter getTimeFilter(final StatCriteria criteria) {
		final TimeOption timeOption = TimeOption.valueOf(criteria.getTimeOption());
		final String now = '\'' + Instant.now().toString() + '\'';

		return TimeFilter.builder(now + " - " + timeOption.getRange(), now).withTimeDim(timeOption.getGrain()).build();
	}

}
