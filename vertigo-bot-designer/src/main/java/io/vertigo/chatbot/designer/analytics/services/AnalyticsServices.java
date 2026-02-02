/**
 * vertigo - simple java starter
 * <p>
 * Copyright (C) 2020, Vertigo.io, team@vertigo.io
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vertigo.chatbot.designer.analytics.services;

import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TopicCategory;
import io.vertigo.chatbot.commons.domain.topic.TopicIhm;
import io.vertigo.chatbot.commons.influxDb.TimeSerieServices;
import io.vertigo.chatbot.designer.analytics.utils.AnalyticsServicesUtils;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.chatbot.designer.domain.analytics.CategoryStat;
import io.vertigo.chatbot.designer.domain.analytics.ConversationCriteria;
import io.vertigo.chatbot.designer.domain.analytics.ConversationDetail;
import io.vertigo.chatbot.designer.domain.analytics.ConversationStat;
import io.vertigo.chatbot.designer.domain.analytics.SentenseDetail;
import io.vertigo.chatbot.designer.domain.analytics.StatCriteria;
import io.vertigo.chatbot.designer.domain.analytics.TopIntent;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.database.timeseries.TabularDatas;
import io.vertigo.database.timeseries.TimedDataSerie;
import io.vertigo.database.timeseries.TimedDatas;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.util.VCollectors;

/**
 * Service for analytics operations on chatbot data.
 * This service provides methods to retrieve and process analytics data including
 * sentence details, conversations, top intents, category statistics, and documentary resources.
 *
 * @author Chatbot Team
 */
@Transactional
public class AnalyticsServices implements Component {

	public static final Double TRUE_BIGDECIMAL = 1D;
	public static final String TRUE_STRING = "1";
	public static final Double FALSE_BIGDECIMAL = 0D;
	public static final String FALSE_STRING = "0";

	@Inject
	private TopicServices topicServices;

	@Inject
	private TimeSerieServices timeSerieServices;

	/**
	 * Get the sentence unreconized by the bot
	 *
	 * @param criteria statscriteria for unreconized sentence
	 * @return the unknown sentences
	 */
	public DtList<SentenseDetail> getSentenseDetails(final StatCriteria criteria) {
		// get data from influxdb
		final TimedDatas tabularTimedData = timeSerieServices.getSentenceDetails(criteria);

		// build DtList from InfluxDb data
		final DtList<SentenseDetail> retour = new DtList<>(SentenseDetail.class);
		for (final TimedDataSerie timedData : tabularTimedData.timedDataSeries()) {
			final Map<String, Object> values = timedData.getValues();

			final SentenseDetail newSentenseDetail = new SentenseDetail();
			newSentenseDetail.setDate(timedData.getTime());
			newSentenseDetail.setText((String) values.get("text"));
			newSentenseDetail.setConfidence(new BigDecimal((Double) values.get("confidence")));
			newSentenseDetail.setModelName((String) values.get("modelName"));
			retour.add(newSentenseDetail);
		}

		return retour;
	}

	/**
	 * Get conversation details for a specific session
	 *
	 * @param criteria stat criteria for filtering
	 * @param sessionId session identifier
	 * @return list of conversation details
	 */
	public DtList<ConversationDetail> getConversationDetails(final StatCriteria criteria, final String sessionId) {
		// get data from influxdb
		final TimedDatas tabularTimedData = timeSerieServices.getConversationDetails(criteria, sessionId);

		// build DtList from InfluxDb data
		final DtList<ConversationDetail> retour = new DtList<>(ConversationDetail.class);
		for (final TimedDataSerie timedData : tabularTimedData.timedDataSeries()) {
			final Map<String, Object> values = timedData.getValues();
			final String text = (String) values.get("text");
			if (text != null) {
				Arrays.stream(text.split("\0")).forEach(bubble -> {
					final ConversationDetail conversationDetail = new ConversationDetail();
					conversationDetail.setDate(timedData.getTime());
					conversationDetail.setSessionId((String) values.get("sessionId"));
					conversationDetail.setText(bubble);
					conversationDetail.setIsUserMessage(TRUE_STRING.equals(values.get("isUserMessage")));
					conversationDetail.setIsBotMessage(TRUE_STRING.equals(values.get("isBotMessage")));
					retour.add(conversationDetail);
				});
			}
		}

		return retour;
	}

