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

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TopicCategory;
import io.vertigo.chatbot.commons.domain.topic.TopicIhm;
import io.vertigo.chatbot.commons.domain.topic.TopicLabel;
import io.vertigo.chatbot.commons.influxDb.TimeSerieServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.chatbot.designer.domain.analytics.ConversationCriteria;
import io.vertigo.chatbot.designer.domain.analytics.ConversationDetail;
import io.vertigo.chatbot.designer.domain.analytics.ConversationStat;
import io.vertigo.chatbot.designer.domain.analytics.SentenseDetail;
import io.vertigo.chatbot.designer.domain.analytics.StatCriteria;
import io.vertigo.chatbot.designer.domain.analytics.TopIntent;
import io.vertigo.chatbot.designer.domain.analytics.TopIntentCriteria;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.database.timeseries.TabularDataSerie;
import io.vertigo.database.timeseries.TabularDatas;
import io.vertigo.database.timeseries.TimedDataSerie;
import io.vertigo.database.timeseries.TimedDatas;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.util.VCollectors;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vertigo.chatbot.commons.domain.topic.KindTopicEnum.END;
import static io.vertigo.chatbot.commons.domain.topic.KindTopicEnum.START;

@Transactional
public class AnalyticsServices implements Component {

	public static final Double TRUE_BIGDECIMAL = 1D;

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
			newSentenseDetail.setConfidence(BigDecimal.valueOf((Double) values.get("confidence")));
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

			final ConversationDetail conversationDetail = new ConversationDetail();
			conversationDetail.setDate(timedData.getTime());
			conversationDetail.setSessionId((String) values.get("sessionId"));
			conversationDetail.setText((String) values.get("text"));
			conversationDetail.setIsUserMessage(values.get("isUserMessage").equals(TRUE_BIGDECIMAL));
			conversationDetail.setIsBotMessage(values.get("isBotMessage").equals(TRUE_BIGDECIMAL));
			retour.add(conversationDetail);
		}

		return retour;
	}

	public DtList<ConversationStat> getConversationsStats(final StatCriteria criteria) {

		final TabularDatas tabularTimedData = timeSerieServices.getConversationStats(criteria);

		// build DtList from InfluxDb data
		final DtList<ConversationStat> retour = new DtList<>(ConversationStat.class);
		for (final TabularDataSerie tabularDataSerie : tabularTimedData.getTabularDataSeries()) {
			final Map<String, Object> values = tabularDataSerie.getValues();

			final ConversationStat conversationStat = new ConversationStat();
			conversationStat.setDate(Instant.now());
			conversationStat.setEnded(false);
			conversationStat.setSessionId((String) values.get("sessionId"));
			conversationStat.setInteractions((Long) values.get("isUserMessage"));

			timeSerieServices.getSessionTechnicalIntents(criteria, conversationStat.getSessionId()).getTimedDataSeries().forEach( technicalIntent -> {
				final String name = (String) technicalIntent.getValues().get("name");
				if (name.replace("!", "").equals(START.name())) {
					conversationStat.setModelName((String) technicalIntent.getValues().get("modelName"));
					conversationStat.setDate(technicalIntent.getTime());
				}
				if (name.replace("!", "").equals(END.name())) {
					conversationStat.setEnded(true);
				}
			});
			conversationStat.setRate(getSessionRating(criteria, conversationStat.getSessionId()));
			retour.add(conversationStat);
		}

		return retour;
	}

	public DtList<ConversationStat> getConversationsStats(final StatCriteria criteria, final ConversationCriteria conversationCriteria) {
		Stream<ConversationStat> conversationStatStream = getConversationsStats(criteria).stream();
		if (!conversationCriteria.getRatings().isEmpty()) {
			conversationStatStream = conversationStatStream.filter(conversationStat ->
					conversationStat.getRate() != null && conversationCriteria.getRatings().contains(conversationStat.getRate()));
		}
		if (conversationCriteria.getModelName() != null) {
			conversationStatStream = conversationStatStream.filter(conversationStat ->
					conversationStat.getModelName() != null && conversationStat.getModelName().contains(conversationCriteria.getModelName()));
		}
		return conversationStatStream.collect(VCollectors.toDtList(ConversationStat.class));
	}

	private Long getSessionRating(final StatCriteria criteria, final String sessionId) {
		final List<TimedDataSerie> ratingSeries =  timeSerieServices.getRatingForSession(criteria, sessionId).getTimedDataSeries();
		if (ratingSeries.isEmpty()) {
			return null;
		} else {
			return getRating(ratingSeries.get(0).getValues());
		}
	}

	private Long getRating(final Map<String, Object> values) {
		for (long i = 1; i <= 5; i++) {
			if (values.get("rating" + i) != null) {
				return i;
			}
		}
		return 0L;
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

	public DtList<TopIntent> getTopIntents(final Chatbot bot, final String locale, final StatCriteria criteria,
										   final TopIntentCriteria topIntentCriteria, final DtList<TopicCategory> categories, final DtList<TopicLabel> topicLabels) {
		Stream<TopIntent> topIntents = getTopIntents(bot, locale, criteria).stream();
		if (!topIntentCriteria.getCatIds().isEmpty()) {
			final List<String> catLabels =
					categories.stream().filter(topicCategory -> topIntentCriteria.getCatIds()
							.contains(topicCategory.getTopCatId())).map(TopicCategory::getLabel).collect(Collectors.toList());

			topIntents = topIntents.filter(topIntent -> catLabels.contains(topIntent.getCatLabel()));
		}
		if (!topIntentCriteria.getLabels().isEmpty()) {
			final List<String> labels =
					topicLabels.stream().filter(topicLabel -> topIntentCriteria.getLabels()
							.contains(topicLabel.getLabelId())).map(TopicLabel::getLabel).collect(Collectors.toList());
			topIntents = topIntents.filter(topIntent -> topIntent.getLabels() != null && labels.stream().anyMatch(label -> topIntent.getLabels().contains(label)));
		}
		return topIntents.collect(VCollectors.toDtList(TopIntent.class));
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

}
