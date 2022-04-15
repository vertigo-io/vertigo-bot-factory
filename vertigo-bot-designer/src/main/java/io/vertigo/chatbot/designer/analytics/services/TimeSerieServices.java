package io.vertigo.chatbot.designer.analytics.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.vertigo.chatbot.designer.analytics.utils.AnalyticsServicesUtils;
import io.vertigo.chatbot.designer.analytics.utils.InfluxRequestBuilder;
import io.vertigo.chatbot.designer.analytics.utils.InfluxRequestUtil;
import io.vertigo.chatbot.designer.domain.analytics.StatCriteria;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.connectors.influxdb.InfluxDbConnector;
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

	//@Inject
	//private TimeSeriesManager timeSeriesManager;

	@Inject
	private InfluxDbConnector influxDbConnector;

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
		return InfluxRequestUtil.getTimeSeries(influxDbConnector.getClient(), influxDbName, Arrays.asList("isSessionStart:sum"),
				AnalyticsServicesUtils.getDataFilter(criteria, AnalyticsServicesUtils.MESSAGES_MSRMT).build(),
				Map.of(),
				AnalyticsServicesUtils.getTimeFilter(criteria));
	}

	/**
	 * get all nlu messages and fallback messages
	 *
	 * @param criteria statcriteria
	 * @return timeDatas with messages sum and fallbacksum
	 */
	public TimedDatas getRequestStats(final StatCriteria criteria) {
		return InfluxRequestUtil.getTimeSeries(influxDbConnector.getClient(), influxDbName, Arrays.asList("name:count", "isFallback:sum", "isNlu:sum"),
				AnalyticsServicesUtils.getDataFilter(criteria, AnalyticsServicesUtils.MESSAGES_MSRMT)
						.build(),
				Map.of("isUserMessage", "1"),
				AnalyticsServicesUtils.getTimeFilter(criteria));
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
		final String q = new InfluxRequestBuilder(influxDbName)
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.filterFields(AnalyticsServicesUtils.MESSAGES_MSRMT, List.of("confidence", "isFallback"))
				.filterColumn(AnalyticsServicesUtils.getBotNodFilter(criteria))
				.keep(List.of("_time", "text", "name", "modelName", "_field", "_value"))
				.pivot()
				.filterColumn(Map.of("isFallback", "1"))
				.build();

		return InfluxRequestUtil.executeTimedQuery(influxDbConnector.getClient(), q);
	}

	/**
	 * Get all sessions for export
	 *
	 * @param criteria statscriteria
	 * @return all the messages unrecognized
	 */
	public TimedDatas getSessionsExport(final StatCriteria criteria) {
		final String q = new InfluxRequestBuilder(influxDbName)
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.filterFields(AnalyticsServicesUtils.MESSAGES_MSRMT, List.of("isSessionStart"))
				.filterColumn(AnalyticsServicesUtils.getBotNodFilter(criteria))
				.keep(List.of("_time", "name", "modelName", "botId", "traId", "_field", "_value"))
				.pivot()
				.filterColumn(Map.of("isSessionStart", "1"))
				.build();

		return InfluxRequestUtil.executeTimedQuery(influxDbConnector.getClient(), q);
	}

	/**
	 * Get all messages unrecognized for export
	 *
	 * @param criteria statscriteria
	 * @return all the messages unrecognized
	 */
	public TimedDatas getUnknowSentenceExport(final StatCriteria criteria) {
		final String q = new InfluxRequestBuilder(influxDbName)
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.filterFields(AnalyticsServicesUtils.MESSAGES_MSRMT, List.of("isSessionStart"))
				.filterColumn(AnalyticsServicesUtils.getBotNodFilter(criteria))
				.keep(List.of("_time", "text", "name", "confidence", "modelName", "botId", "traId", "_field", "_value"))
				.pivot()
				.filterColumn(Map.of("isFallback", "1"))
				.build();

		return InfluxRequestUtil.executeTimedQuery(influxDbConnector.getClient(), q);
	}

	/**
	 * Get all top intent with no restrictions
	 * Add additional where clause if we want to get a specific top number
	 *
	 * @param criteria
	 * @return all topIntents
	 */
	public TabularDatas getAllTopIntents(final StatCriteria criteria) {
		final String q = new InfluxRequestBuilder(influxDbName)
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.filterFields(AnalyticsServicesUtils.MESSAGES_MSRMT, List.of("isNlu", "isTechnical"))
				.filterColumn(AnalyticsServicesUtils.getBotNodFilter(criteria))
				.keep(List.of("_time", "name", "_field", "_value"))
				.pivot()
				.filterColumn(Map.of("isNlu", "1", "isTechnical", "0"))
				.keep(List.of("name"))
				.append("|> duplicate(column: \"name\", as: \"name:count\")")
				.append("|> count(column: \"name:count\")")
				.build();

		return InfluxRequestUtil.executeTabularQuery(influxDbConnector.getClient(), q);
	}

	/**
	 * Get the sentences recognized for a specific intentRasa
	 *
	 * @param criteria
	 * @param intentRasa
	 * @return all the sentences recognized
	 */
	public TimedDatas getKnowSentence(final StatCriteria criteria, final String intentRasa) {
		final String q = new InfluxRequestBuilder(influxDbName)
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.filterFields(AnalyticsServicesUtils.MESSAGES_MSRMT, List.of("isNlu", "confidence"))
				.filterColumn(AnalyticsServicesUtils.getBotNodFilter(criteria))
				.filterColumn(Map.of("name", '"' + intentRasa + '"'))
				.keep(List.of("_time", "text", "_field", "_value"))
				.pivot()
				.filterColumn(Map.of("isNlu", "1"))
				.keep(List.of("_time", "text", "confidence"))
				.build(5_000L);

		return InfluxRequestUtil.executeTimedQuery(influxDbConnector.getClient(), q);
	}

	/**
	 * Get all topics already used in test bot
	 *
	 * @param criteria
	 * @return all topics used at least one time
	 */
	public TimedDatas getTopicsStats(final StatCriteria criteria) {
		return new TimedDatas(new ArrayList<>(), new ArrayList<>());
		//return timeSeriesManager.getFlatTabularTimedData(influxDbName, Arrays.asList("name"),
		//		AnalyticsServicesUtils.getDataFilter(criteria, AnalyticsServicesUtils.MESSAGES_MSRMT).build(),
		//		AnalyticsServicesUtils.getTimeFilter(criteria),
		//		Optional.empty());
	}

	public TimedDatas getRatingStats(final StatCriteria criteria) {
		return InfluxRequestUtil.getTimeSeries(influxDbConnector.getClient(), influxDbName,
				Arrays.asList("rating1:count", "rating2:count", "rating3:count", "rating4:count", "rating5:count"),
				AnalyticsServicesUtils.getDataFilter(criteria, AnalyticsServicesUtils.RATING_MSRMT).build(),
				Map.of(),
				AnalyticsServicesUtils.getTimeFilter(criteria));
	}

}
