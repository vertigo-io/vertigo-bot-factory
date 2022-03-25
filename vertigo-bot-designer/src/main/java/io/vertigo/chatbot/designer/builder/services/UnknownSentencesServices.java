package io.vertigo.chatbot.designer.builder.services;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Map;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.dao.UnknownSentenceDetailDAO;
import io.vertigo.chatbot.commons.domain.UnknownSentenceDetail;
import io.vertigo.chatbot.commons.domain.UnknownSentenceStatusEnum;
import io.vertigo.chatbot.commons.domain.UnknownSentencesCriteria;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.param.ParamManager;
import io.vertigo.database.timeseries.DataFilter;
import io.vertigo.database.timeseries.DataFilterBuilder;
import io.vertigo.database.timeseries.TimeFilter;
import io.vertigo.database.timeseries.TimedDataSerie;
import io.vertigo.database.timeseries.TimedDatas;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

@Transactional
public class UnknownSentencesServices implements Component, Activeable {

	private String influxDbName;

	@Inject
	private UnknownSentenceDetailDAO unknownSentenceDetailDAO;

	//@Inject
	//private TimeSeriesManager timeSeriesManager;

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

	public UnknownSentenceDetail findById(final Long unknownSentenceId) {
		return unknownSentenceDetailDAO.get(unknownSentenceId);
	}

	public void delete(final long unknownSentenceId) {
		unknownSentenceDetailDAO.delete(unknownSentenceId);
	}

	public UnknownSentenceDetail updateStatus(final UnknownSentenceDetail unknownSentenceDetail, final UnknownSentenceStatusEnum status) {
		unknownSentenceDetail.setStatus(status.name());
		return save(unknownSentenceDetail);
	}

	public DtList<UnknownSentenceDetail> findUnknownSentences(final Long botId) {

		final UnknownSentencesCriteria unknownSentencesCriteria = new UnknownSentencesCriteria();
		unknownSentencesCriteria.setBotId(botId);

		final UnknownSentenceDetail latestUnknownSentenceDetail = findLatestUnknownSentence(botId);
		if (latestUnknownSentenceDetail != null) {
			unknownSentencesCriteria.setFromDate(latestUnknownSentenceDetail.getDate());
		}

		final TimedDatas tabularTimedData = getSentenceDetails(unknownSentencesCriteria);

		for (final TimedDataSerie timedData : tabularTimedData.getTimedDataSeries()) {
			final Map<String, Object> values = timedData.getValues();

			final UnknownSentenceDetail unknownSentenceDetail = new UnknownSentenceDetail();
			unknownSentenceDetail.setUnkSeId(null);
			unknownSentenceDetail.setBotId(botId);
			unknownSentenceDetail.setDate(timedData.getTime());
			unknownSentenceDetail.setText((String) values.get("text"));
			unknownSentenceDetail.setModelName((String) values.get("modelName"));
			unknownSentenceDetail.setStatus(UnknownSentenceStatusEnum.TO_TREAT.name());
			save(unknownSentenceDetail);

		}
		return findAllByBotId(botId);
	}

	private UnknownSentenceDetail findLatestUnknownSentence(final Long botId) {
		return unknownSentenceDetailDAO.findLatestUnknownSentence(botId).orElse(null);
	}

	public UnknownSentenceDetail save(final UnknownSentenceDetail unknownSentenceDetail) {
		return unknownSentenceDetailDAO.save(unknownSentenceDetail);
	}

	private DtList<UnknownSentenceDetail> findAllByBotId(final Long botId) {
		return unknownSentenceDetailDAO.findAll(Criterions.isEqualTo(DtDefinitions.UnknownSentenceDetailFields.botId, botId), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}

	public void deleteAllByBotId(final long botId) {
		findAllByBotId(botId).forEach(unknownSentenceDetail -> delete(unknownSentenceDetail.getUnkSeId()));
	}

	/**
	 * Get all messages unrecognized
	 *
	 * @param unknownSentencesCriteria unknownSentencesCriteria
	 * @return all the messages unrecognized
	 */
	public TimedDatas getSentenceDetails(final UnknownSentencesCriteria unknownSentencesCriteria) {
		return null;
		//return timeSeriesManager.getFlatTabularTimedData(influxDbName, Arrays.asList("text", "name", "confidence", "modelName"),
		//		getDataFilter(unknownSentencesCriteria, AnalyticsServicesUtils.MESSAGES_MSRMT).withAdditionalWhereClause("isFallback = 1").build(),
		//		getTimeFilter(unknownSentencesCriteria),
		//		Optional.empty());
	}

	private DataFilterBuilder getDataFilter(final UnknownSentencesCriteria unknownSentencesCriteria, final String measurement) {
		final DataFilterBuilder dataFilterBuilder = DataFilter.builder(measurement);
		if (unknownSentencesCriteria.getBotId() != null) {
			dataFilterBuilder.addFilter("botId", unknownSentencesCriteria.getBotId().toString());
		}
		return dataFilterBuilder;
	}

	private TimeFilter getTimeFilter(final UnknownSentencesCriteria unknownSentencesCriteria) {
		Instant toDate = Instant.now();
		if (unknownSentencesCriteria.getToDate() != null) {
			toDate = unknownSentencesCriteria.getToDate();
		}
		Instant fromDate = ZonedDateTime.now().minusYears(1).toInstant();
		if (unknownSentencesCriteria.getFromDate() != null) {
			fromDate = unknownSentencesCriteria.getFromDate();
		}
		final String toDateString = '\'' + toDate.toString() + '\'';
		final String fromDateString = '\'' + fromDate.toString() + '\'';

		return TimeFilter.builder(fromDateString, toDateString).build();

	}

}
