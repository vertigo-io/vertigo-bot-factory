package io.vertigo.chatbot.designer.builder.services.bot;

import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.LogsUtils;
import io.vertigo.chatbot.commons.dao.ContextValueDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ContextValue;
import io.vertigo.chatbot.commons.multilingual.context.ContextValueMultilingualResources;
import io.vertigo.chatbot.designer.builder.services.HistoryServices;
import io.vertigo.chatbot.designer.builder.services.IRecordable;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.designer.domain.History;
import io.vertigo.chatbot.designer.domain.HistoryActionEnum;
import io.vertigo.chatbot.domain.DtDefinitions.ContextValueFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.vega.engines.webservice.json.JsonEngine;

import javax.inject.Inject;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.HashMap;
import java.util.Map;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;


@Transactional
public class ContextValueServices implements Component, IRecordable<ContextValue> {

	@Inject
	private ContextValueDAO contextValueDAO;
	@Inject
	private JsonEngine jsonEngine;
	@Inject
	private NodeServices nodeServices;

	@Inject
	private HistoryServices historyServices;

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
		checkPatternKey(contextValue.getXpath());
		HistoryActionEnum action;
		if (contextValue.getCvaId()!= null) {
			action = HistoryActionEnum.UPDATED;
			ContextValue oldContextValue = contextValueDAO.get(contextValue.getCvaId());
			if (!oldContextValue.getLabel().equals(contextValue.getLabel()) || !oldContextValue.getXpath().equals(contextValue.getXpath())) {
				nodeServices.updateNodes(bot);
			}
		} else {
			action = HistoryActionEnum.ADDED;
			nodeServices.updateNodes(bot);
		}
		ContextValue savedContextValue = contextValueDAO.save(contextValue);
		record(bot, savedContextValue, action);
		return savedContextValue;

	}

	/**
	 * delete contextValue
	 *
	 * @param bot
	 * @param cvaId
	 */
	public void deleteContextValue(@SecuredOperation("botAdm") final Chatbot bot, final Long cvaId) {
		ContextValue contextValue = contextValueDAO.get(cvaId);
		contextValueDAO.delete(cvaId);
		nodeServices.updateNodes(bot);
		record(bot, contextValue, HistoryActionEnum.DELETED);
	}

	public void delete(final long contextValueId) {
		contextValueDAO.delete(contextValueId);
	}

	public DtList<ContextValue> getAllContextValueByBotId(final Long botId) {
		return contextValueDAO.findAll(Criterions.isEqualTo(ContextValueFields.botId, botId), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}

	public void deleteAllByBotId(final long botId) {
		getAllContextValueByBotId(botId).forEach(contextValue -> delete(contextValue.getCvaId()));
	}

	public ContextValue getNewContextValue(@SecuredOperation("botAdm") final Chatbot bot) {
		final ContextValue contextValue = new ContextValue();
		contextValue.setBotId(bot.getBotId());
		return contextValue;
	}

	private static void checkPatternKey(final String key) {

		if (key == null) {
			throw new VUserException(ContextValueMultilingualResources.XPATH_PATTERN_NULL_ERROR);
		}

		XPathFactory factory = XPathFactory.newInstance();
		XPath xPath = factory.newXPath();
		try {
			xPath.compile(key);
		} catch (XPathExpressionException e) {
			throw new VUserException(ContextValueMultilingualResources.XPATH_PATTERN_DIGIT_ERROR);
		}

	}

	public String exportContextValuesToMapByBot(@SecuredOperation("botAdm") final Chatbot bot, final StringBuilder logs) {
		LogsUtils.addLogs(logs, "Export Map Context : ");
		try {
			final DtList<ContextValue> list = getAllContextValueByBotId(bot.getBotId());

			final Map<String, String> map = new HashMap<>();
			for (final ContextValue contextValue : list) {
				map.put(contextValue.getLabel(), contextValue.getXpath());
			}
			if (!map.containsKey("url")) {
				map.put("url", "");
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

	@Override
	public History record(Chatbot bot, ContextValue contextValue, HistoryActionEnum action) {
		return historyServices.record(bot, action, contextValue.getClass().getSimpleName(), contextValue.getLabel());
	}
}
