package io.vertigo.chatbot.commons.influxDb;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;

import io.vertigo.chatbot.designer.analytics.utils.AnalyticsServicesUtils;
import io.vertigo.chatbot.designer.analytics.utils.DataRequestBuilder;
import io.vertigo.chatbot.designer.analytics.utils.InfluxRequestBuilder;
import io.vertigo.chatbot.designer.analytics.utils.InfluxRequestUtil;
import io.vertigo.chatbot.designer.domain.analytics.ConversationCriteria;
import io.vertigo.chatbot.designer.domain.analytics.StatCriteria;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.param.Param;
import io.vertigo.core.param.ParamManager;
import io.vertigo.database.timeseries.TabularDatas;
import io.vertigo.database.timeseries.TimedDatas;
import okhttp3.OkHttpClient;

/**
 * Use for search in influxdb
 * All the request need at least a statCriteria
 *
 * @author vbaillet, skerdudou
 */
@Transactional
public class TimeSerieServices implements Component, Activeable {

	private String influxDbName;

	private InfluxDBClient influxDBClient;

	@Inject
	private ParamManager paramManager;

	@Override
	public void start() {
		final String influxDbUrl = paramManager.getParam("INFLUXDB_URL").getValueAsString();
		final String influxDbToken = paramManager.getParam("INFLUXDB_TOKEN").getValueAsString();
		final String influxDbOrg = paramManager.getOptionalParam("INFLUXDB_ORG")
				.map(Param::getValueAsString).orElse("chatbot");
		final int influxDbReadTimeout = paramManager.getOptionalParam("INFLUXDB_READ_TIMEOUT")
				.map(Param::getValueAsInt).orElse(30);
		influxDbName = paramManager.getParam("boot.ANALYTICA_DBNAME").getValueAsString();
		final OkHttpClient.Builder builder = new OkHttpClient.Builder().readTimeout(influxDbReadTimeout, TimeUnit.SECONDS);

		final InfluxDBClientOptions options = InfluxDBClientOptions.builder()
				.url(influxDbUrl)
				.authenticateToken(influxDbToken.toCharArray())
				.org(influxDbOrg)
				.okHttpClient(builder)
				.build();

		influxDBClient = InfluxDBClientFactory.create(options);
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
		return InfluxRequestUtil.getTimeSeries(influxDBClient, influxDbName,
				Arrays.asList(DataRequestBuilder.ofTag("isSessionStart", "count").build()),
				AnalyticsServicesUtils.getDataFilter(criteria, AnalyticsServicesUtils.MESSAGES_MSRMT)
						.addFilter("isSessionStart", "1")
						.addFilter("_field", "name")
						.build(),
				AnalyticsServicesUtils.getTimeFilter(criteria));
	}

	/**
	 * get all nlu messages and fallback messages
	 *
	 * @param criteria statcriteria
	 * @return timeDatas with messages sum and fallbacksum
	 */
	public TimedDatas getRequestStats(final StatCriteria criteria) {
		return InfluxRequestUtil.getTimeSeries(influxDBClient, influxDbName,
				Arrays.asList(
						DataRequestBuilder.ofTag("isFallback", "count").addFilter("==", "1").build(),
						DataRequestBuilder.ofTag("isNlu", "count").addFilter("==", "1").build()),
				AnalyticsServicesUtils.getDataFilter(criteria, AnalyticsServicesUtils.MESSAGES_MSRMT)
						.addFilter("isUserMessage", "1")
						.addFilter("_field", "name")
						.build(),
				AnalyticsServicesUtils.getTimeFilter(criteria));
	}