	/**
	 * Get conversation statistics based on criteria
	 *
	 * @param criteria stat criteria for filtering
	 * @param conversationCriteria additional conversation-specific criteria
	 * @return list of conversation statistics
	 */
	public DtList<ConversationStat> getConversationsStats(final StatCriteria criteria, final ConversationCriteria conversationCriteria) {

		final TimedDatas timedDatas = timeSerieServices.getConversationStats(criteria, conversationCriteria);
		// build DtList from InfluxDb data
		final DtList<ConversationStat> retour = new DtList<>(ConversationStat.class);
		for (final TimedDataSerie timedDataSerie : timedDatas.timedDataSeries()) {
			final Map<String, Object> values = timedDataSerie.getValues();

			final ConversationStat conversationStat = new ConversationStat();
			conversationStat.setDate(timedDataSerie.getTime());
			conversationStat.setEnded(TRUE_STRING.equals(values.get("isEnded")));
			conversationStat.setSessionId((String) values.get("sessionId"));
			conversationStat.setInteractions(AnalyticsServicesUtils.getLongValue(timedDataSerie, "interactions", null));
			conversationStat.setRating(AnalyticsServicesUtils.getLongValue(timedDataSerie, "rating", null));
			conversationStat.setRatingComment((String) values.get("ratingComment"));
			conversationStat.setModelName((String) values.get("modelName"));
			conversationStat.setLastTopic((String) values.get("lastTopic"));
			retour.add(conversationStat);
		}

		return retour;
	}

	/**
	 * Build category statistics from topic categories and intents
	 *
	 * @param categories list of topic categories
	 * @param intents list of top intents
	 * @return list of category statistics with usage and percentage
	 */
	public DtList<CategoryStat> buildCategoryStats(final DtList<TopicCategory> categories, final DtList<TopIntent> intents) {
		final DtList<CategoryStat> categoryStats = new DtList<>(CategoryStat.class);
		final long totalCount = intents.stream().mapToLong(TopIntent::getCount).sum();
		final Map<String, Long> categoriesCount = new HashMap<>();
		intents.forEach(topIntent -> categoriesCount.put(topIntent.getCatLabel(),
				categoriesCount.getOrDefault(topIntent.getCatLabel(), 0L) + topIntent.getCount()));
		categories.forEach(topicCategory -> {
			final CategoryStat categoryStat = new CategoryStat();
			categoryStat.setLabel(topicCategory.getLabel());
			categoryStat.setCode(topicCategory.getCode());
			final long count = categoriesCount.getOrDefault(topicCategory.getLabel(), 0L);
			categoryStat.setUsage(count);
			if (totalCount != 0) {
				categoryStat.setPercentage(BigDecimal.valueOf(((double) count / totalCount) * 100));
			} else {
				categoryStat.setPercentage(BigDecimal.ZERO);
			}
			categoryStats.add(categoryStat);
		});
		return categoryStats;
	}

	/**
	 * Get top intents for a bot based on criteria
	 *
	 * @param bot chatbot
	 * @param locale locale for internationalization
	 * @param criteria stat criteria for filtering
	 * @return list of top intents with usage count
	 */
	public DtList<TopIntent> getTopIntents(final Chatbot bot, final String locale, final StatCriteria criteria) {
		// get data from influxdb
		final TabularDatas tabularDatas = timeSerieServices.getAllTopIntents(criteria);
		// build DtList from InfluxDb data
		final DtList<TopicIhm> topics = topicServices.getAllNonTechnicalTopicIhmByBot(bot, locale);
		final Map<String, Long> topicCountMap = new HashMap<>();
		tabularDatas.tabularDataSeries().forEach(x -> topicCountMap.put(x.getValues().get("name").toString(), ((Long) x.getValues().get("name:count"))));

		return topics.stream().map(topic -> {
			final TopIntent topIntent = new TopIntent();
			topIntent.setIntentRasa(topic.getTitle());
			topIntent.setTopId(topic.getTopId());
			topIntent.setCode(topic.getCode());
			topIntent.setCatLabel(topic.getCatLabel());
			topIntent.setLabels(topic.getLabels());
			topIntent.setCount(topicCountMap.getOrDefault(topic.getCode(), 0L));
			return topIntent;
		}).collect(VCollectors.toDtList(TopIntent.class));
	}

	/**
	 * Get the known sentence for a specific intentRasa
	 *
	 * @param criteria statCriteria
	 * @param intentRasa intent associated
	 * @return List of know sentences
	 */
	public DtList<SentenseDetail> getKnownSentensesDetail(final StatCriteria criteria, final String intentRasa) {
		// get data from influxdb
		final TimedDatas tabularTimedData = timeSerieServices.getKnowSentence(criteria, intentRasa);
		final DtList<SentenseDetail> retour = new DtList<>(SentenseDetail.class);
		final Optional<Topic> topic = topicServices.getTopicByCode(intentRasa, criteria.getBotId());
		for (final TimedDataSerie timedData : tabularTimedData.timedDataSeries()) {
			final Map<String, Object> values = timedData.getValues();

			final SentenseDetail newSentenseDetail = new SentenseDetail();
			newSentenseDetail.setDate(timedData.getTime());
			newSentenseDetail.setText((String) values.get("text"));
			newSentenseDetail.setIntentRasa(intentRasa);
			newSentenseDetail.setConfidence(BigDecimal.valueOf((Double) values.get("confidence")));
			newSentenseDetail.setTopId(topic.map(Topic::getTopId).orElse(null));

			retour.add(newSentenseDetail);
		}

		return retour;
	}

