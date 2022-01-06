package io.vertigo.chatbot.designer.builder.controllers.bot;

import io.vertigo.chatbot.commons.dao.UnknownSentenceStatusDAO;
import io.vertigo.chatbot.commons.domain.UnknownSentenceStatus;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

import javax.inject.Inject;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
public class UnknownSentenceStatusServices implements Component {

	@Inject
	private UnknownSentenceStatusDAO unknownSentenceStatusDAO;

	public UnknownSentenceStatus findStatusById(final String id) {
		return unknownSentenceStatusDAO.get(id);
	}

	public DtList<UnknownSentenceStatus> findAll() {
		return unknownSentenceStatusDAO.findAll(Criterions.alwaysTrue(), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}

}
