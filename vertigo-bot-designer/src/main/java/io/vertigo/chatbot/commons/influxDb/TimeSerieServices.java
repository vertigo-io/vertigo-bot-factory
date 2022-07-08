package io.vertigo.chatbot.commons.influxDb;

import io.vertigo.chatbot.designer.analytics.utils.AnalyticsServicesUtils;
import io.vertigo.chatbot.designer.analytics.utils.InfluxRequestBuilder;
import io.vertigo.chatbot.designer.analytics.utils.InfluxRequestUtil;
import io.vertigo.chatbot.designer.domain.analytics.ConversationCriteria;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
		return InfluxRequestUtil.getTimeSeries(influxDbConnector.getClient(), influxDbName, Arrays.asList("name:count", "isSessionStart:sum"),
				AnalyticsServicesUtils.getDataFilter(criteria, AnalyticsServicesUtils.MESSAGES_MSRMT).build(),
				Map.of(),
				AnalyticsServicesUtils.getTimeFilter(criteria));
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

	public TimedDatas getAllIntents(final StatCriteria criteria) {
		final String q = new InfluxRequestBuilder(influxDbName)
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.filterFields(AnalyticsServicesUtils.MESSAGES_MSRMT, List.of("isTechnical"))
				.filterByColumn(AnalyticsServicesUtils.getBotNodFilter(criteria))
				.keep(List.of("_time", "name", "_field", "_value", "sessionId", "modelName"))
				.pivot()
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
		final Map<String, String> filterByColumnMap = AnalyticsServicesUtils.getBotNodFilter(criteria);
		filterByColumnMap.put("sessionId", '"' + sessionId + '"');
		final String q = new InfluxRequestBuilder(influxDbName)
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.filterFields(AnalyticsServicesUtils.CONVERSATION_MSRMT, List.of("isUserMessage", "isBotMessage"))
				.filterByColumn(filterByColumnMap)
				.keep(List.of("_time", "text", "_field", "_value", "sessionId"))
				.pivot()
				.keep(List.of("_time", "text", "isUserMessage", "isBotMessage"))
				.build(5_000L, true, "isBotMessage");

		return InfluxRequestUtil.executeTimedQuery(influxDbConnector.getClient(), q);
	}

	public TimedDatas getConversationStats(final StatCriteria criteria, final ConversationCriteria conversationCriteria) {
		final Map<String, String> botNodeFilterMap = AnalyticsServicesUtils.getBotNodFilter(criteria);
		final StringBuilder query = new StringBuilder();
		query.append("import \"strings\"\n\n");
		query.append("conv =  ").append(new InfluxRequestBuilder(influxDbName)
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.append("|> filter(fn: (r) => r._measurement == \"conversation\" and r._field == \"isUserMessage\" and r._value == 1)")
				.append("|> filter(fn: (r) => exists r.sessionId)")
				.filterByColumn(botNodeFilterMap)
				.keep(List.of("_time", "_field", "_value", "isUserMessage", "sessionId", "modelName"))
				.group(List.of("sessionId")).buildRaw());
		query.append("\n\n");

		query.append("rate = ").append(new InfluxRequestBuilder(influxDbName)
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.filterFields(AnalyticsServicesUtils.RATING_MSRMT, List.of("rating"))
				.filterByColumn(botNodeFilterMap)
				.append("|> filter(fn: (r) => exists r.sessionId)")
				.keep(List.of("_field", "_value", "sessionId", "rating"))
				.group(List.of("sessionId")).buildRaw());

		query.append("\n\n");

		final Map<String, String> endMsgBotNodeFilterMap = new HashMap<>(botNodeFilterMap);
		endMsgBotNodeFilterMap.put("name", "\"!END\"");
		query.append("chatbotMessages = ").append(new InfluxRequestBuilder(influxDbName)
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.filterFields(AnalyticsServicesUtils.MESSAGES_MSRMT, List.of("name"))
				.filterByColumn(endMsgBotNodeFilterMap )
				.append("|> filter(fn: (r) => exists r.sessionId)")
				.keep(List.of("sessionId"))
				.append("|> set(key: \"_field\", value: \"isEnded\")")
				.append("|> set(key: \"_value\", value: \"1\")")
				.append("|> toFloat()")
				.group(List.of("sessionId")).buildRaw());
		query.append("\n\n");

		query.append("msgCount = conv \n")
				.append("|> count() \n")
				.append("|> set(key: \"_field\", value: \"interactions\") \n")
				.append("|> toFloat() \n\n");

		query.append("modelName = conv \n")
				.append("|> keep(columns: [\"_value\", \"modelName\", \"sessionId\"]) \n")
				.append("|> first()");

		query.append("\n\n");

		query.append("sessionTime = conv").append("\n")
				.append("|> keep(columns: [\"_time\", \"sessionId\"]) \n")
				.append("|> rename(columns: {_time: \"_value\"}) \n")
				.append("|> min()").append("\n")
				.append("|> rename(columns: {_value: \"_time\"})");

		query.append("\n\n");

		query.append("unionCountRate = union(tables: [msgCount, rate, chatbotMessages]) \n")
				.append("|> pivot(rowKey:[\"sessionId\"], columnKey: [\"_field\"], valueColumn: \"_value\") \n")
				.append("|> group()");

		query.append("\n\n");

		query.append("joinTable = join(tables: {key1: unionCountRate, key2: sessionTime}, on: [\"sessionId\"], method: \"inner\") \n")
				.append("|> group()");

		query.append("\n\n");

		query.append("join(tables: {key1: joinTable, key2: modelName}, on: [\"sessionId\"], method: \"inner\") \n");
		if (!conversationCriteria.getRatings().isEmpty()) {
			query.append("|> filter(fn: (r) => ");
			query.append(conversationCriteria.getRatings().stream().map(r -> "r.rating ==  " + r).collect(Collectors.joining(" or ")));
			query.append(")");
		}
		if (conversationCriteria.getModelName() != null) {
			query.append("|> filter(fn: (r) => strings.containsStr(v: r.modelName, substr: \"").append(conversationCriteria.getModelName()).append("\")) \n");
		}
		query.append("|> group()");

		return InfluxRequestUtil.executeTimedQuery(influxDbConnector.getClient(), query.toString());
	}

	public TimedDatas getRatingStats(final StatCriteria criteria) {
		return InfluxRequestUtil.getTimeSeries(influxDbConnector.getClient(), influxDbName,
				Arrays.asList("rating1:count", "rating2:count", "rating3:count", "rating4:count", "rating5:count"),
				AnalyticsServicesUtils.getDataFilter(criteria, AnalyticsServicesUtils.RATING_MSRMT).build(),
				Map.of(),
				AnalyticsServicesUtils.getTimeFilter(criteria));
	}

	public TabularDatas getRatingDetailsStats(final StatCriteria criteria) {
		final StringBuilder query = new StringBuilder();
		query.append("lastTopic = ").append(new InfluxRequestBuilder(influxDbName)
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.append("|> filter(fn: (r) => r._measurement == \"chatbotmessages\" and r._field == \"isTechnical\" and r._value == 0)")
				.filterByColumn(AnalyticsServicesUtils.getBotNodFilter(criteria))
				.keep(List.of("_time", "_field", "name", "sessionId"))
				.append("|> rename(columns: {name: \"_value\"})")
				.append("|> set(key: \"_field\", value: \"lastTopic\")")
				.group(List.of("sessionId"))
				.append("|> sort(columns: [\"_time\"], desc:false)")
				.append("|> last()").buildRaw());

		query.append("\n\n");


		query.append("time = ").append(new InfluxRequestBuilder(influxDbName)
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.append("|> filter(fn: (r) => r._measurement == \"rating\" and r._field==\"rating\" and not exists r.ratingComment)")
				.filterByColumn(AnalyticsServicesUtils.getBotNodFilter(criteria))
				.keep(List.of("_time", "_field", "sessionId"))
				.append("|> map(fn: (r) => ({r with time: string(v: r._time)}))")
				.append("|> rename(columns: {time: \"_value\"})")
				.append("|> set(key: \"_field\", value: \"time\")")
				.group(List.of("sessionId"))
				.append("|> sort(columns: [\"_time\"], desc:false)")
				.append("|> last()").buildRaw());

		query.append("\n\n");

		query.append("rating = ").append(new InfluxRequestBuilder(influxDbName)
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.append("|> filter(fn: (r) => r._measurement == \"rating\" and r._field==\"rating\" and not exists r.ratingComment)")
				.filterByColumn(AnalyticsServicesUtils.getBotNodFilter(criteria))
				.keep(List.of("_time", "_field", "_value", "sessionId"))
				.buildRaw());

		query.append("\n\n");

		query.append("ratingComment = ").append(new InfluxRequestBuilder(influxDbName)
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.append("|> filter(fn: (r) => r._measurement == \"rating\" and r._field==\"rating\" and exists r.ratingComment)")
				.filterByColumn(AnalyticsServicesUtils.getBotNodFilter(criteria))
				.keep(List.of("_time", "_field", "sessionId", "ratingComment"))
				.append("|> set(key: \"_field\", value: \"ratingComment\")")
				.append("|> rename(columns: {ratingComment: \"_value\"})")
				.buildRaw());

		query.append("union(tables: [rating, time, ratingComment, lastTopic]) \n");
		query.append("|> pivot(rowKey:[\"sessionId\"], columnKey: [\"_field\"], valueColumn: \"_value\") \n");
		query.append("|> group() \n");
		query.append("|> limit(n:5000)");

		return InfluxRequestUtil.executeTabularQuery(influxDbConnector.getClient(), query.toString());
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