	/**
	 * Get documentary resource statistics with filters
	 *
	 * @param criteria stat criteria
	 * @param resourceCriteria filter criteria for resources
	 * @return list of documentary resource stats
	 */
	public DtList<io.vertigo.chatbot.designer.domain.analytics.DocumentaryResourceStat> getDocumentaryResourceStats(
			final StatCriteria criteria,
			final io.vertigo.chatbot.designer.domain.analytics.DocumentaryResourceCriteria resourceCriteria) {
		// Get data from InfluxDB with filters
		final TabularDatas tabularData = timeSerieServices.getDocumentaryResourceStats(
				criteria,
				resourceCriteria.getDreTypeCd(),
				resourceCriteria.getSearchText());

		// Build DtList from InfluxDB data
		final DtList<io.vertigo.chatbot.designer.domain.analytics.DocumentaryResourceStat> result =
				new DtList<>(io.vertigo.chatbot.designer.domain.analytics.DocumentaryResourceStat.class);

		tabularData.tabularDataSeries().forEach(serie -> {
			final var stat = new io.vertigo.chatbot.designer.domain.analytics.DocumentaryResourceStat();
			final Map<String, Object> values = serie.getValues();

			stat.setDreId(NumberUtils.toLong((String) values.get("dreId")));
			stat.setTitle((String) values.get("title"));
			stat.setDreTypeCd((String) values.get("dreTypeCd"));
			stat.setCount(toLongSafe(values.get("dreId:count")));

			result.add(stat);
		});

		return result;
	}

/**
 * Get total documentary resource clicks for the period
 *
 * @param criteria stat criteria
 * @return total clicks count
 */
public Double getTotalDocumentaryResourceClicks(final StatCriteria criteria) {
	final TimedDatas timedData = timeSerieServices.getTotalDocumentaryResourceClicks(criteria);
	return timedData.timedDataSeries().stream()
			.mapToDouble(it -> Optional.ofNullable(AnalyticsServicesUtils.getLongValue(it, "clicks:count", 0L))
					.orElse(0L)
					.doubleValue())
			.sum();
}

	/**
	 * Get question/answer statistics with filters
	 *
	 * @param criteria stat criteria
	 * @param questionAnswerCriteria filter criteria for Q&A
	 * @return list of question/answer stats
	 */
	public DtList<io.vertigo.chatbot.designer.domain.analytics.QuestionAnswerStat> getQuestionAnswerStats(
			final StatCriteria criteria,
			final io.vertigo.chatbot.designer.domain.analytics.QuestionAnswerCriteria questionAnswerCriteria) {
		// Get data from InfluxDB with filters
		final TabularDatas tabularData = timeSerieServices.getQuestionAnswerStats(
				criteria,
				questionAnswerCriteria.getCatLabel(),
				questionAnswerCriteria.getSearchText());

		// Build DtList from InfluxDB data
		final DtList<io.vertigo.chatbot.designer.domain.analytics.QuestionAnswerStat> result =
				new DtList<>(io.vertigo.chatbot.designer.domain.analytics.QuestionAnswerStat.class);

		tabularData.tabularDataSeries().forEach(serie -> {
			final var stat = new io.vertigo.chatbot.designer.domain.analytics.QuestionAnswerStat();
			final Map<String, Object> values = serie.getValues();

			stat.setQaId(NumberUtils.toLong((String) values.get("qaId")));
			stat.setQuestion((String) values.get("question"));
			stat.setCatLabel((String) values.get("catLabel"));
			stat.setCount(toLongSafe(values.get("qaId:count")));

			result.add(stat);
		});

		return result;
	}

	/**
	 * Get total question/answer clicks for the period
	 *
	 * @param criteria stat criteria
	 * @return total clicks count
	 */
	public Double getTotalQuestionAnswerClicks(final StatCriteria criteria) {
		final TimedDatas timedData = timeSerieServices.getTotalQuestionAnswerClicks(criteria);
		return timedData.timedDataSeries().stream()
				.mapToDouble(it -> Optional.ofNullable(AnalyticsServicesUtils.getLongValue(it, "clicks:count", 0L))
						.orElse(0L)
						.doubleValue())
				.sum();
	}

	/**
	 * Safely converts an Object value to Long.
	 * Supports Number types (Long, Double, Integer, etc.)
	 *
	 * @param value the value to convert
	 * @return the value converted to Long, or 0L if null or unsupported type
	 */
	private static Long toLongSafe(final Object value) {
		return value instanceof final Number number ? number.longValue() : 0L;
	}

}
