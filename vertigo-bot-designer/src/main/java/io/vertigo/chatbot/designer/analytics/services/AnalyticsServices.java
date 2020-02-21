package io.vertigo.chatbot.designer.analytics.services;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.RasaTypeAction;
import io.vertigo.chatbot.designer.domain.StatCriteria;
import io.vertigo.chatbot.designer.domain.TopIntent;
import io.vertigo.chatbot.designer.domain.UnknownSentense;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.component.Activeable;
import io.vertigo.core.component.Component;
import io.vertigo.core.param.ParamManager;
import io.vertigo.database.timeseries.DataFilter;
import io.vertigo.database.timeseries.DataFilterBuilder;
import io.vertigo.database.timeseries.TabularDataSerie;
import io.vertigo.database.timeseries.TabularDatas;
import io.vertigo.database.timeseries.TimeFilter;
import io.vertigo.database.timeseries.TimeSeriesDataBaseManager;
import io.vertigo.database.timeseries.TimedDataSerie;
import io.vertigo.database.timeseries.TimedDatas;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.lang.Assertion;

@Transactional
public class AnalyticsServices implements Component, Activeable {

	@Inject
	private TimeSeriesDataBaseManager timeSeriesDataBaseManager;

	@Inject
	private ParamManager paramManager;

	private String influxDbName;

	@Override
	public void start() {
		influxDbName = paramManager.getParam("ANALYTICA_DBNAME").getValueAsString();
	}

	@Override
	public void stop() {
		// Nothing
	}

	public TimedDatas getSessionsStats(final StatCriteria criteria) {
		return timeSeriesDataBaseManager.getTimeSeries(influxDbName, Arrays.asList("isTypeOpen:sum"),
				getDataFilter(criteria).build(),
				getTimeFilter(criteria));
	}

	public TimedDatas getRequestStats(final StatCriteria criteria) {
		return timeSeriesDataBaseManager.getTimeSeries(influxDbName, Arrays.asList("name:count", "isFallback:sum"),
				getDataFilter(criteria).withAdditionalWhereClause("\"type\" <> '" + RasaTypeAction.OPEN + "'").build(),
				getTimeFilter(criteria));

	}

	public DtList<UnknownSentense> getUnknownSentenses(final StatCriteria criteria) {
		// get data from influxdb
		final TimedDatas tabularTimedData = timeSeriesDataBaseManager.getFlatTabularTimedData(influxDbName, Arrays.asList("messageId", "text", "name", "confidence"),
				getDataFilter(criteria).withAdditionalWhereClause("isFallback = 1").build(),
				getTimeFilter(criteria),
				Optional.empty());

		// build DtList from InfluxDb data
		final DtList<UnknownSentense> retour = new DtList<>(UnknownSentense.class);
		for (final TimedDataSerie timedData : tabularTimedData.getTimedDataSeries()) {
			final Map<String, Object> values = timedData.getValues();
			final String intentName = (String) values.get("name");
			final Long smtId = extractSmtIdFromIntentName(intentName);

			final UnknownSentense newUnknownSentense = new UnknownSentense();
			newUnknownSentense.setDate(timedData.getTime());
			newUnknownSentense.setMessageId((String) values.get("messageId"));
			newUnknownSentense.setText((String) values.get("text"));
			newUnknownSentense.setIntentRasa(intentName);
			newUnknownSentense.setConfidence(BigDecimal.valueOf((Double) values.get("confidence")));
			newUnknownSentense.setSmtId(smtId);

			retour.add(newUnknownSentense);
		}

		return retour;
	}

	public DtList<TopIntent> getTopIntents(final StatCriteria criteria) {
		// get data from influxdb
		final TabularDatas tabularDatas = timeSeriesDataBaseManager.getTabularData(influxDbName, Arrays.asList("name:count"),
				getDataFilter(criteria)
						.addFilter("type", RasaTypeAction.MESSAGE.name())
						.withAdditionalWhereClause("isFallback = 0")
						.build(),
				getTimeFilter(criteria),
				"name");

		// build DtList from InfluxDb data
		final DtList<TopIntent> retour = new DtList<>(TopIntent.class);
		for (final TabularDataSerie tabularData : tabularDatas.getTabularDataSeries()) {
			final Map<String, Object> values = tabularData.getValues();
			final String intentName = (String) values.get("name");
			final Long smtId = extractSmtIdFromIntentName(intentName);

			if (smtId != null) {
				final TopIntent topIntent = new TopIntent();
				topIntent.setCount(((Double) values.get("name:count")).longValue());
				topIntent.setIntentRasa(intentName);
				topIntent.setSmtId(smtId);

				retour.add(topIntent);
			}
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

	private TimedDatas mergeTimedDatas(final TimedDatas data, final TimedDatas data2, final TimedDatas... otherDatas) {
		Assertion.checkNotNull(data);
		Assertion.checkNotNull(data2);

		final TimedDatas newTimedDatas = new TimedDatas(new ArrayList<>(), new ArrayList<>());

		// Juste recopy first TimedDatas
		newTimedDatas.getSeriesNames().addAll(data.getSeriesNames());
		for (final TimedDataSerie timedDataSerie : data.getTimedDataSeries()) {
			final Map<String, Object> newMapValues = new HashMap<>();
			newMapValues.putAll(timedDataSerie.getValues());

			final TimedDataSerie newTimedDataSerie = new TimedDataSerie(timedDataSerie.getTime(), newMapValues);

			newTimedDatas.getTimedDataSeries().add(newTimedDataSerie);
		}

		// add every other timedDatas with consistency check
		addToTimedDatas(newTimedDatas, data2);
		for (final TimedDatas otherData : otherDatas) {
			addToTimedDatas(newTimedDatas, otherData);
		}

		return newTimedDatas;
	}

	private void addToTimedDatas(final TimedDatas data, final TimedDatas otherData) {
		if (otherData.getSeriesNames().isEmpty()) {
			return; // no data, no merge
		}

		Assertion.checkArgument(data.getTimedDataSeries().size() == otherData.getTimedDataSeries().size(), "Series haven't the same size");
		Assertion.checkArgument(otherData.getSeriesNames().stream().noneMatch(name -> data.getSeriesNames().contains(name)), "Duplicated series");

		data.getSeriesNames().addAll(otherData.getSeriesNames());

		int i = 0;
		for (final TimedDataSerie timedDataSerie : otherData.getTimedDataSeries()) {
			final TimedDataSerie curTimedDataSerie = data.getTimedDataSeries().get(i);

			Assertion.checkState(timedDataSerie.getTime().equals(curTimedDataSerie.getTime()), "Series are not time synchronous");

			curTimedDataSerie.getValues().putAll(timedDataSerie.getValues());

			i++;
		}

	}
}
