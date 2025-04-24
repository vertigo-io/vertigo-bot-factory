package io.vertigo.chatbot.designer.builder.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.dao.UnknownSentenceDetailDAO;
import io.vertigo.chatbot.commons.domain.UnknownSentenceDetail;
import io.vertigo.chatbot.commons.domain.UnknownSentenceStatusEnum;
import io.vertigo.chatbot.commons.influxDb.TimeSerieServices;
import io.vertigo.chatbot.designer.domain.analytics.StatCriteria;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.database.timeseries.TimedDataSerie;
import io.vertigo.database.timeseries.TimedDatas;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
public class UnknownSentencesServices implements Component {

	@Inject
	private UnknownSentenceDetailDAO unknownSentenceDetailDAO;

	@Inject
	private TimeSerieServices timeSerieServices;

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
		return findAllByBotId(botId);
	}

	public UnknownSentenceDetail save(final UnknownSentenceDetail unknownSentenceDetail) {
		return unknownSentenceDetailDAO.save(unknownSentenceDetail);
	}

	public DtList<UnknownSentenceDetail> findAllByBotId(final Long botId) {
		return unknownSentenceDetailDAO.findAll(Criterions.isEqualTo(DtDefinitions.UnknownSentenceDetailFields.botId, botId),
				DtListState.of(MAX_ELEMENTS_PLUS_ONE, 0, DtDefinitions.UnknownSentenceDetailFields.date.name(), true));
	}

	public DtList<UnknownSentenceDetail> findAllByBotIdWithoutLimit(final Long botId) {
		return unknownSentenceDetailDAO.findAll(Criterions.isEqualTo(DtDefinitions.UnknownSentenceDetailFields.botId, botId),
				DtListState.of(null));
	}

	public void deleteAllByBotId(final long botId) {
		findAllByBotIdWithoutLimit(botId).forEach(unknownSentenceDetail -> delete(unknownSentenceDetail.getUnkSeId()));
	}


	public void saveLatestUnknownSentences(final long botId) {
		final StatCriteria unknownSentencesCriteria = new StatCriteria();
		unknownSentencesCriteria.setBotId(botId);
		unknownSentencesCriteria.setFromInstant(Instant.now().minus(30, ChronoUnit.DAYS));

		unknownSentenceDetailDAO.findLatestUnknownSentence(botId)
				.ifPresent(latestUnknownSentence -> unknownSentencesCriteria.setFromInstant(latestUnknownSentence.getDate().plusNanos(1)));

		final TimedDatas tabularTimedData = timeSerieServices.getUnrecognizedSentences(unknownSentencesCriteria);

		for (final TimedDataSerie timedData : tabularTimedData.timedDataSeries()) {
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
	}
}
