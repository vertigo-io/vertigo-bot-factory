package io.vertigo.chatbot.designer.builder.services;

import io.vertigo.chatbot.commons.dao.JiraFieldSettingDAO;
import io.vertigo.chatbot.commons.domain.JiraFieldEnum;
import io.vertigo.chatbot.commons.domain.JiraFieldSetting;
import io.vertigo.chatbot.commons.domain.JiraFieldSettingExport;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.util.VCollectors;

import javax.inject.Inject;
import java.util.Optional;

@Transactional
public class JiraFieldSettingServices implements Component {

	@Inject
	private JiraFieldSettingDAO jiraFieldSettingDAO;

	@Inject
	private JiraFieldService jiraFieldService;

	public JiraFieldSetting findById(final long id) {
		return jiraFieldSettingDAO.get(id);
	}

	public JiraFieldSetting save (final JiraFieldSetting jiraFieldSetting) {
		return jiraFieldSettingDAO.save(jiraFieldSetting);
	}

	public Optional<JiraFieldSetting> findByBotIdAndFieldName(final long botId, final String fieldName) {
		return jiraFieldSettingDAO.findOptional(Criterions.isEqualTo(DtDefinitions.JiraFieldSettingFields.botId, botId)
				.and(Criterions.isEqualTo(DtDefinitions.JiraFieldSettingFields.jirFieldCd, fieldName)));
	}

	public void delete (final long id) {
		jiraFieldSettingDAO.delete(id);
	}

	public void deleteAllByBotId(final long botId) {
		findAllByBotId(botId).forEach(jiraFieldSetting -> delete(jiraFieldSetting.getJirFieldSetId()));
	}

	public DtList<JiraFieldSetting> findAllByBotId(final long botId) {
		return jiraFieldService.findAll().stream().map(value -> findByBotIdAndFieldName(botId, value.getJirFieldCd()).orElseGet(() -> {
			JiraFieldSetting jiraFieldSetting = new JiraFieldSetting();
			jiraFieldSetting.setBotId(botId);
			jiraFieldSetting.setJirFieldCd(value.getJirFieldCd());
			if (value.getJirFieldCd().equals(JiraFieldEnum.SUMMARY.name())
					|| value.getJirFieldCd().equals(JiraFieldEnum.DESCRIPTION.name())
					|| value.getJirFieldCd().equals(JiraFieldEnum.TYPE.name())) {
				jiraFieldSetting.setEnabled(true);
				jiraFieldSetting.setMandatory(true);
			} else {
				jiraFieldSetting.setEnabled(false);
				jiraFieldSetting.setMandatory(false);
			}
			return jiraFieldSettingDAO.save(jiraFieldSetting);
		})).collect(VCollectors.toDtList(JiraFieldSetting.class));
	}

	public DtList<JiraFieldSettingExport> exportJiraSetting(final long botId) {
		return findAllByBotId(botId).stream().map(jiraFieldSetting -> {
			JiraFieldSettingExport jiraFieldSettingExport = new JiraFieldSettingExport();
			jiraFieldSetting.jiraField().load();
			jiraFieldSettingExport.setFieldKey(jiraFieldSetting.jiraField().get().getJiraId());
			jiraFieldSettingExport.setEnabled(jiraFieldSetting.getEnabled());
			jiraFieldSettingExport.setMandatory(jiraFieldSetting.getMandatory());
			return jiraFieldSettingExport;
		}).collect(VCollectors.toDtList(JiraFieldSettingExport.class));
	}
}
