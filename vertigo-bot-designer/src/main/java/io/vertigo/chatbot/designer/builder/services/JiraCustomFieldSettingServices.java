package io.vertigo.chatbot.designer.builder.services;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.JiraCustomFieldSettingDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.JiraCustomFieldSetting;
import io.vertigo.chatbot.commons.domain.JiraCustomFieldSettingExport;
import io.vertigo.chatbot.commons.domain.JiraValueList;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;
import io.vertigo.datamodel.data.util.VCollectors;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
@Secured("BotUser")
public class JiraCustomFieldSettingServices implements Component {

	@Inject
	private JiraCustomFieldSettingDAO jiraCustomFieldSettingDAO;

	@Inject
	private JiraValueListServices jiraValueListServices;

	/**
	 * Recherche un setting par ID et vérifie qu'il appartient au bot spécifié.
	 * Cette méthode prévient les attaques IDOR en validant l'appartenance.
	 * @param id l'ID du setting
	 * @param bot le bot propriétaire attendu
	 * @return le setting si trouvé ET appartenant au bot
	 */
	public Optional<JiraCustomFieldSetting> findOptionalByIdAndBot(final long id, final Chatbot bot) {
		return jiraCustomFieldSettingDAO.findOptional(
				Criterions.isEqualTo(DtDefinitions.JiraCustomFieldSettingFields.jirCusFieldSetId, id)
						.and(Criterions.isEqualTo(DtDefinitions.JiraCustomFieldSettingFields.botId, bot.getBotId())));
	}

	public JiraCustomFieldSetting save(@SecuredOperation("botAdm") final Chatbot bot, final JiraCustomFieldSetting jiraCustomFieldSetting) {
		// En cas d'update, vérifier que l'entité appartient bien au bot
		if (jiraCustomFieldSetting.getJirCusFieldSetId() != null) {
			findOptionalByIdAndBot(jiraCustomFieldSetting.getJirCusFieldSetId(), bot)
					.orElseThrow(() -> new SecurityException("Access denied: setting does not belong to this bot"));
		}
		if (jiraCustomFieldSetting.getJirValListId() != null) {
			jiraValueListServices.findOptionalByIdAndBot(jiraCustomFieldSetting.getJirValListId(), bot)
					.orElseThrow(() -> new SecurityException("Access denied: value list does not belong to this bot"));
		}
		jiraCustomFieldSetting.setBotId(bot.getBotId());
		return jiraCustomFieldSettingDAO.save(jiraCustomFieldSetting);
	}

	public void delete(@SecuredOperation("botAdm") final Chatbot bot, final long id) {
		// Vérifier que le setting appartient bien au bot avant suppression
		findOptionalByIdAndBot(id, bot)
				.ifPresent(setting -> jiraCustomFieldSettingDAO.delete(id));
	}

	public void deleteAllByBotId(@SecuredOperation("botAdm") final Chatbot bot) {
		findAllByBotId(bot).forEach(setting -> jiraCustomFieldSettingDAO.delete(setting.getJirCusFieldSetId()));
	}

	public DtList<JiraCustomFieldSetting> findAllByBotId(final Chatbot bot) {
		return jiraCustomFieldSettingDAO.findAll(
				Criterions.isEqualTo(DtDefinitions.JiraCustomFieldSettingFields.botId, bot.getBotId()),
				DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}

	public DtList<JiraCustomFieldSettingExport> exportCustomFields(final Chatbot bot) {
		final Map<Long, String> valueListCodes = jiraValueListServices.findAllByBotId(bot).stream()
				.collect(Collectors.toMap(JiraValueList::getJirValListId, JiraValueList::getCode));
		return findAllByBotId(bot).stream().map(setting -> {
			final JiraCustomFieldSettingExport export = new JiraCustomFieldSettingExport();
			export.setLabel(setting.getLabel());
			export.setFieldKey(setting.getFieldKey());
			export.setFieldType(setting.getJcfTypeCd());
			export.setEnabled(setting.getEnabled());
			export.setMandatory(setting.getMandatory());
			export.setValueListCode(valueListCodes.get(setting.getJirValListId()));
			return export;
		}).collect(VCollectors.toDtList(JiraCustomFieldSettingExport.class));
	}
}
