package io.vertigo.chatbot.designer.analytics.services;

import io.vertigo.chatbot.designer.dao.topic.export.TypeBotExportDAO;
import io.vertigo.chatbot.designer.domain.topic.export.TypeBotExport;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

import javax.inject.Inject;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
public class TypeBotExportServices implements Component {

	@Inject
	private TypeBotExportDAO typeBotExportDAO;

	public DtList<TypeBotExport> getAllTypeBotExport() {
		return typeBotExportDAO.findAll(Criterions.alwaysTrue(), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}
}
