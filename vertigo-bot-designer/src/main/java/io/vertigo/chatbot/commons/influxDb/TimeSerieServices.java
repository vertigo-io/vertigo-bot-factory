package io.vertigo.chatbot.commons.influxDb;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.InfluxDBClientOptions;

import io.vertigo.chatbot.designer.analytics.utils.AnalyticsServicesUtils;
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
	 * get all nlu messages and fallback messages
	 *
	 * @param criteria statcriteria
	 * @return timeDatas with messages sum and fallbacksum
	 */
	public TimedDatas getRequestStats(final StatCriteria criteria) {
		final var timeFilter = AnalyticsServicesUtils.getTimeFilter(criteria);
		final var columnCriteria = AnalyticsServicesUtils.getBotNodFilter(criteria);

		final var q = new InfluxRequestBuilder(influxDbName)
				.range(timeFilter)
				.filterFields(AnalyticsServicesUtils.MESSAGES_STAT_MSRMT, List.of("isSessionStart:count", "isFallback:count", "isNlu:count", "userAction:count"))
				.filterByColumn(columnCriteria);
		if (criteria.getNodId() == null) {
			q.append("|> drop(columns: [\"nodId\"])");
		}
		q.append("|> window(every: " + timeFilter.dim() + ", createEmpty:true )")
				.append("|> sum()")
				.append("|> rename(columns: {_start: \"_time\"})")
				.append("|> drop(columns: [\"_stop\"])")
				.pivot();

		return InfluxRequestUtil.executeTimedQuery(influxDBClient, q.build(false));
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
				.append("|> window(every: " + timeFilter.dim() + ", createEmpty:true )")
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
	 * @param criteria stat criteria for filtering
	 * @return all topIntents
	 */
	public TabularDatas getAllTopIntents(final StatCriteria criteria) {
		final var timeFilter = AnalyticsServicesUtils.getTimeFilter(criteria);

		final var q = new InfluxRequestBuilder(influxDbName)
				.range(timeFilter)
				.filterFields(AnalyticsServicesUtils.MESSAGES_STAT_MSRMT, List.of("name:count"))
				.filterByColumn(AnalyticsServicesUtils.getBotNodFilter(criteria));
		if (criteria.getNodId() == null) {
			q.append("|> drop(columns: [\"nodId\"])");
		}
		q.append("|> sum()")
				.append("|> rename(columns: {_value: \"name:count\"})")
				.append("|> keep(columns: [\"name\", \"name:count\"])");

		return InfluxRequestUtil.executeTabularQuery(influxDBClient, q.build(false));
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
		final String q = new InfluxRequestBuilder(influxDbName)
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.filterFields(AnalyticsServicesUtils.CONVERSATION_MSRMT, List.of("name"))
				.filterByColumn(AnalyticsServicesUtils.getBotNodFilter(criteria))
				.filterByColumn(Map.of("sessionId", '"' + sessionId + '"'))
				.keep(List.of("_time", "text", "isUserMessage", "isBotMessage"))
				.build(5_000L, true, "isBotMessage");

		return InfluxRequestUtil.executeTimedQuery(influxDBClient, q);
	}

	public TimedDatas getConversationStats(final StatCriteria criteria, final ConversationCriteria conversationCriteria) {
		final Map<String, String> botNodeFilterMap = AnalyticsServicesUtils.getBotNodFilter(criteria);
		final var query = new InfluxRequestBuilder(influxDbName, List.of("strings"))
				.range(AnalyticsServicesUtils.getTimeFilter(criteria))
				.filter("r._measurement == \"" + AnalyticsServicesUtils.CONVERSATION_STAT_MSRMT + "\"")
				.filterByColumn(botNodeFilterMap)
				.pivot();
		if (!conversationCriteria.getRatings().isEmpty()) {
			query.filter(conversationCriteria.getRatings().stream().map(r -> "r.rating ==  \"" + r + '"').collect(Collectors.joining(" or ")));
		}
		if (conversationCriteria.getModelName() != null) {
			query.filter("strings.containsStr(v: r.modelName, substr: \"" + conversationCriteria.getModelName() + "\")");
		}
		query.keep(List.of("_time", "interactions", "isEnded", "lastTopic", "rating", "ratingComment", "sessionId", "modelName"));

		return InfluxRequestUtil.executeTimedQuery(influxDBClient, query.build(true));
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
				.append("|> window(every: " + timeFilter.dim() + ", createEmpty:true ) ")
				.count("_value")
				.append("|> rename(columns: {_start: \"_time\"}) ")
				.append("|> drop(columns: [ \"_stop\"]) ")
				.pivot()
				.build(false);

		return InfluxRequestUtil.executeTimedQuery(influxDBClient, q);
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

	/**
	 * Get documentary resource click statistics
	 *
	 * @param criteria filter criteria
	 * @param dreTypeCd optional type filter
	 * @param searchText optional search filter
	 * @return tabular data with documentary resource stats
	 */
	public TabularDatas getDocumentaryResourceStats(final StatCriteria criteria, final String dreTypeCd, final String searchText) {
		final var timeFilter = AnalyticsServicesUtils.getTimeFilter(criteria);
		final var columnCriteria = AnalyticsServicesUtils.getBotNodFilter(criteria);

		// Add dreTypeCd to column criteria for filterByColumn (with exists check)
		if (dreTypeCd != null && !dreTypeCd.isEmpty()) {
			columnCriteria.put("dreTypeCd", "\"" + dreTypeCd + "\"");
		}

		final var q = new InfluxRequestBuilder(influxDbName)
				.range(timeFilter)
				.filterFields(AnalyticsServicesUtils.DOCUMENTARY_RESOURCE_STAT_MSRMT, List.of("dreId:count"))
				.filterByColumn(columnCriteria);

		// Filter by search text if specified (case-insensitive regex)
		if (searchText != null && !searchText.isEmpty()) {
			final String escapedText = AnalyticsServicesUtils.escapeFluxRegex(searchText);
			q.append("|> filter(fn: (r) => r.title =~ /(?i)" + escapedText + "/)");
		}

		q.append("|> group(columns: [\"dreId\", \"title\", \"dreTypeCd\"])")
				.append("|> sum()")
				.append("|> rename(columns: {_value: \"dreId:count\"})")
				.keep(List.of("dreId", "title", "dreTypeCd", "dreId:count"));

		return InfluxRequestUtil.executeTabularQuery(influxDBClient, q.build(false));
	}

	/**
	 * Get total documentary resource clicks
	 *
	 * @param criteria filter criteria
	 * @return timed data with total clicks
	 */
	public TimedDatas getTotalDocumentaryResourceClicks(final StatCriteria criteria) {
		final var timeFilter = AnalyticsServicesUtils.getTimeFilter(criteria);
		final var columnCriteria = AnalyticsServicesUtils.getBotNodFilter(criteria);

		final var q = new InfluxRequestBuilder(influxDbName)
				.range(timeFilter)
				.filterFields(AnalyticsServicesUtils.DOCUMENTARY_RESOURCE_STAT_MSRMT, List.of("clicks:count"))
				.filterByColumn(columnCriteria);

		if (criteria.getNodId() == null) {
			q.append("|> drop(columns: [\"nodId\"])");
		}

		q.append("|> window(every: " + timeFilter.dim() + ", createEmpty:true)")
				.append("|> sum()")
				.append("|> rename(columns: {_start: \"_time\"})")
				.append("|> drop(columns: [\"_stop\"])")
				.pivot();

		return InfluxRequestUtil.executeTimedQuery(influxDBClient, q.build(true));
	}

	/**
	 * Get question/answer click statistics
	 *
	 * @param criteria filter criteria
	 * @param catLabel optional category filter
	 * @param searchText optional search filter
	 * @return tabular data with question/answer stats
	 */
	public TabularDatas getQuestionAnswerStats(final StatCriteria criteria, final String catLabel, final String searchText) {
		final var timeFilter = AnalyticsServicesUtils.getTimeFilter(criteria);
		final var columnCriteria = AnalyticsServicesUtils.getBotNodFilter(criteria);

		// Add catLabel to column criteria for filterByColumn (with exists check)
		if (catLabel != null && !catLabel.isEmpty()) {
			columnCriteria.put("catLabel", "\"" + catLabel + "\"");
		}

		final var q = new InfluxRequestBuilder(influxDbName)
				.range(timeFilter)
				.filterFields(AnalyticsServicesUtils.QUESTION_ANSWER_STAT_MSRMT, List.of("qaId:count"))
				.filterByColumn(columnCriteria);

		// Filter by search text if specified (case-insensitive regex)
		if (searchText != null && !searchText.isEmpty()) {
			final String escapedText = AnalyticsServicesUtils.escapeFluxRegex(searchText);
			q.append("|> filter(fn: (r) => r.question =~ /(?i)" + escapedText + "/)");
		}

		q.append("|> group(columns: [\"qaId\", \"question\", \"catLabel\"])")
				.append("|> sum()")
				.append("|> rename(columns: {_value: \"qaId:count\"})")
				.keep(List.of("qaId", "question", "catLabel", "qaId:count"));

		return InfluxRequestUtil.executeTabularQuery(influxDBClient, q.build(false));
	}

	/**
	 * Get total question/answer clicks
	 *
	 * @param criteria filter criteria
	 * @return timed data with total clicks
	 */
	public TimedDatas getTotalQuestionAnswerClicks(final StatCriteria criteria) {
		final var timeFilter = AnalyticsServicesUtils.getTimeFilter(criteria);
		final var columnCriteria = AnalyticsServicesUtils.getBotNodFilter(criteria);

		final var q = new InfluxRequestBuilder(influxDbName)
				.range(timeFilter)
				.filterFields(AnalyticsServicesUtils.QUESTION_ANSWER_STAT_MSRMT, List.of("clicks:count"))
				.filterByColumn(columnCriteria);

		if (criteria.getNodId() == null) {
			q.append("|> drop(columns: [\"nodId\"])");
		}

		q.append("|> window(every: " + timeFilter.dim() + ", createEmpty:true)")
				.append("|> sum()")
				.append("|> rename(columns: {_start: \"_time\"})")
				.append("|> drop(columns: [\"_stop\"])")
				.pivot();

		return InfluxRequestUtil.executeTimedQuery(influxDBClient, q.build(true));
	}

}
