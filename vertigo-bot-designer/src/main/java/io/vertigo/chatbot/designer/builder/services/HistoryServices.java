package io.vertigo.chatbot.designer.builder.services;

import java.time.Instant;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.dao.HistoryDAO;
import io.vertigo.chatbot.designer.domain.History;
import io.vertigo.chatbot.designer.domain.HistoryActionEnum;
import io.vertigo.chatbot.designer.domain.HistoryCriteria;
import io.vertigo.chatbot.designer.utils.UserSessionUtils;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
public class HistoryServices implements Component {

	@Inject
	private HistoryDAO historyDAO;

	public History findById(final Long id) {
		return historyDAO.get(id);
	}

	public DtList<History> findAllByBotId(final Long botId) {
		return historyDAO.findAll(Criterions.isEqualTo(DtDefinitions.HistoryFields.botId, botId),
				DtListState.of(MAX_ELEMENTS_PLUS_ONE, 0, DtDefinitions.HistoryFields.date.name(), true));
	}

	public DtList<History> findAllByBotIdWithLimit(final Long botId) {
		return historyDAO.findAll(Criterions.isEqualTo(DtDefinitions.HistoryFields.botId, botId), DtListState.of(null));
	}

	public History save(final History history) {
		return historyDAO.save(history);
	}

	public void deleteAllByBotId(final Long botId) {
		findAllByBotIdWithLimit(botId).forEach(history -> historyDAO.delete(history.getHistId()));
	}

	public History record(final Chatbot bot, final HistoryActionEnum action, final String className, final String message) {
		final History history = new History();
		history.setDate(Instant.now());
		history.setBotId(bot.getBotId());
		history.setUserName(UserSessionUtils.getLoggedPerson().getName());
		history.setHacCd(action.name());
		history.setMessage(message);
		history.setClassName(className);
		return save(history);
	}

	public DtList<History> findByCriteria(final Long botId, final HistoryCriteria criteria) {
		return historyDAO.searchHistory(botId, criteria);
	}
}
