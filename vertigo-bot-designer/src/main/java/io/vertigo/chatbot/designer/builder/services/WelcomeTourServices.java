package io.vertigo.chatbot.designer.builder.services;

import io.vertigo.chatbot.commons.LogsUtils;
import io.vertigo.chatbot.commons.dao.WelcomeTourDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.WelcomeTour;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.vega.engines.webservice.json.JsonEngine;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Transactional
public class WelcomeTourServices implements Component {

	@Inject
	private WelcomeTourDAO welcomeTourDAO;

	@Inject
	private JsonEngine jsonEngine;

	public WelcomeTour findById(final long id)  {
		return welcomeTourDAO.get(id);
	}

	public WelcomeTour save (final WelcomeTour welcomeTour) {
		return welcomeTourDAO.save(welcomeTour);
	}

	public void delete (final long id) {
		welcomeTourDAO.delete(id);
	}

	public DtList<WelcomeTour> findAllByBotId(final long botId) {
		return welcomeTourDAO.findAll(Criterions.isEqualTo(DtDefinitions.WelcomeTourFields.botId, botId), DtListState.of(1000));
	}

	public String exportBotWelcomeTours(final Chatbot bot, final StringBuilder logs) {
		LogsUtils.addLogs(logs, " Welcome tours export...");
		Map<String, String> welcomeTourMap = new HashMap<>();
		findAllByBotId(bot.getBotId()).forEach(welcomeTour -> welcomeTourMap.put(welcomeTour.getLabel(), welcomeTour.getTechnicalCode()));
		LogsUtils.logOK(logs);
		return jsonEngine.toJson(welcomeTourMap);
	}
}