	public TimedDatas getUserInteractions(final StatCriteria criteria) {

		final var timeFilter = AnalyticsServicesUtils.getTimeFilter(criteria);
		final var columnCriteria = AnalyticsServicesUtils.getBotNodFilter(criteria);

		final String q = new InfluxRequestBuilder(influxDbName)
				.range(timeFilter)
				.filterFields(AnalyticsServicesUtils.MESSAGES_MSRMT, List.of("name"))
				.filterByColumn(columnCriteria)
				.keep(List.of("_time", "_field", "_value", "sessionId"))
				.pivot()
				.keep(List.of("_time", "name"))
				.append("|> rename(columns: {name: \"_value\"}) ")
				.append("|> window(every: " + timeFilter.getDim() + ", createEmpty:true )")
				.count("_value")
				.append("|> toFloat()")
				.append("|> rename(columns: {_value: \"name:count\"})")
				.append("|> rename(columns: {_start: \"_time\"})")
				.append("|> drop(columns: [ \"_stop\"])")
				.build(false);

		return InfluxRequestUtil.executeTimedQuery(influxDBClient, q);
	}

	/**
	 * Get all messages unrecognized
	 *
	 * @param criteria statscriteria
	 * @return all the messages unrecognized
	 */
	public TimedDatas getSentenceDetails(final StatCriteria criteria) {
		final var columnCriteria = AnalyticsServicesUtils.getBotNodFilter(criteria);
		columnCriteria.put("isFallback", "\"1\"");

		final String q = new InfluxRequestBuilder(influxDbName)
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.filterFields(AnalyticsServicesUtils.MESSAGES_MSRMT, List.of("confidence"))
				.filterByColumn(columnCriteria)
				.pivot()
				.keep(List.of("_time", "text", "name", "modelName", "confidence"))
				.build(true);

		return InfluxRequestUtil.executeTimedQuery(influxDBClient, q);
	}

	/**
	 * Get all sessions for export
	 *
	 * @param criteria statscriteria
	 * @return all the messages unrecognized
	 */
	public TimedDatas getSessionsExport(final StatCriteria criteria) {
		return InfluxRequestUtil.getTimeSeries(influxDBClient, influxDbName,
				Arrays.asList(
						DataRequestBuilder.ofTag("name", "count").build(),
						DataRequestBuilder.ofTag("isSessionStart", "count").addFilter("==", "1").build()),
				AnalyticsServicesUtils.getDataFilter(criteria, AnalyticsServicesUtils.MESSAGES_MSRMT)
						.addFilter("_field", "name")
						.build(),
				AnalyticsServicesUtils.getTimeFilter(criteria));
	}

	/**
	 * Get all messages unrecognized for export
	 *
	 * @param criteria statscriteria
	 * @return all the messages unrecognized
	 */
	public TimedDatas getUnknowSentenceExport(final StatCriteria criteria) {
		final var filter = AnalyticsServicesUtils.getBotNodFilter(criteria);
		filter.put("isFallback", "\"1\"");

		final String q = new InfluxRequestBuilder(influxDbName)
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.filterFields(AnalyticsServicesUtils.MESSAGES_MSRMT, List.of("name"))
				.filterByColumn(filter)
				.keep(List.of("_time", "text", "name", "confidence", "modelName", "botId", "nodId", "traId", "isFallback"))
				.build(true);

		return InfluxRequestUtil.executeTimedQuery(influxDBClient, q);
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
				.filterFields(AnalyticsServicesUtils.MESSAGES_MSRMT, List.of("name"))
				.filterByColumn(AnalyticsServicesUtils.getBotNodFilter(criteria))
				.filterByColumn(Map.of("isTechnical", "\"0\""))
				.keep(List.of("name"))
				.append("|> duplicate(column: \"name\", as: \"name:count\")")
				.append("|> count(column: \"name:count\")")
				.build(false);

		return InfluxRequestUtil.executeTabularQuery(influxDBClient, q);
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
				.filterFields(AnalyticsServicesUtils.MESSAGES_MSRMT, List.of("confidence"))
				.filterByColumn(AnalyticsServicesUtils.getBotNodFilter(criteria))
				.filterByColumn(Map.of(
						"name", '"' + intentRasa + '"',
						"isNlu", "\"1\""))
				.append("|> rename(columns: {_value: \"confidence\"})")
				.keep(List.of("_time", "text", "confidence"))
				.build(5_000L, true);

		return InfluxRequestUtil.executeTimedQuery(influxDBClient, q);
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

		return InfluxRequestUtil.executeTimedQuery(influxDBClient, q);
	}

