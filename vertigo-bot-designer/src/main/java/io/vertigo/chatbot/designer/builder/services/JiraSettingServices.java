package io.vertigo.chatbot.designer.builder.services;

import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.JiraSettingDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.JiraSetting;
import io.vertigo.chatbot.commons.domain.JiraSettingExport;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
public class JiraSettingServices implements Component, Activeable {

	@Inject
	private JiraSettingDAO jiraSettingDAO;

	@Inject
	private PasswordEncryptionServices passwordEncryptionServices;

	@Override
	public void start() {
		//For migration purposes only
		//TODO remove when migration in done
		jiraSettingDAO.findAll(Criterions.alwaysTrue(), DtListState.of(MAX_ELEMENTS_PLUS_ONE)).forEach(jiraSetting -> {
			try {
				passwordEncryptionServices.decryptPassword(jiraSetting.getPassword());
			} catch (final IllegalArgumentException e) {
				jiraSetting.setPassword(passwordEncryptionServices.encryptPassword(jiraSetting.getPassword()));
				jiraSettingDAO.save(jiraSetting);
			}
		});
	}

	public JiraSetting findById(final long id) {
		return jiraSettingDAO.get(id);
	}

	@Secured("BotUser")
	public JiraSetting save (@SecuredOperation("botAdm") final Chatbot bot, final JiraSetting jiraSetting) {
		if (jiraSetting.getJirSetId() == null) {
			jiraSetting.setPassword(passwordEncryptionServices.encryptPassword(jiraSetting.getPassword()));
		} else {
			final JiraSetting oldJiraSetting = jiraSettingDAO.get(jiraSetting.getJirSetId());
			if (!oldJiraSetting.getPassword().equals(jiraSetting.getPassword())) {
				jiraSetting.setPassword(passwordEncryptionServices.encryptPassword(jiraSetting.getPassword()));
			}
		}
		return jiraSettingDAO.save(jiraSetting);
	}

	@Secured("BotUser")
	public void delete (@SecuredOperation("botAdm") final Chatbot bot, final long id) {
		jiraSettingDAO.delete(id);
	}

	public DtList<JiraSetting> findAllByBotId(@SecuredOperation("botContributor") final Chatbot bot) {
		return jiraSettingDAO.findAll(Criterions.isEqualTo(DtDefinitions.JiraSettingFields.botId, bot.getBotId()), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}

	public Optional<JiraSettingExport> exportJiraSetting(final long botId, final long nodId) {
		return jiraSettingDAO.findOptional(Criterions.isEqualTo(DtDefinitions.JiraSettingFields.botId, botId)
				.and(Criterions.isEqualTo(DtDefinitions.JiraSettingFields.nodId, nodId))).map(jiraSetting -> {
			final JiraSettingExport jiraSettingExport = new JiraSettingExport();
			jiraSettingExport.setUrl(jiraSetting.getUrl());
			jiraSettingExport.setLogin(jiraSetting.getLogin());
			jiraSettingExport.setPassword(jiraSetting.getPassword());
			jiraSettingExport.setProject(jiraSetting.getProject());
			return Optional.of(jiraSettingExport);
		}).orElseGet(Optional::empty);
	}

	@Override
	public void stop() {

	}
}
