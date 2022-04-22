package io.vertigo.chatbot.designer.builder.services;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

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
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

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

		final StatCriteria unknownSentencesCriteria = new StatCriteria();
		unknownSentencesCriteria.setBotId(botId);

		unknownSentenceDetailDAO.findLatestUnknownSentence(botId)
				.ifPresent(latestUnknownSentence -> unknownSentencesCriteria.setFromInstant(latestUnknownSentence.getDate()));

		final TimedDatas tabularTimedData = timeSerieServices.getUnrecognizedSentences(unknownSentencesCriteria);

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
		return

		findAllByBotId(botId);
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

}
