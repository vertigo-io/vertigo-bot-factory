package io.vertigo.chatbot.designer.builder.services;

import io.vertigo.chatbot.commons.dao.JiraFieldDAO;
import io.vertigo.chatbot.commons.domain.JiraField;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;

import javax.inject.Inject;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
public class JiraFieldService implements Component {

	@Inject
	private JiraFieldDAO jiraFieldDAO;

	public JiraField findById(final String id) {
		return jiraFieldDAO.get(id);
	}

	public DtList<JiraField> findAll() {
		return jiraFieldDAO.findAll(Criterions.alwaysTrue(), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}
}
