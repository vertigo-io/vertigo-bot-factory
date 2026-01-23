package io.vertigo.chatbot.designer.builder.services;

import io.vertigo.chatbot.commons.dao.JiraCustomFieldTypeDAO;
import io.vertigo.chatbot.commons.domain.JiraCustomFieldType;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;

import javax.inject.Inject;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
public class JiraCustomFieldTypeService implements Component {

	@Inject
	private JiraCustomFieldTypeDAO jiraCustomFieldTypeDAO;

	public DtList<JiraCustomFieldType> findAll() {
		return jiraCustomFieldTypeDAO.findAll(Criterions.alwaysTrue(), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}
}
