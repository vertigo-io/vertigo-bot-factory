package io.vertigo.chatbot.designer.builder.services;

import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.JiraFieldSettingDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.JiraFieldEnum;
import io.vertigo.chatbot.commons.domain.JiraFieldSetting;
import io.vertigo.chatbot.commons.domain.JiraFieldSettingExport;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.util.VCollectors;

@Transactional
@Secured("BotUser")
public class JiraFieldSettingServices implements Component {

	@Inject
	private JiraFieldSettingDAO jiraFieldSettingDAO;

	@Inject
	private JiraFieldService jiraFieldService;

	public JiraFieldSetting findById(final long id) {
		return jiraFieldSettingDAO.get(id);
	}

	public JiraFieldSetting save (@SecuredOperation("botAdm") final Chatbot bot, final JiraFieldSetting jiraFieldSetting) {
		return jiraFieldSettingDAO.save(jiraFieldSetting);
	}

	public Optional<JiraFieldSetting> findByBotIdAndFieldName(@SecuredOperation("botContributor") final Chatbot bot, final String fieldName) {
		return jiraFieldSettingDAO.findOptional(Criterions.isEqualTo(DtDefinitions.JiraFieldSettingFields.botId, bot.getBotId())
				.and(Criterions.isEqualTo(DtDefinitions.JiraFieldSettingFields.jirFieldCd, fieldName)));
	}

	public void delete (final long id) {
		jiraFieldSettingDAO.delete(id);
	}

	public void deleteAllByBotId(@SecuredOperation("botAdm") final Chatbot bot) {
		findAllByBotId(bot).forEach(jiraFieldSetting -> delete(jiraFieldSetting.getJirFieldSetId()));
	}

	public DtList<JiraFieldSetting> findAllByBotId(final Chatbot bot) {
		return jiraFieldService.findAll().stream().map(value -> findByBotIdAndFieldName(bot, value.getJirFieldCd()).orElseGet(() -> {
			final JiraFieldSetting jiraFieldSetting = new JiraFieldSetting();
			jiraFieldSetting.setBotId(bot.getBotId());
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

	public DtList<JiraFieldSettingExport> exportJiraSetting(final Chatbot bot) {
		return findAllByBotId(bot).stream().map(jiraFieldSetting -> {
			final JiraFieldSettingExport jiraFieldSettingExport = new JiraFieldSettingExport();
			jiraFieldSetting.jiraField().load();
			jiraFieldSettingExport.setFieldKey(jiraFieldSetting.jiraField().get().getJiraId());
			jiraFieldSettingExport.setEnabled(jiraFieldSetting.getEnabled());
			jiraFieldSettingExport.setMandatory(jiraFieldSetting.getMandatory());
			return jiraFieldSettingExport;
		}).collect(VCollectors.toDtList(JiraFieldSettingExport.class));
	}
}
