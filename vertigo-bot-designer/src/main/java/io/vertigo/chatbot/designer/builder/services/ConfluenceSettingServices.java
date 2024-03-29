package io.vertigo.chatbot.designer.builder.services;

import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.PasswordEncryptionServices;
import io.vertigo.chatbot.commons.dao.ConfluenceSettingDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ConfluenceSetting;
import io.vertigo.chatbot.commons.domain.ConfluenceSettingExport;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.util.VCollectors;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
public class ConfluenceSettingServices implements Component {

	@Inject
	private ConfluenceSettingDAO confluenceSettingDAO;

	@Inject
	private PasswordEncryptionServices passwordEncryptionServices;

	public ConfluenceSetting findById(final long id) {
		return confluenceSettingDAO.get(id);
	}

	@Secured("BotUser")
	public ConfluenceSetting save (@SecuredOperation("botAdm") final Chatbot bot, final ConfluenceSetting confluenceSetting) {
		if (confluenceSetting.getPassword() != null && !confluenceSetting.getPassword().isEmpty()) {
			confluenceSetting.setPassword(passwordEncryptionServices.encryptPassword(confluenceSetting.getPassword()));
		} else if (confluenceSetting.getConSetId() != null){
			confluenceSetting.setPassword(confluenceSettingDAO.get(confluenceSetting.getConSetId()).getPassword());
		}
		return confluenceSettingDAO.save(confluenceSetting);
	}

	@Secured("BotUser")
	public void delete (@SecuredOperation("botAdm") final Chatbot bot, final long id) {
		confluenceSettingDAO.delete(id);
	}

	@Secured("BotUser")
	public DtList<ConfluenceSetting> findAllByBotId(@SecuredOperation("botContributor") final Chatbot bot) {
		return confluenceSettingDAO.findAll(Criterions.isEqualTo(DtDefinitions.ConfluenceSettingFields.botId, bot.getBotId()), DtListState.of(MAX_ELEMENTS_PLUS_ONE))
				.stream().peek(confluenceSetting -> confluenceSetting.setPassword("")).collect(VCollectors.toDtList(ConfluenceSetting.class));
	}

	@Secured("BotUser")
	public void deleteAllByNodeId(@SecuredOperation("botAdm") final Chatbot bot, final long nodeId) {
		confluenceSettingDAO.findAll(Criterions.isEqualTo(DtDefinitions.ConfluenceSettingFields.nodId, nodeId),
				DtListState.of(MAX_ELEMENTS_PLUS_ONE)).forEach(confluenceSetting -> this.delete(bot, confluenceSetting.getConSetId()));
	}

	@Secured("BotUser")
	public void deleteAllByBotId(@SecuredOperation("botAdm") final Chatbot bot) {
		this.findAllByBotId(bot).forEach(confluenceSetting -> this.delete(bot, confluenceSetting.getConSetId()));
	}

	public Optional<ConfluenceSettingExport> exportConfluenceSetting(final long botId, final long nodId) {
		return confluenceSettingDAO.findOptional(Criterions.isEqualTo(DtDefinitions.ConfluenceSettingFields.botId, botId)
				.and(Criterions.isEqualTo(DtDefinitions.ConfluenceSettingFields.nodId, nodId))).map(confluenceSetting -> {
			final ConfluenceSettingExport confluenceSettingExport = new ConfluenceSettingExport();
			confluenceSettingExport.setUrl(confluenceSetting.getUrl());
			confluenceSettingExport.setLogin(confluenceSetting.getLogin());
			confluenceSettingExport.setPassword(confluenceSetting.getPassword());
			confluenceSettingExport.setNumberOfResults(confluenceSetting.getNumberOfResults());
			return Optional.of(confluenceSettingExport);
		}).orElseGet(Optional::empty);
	}
}
