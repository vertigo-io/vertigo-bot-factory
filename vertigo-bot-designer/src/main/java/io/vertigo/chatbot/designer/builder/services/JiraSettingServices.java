package io.vertigo.chatbot.designer.builder.services;

import io.vertigo.chatbot.commons.dao.JiraSettingDAO;
import io.vertigo.chatbot.commons.domain.JiraSetting;
import io.vertigo.chatbot.commons.domain.JiraSettingExport;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

import javax.inject.Inject;
import java.util.Optional;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
public class JiraSettingServices implements Component {

	@Inject
	private JiraSettingDAO jiraSettingDAO;

	public JiraSetting findById(final long id) {
		return jiraSettingDAO.get(id);
	}

	public JiraSetting save (final JiraSetting jiraSetting) {
		return jiraSettingDAO.save(jiraSetting);
	}

	public void delete (final long id) {
		jiraSettingDAO.delete(id);
	}

	public DtList<JiraSetting> findAllByBotId(final long botId) {
		return jiraSettingDAO.findAll(Criterions.isEqualTo(DtDefinitions.JiraSettingFields.botId, botId), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}

	public Optional<JiraSettingExport> exportJiraSetting(final long botId, final long nodId) {
		return jiraSettingDAO.findOptional(Criterions.isEqualTo(DtDefinitions.JiraSettingFields.botId, botId)
				.and(Criterions.isEqualTo(DtDefinitions.JiraSettingFields.nodId, nodId))).map(jiraSetting -> {
			JiraSettingExport jiraSettingExport = new JiraSettingExport();
			jiraSettingExport.setUrl(jiraSetting.getUrl());
			jiraSettingExport.setLogin(jiraSetting.getLogin());
			jiraSettingExport.setPassword(jiraSetting.getPassword());
			jiraSettingExport.setProject(jiraSetting.getProject());
			return Optional.of(jiraSettingExport);
		}).orElseGet(Optional::empty);
	}
}