	public TimedDatas getConversationStats(final StatCriteria criteria, final ConversationCriteria conversationCriteria) {
		final Map<String, String> botNodeFilterMap = AnalyticsServicesUtils.getBotNodFilter(criteria);
		final StringBuilder query = new StringBuilder();
		query.append("conv =  ").append(new InfluxRequestBuilder(influxDbName)
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.append("|> filter(fn: (r) => r._measurement == \"" + AnalyticsServicesUtils.CONVERSATION_MSRMT + "\" and r._field == \"isUserMessage\" and r._value == 1)")
				.filter("exists r.sessionId")
				.filterByColumn(botNodeFilterMap)
				.keep(List.of("_time", "_field", "_value", "isUserMessage", "sessionId", "modelName"))
				.group(List.of("sessionId")).buildRaw());
		query.append("\n\n");

		query.append("rate = ").append(new InfluxRequestBuilder(influxDbName)
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.filterFields(AnalyticsServicesUtils.RATING_MSRMT, List.of("rating"))
				.filterByColumn(botNodeFilterMap)
				.filter("exists r.sessionId")
				.keep(List.of("_field", "_value", "sessionId", "rating"))
				.group(List.of("_field")).buildRaw());

		query.append("\n\n");

		query.append("chatbotMessages = ").append(new InfluxRequestBuilder(influxDbName)
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.filterFields(AnalyticsServicesUtils.MESSAGES_MSRMT, List.of("name"))
				.filterByColumn(botNodeFilterMap)
				.filterByColumn(Map.of("name", "\"!END\""))
				.filter("exists r.sessionId")
				.keep(List.of("sessionId"))
				.append("|> set(key: \"_field\", value: \"isEnded\")")
				.append("|> set(key: \"_value\", value: \"1\")")
				.append("|> toFloat()")
				.group(List.of("_field")).buildRaw());
		query.append("\n\n");

		query.append("msgCount = conv \n")
				.append("|> count() \n")
				.append("|> set(key: \"_field\", value: \"interactions\") \n")
				.append("|> toFloat() \n")
				.append("|> group(columns: [\"_field\"]) \n\n");

		query.append("modelName = conv \n")
				.append("|> keep(columns: [\"modelName\", \"sessionId\"]) \n")
				.append("|> first(column: \"modelName\") \n")
				.append("|> group(columns: [\"_field\"]) \n")
				.append("|> rename(columns: {modelName: \"_value\"}) \n")
				.append("|> set(key: \"_field\", value: \"modelName\") \n\n");

		query.append("sessionTime = conv").append("\n")
				.append("|> keep(columns: [\"_time\", \"sessionId\"]) \n")
				.append("|> rename(columns: {_time: \"_value\"}) \n")
				.append("|> min() \n")
				.append("|> set(key: \"_field\", value: \"_time\") \n")
				.append("|> group(columns: [\"_field\"]) \n\n");

		query.append("union(tables: [msgCount, rate, chatbotMessages, sessionTime, modelName]) \n")
				.append("|> pivot(rowKey:[\"sessionId\"], columnKey: [\"_field\"], valueColumn: \"_value\") \n")
				.append("|> group() \n")
				.append("|> filter(fn: (r) => exists r._time)\n");
		if (!conversationCriteria.getRatings().isEmpty()) {
			query.append("|> filter(fn: (r) => ");
			query.append(conversationCriteria.getRatings().stream().map(r -> "r.rating ==  " + r).collect(Collectors.joining(" or ")));
			query.append(")");
		}
		if (conversationCriteria.getModelName() != null) {
			query.append("|> filter(fn: (r) => strings.containsStr(v: r.modelName, substr: \"").append(conversationCriteria.getModelName()).append("\")) \n");
		}
		query.append("|> sort(columns: [\"_time\"], desc: true)");

		return InfluxRequestUtil.executeTimedQuery(influxDBClient, query.toString());
	}

