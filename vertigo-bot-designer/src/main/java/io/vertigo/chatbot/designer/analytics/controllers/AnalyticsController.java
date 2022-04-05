/**
 * vertigo - simple java starter
 *
 * Copyright (C) 2020, Vertigo.io, team@vertigo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vertigo.chatbot.designer.analytics.controllers;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotCustomConfig;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TopicIhm;
import io.vertigo.chatbot.designer.analytics.multilingual.AnalyticsMultilingualResources;
import io.vertigo.chatbot.designer.analytics.services.AnalyticsExportServices;
import io.vertigo.chatbot.designer.analytics.services.AnalyticsServices;
import io.vertigo.chatbot.designer.analytics.services.TimeOption;
import io.vertigo.chatbot.designer.analytics.services.TypeExportAnalyticsServices;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.designer.builder.services.bot.ChabotCustomConfigServices;
import io.vertigo.chatbot.designer.builder.services.bot.ChatbotServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.chatbot.designer.commons.controllers.AbstractDesignerController;
import io.vertigo.chatbot.designer.commons.ihm.enums.TimeEnum;
import io.vertigo.chatbot.designer.commons.services.EnumIHMManager;
import io.vertigo.chatbot.designer.domain.analytics.SentenseDetail;
import io.vertigo.chatbot.designer.domain.analytics.SessionExport;
import io.vertigo.chatbot.designer.domain.analytics.StatCriteria;
import io.vertigo.chatbot.designer.domain.analytics.TopIntent;
import io.vertigo.chatbot.designer.domain.analytics.TypeExportAnalytics;
import io.vertigo.chatbot.designer.domain.analytics.UnknownSentenseExport;
import io.vertigo.chatbot.designer.domain.commons.SelectionOption;
import io.vertigo.chatbot.domain.DtDefinitions.SelectionOptionFields;
import io.vertigo.chatbot.domain.DtDefinitions.SentenseDetailFields;
import io.vertigo.chatbot.domain.DtDefinitions.TopIntentFields;
import io.vertigo.chatbot.domain.DtDefinitions.TopicIhmFields;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.locale.LocaleManager;
import io.vertigo.database.timeseries.TimedDatas;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.util.VCollectors;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.validation.UiMessageStack;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.vertigo.chatbot.designer.utils.ListUtils.listLimitReached;

@Controller
@RequestMapping("/analytics")
public class AnalyticsController extends AbstractDesignerController {

	private static final ViewContextKey<TimedDatas> sessionStatsKey = ViewContextKey.of("sessionStats");
	private static final ViewContextKey<TimedDatas> requestsStatsKey = ViewContextKey.of("requestsStats");
	private static final ViewContextKey<TimedDatas> userInteractionsStatsKey = ViewContextKey.of("userInteractionsStats");
	private static final ViewContextKey<TimedDatas> ratingStatsKey = ViewContextKey.of("ratingStats");
	private static final ViewContextKey<SentenseDetail> unknownSentensesKey = ViewContextKey.of("unknownSentenses");
	private static final ViewContextKey<ChatbotCustomConfig> chatbotCustomConfigKey = ViewContextKey.of("chatbotCustomConfig");
	private static final ViewContextKey<TopIntent> topIntentsKey = ViewContextKey.of("topIntents");
	private static final ViewContextKey<SentenseDetail> intentDetailsKey = ViewContextKey.of("intentDetails");
	private static final ViewContextKey<SelectionOption> timeOptionsList = ViewContextKey.of("timeOptions");
	private static final ViewContextKey<TypeExportAnalytics> typeExportAnalyticsListKey = ViewContextKey.of("typeExportAnalyticsList");
	private static final ViewContextKey<TypeExportAnalytics> selectTypeExportAnalyticsKey = ViewContextKey.of("selectTypeExportAnalytics");

	private static final ViewContextKey<Topic> topicsKey = ViewContextKey.of("topics");
	private static final ViewContextKey<TopicIhm> topicsNotUsedKey = ViewContextKey.of("topicsNotUsed");

	private static final ViewContextKey<Chatbot> botsKey = ViewContextKey.of("bots");
	private static final ViewContextKey<ChatbotNode> nodesKey = ViewContextKey.of("nodes");

	private static final ViewContextKey<StatCriteria> criteriaKey = ViewContextKey.of("criteria");

	private static final ViewContextKey<String> localeKey = ViewContextKey.of("locale");

	@Inject
	private AnalyticsServices analyticsServices;

	@Inject
	private ChatbotServices chatbotServices;

	@Inject
	private NodeServices nodeServices;

	@Inject
	private TopicServices topicServices;

	@Inject
	private TypeExportAnalyticsServices typeExportAnalyticsServices;

	@Inject
	private AnalyticsExportServices analyticsExportServices;

	@Inject
	private ChabotCustomConfigServices chabotCustomConfigServices;

	@Inject
	private EnumIHMManager enumIHMManager;

	@Inject
	private LocaleManager localeManager;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext,
							@RequestParam("botId") final Optional<Long> botId,
							@RequestParam("nodId") final Optional<Long> nodId,
							@RequestParam("time") final Optional<TimeOption> timeOption,
							final UiMessageStack uiMessageStack) {
		viewContext.publishDtList(botsKey, chatbotServices.getMySupervisedChatbots());
		final StatCriteria statCriteria = new StatCriteria();

		if (botId.isPresent()) {
			Chatbot chatbot = chatbotServices.getChatbotById(botId.get());
			viewContext.publishDtList(nodesKey, nodeServices.getNodesByBot(chatbot));
			statCriteria.setBotId(botId.get());
			viewContext.publishDtListModifiable(typeExportAnalyticsListKey, typeExportAnalyticsServices.getAllTypeExportAnalytics());
			viewContext.publishDto(chatbotCustomConfigKey, chabotCustomConfigServices.getChatbotCustomConfigByBotId(botId.get()));
		} else {
			viewContext.publishDtList(nodesKey, new DtList<ChatbotNode>(ChatbotNode.class));
			viewContext.publishDtListModifiable(typeExportAnalyticsListKey, typeExportAnalyticsServices.getNonBotRelatedTypeExportAnalytics());
			viewContext.publishDto(chatbotCustomConfigKey, chabotCustomConfigServices.getDefaultChatbotCustomConfig());
		}

		statCriteria.setToDate(LocalDate.now());
		statCriteria.setTimeOption(timeOption.orElse(TimeOption.DAY).name());

		nodId.ifPresent(statCriteria::setNodId);

		viewContext.publishDtList(timeOptionsList, SelectionOptionFields.label, enumIHMManager.getSelectionOptions(TimeEnum.values()));
		viewContext.publishDto(criteriaKey, statCriteria);

		viewContext.publishDto(selectTypeExportAnalyticsKey, new TypeExportAnalytics());

		viewContext.publishRef(localeKey, localeManager.getCurrentLocale().toString());

		updateGraph(viewContext, statCriteria);

		listLimitReached(viewContext, uiMessageStack);
		toModeEdit();
	}

	@PostMapping("/_updateStats")
	public ViewContext doUpdateStats(final ViewContext viewContext,
			@ViewAttribute("criteria") final StatCriteria criteria, final UiMessageStack uiMessageStack) {

		updateGraph(viewContext, criteria);
		listLimitReached(viewContext, uiMessageStack);
		return viewContext;
	}

	private void updateGraph(final ViewContext viewContext, final StatCriteria criteria) {

		viewContext.publishRef(sessionStatsKey, analyticsServices.getSessionsStats(criteria));
		viewContext.publishRef(requestsStatsKey, analyticsServices.getRequestStats(criteria));
		viewContext.publishRef(userInteractionsStatsKey, analyticsServices.getUserInteractions(criteria));
		viewContext.publishDtList(topicsNotUsedKey, TopicIhmFields.code, refreshTopicsList(criteria));
		if (criteria.getBotId() != null) {
			final Chatbot bot = chatbotServices.getChatbotById(criteria.getBotId());
			viewContext.publishDtList(unknownSentensesKey, SentenseDetailFields.topId, analyticsServices.getSentenseDetails(criteria));
			viewContext.publishDtList(topIntentsKey, TopIntentFields.topId, analyticsServices.getTopIntents(criteria));
			viewContext.publishDtList(topicsKey, topicServices.getAllTopicByBot(bot));
			viewContext.publishRef(ratingStatsKey, analyticsServices.getRatingStats(criteria));
			viewContext.publishDto(chatbotCustomConfigKey, chabotCustomConfigServices.getChatbotCustomConfigByBotId(bot.getBotId()));
			viewContext.publishDtList(nodesKey, nodeServices.getNodesByBot(bot));
			viewContext.publishDtListModifiable(typeExportAnalyticsListKey, typeExportAnalyticsServices.getAllTypeExportAnalytics());
		} else {
			viewContext.publishDtList(unknownSentensesKey, SentenseDetailFields.topId, new DtList<SentenseDetail>(SentenseDetail.class));
			viewContext.publishDtList(topIntentsKey, TopIntentFields.topId, new DtList<TopIntent>(TopIntent.class));
			viewContext.publishDtList(topicsKey, new DtList<Topic>(Topic.class));
			viewContext.publishRef(ratingStatsKey, new TimedDatas(new ArrayList<>(), new ArrayList<>()));
			viewContext.publishDto(chatbotCustomConfigKey, chabotCustomConfigServices.getDefaultChatbotCustomConfig());
			viewContext.publishDtListModifiable(typeExportAnalyticsListKey, typeExportAnalyticsServices.getNonBotRelatedTypeExportAnalytics());
		}

		viewContext.publishDtList(intentDetailsKey, SentenseDetailFields.topId, new DtList<SentenseDetail>(SentenseDetail.class));
	}

	private DtList<TopicIhm> refreshTopicsList(final StatCriteria criteria) {
		if (criteria.getBotId() != null) {
			final List<String> resultTimedData = analyticsServices.getDistinctCodeByTimeDatas(criteria);
			DtList<TopicIhm> topics = topicServices.getAllNonTechnicalTopicIhmByBot(chatbotServices.getChatbotById(criteria.getBotId()), localeManager.getCurrentLocale().toString());
			topics = topics.stream()
					.filter(x -> !resultTimedData.contains(x.getCode()))
					.collect(VCollectors.toDtList(TopicIhm.class));
			return topics;
		}
		return new DtList<>(TopicIhm.class);
	}

	@PostMapping("/_intentDetails")
	public ViewContext doGetIntentDetails(final ViewContext viewContext,
			@ViewAttribute("criteria") final StatCriteria criteria,
			@RequestParam("intentRasa") final String intentRasa, final UiMessageStack uiMessageStack) {

		viewContext.publishDtList(intentDetailsKey, SentenseDetailFields.text, analyticsServices.getKnownSentensesDetail(criteria, intentRasa));
		listLimitReached(viewContext, uiMessageStack);
		return viewContext;
	}

	/*
	 * Export statistics from criteria and typeExportAnalytics
	 */
	@PostMapping("/_exportStatisticFile")
	public VFile doExportStatisticFile(final ViewContext viewContext,
			@ViewAttribute("criteria") final StatCriteria criteria,
			@ViewAttribute("selectTypeExportAnalytics") final String selectTypeExportAnalytics) {

		if (selectTypeExportAnalytics.isEmpty()) {
			throw new VUserException(AnalyticsMultilingualResources.MANDATORY_TYPE_EXPORT_ANALYTICS);
		}
		switch (selectTypeExportAnalytics) {
			case "SESSIONS":
				final DtList<SessionExport> listSessionExport = analyticsExportServices.getSessionExport(criteria);
				return analyticsExportServices.exportSessions(listSessionExport);
			case "UNKNOWN_MESSAGES":
				final DtList<UnknownSentenseExport> listUnknownSentenseExport = analyticsExportServices.getUnknownSentenseExport(criteria);
				return analyticsExportServices.exportUnknownMessages(listUnknownSentenseExport);
			default:
				throw new VUserException(AnalyticsMultilingualResources.MANDATORY_TYPE_EXPORT_ANALYTICS);
		}

	}

}
