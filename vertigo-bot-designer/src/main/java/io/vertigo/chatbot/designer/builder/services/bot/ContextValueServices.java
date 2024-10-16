package io.vertigo.chatbot.designer.builder.services.bot;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.LogsUtils;
import io.vertigo.chatbot.commons.dao.ContextValueDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ContextValue;
import io.vertigo.chatbot.commons.multilingual.context.ContextValueMultilingualResources;
import io.vertigo.chatbot.designer.builder.services.DocumentaryResourceContextServices;
import io.vertigo.chatbot.designer.builder.services.HistoryServices;
import io.vertigo.chatbot.designer.builder.services.IRecordable;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.designer.builder.services.questionanswer.QuestionAnswerContextServices;
import io.vertigo.chatbot.designer.domain.History;
import io.vertigo.chatbot.designer.domain.HistoryActionEnum;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.chatbot.domain.DtDefinitions.ContextValueFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.vega.engines.webservice.json.JsonEngine;

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

	@Inject
	private DocumentaryResourceContextServices documentaryResourceContextServices;

	@Inject
	private QuestionAnswerContextServices questionAnswerContextServices;

	@Inject
	private ContextPossibleValueServices contextPossibleValueServices;

	@Inject
	private ContextEnvironmentValueServices contextEnvironmentValueServices;

	private static final String URL = "url";

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
	@Secured("BotUser")
	public ContextValue save(@SecuredOperation("botAdm") final Chatbot bot, final ContextValue contextValue) {
		if (!contextValue.getLabel().equals(URL)) {
			checkPatternKey(contextValue.getXpath());
		}
		final HistoryActionEnum action;
		if (contextValue.getCvaId()!= null) {
			action = HistoryActionEnum.UPDATED;
			final ContextValue oldContextValue = contextValueDAO.get(contextValue.getCvaId());
			if(oldContextValue.getLabel().equals(URL)){
				throw new VUserException(ContextValueMultilingualResources.CONTEXT_VALUE_URL_EDIT_ERROR);
			}
			else if (!oldContextValue.getLabel().equals(contextValue.getLabel()) || !oldContextValue.getXpath().equals(contextValue.getXpath())) {
				nodeServices.updateNodes(bot);
			}
		} else {
			if(contextValue.getLabel().equals(URL)){
				int nbURL = contextValueDAO.findAll(Criterions.isEqualTo(DtDefinitions.ContextValueFields.botId,bot.getBotId()).and(Criterions.isEqualTo(DtDefinitions.ContextValueFields.label, URL)), DtListState.of(MAX_ELEMENTS_PLUS_ONE)).size();
				if(nbURL!=0)throw new VUserException(ContextValueMultilingualResources.CONTEXT_VALUE_URL_NEW_ERROR);
			}
			action = HistoryActionEnum.ADDED;
			nodeServices.updateNodes(bot);
		}
		final ContextValue savedContextValue = contextValueDAO.save(contextValue);
		record(bot, savedContextValue, action);
		return savedContextValue;

	}

	/**
	 * delete contextValue
	 *
	 * @param bot
	 * @param cvaId
	 */
	@Secured("BotUser")
	public void deleteContextValue(@SecuredOperation("botAdm") final Chatbot bot, final Long cvaId) {
		final ContextValue contextValue = contextValueDAO.get(cvaId);
		if(!contextValue.getLabel().equals(URL)) {
			delete(bot, cvaId);
		}
		else{
			throw new VUserException(ContextValueMultilingualResources.CONTEXT_VALUE_URL_DELETE_ERROR);
		}
		nodeServices.updateNodes(bot);
		record(bot, contextValue, HistoryActionEnum.DELETED);
	}

	public void delete(final Chatbot bot, final long contextValueId) {
		documentaryResourceContextServices.deleteAllDocumentaryResourceContextByCvaId(bot, contextValueId);
		questionAnswerContextServices.deleteAllQuestionAnswerContextByCvaId(bot, contextValueId);
		contextPossibleValueServices.deleteContextPossibleValuesByCvaId(bot, contextValueId);
		contextEnvironmentValueServices.deleteContextEnvironmentValue(contextValueId);
		contextValueDAO.delete(contextValueId);
	}

	public DtList<ContextValue> getAllContextValueByBotId(final Long botId) {
		return contextValueDAO.findAll(Criterions.isEqualTo(ContextValueFields.botId, botId), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}

	public void deleteAllByBotId(final Chatbot bot) {
		getAllContextValueByBotId(bot.getBotId()).forEach(contextValue -> delete(bot, contextValue.getCvaId()));
	}

	@Secured("BotUser")

	public ContextValue getNewContextValue(@SecuredOperation("botAdm") final Chatbot bot) {
		final ContextValue contextValue = new ContextValue();
		contextValue.setBotId(bot.getBotId());
		return contextValue;
	}

	private static void checkPatternKey(final String key) {

		if (key == null) {
			throw new VUserException(ContextValueMultilingualResources.XPATH_PATTERN_NULL_ERROR);
		}

		final XPathFactory factory = XPathFactory.newInstance();
		final XPath xPath = factory.newXPath();
		try {
			xPath.compile(key);
		} catch (final XPathExpressionException e) {
			throw new VUserException(ContextValueMultilingualResources.XPATH_PATTERN_DIGIT_ERROR);
		}

	}

	@Secured("BotUser")
	public String exportContextValuesToMapByBot(@SecuredOperation("botContributor") final Chatbot bot, final StringBuilder logs) {
		LogsUtils.addLogs(logs, "Export Map Context : ");
		try {
			final DtList<ContextValue> list = getAllContextValueByBotId(bot.getBotId());

			final Map<String, String> map = new HashMap<>();
			for (final ContextValue contextValue : list) {
				map.put(contextValue.getLabel(), contextValue.getXpath());
			}
			if (!map.containsKey(URL)) {
				map.put(URL, "");
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
	public History record(final Chatbot bot, final ContextValue contextValue, final HistoryActionEnum action) {
		return historyServices.record(bot, action, contextValue.getClass().getSimpleName(), contextValue.getLabel());
	}

	@Secured("BotUser")
	public ContextValue initContextValueURL(@SecuredOperation("botAdm") final Chatbot bot) {
		final ContextValue contextValue = new ContextValue();
		contextValue.setBotId(bot.getBotId());
		contextValue.setLabel(URL);
		contextValue.setXpath("");
		return save(bot, contextValue);
	}
}
