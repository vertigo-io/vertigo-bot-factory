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
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.RasaTypeAction;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.chatbot.designer.domain.SentenseDetail;
import io.vertigo.chatbot.designer.domain.StatCriteria;
import io.vertigo.chatbot.designer.domain.TopIntent;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.param.ParamManager;
import io.vertigo.database.timeseries.DataFilter;
import io.vertigo.database.timeseries.DataFilterBuilder;
import io.vertigo.database.timeseries.TabularDataSerie;
import io.vertigo.database.timeseries.TabularDatas;
import io.vertigo.database.timeseries.TimeFilter;
import io.vertigo.database.timeseries.TimeSeriesManager;
import io.vertigo.database.timeseries.TimedDataSerie;
import io.vertigo.database.timeseries.TimedDatas;
import io.vertigo.datamodel.structure.model.DtList;

@Transactional
public class AnalyticsServices implements Component, Activeable {

	@Inject
	private TimeSeriesManager timeSeriesManager;

	@Inject
	private ParamManager paramManager;

	@Inject
	private TopicServices topicServices;

	private String influxDbName;

	@Override
	public void start() {
		influxDbName = paramManager.getParam("boot.ANALYTICA_DBNAME").getValueAsString();
	}

	@Override
	public void stop() {
		// Nothing
	}

	public TimedDatas getSessionsStats(final StatCriteria criteria) {
		return timeSeriesManager.getTimeSeries(influxDbName, Arrays.asList("isSessionStart:sum"),
				getDataFilter(criteria).build(),
				getTimeFilter(criteria));
	}

	public TimedDatas getRequestStats(final StatCriteria criteria) {
		return timeSeriesManager.getTimeSeries(influxDbName, Arrays.asList("name:count", "isFallback:sum", "isNlu:sum"),
				getDataFilter(criteria).withAdditionalWhereClause("isUserMessage = 1").build(),
				getTimeFilter(criteria));

	}

	public DtList<SentenseDetail> getSentenseDetails(final StatCriteria criteria) {
		// get data from influxdb
		final TimedDatas tabularTimedData = timeSeriesManager.getFlatTabularTimedData(influxDbName, Arrays.asList("messageId", "text", "name", "confidence"),
				getDataFilter(criteria).withAdditionalWhereClause("isFallback = 1").build(),
				getTimeFilter(criteria),
				Optional.empty());

		// build DtList from InfluxDb data
		final DtList<SentenseDetail> retour = new DtList<>(SentenseDetail.class);
		for (final TimedDataSerie timedData : tabularTimedData.getTimedDataSeries()) {
			final Map<String, Object> values = timedData.getValues();
			final String intentName = (String) values.get("name");

			//get the failure or fallback topic
			final Optional<Topic> topicOpt = getTopicByCode("FAILURE", criteria.getBotId());
			if (topicOpt.isPresent()) {

				final Topic topic = topicOpt.get();
				final SentenseDetail newSentenseDetail = new SentenseDetail();
				newSentenseDetail.setDate(timedData.getTime());
				newSentenseDetail.setMessageId((String) values.get("messageId"));
				newSentenseDetail.setText((String) values.get("text"));
				newSentenseDetail.setIntentRasa(intentName);
				newSentenseDetail.setConfidence(BigDecimal.valueOf((Double) values.get("confidence")));
				newSentenseDetail.setTopId(topic.getTopId());

				retour.add(newSentenseDetail);
			}
		}

		return retour;
	}

	public DtList<TopIntent> getTopIntents(final StatCriteria criteria) {
		// get data from influxdb
		final TabularDatas tabularDatas = timeSeriesManager.getTabularData(influxDbName, Arrays.asList("name:count"),
				getDataFilter(criteria)
						.withAdditionalWhereClause("isFallback = 0")
						.build(),
				getTimeFilter(criteria),
				"name");

		// build DtList from InfluxDb data
		final DtList<TopIntent> retour = new DtList<>(TopIntent.class);
		for (final TabularDataSerie tabularData : tabularDatas.getTabularDataSeries()) {
			final Map<String, Object> values = tabularData.getValues();
			final String intentName = (String) values.get("name");
			final Optional<Topic> topicOpt = getTopicByCode(intentName, criteria.getBotId());
			//Don't do anythings if intentName is null or intentName is a technical topic (start, end, fallback)
			if (intentName != null && !intentName.startsWith("!") && topicOpt.isPresent()) {
				final Topic topic = topicOpt.get();
				final TopIntent topIntent = new TopIntent();
				topIntent.setCount(((Double) values.get("name:count")).longValue());
				topIntent.setIntentRasa(topic.getTitle());
				topIntent.setTopId(topic.getTopId());

				retour.add(topIntent);
			}
		}

		return retour;
	}

