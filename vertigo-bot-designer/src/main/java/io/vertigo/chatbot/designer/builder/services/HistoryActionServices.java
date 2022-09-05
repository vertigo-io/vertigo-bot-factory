package io.vertigo.chatbot.designer.builder.services;

import io.vertigo.chatbot.designer.dao.HistoryActionDAO;
import io.vertigo.chatbot.designer.domain.HistoryAction;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

import javax.inject.Inject;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
public class HistoryActionServices implements Component {

	@Inject
	private HistoryActionDAO historyActionDAO;

	public HistoryAction findById(final String id) {
		return historyActionDAO.get(id);
	}

	public DtList<HistoryAction> findAll() {
		return historyActionDAO.findAll(Criterions.alwaysTrue(), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}
}
