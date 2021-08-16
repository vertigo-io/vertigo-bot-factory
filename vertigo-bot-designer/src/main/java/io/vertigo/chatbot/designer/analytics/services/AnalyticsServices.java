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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.chatbot.designer.domain.analytics.SentenseDetail;
import io.vertigo.chatbot.designer.domain.analytics.StatCriteria;
import io.vertigo.chatbot.designer.domain.analytics.TopIntent;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Tuple;
import io.vertigo.core.node.component.Component;
import io.vertigo.database.timeseries.TabularDatas;
import io.vertigo.database.timeseries.TimedDataSerie;
import io.vertigo.database.timeseries.TimedDatas;
import io.vertigo.datamodel.structure.model.DtList;

@Transactional
public class AnalyticsServices implements Component {

	@Inject
	private TopicServices topicServices;

	@Inject
	private TimeSerieServices timeSerieServices;

	public TimedDatas getSessionsStats(final StatCriteria criteria) {
		return timeSerieServices.getSessionsStats(criteria);
	}

	public TimedDatas getRequestStats(final StatCriteria criteria) {
		return timeSerieServices.getRequestStats(criteria);

	}

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

	public DtList<TopIntent> getTopIntents(final StatCriteria criteria) {
		// get data from influxdb
		final TabularDatas tabularDatas = timeSerieServices.getAllTopIntents(criteria);
		// build DtList from InfluxDb data
		final DtList<TopIntent> retour = new DtList<>(TopIntent.class);
		final DtList<Topic> topics = topicServices.getAllTopicByBotId(criteria.getBotId());
		//Get the tuple (name, name:count)
		final List<Tuple<Object, Object>> listValues = tabularDatas.getTabularDataSeries().stream().map(x -> Tuple.of(x.getValues().get("name"), x.getValues().get("name:count")))
				.collect(Collectors.toList());
		//create top intent for each values in listValues
		listValues.stream().forEach(x -> createTopIntent(x, topics, retour));

		return retour;
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

	public TimedDatas getTopicStats(final StatCriteria criteria) {
		return timeSerieServices.getTopicsStats(criteria);

	}

	public List<String> getDistinctCodeByTimeDatas(final StatCriteria criteria) {
		final TimedDatas timedData = getTopicStats(criteria);
		return timedData.getTimedDataSeries().stream()
				.flatMap(serie -> serie.getValues().values().stream())
				.map(Object::toString)
				.distinct()
				.collect(Collectors.toList());
	}

	//Filter topics and create topIntent object
	private static void createTopIntent(final Tuple<Object, Object> values, final DtList<Topic> topics, final DtList<TopIntent> retour) {
		final String intentName = values.getVal1().toString();
		final Topic topic = topics.stream().filter(x -> x.getCode().equals(intentName)).findFirst().orElse(null);
		if (topic != null) {
			final TopIntent topIntent = new TopIntent();
			topIntent.setCount(((Double) values.getVal2()).longValue());
			topIntent.setIntentRasa(topic.getTitle());
			topIntent.setTopId(topic.getTopId());
			topIntent.setCode(topic.getCode());

			retour.add(topIntent);
		}
	}

	public TimedDatas getRatingStats(final StatCriteria criteria) {
		return timeSerieServices.getRatingStats(criteria);
	}

}