	public DtList<SentenseDetail> getKnownSentensesDetail(final StatCriteria criteria, final String intentRasa) {
		// get data from influxdb
		final TimedDatas tabularTimedData = timeSeriesManager.getFlatTabularTimedData(influxDbName, Arrays.asList("messageId", "text", "name", "confidence"),
				getDataFilter(criteria)
						.addFilter("type", RasaTypeAction.MESSAGE.name())
						.addFilter("name", intentRasa)
						.build(),
				getTimeFilter(criteria),
				Optional.of(5000L));

		// build DtList from InfluxDb data
		final DtList<SentenseDetail> retour = new DtList<>(SentenseDetail.class);
		for (final TimedDataSerie timedData : tabularTimedData.getTimedDataSeries()) {
			final Map<String, Object> values = timedData.getValues();
			final Long smtId = extractSmtIdFromIntentName(intentRasa);

			final SentenseDetail newSentenseDetail = new SentenseDetail();
			newSentenseDetail.setDate(timedData.getTime());
			newSentenseDetail.setMessageId((String) values.get("messageId"));
			newSentenseDetail.setText((String) values.get("text"));
			newSentenseDetail.setIntentRasa(intentRasa);
			newSentenseDetail.setConfidence(BigDecimal.valueOf((Double) values.get("confidence")));
			newSentenseDetail.setTopId(smtId);

			retour.add(newSentenseDetail);
		}

		return retour;
	}

	private Long extractSmtIdFromIntentName(final String intentName) {
		final String[] intentSplit = intentName.split("_");
		if (intentSplit.length < 2) {
			return null;
		}
		try {
			// try to extract the ID from the name
			return Long.valueOf(intentSplit[1]);
		} catch (final NumberFormatException e) {
			// fail silently, unknown intent
		}
		return null;
	}

	private Optional<Topic> getTopicByCode(final String intentName, final Long botId) {
		return topicServices.getTopicByCode(intentName, botId);
	}

	private DataFilterBuilder getDataFilter(final StatCriteria criteria) {
		final DataFilterBuilder dataFilterBuilder = DataFilter.builder("chatbotmessages");
		if (criteria.getBotId() != null) {
			dataFilterBuilder.addFilter("botId", criteria.getBotId().toString());
			if (criteria.getNodId() != null) {
				dataFilterBuilder.addFilter("nodId", criteria.getNodId().toString());
			}
		}
		return dataFilterBuilder;
	}

	private TimeFilter getTimeFilter(final StatCriteria criteria) {
		final TimeOption timeOption = TimeOption.valueOf(criteria.getTimeOption());
		final String now = '\'' + Instant.now().toString() + '\'';

		return TimeFilter.builder(now + " - " + timeOption.getRange(), now).withTimeDim(timeOption.getGrain()).build();
	}

	public TimedDatas getTopicStats(final StatCriteria criteria) {
		return timeSeriesManager.getFlatTabularTimedData(influxDbName, Arrays.asList("codeTopic", "name"),
				getDataFilter(criteria).build(),
				getTimeFilter(criteria),
				Optional.empty());

	}

	public List<String> getDistinctCodeByTimeDatas(final StatCriteria criteria) {
		final TimedDatas timedData = getTopicStats(criteria);
		final List<String> resultTimedData = new ArrayList<String>();
		for (TimedDataSerie serie : timedData.getTimedDataSeries()) {
			final Map<String, Object> values = serie.getValues();
			for (Entry<String, Object> value : values.entrySet()) {
				if (!resultTimedData.contains(value.getValue().toString())) {
					resultTimedData.add(value.getValue().toString());
				}
			}
		}
		return resultTimedData;
	}

}
