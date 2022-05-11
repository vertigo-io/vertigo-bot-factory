package io.vertigo.chatbot.commons.influxDb;

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

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Use for search in influxdb
 * All the request need at least a statCriteria
 *
 * @author vbaillet, skerdudou
 */
@Transactional
public class TimeSerieServices implements Component, Activeable {

	private String influxDbName;

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
		return InfluxRequestUtil.getTimeSeries(influxDbConnector.getClient(), influxDbName, Arrays.asList("name:count"),
				AnalyticsServicesUtils.getDataFilter(criteria, AnalyticsServicesUtils.MESSAGES_MSRMT).build(),
				Map.of(),
				AnalyticsServicesUtils.getTimeFilter(criteria));
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
				.filterByColumn(AnalyticsServicesUtils.getBotNodFilter(criteria))
				.keep(List.of("_time", "text", "name", "modelName", "_field", "_value"))
				.pivot()
				.filterByColumn(Map.of("isFallback", "1"))
				.build(true);

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
				.filterByColumn(AnalyticsServicesUtils.getBotNodFilter(criteria))
				.keep(List.of("_time", "name", "modelName", "botId", "nodId", "traId", "_field", "_value"))
				.pivot()
				.filterByColumn(Map.of("isSessionStart", "1"))
				.build(true);

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
				.filterByColumn(AnalyticsServicesUtils.getBotNodFilter(criteria))
				.keep(List.of("_time", "text", "name", "confidence", "modelName", "botId", "nodId", "traId", "_field", "_value"))
				.pivot()
				.filterByColumn(Map.of("isFallback", "1"))
				.build(true);

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
				.filterFields(AnalyticsServicesUtils.MESSAGES_MSRMT, List.of("isTechnical"))
				.filterByColumn(AnalyticsServicesUtils.getBotNodFilter(criteria))
				.keep(List.of("_time", "name", "_field", "_value"))
				.pivot()
				.filterByColumn(Map.of( "isTechnical", "0"))
				.keep(List.of("name"))
				.append("|> duplicate(column: \"name\", as: \"name:count\")")
				.append("|> count(column: \"name:count\")")
				.build(true);

		return InfluxRequestUtil.executeTabularQuery(influxDbConnector.getClient(), q);
	}

	public TimedDatas getSessionTechnicalIntents(final StatCriteria criteria, final String sessionId) {
		final String q = new InfluxRequestBuilder(influxDbName)
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.filterFields(AnalyticsServicesUtils.MESSAGES_MSRMT, List.of("isTechnical"))
				.filterByColumn(AnalyticsServicesUtils.getBotNodFilter(criteria))
				.keep(List.of("_time", "name", "_field", "_value", "sessionId", "modelName"))
				.pivot()
				.filterByColumn(Map.of("sessionId", '"' + sessionId + '"', "isTechnical", "1"))
				.build(true);

		return InfluxRequestUtil.executeTimedQuery(influxDbConnector.getClient(), q);
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
				.filterByColumn(AnalyticsServicesUtils.getBotNodFilter(criteria))
				.filterByColumn(Map.of("name", '"' + intentRasa + '"'))
				.keep(List.of("_time", "text", "_field", "_value"))
				.pivot()
				.filterByColumn(Map.of("isNlu", "1"))
				.keep(List.of("_time", "text", "confidence"))
				.build(5_000L, true);

		return InfluxRequestUtil.executeTimedQuery(influxDbConnector.getClient(), q);
	}

	public TimedDatas getConversationDetails(final StatCriteria criteria, final String sessionId) {
		final String q = new InfluxRequestBuilder(influxDbName)
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.filterFields(AnalyticsServicesUtils.CONVERSATION_MSRMT, List.of("isUserMessage", "isBotMessage"))
				.filterByColumn(AnalyticsServicesUtils.getBotNodFilter(criteria))
				.keep(List.of("_time", "text", "_field", "_value", "sessionId"))
				.pivot()
				.filterByColumn(Map.of("sessionId", '"' + sessionId + '"'))
				.keep(List.of("_time", "text", "isUserMessage", "isBotMessage"))
				.build(5_000L, true);

		return InfluxRequestUtil.executeTimedQuery(influxDbConnector.getClient(), q);
	}

	public TabularDatas getConversationStats(final StatCriteria criteria) {
		final String q = new InfluxRequestBuilder(influxDbName)
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.filterFields(AnalyticsServicesUtils.CONVERSATION_MSRMT, List.of("isBotMessage"))
				.filterByColumn(AnalyticsServicesUtils.getBotNodFilter(criteria))
				.keep(List.of("_time", "_field", "_value", "isBotMessage", "sessionId"))
				.pivot()
				.group(List.of("sessionId")).count("isBotMessage").build(5_000L, true);
		return InfluxRequestUtil.executeTabularQuery(influxDbConnector.getClient(), q);
	}

	public TimedDatas getRatingStats(final StatCriteria criteria) {
		return InfluxRequestUtil.getTimeSeries(influxDbConnector.getClient(), influxDbName,
				Arrays.asList("rating1:count", "rating2:count", "rating3:count", "rating4:count", "rating5:count"),
				AnalyticsServicesUtils.getDataFilter(criteria, AnalyticsServicesUtils.RATING_MSRMT).build(),
				Map.of(),
				AnalyticsServicesUtils.getTimeFilter(criteria));
	}

	public TimedDatas getRatingForSession(final StatCriteria criteria, final String sessionId) {

		final String q = new InfluxRequestBuilder(influxDbName)
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.filterFields(AnalyticsServicesUtils.RATING_MSRMT, List.of("rating1", "rating2", "rating3", "rating4", "rating5", "sessionId"))
				.filterByColumn(AnalyticsServicesUtils.getBotNodFilter(criteria))
				.keep(List.of("_time", "_field", "_value", "sessionId"))
				.pivot()
				.filterByColumn(Map.of("sessionId", '"' + sessionId + '"'))
				.build(5_000L, true);
		return InfluxRequestUtil.executeTimedQuery(influxDbConnector.getClient(), q);
	}

	/**
	 * Get all messages unrecognized
	 *
	 * @param criteria StatCriteria
	 * @return all the messages unrecognized
	 */
	public TimedDatas getUnrecognizedSentences(final StatCriteria criteria) {
		final String q = new InfluxRequestBuilder(influxDbName)
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.filterFields(AnalyticsServicesUtils.MESSAGES_MSRMT, List.of("isFallback", "confidence"))
				.filterByColumn(AnalyticsServicesUtils.getBotNodFilter(criteria))
				.keep(List.of("_time", "text", "name", "modelName", "_field", "_value"))
				.pivot()
				.filterByColumn(Map.of("isFallback", "1"))
				.keep(List.of("_time", "text", "name", "confidence", "modelName"))
				.build(true);

		return InfluxRequestUtil.executeTimedQuery(influxDbConnector.getClient(), q);
	}

}
