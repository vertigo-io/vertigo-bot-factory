package io.vertigo.chatbot.designer.builder.services;

import io.vertigo.chatbot.commons.dao.ConfluenceSettingDAO;
import io.vertigo.chatbot.commons.domain.ConfluenceSetting;
import io.vertigo.chatbot.commons.domain.ConfluenceSettingExport;
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
public class ConfluenceSettingServices implements Component {

	@Inject
	private ConfluenceSettingDAO confluenceSettingDAO;

	public ConfluenceSetting findById(final long id) {
		return confluenceSettingDAO.get(id);
	}

	public ConfluenceSetting save (ConfluenceSetting confluenceSetting) {
		return confluenceSettingDAO.save(confluenceSetting);
	}

	public void delete (final long id) {
		confluenceSettingDAO.delete(id);
	}

	public DtList<ConfluenceSetting> findAllByBotId(final long botId) {
		return confluenceSettingDAO.findAll(Criterions.isEqualTo(DtDefinitions.ConfluenceSettingFields.botId, botId), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}

	public Optional<ConfluenceSettingExport> exportConfluenceSetting(final long botId, final long nodId) {
		return confluenceSettingDAO.findOptional(Criterions.isEqualTo(DtDefinitions.ConfluenceSettingFields.botId, botId)
				.and(Criterions.isEqualTo(DtDefinitions.ConfluenceSettingFields.nodId, nodId))).map(confluenceSetting -> {
			ConfluenceSettingExport confluenceSettingExport = new ConfluenceSettingExport();
			confluenceSettingExport.setUrl(confluenceSetting.getUrl());
			confluenceSettingExport.setLogin(confluenceSetting.getLogin());
			confluenceSettingExport.setPassword(confluenceSetting.getPassword());
			confluenceSettingExport.setNumberOfResults(confluenceSetting.getNumberOfResults());
			return Optional.of(confluenceSettingExport);
		}).orElseGet(Optional::empty);
	}
}
