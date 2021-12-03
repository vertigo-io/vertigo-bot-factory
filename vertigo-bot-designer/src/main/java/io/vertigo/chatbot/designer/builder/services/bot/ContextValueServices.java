package io.vertigo.chatbot.designer.builder.services.bot;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.LogsUtils;
import io.vertigo.chatbot.commons.dao.ContextValueDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ContextValue;
import io.vertigo.chatbot.commons.multilingual.context.ContextValueMultilingualResources;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.domain.DtDefinitions.ContextValueFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.vega.engines.webservice.json.JsonEngine;
import kotlin.text.Regex;

@Transactional
public class ContextValueServices implements Component {

	@Inject
	private ContextValueDAO contextValueDAO;
	@Inject
	private JsonEngine jsonEngine;
	@Inject
	private NodeServices nodeServices;

	/**
	 * get ContextValue by id
	 *
	 * @param id
	 * @return contextValue
	 */
	public ContextValue findContextValueById(final Long id) {
		return contextValueDAO.get(id);
	}

	/**
	 * Save contextValue
	 *
	 * @param contextValue
	 * @return contextValue
	 */
	public ContextValue save(@SecuredOperation("botAdm") final Chatbot bot, final ContextValue contextValue) {
		checkPatternKey(contextValue.getKey());
		if (contextValue.getCvaId()!= null) {
			ContextValue oldContextValue = contextValueDAO.get(contextValue.getCvaId());
			if (!oldContextValue.getKey().equals(contextValue.getKey()) || !oldContextValue.getLabel().equals(contextValue.getLabel())) {
				nodeServices.updateNodes(bot);
			}
		} else {
			nodeServices.updateNodes(bot);
		}

		return contextValueDAO.save(contextValue);

	}

	/**
	 * delete contextValue
	 *
	 * @param bot
	 * @param cvaId
	 */
	public void deleteContextValue(@SecuredOperation("botAdm") final Chatbot bot, final Long cvaId) {
		contextValueDAO.delete(cvaId);
		nodeServices.updateNodes(bot);
	}

	public DtList<ContextValue> getAllContextValueByBotId(final Long botId) {
		return contextValueDAO.findAll(Criterions.isEqualTo(ContextValueFields.botId, botId), DtListState.of(1000));
	}

	public ContextValue getNewContextValue(@SecuredOperation("botAdm") final Chatbot bot) {
		final ContextValue contextValue = new ContextValue();
		contextValue.setBotId(bot.getBotId());
		return contextValue;
	}

	private static void checkPatternKey(final String key) {
		// regex : letters, digits and "/"
		final Regex regex = new Regex("^[a-z0-9]+(\\/[a-z0-9]+)*$");

		if (key == null || !regex.matches(key)) {
			throw new VUserException(ContextValueMultilingualResources.KEY_PATTERN_DIGIT_ERROR);
		}

		if (key.length() > 10) {
			throw new VUserException(ContextValueMultilingualResources.KEY_PATTERN_LENGTH);
		}
	}

	public String exportContextValuesToMapByBot(@SecuredOperation("botAdm") final Chatbot bot, final StringBuilder logs) {
		LogsUtils.addLogs(logs, "Export Map Context : ");
		try {
			final DtList<ContextValue> list = getAllContextValueByBotId(bot.getBotId());

			final Map<String, String> map = new HashMap<>();
			for (final ContextValue contextValue : list) {
				map.put(contextValue.getLabel(), "/user/global/context/" + contextValue.getKey());
			}
			final String json = jsonEngine.toJson(map);

			LogsUtils.logOK(logs);
			LogsUtils.addLogs(logs, json);
			LogsUtils.breakLine(logs);
			LogsUtils.breakLine(logs);
			return json;
		} catch (final Exception e) {
			LogsUtils.logKO(logs);
			LogsUtils.addLogs(logs, e);
			return null;
		}

	}

}