	public TimedDatas getRatingStats(final StatCriteria criteria) {
		final var timeFilter = AnalyticsServicesUtils.getTimeFilter(criteria);
		final String q = new InfluxRequestBuilder(influxDbName)
				.range(timeFilter)
				.filterFields(AnalyticsServicesUtils.RATING_MSRMT, List.of("rating"))
				.filterByColumn(AnalyticsServicesUtils.getBotNodFilter(criteria))
				.keep(List.of("_time", "_value"))
				.append("|> map(fn: (r) => ({ r with _field: \"rating\" + string(v: r._value) + \":count\"}))")
				.group(List.of("_field"))
				.append("|> window(every: " + timeFilter.getDim() + ", createEmpty:true ) ")
				.count("_value")
				.append("|> rename(columns: {_start: \"_time\"}) ")
				.append("|> drop(columns: [ \"_stop\"]) ")
				.pivot()
				.build(false);

		return InfluxRequestUtil.executeTimedQuery(influxDBClient, q);
	}

	public TimedDatas getRatingDetailsStats(final StatCriteria criteria) {
		final StringBuilder query = new StringBuilder();
		query.append("lastTopic = ").append(new InfluxRequestBuilder(influxDbName)
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.append("|> filter(fn: (r) => r._measurement == \"" + AnalyticsServicesUtils.MESSAGES_MSRMT + "\" and r._field == \"name\" and r.isTechnical == \"0\")")
				.filterByColumn(AnalyticsServicesUtils.getBotNodFilter(criteria))
				.append("|> filter(fn: (r) => exists r.sessionId)")
				.keep(List.of("_time", "name", "sessionId"))
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
				.append("|> filter(fn: (r) => exists r.sessionId)")
				.keep(List.of("_time", "sessionId"))
				.append("|> duplicate(column: \"_time\", as: \"_value\")")
				.append("|> set(key: \"_field\", value: \"_time\")")
				.group(List.of("sessionId", "_field"))
				.append("|> sort(columns: [\"_time\"], desc:false)")
				.append("|> last(column: \"_time\")").buildRaw());

		query.append("\n\n");

		query.append("rating = ").append(new InfluxRequestBuilder(influxDbName)
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.append("|> filter(fn: (r) => r._measurement == \"rating\" and r._field==\"rating\" and not exists r.ratingComment)")
				.filterByColumn(AnalyticsServicesUtils.getBotNodFilter(criteria))
				.append("|> filter(fn: (r) => exists r.sessionId)")
				.keep(List.of("_time", "_field", "_value", "sessionId"))
				.buildRaw());

		query.append("\n\n");

		query.append("ratingComment = ").append(new InfluxRequestBuilder(influxDbName)
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.append("|> filter(fn: (r) => r._measurement == \"rating\" and r._field==\"rating\" and exists r.ratingComment)")
				.filterByColumn(AnalyticsServicesUtils.getBotNodFilter(criteria))
				.append("|> filter(fn: (r) => exists r.sessionId)")
				.keep(List.of("_time", "_field", "sessionId", "ratingComment"))
				.append("|> set(key: \"_field\", value: \"ratingComment\")")
				.append("|> rename(columns: {ratingComment: \"_value\"})")
				.buildRaw());

		query.append("\n\n");

		query.append("union(tables: [rating, time, ratingComment, lastTopic]) \n");
		//query.append("union(tables: [rating, time, ratingComment]) \n"); // lastTopic désactivé pour les perfs => migrer dans le measurement des ratings
		query.append("|> pivot(rowKey:[\"sessionId\"], columnKey: [\"_field\"], valueColumn: \"_value\") \n");
		query.append("|> filter(fn: (r) => exists r._time and exists r.rating) \n");
		query.append("|> group() \n");
		query.append("|> sort(columns: [\"_time\"], desc:true)\n");
		query.append("|> limit(n:5000)");

		return InfluxRequestUtil.executeTimedQuery(influxDBClient, query.toString());
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
				.filterFields(AnalyticsServicesUtils.MESSAGES_MSRMT, List.of("confidence"))
				.filterByColumn(AnalyticsServicesUtils.getBotNodFilter(criteria))
				.filterByColumn(Map.of("isFallback", "\"1\""))
				.pivot()
				.keep(List.of("_time", "text", "name", "confidence", "modelName"))
				.build(true);

		return InfluxRequestUtil.executeTimedQuery(influxDBClient, q);
	}

}
