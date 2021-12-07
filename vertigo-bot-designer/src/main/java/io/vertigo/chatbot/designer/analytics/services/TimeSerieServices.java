package io.vertigo.chatbot.designer.analytics.services;

import javax.inject.Inject;

import io.vertigo.chatbot.designer.domain.analytics.StatCriteria;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.param.ParamManager;
import io.vertigo.database.timeseries.TabularDatas;
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

	//	@Inject
	//	private TimeSeriesManager timeSeriesManager;

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
		return null;
		//return timeSeriesManager.getTimeSeries(influxDbName, Arrays.asList("isSessionStart:sum"),
		//		AnalyticsServicesUtils.getDataFilter(criteria, AnalyticsServicesUtils.MESSAGES_MSRMT).build(),
		//		AnalyticsServicesUtils.getTimeFilter(criteria));
	}

	/**
	 * get all nlu messages and fallback messages
	 *
	 * @param criteria statcriteria
	 * @return timeDatas with messages sum and fallbacksum
	 */
	public TimedDatas getRequestStats(final StatCriteria criteria) {
		return null;
		//return timeSeriesManager.getTimeSeries(influxDbName, Arrays.asList("name:count", "isFallback:sum", "isNlu:sum"),
		//				AnalyticsServicesUtils.getDataFilter(criteria, AnalyticsServicesUtils.MESSAGES_MSRMT).withAdditionalWhereClause("isUserMessage = 1").build(),
		//	AnalyticsServicesUtils.getTimeFilter(criteria));

	}

	public TimedDatas getUserInteractions(final StatCriteria criteria) {
		return null;
		//return timeSeriesManager.getTimeSeries(influxDbName, List.of("name:count"),
		//		AnalyticsServicesUtils.getDataFilter(criteria, AnalyticsServicesUtils.MESSAGES_MSRMT).build(),
		//		AnalyticsServicesUtils.getTimeFilter(criteria));
	}

	/**
	 * Get all messages unrecognized
	 *
	 * @param criteria statscriteria
	 * @return all the messages unrecognized
	 */
	public TimedDatas getSentenceDetails(final StatCriteria criteria) {
		return null;
		//		return timeSeriesManager.getFlatTabularTimedData(influxDbName, Arrays.asList("text", "name", "confidence", "modelName"),
		//				AnalyticsServicesUtils.getDataFilter(criteria, AnalyticsServicesUtils.MESSAGES_MSRMT).withAdditionalWhereClause("isFallback = 1").build(),
		//				AnalyticsServicesUtils.getTimeFilter(criteria),
		//				Optional.empty());
	}

	/**
	 * Get all sessions for export
	 *
	 * @param criteria statscriteria
	 * @return all the messages unrecognized
	 */
	public TimedDatas getSessionsExport(final StatCriteria criteria) {
		return null;
		//return timeSeriesManager.getFlatTabularTimedData(influxDbName, Arrays.asList("name", "modelName", "botId", "traId"),
		//		AnalyticsServicesUtils.getDataFilter(criteria, AnalyticsServicesUtils.MESSAGES_MSRMT).withAdditionalWhereClause("isSessionStart = 1").build(),
		//		AnalyticsServicesUtils.getTimeFilter(criteria),
		//		Optional.empty());
	}

	/**
	 * Get all messages unrecognized for export
	 *
	 * @param criteria statscriteria
	 * @return all the messages unrecognized
	 */
	public TimedDatas getUnknowSentenceExport(final StatCriteria criteria) {
		return null;
		//		return timeSeriesManager.getFlatTabularTimedData(influxDbName, Arrays.asList("text", "name", "confidence", "modelName", "botId", "traId"),
		//		AnalyticsServicesUtils.getDataFilter(criteria, AnalyticsServicesUtils.MESSAGES_MSRMT).withAdditionalWhereClause("isFallback = 1").build(),
		//		AnalyticsServicesUtils.getTimeFilter(criteria),
		//		Optional.empty());
	}

	/**
	 * Get all top intent with no restrictions
	 * Add additional where clause if we want to get a specific top number
	 *
	 * @param criteria
	 * @return all topIntents
	 */
	public TabularDatas getAllTopIntents(final StatCriteria criteria) {
		return null;
		//return timeSeriesManager.getTabularData(influxDbName, Arrays.asList("name:count"),
		//		AnalyticsServicesUtils.getDataFilter(criteria, AnalyticsServicesUtils.MESSAGES_MSRMT)
		//				.withAdditionalWhereClause("isNlu = 1")
		//No technical issues must be get
		//				.withAdditionalWhereClause("isTechnical = 0")
		//				.build(),
		//		AnalyticsServicesUtils.getTimeFilter(criteria),
		//		"name");

	}

	/**
	 * Get the sentences recognized for a specific intentRasa
	 *
	 * @param criteria
	 * @param intentRasa
	 * @return all the sentences recognized
	 */
	public TimedDatas getKnowSentence(final StatCriteria criteria, final String intentRasa) {
		return null;
		//return timeSeriesManager.getFlatTabularTimedData(influxDbName, Arrays.asList("text", "name", "confidence"),
		//		AnalyticsServicesUtils.getDataFilter(criteria, AnalyticsServicesUtils.MESSAGES_MSRMT)
		//				.addFilter("name", intentRasa)
		//				.withAdditionalWhereClause("isNlu = 1")
		//				.build(),
		//		AnalyticsServicesUtils.getTimeFilter(criteria),
		//		Optional.of(5000L));
	}

	/**
	 * Get all topics already used in test bot
	 *
	 * @param criteria
	 * @return all topics used at least one time
	 */
	public TimedDatas getTopicsStats(final StatCriteria criteria) {
		return null;
		//return timeSeriesManager.getFlatTabularTimedData(influxDbName, Arrays.asList("name"),
		//		AnalyticsServicesUtils.getDataFilter(criteria, AnalyticsServicesUtils.MESSAGES_MSRMT).build(),
		//		AnalyticsServicesUtils.getTimeFilter(criteria),
		//		Optional.empty());
	}

	public TimedDatas getRatingStats(final StatCriteria criteria) {
		return null;
		//return timeSeriesManager.getTimeSeries(influxDbName,
		//		Arrays.asList("rating1:count", "rating2:count", "rating3:count", "rating4:count", "rating5:count"),
		//		AnalyticsServicesUtils.getDataFilter(criteria, AnalyticsServicesUtils.RATING_MSRMT).build(),
		//		AnalyticsServicesUtils.getTimeFilter(criteria));
	}

}
