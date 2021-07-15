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

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TopicIhm;
import io.vertigo.chatbot.designer.analytics.services.AnalyticsServices;
import io.vertigo.chatbot.designer.analytics.services.TimeOption;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.designer.builder.services.bot.ChatbotServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.chatbot.designer.commons.controllers.AbstractDesignerController;
import io.vertigo.chatbot.designer.commons.ihmEnum.TimeEnum;
import io.vertigo.chatbot.designer.commons.services.EnumIHMManager;
import io.vertigo.chatbot.designer.domain.SentenseDetail;
import io.vertigo.chatbot.designer.domain.StatCriteria;
import io.vertigo.chatbot.designer.domain.TopIntent;
import io.vertigo.chatbot.designer.domain.commons.SelectionOption;
import io.vertigo.chatbot.domain.DtDefinitions.SelectionOptionFields;
import io.vertigo.chatbot.domain.DtDefinitions.SentenseDetailFields;
import io.vertigo.chatbot.domain.DtDefinitions.TopIntentFields;
import io.vertigo.chatbot.domain.DtDefinitions.TopicIhmFields;
import io.vertigo.database.timeseries.TimedDatas;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.util.VCollectors;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;

@Controller
@RequestMapping("/analytics")
public class AnalyticsController extends AbstractDesignerController {

	private static final ViewContextKey<TimedDatas> sessionStatsKey = ViewContextKey.of("sessionStats");
	private static final ViewContextKey<TimedDatas> requestsStatsKey = ViewContextKey.of("requestsStats");
	private static final ViewContextKey<SentenseDetail> unknownSentensesKey = ViewContextKey.of("unknownSentenses");
	private static final ViewContextKey<TopIntent> topIntentsKey = ViewContextKey.of("topIntents");
	private static final ViewContextKey<SentenseDetail> intentDetailsKey = ViewContextKey.of("intentDetails");
	private static final ViewContextKey<SelectionOption> timeOptionsList = ViewContextKey.of("timeOptions");

	private static final ViewContextKey<Topic> topicsKey = ViewContextKey.of("topics");
	private static final ViewContextKey<TopicIhm> topicsNotUsedKey = ViewContextKey.of("topicsNotUsed");

	private static final ViewContextKey<Chatbot> botsKey = ViewContextKey.of("bots");
	private static final ViewContextKey<ChatbotNode> nodesKey = ViewContextKey.of("nodes");

	private static final ViewContextKey<StatCriteria> criteriaKey = ViewContextKey.of("criteria");

	@Inject
	private AnalyticsServices analyticsServices;

	@Inject
	private ChatbotServices chatbotServices;

	@Inject
	private NodeServices nodeServices;

	@Inject
	private TopicServices topicServices;

	@Inject
	private EnumIHMManager enumIHMManager;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext,
			@RequestParam("botId") final Optional<Long> botId,
			@RequestParam("nodId") final Optional<Long> nodId,
			@RequestParam("time") final Optional<TimeOption> timeOption) {
		viewContext.publishDtList(botsKey, chatbotServices.getMySupervisedChatbots());
		viewContext.publishDtList(nodesKey, new DtList<ChatbotNode>(ChatbotNode.class));

		final StatCriteria statCriteria = new StatCriteria();
		statCriteria.setTimeOption(timeOption.orElse(TimeOption.DAY).name());
		botId.ifPresent(statCriteria::setBotId);
		nodId.ifPresent(statCriteria::setNodId);

		viewContext.publishDtList(timeOptionsList, SelectionOptionFields.label, enumIHMManager.getSelectionOptions(TimeEnum.values()));
		viewContext.publishDto(criteriaKey, statCriteria);

		updateGraph(viewContext, statCriteria);

		toModeEdit();
	}

	@PostMapping("/_updateStats")
	public ViewContext doUpdateStats(final ViewContext viewContext,
			@ViewAttribute("criteria") final StatCriteria criteria) {

		updateGraph(viewContext, criteria);

		return viewContext;
	}

	private void updateGraph(final ViewContext viewContext, final StatCriteria criteria) {
		if (criteria.getBotId() != null) {
			viewContext.publishDtList(nodesKey, nodeServices.getAllNodesByBotId(criteria.getBotId()));
		}

		viewContext.publishRef(sessionStatsKey, analyticsServices.getSessionsStats(criteria));
		viewContext.publishRef(requestsStatsKey, analyticsServices.getRequestStats(criteria));
		viewContext.publishDtList(topicsNotUsedKey, TopicIhmFields.code, refreshTopicsList(criteria));

		if (criteria.getBotId() != null) {
			final Chatbot bot = chatbotServices.getChatbotById(criteria.getBotId());
			viewContext.publishDtList(unknownSentensesKey, SentenseDetailFields.topId, analyticsServices.getSentenseDetails(criteria));
			viewContext.publishDtList(topIntentsKey, TopIntentFields.topId, analyticsServices.getTopIntents(criteria));

			viewContext.publishDtList(topicsKey, topicServices.getAllTopicByBot(bot));
		} else {
			viewContext.publishDtList(unknownSentensesKey, SentenseDetailFields.topId, new DtList<SentenseDetail>(SentenseDetail.class));
			viewContext.publishDtList(topIntentsKey, TopIntentFields.topId, new DtList<TopIntent>(TopIntent.class));
			viewContext.publishDtList(topicsKey, new DtList<Topic>(Topic.class));
		}

		viewContext.publishDtList(intentDetailsKey, SentenseDetailFields.topId, new DtList<SentenseDetail>(SentenseDetail.class));
	}

	private DtList<TopicIhm> refreshTopicsList(final StatCriteria criteria) {
		if (criteria.getBotId() != null) {
			final List<String> resultTimedData = analyticsServices.getDistinctCodeByTimeDatas(criteria);
			DtList<TopicIhm> topics = topicServices.getAllNonTechnicalTopicIhmByBot(chatbotServices.getChatbotById(criteria.getBotId()));
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
			@RequestParam("intentRasa") final String intentRasa) {

		viewContext.publishDtList(intentDetailsKey, SentenseDetailFields.text, analyticsServices.getKnownSentensesDetail(criteria, intentRasa));

		return viewContext;
	}
}
