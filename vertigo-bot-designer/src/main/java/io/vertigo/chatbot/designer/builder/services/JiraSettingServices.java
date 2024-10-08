package io.vertigo.chatbot.designer.builder.services;

import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.PasswordEncryptionServices;
import io.vertigo.chatbot.commons.dao.JiraSettingDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.JiraSetting;
import io.vertigo.chatbot.commons.domain.JiraSettingExport;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.util.VCollectors;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
public class JiraSettingServices implements Component {

	@Inject
	private JiraSettingDAO jiraSettingDAO;

	@Inject
	private PasswordEncryptionServices passwordEncryptionServices;

	public JiraSetting findById(final long id) {
		return jiraSettingDAO.get(id);
	}

	@Secured("BotUser")
	public JiraSetting save (@SecuredOperation("botAdm") final Chatbot bot, final JiraSetting jiraSetting) {
		if (jiraSetting.getPassword() != null && !jiraSetting.getPassword().isEmpty()) {
			jiraSetting.setPassword(passwordEncryptionServices.encryptPassword(jiraSetting.getPassword()));
		} else if (jiraSetting.getJirSetId() != null){
			jiraSetting.setPassword(jiraSettingDAO.get(jiraSetting.getJirSetId()).getPassword());
		}
		return jiraSettingDAO.save(jiraSetting);
	}

	@Secured("BotUser")
	public void delete (@SecuredOperation("botAdm") final Chatbot bot, final long id) {
		jiraSettingDAO.delete(id);
	}

	@Secured("BotUser")
	public void deleteAllByNodeId(@SecuredOperation("botAdm") final Chatbot bot, final long nodeId) {
		jiraSettingDAO.findAll(Criterions.isEqualTo(DtDefinitions.JiraSettingFields.nodId, nodeId),
				DtListState.of(MAX_ELEMENTS_PLUS_ONE)).forEach(jiraSetting -> this.delete(bot, jiraSetting.getJirSetId()));
	}

	@Secured("BotUser")
	public void deleteAllByBotId(@SecuredOperation("botAdm") final Chatbot bot) {
		findAllByBotId(bot).forEach(jiraSetting -> this.delete(bot, jiraSetting.getJirSetId()));
	}

	public DtList<JiraSetting> findAllByBotId(@SecuredOperation("botContributor") final Chatbot bot) {
		return jiraSettingDAO.findAll(Criterions.isEqualTo(DtDefinitions.JiraSettingFields.botId, bot.getBotId()), DtListState.of(MAX_ELEMENTS_PLUS_ONE))
				.stream().peek(jiraSetting -> jiraSetting.setPassword("")).collect(VCollectors.toDtList(JiraSetting.class));
	}

	public Optional<JiraSettingExport> exportJiraSetting(final long botId, final long nodId) {
		return jiraSettingDAO.findOptional(Criterions.isEqualTo(DtDefinitions.JiraSettingFields.botId, botId)
				.and(Criterions.isEqualTo(DtDefinitions.JiraSettingFields.nodId, nodId))).map(jiraSetting -> {
			final JiraSettingExport jiraSettingExport = new JiraSettingExport();
			jiraSettingExport.setUrl(jiraSetting.getUrl());
			jiraSettingExport.setLogin(jiraSetting.getLogin());
			jiraSettingExport.setPassword(jiraSetting.getPassword());
			jiraSettingExport.setProject(jiraSetting.getProject());
			jiraSettingExport.setNumberOfResults(jiraSetting.getNumberOfResults());
			return Optional.of(jiraSettingExport);
		}).orElseGet(Optional::empty);
	}
}
