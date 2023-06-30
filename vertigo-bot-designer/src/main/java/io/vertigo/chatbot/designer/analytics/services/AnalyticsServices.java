/**
 * vertigo - simple java starter
 *
 * Copyright (C) 2020, Vertigo.io, team@vertigo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vertigo.chatbot.designer.analytics.services;

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
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.chatbot.designer.domain.analytics.CategoryStat;
import io.vertigo.chatbot.designer.domain.analytics.ConversationCriteria;
import io.vertigo.chatbot.designer.domain.analytics.ConversationDetail;
import io.vertigo.chatbot.designer.domain.analytics.ConversationStat;
import io.vertigo.chatbot.designer.domain.analytics.RatingDetail;
import io.vertigo.chatbot.designer.domain.analytics.SentenseDetail;
import io.vertigo.chatbot.designer.domain.analytics.StatCriteria;
import io.vertigo.chatbot.designer.domain.analytics.TopIntent;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.database.timeseries.TabularDatas;
import io.vertigo.database.timeseries.TimedDataSerie;
import io.vertigo.database.timeseries.TimedDatas;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.util.VCollectors;

@Transactional
public class AnalyticsServices implements Component {

	public static final Double TRUE_BIGDECIMAL = 1D;
	public static final Double FALSE_BIGDECIMAL = 0D;

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
		for (final TimedDataSerie timedData : tabularTimedData.getTimedDataSeries()) {
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

	public DtList<ConversationDetail> getConversationDetails(final StatCriteria criteria, final String sessionId) {
		// get data from influxdb
		final TimedDatas tabularTimedData = timeSerieServices.getConversationDetails(criteria, sessionId);

		// build DtList from InfluxDb data
		final DtList<ConversationDetail> retour = new DtList<>(ConversationDetail.class);
		for (final TimedDataSerie timedData : tabularTimedData.getTimedDataSeries()) {
			final Map<String, Object> values = timedData.getValues();
			final String text = (String) values.get("text");
			if (text != null) {
				Arrays.stream(text.split("\0")).forEach(bubble -> {
					final ConversationDetail conversationDetail = new ConversationDetail();
					conversationDetail.setDate(timedData.getTime());
					conversationDetail.setSessionId((String) values.get("sessionId"));
					conversationDetail.setText(bubble);
					conversationDetail.setIsUserMessage(values.get("isUserMessage").equals(TRUE_BIGDECIMAL));
					conversationDetail.setIsBotMessage(values.get("isBotMessage").equals(TRUE_BIGDECIMAL));
					retour.add(conversationDetail);
				});
			}
		}

		return retour;
	}

	public DtList<ConversationStat> getConversationsStats(final StatCriteria criteria, final ConversationCriteria conversationCriteria) {

		final TimedDatas timedDatas = timeSerieServices.getConversationStats(criteria, conversationCriteria);
		// build DtList from InfluxDb data
		final DtList<ConversationStat> retour = new DtList<>(ConversationStat.class);
		for (final TimedDataSerie timedDataSerie : timedDatas.getTimedDataSeries()) {
			final Map<String, Object> values = timedDataSerie.getValues();

			final ConversationStat conversationStat = new ConversationStat();
			conversationStat.setDate(timedDataSerie.getTime());
			conversationStat.setEnded(values.get("isEnded") != null && values.get("isEnded").equals(TRUE_BIGDECIMAL));
			conversationStat.setSessionId((String) values.get("sessionId"));
			conversationStat.setInteractions(values.get("interactions") != null ? ((Double) values.get("interactions")).longValue() : null);
			conversationStat.setRate(values.get("rating") != null ? ((Double) values.get("rating")).longValue() : null);
			conversationStat.setModelName((String) values.get("modelName"));
			retour.add(conversationStat);
		}

		return retour;
	}

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

	public DtList<TopIntent> getTopIntents(final Chatbot bot, final String locale, final StatCriteria criteria) {
		// get data from influxdb
		final TabularDatas tabularDatas = timeSerieServices.getAllTopIntents(criteria);
		// build DtList from InfluxDb data
		final DtList<TopicIhm> topics = topicServices.getAllNonTechnicalTopicIhmByBot(bot, locale);
		final Map<String, Long> topicCountMap = new HashMap<>();
		tabularDatas.getTabularDataSeries().forEach(x -> topicCountMap.put(x.getValues().get("name").toString(), ((Long) x.getValues().get("name:count"))));

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
		for (final TimedDataSerie timedData : tabularTimedData.getTimedDataSeries()) {
			final Map<String, Object> values = timedData.getValues();

			final SentenseDetail newSentenseDetail = new SentenseDetail();
			newSentenseDetail.setDate(timedData.getTime());
			newSentenseDetail.setText((String) values.get("text"));
			newSentenseDetail.setIntentRasa(intentRasa);
			newSentenseDetail.setConfidence(BigDecimal.valueOf((Double) values.get("confidence")));
			newSentenseDetail.setTopId(topic.isPresent() ? topic.get().getTopId() : null);

			retour.add(newSentenseDetail);
		}

		return retour;
	}

	public DtList<RatingDetail> getRatingDetails(final StatCriteria criteria) {
		final TimedDatas ratings = timeSerieServices.getRatingDetailsStats(criteria);
		final DtList<RatingDetail> retour = new DtList<>(RatingDetail.class);
		ratings.getTimedDataSeries().forEach(timedDataSerie -> {
			final Map<String, Object> values = timedDataSerie.getValues();
			final RatingDetail ratingDetail = new RatingDetail();
			ratingDetail.setSessionId((String) values.get("sessionId"));
			ratingDetail.setDate(timedDataSerie.getTime());
			ratingDetail.setRating(((Double) values.get("rating")).longValue());
			ratingDetail.setComment((String) values.get("ratingComment"));
			ratingDetail.setLastTopic((String) values.get("lastTopic"));
			retour.add(ratingDetail);
		});
		return retour;
	}
}
