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

import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.commons.domain.SmallTalk;
import io.vertigo.chatbot.designer.analytics.services.AnalyticsServices;
import io.vertigo.chatbot.designer.analytics.services.TimeOption;
import io.vertigo.chatbot.designer.builder.chatbot.services.ChatbotServices;
import io.vertigo.chatbot.designer.builder.services.DesignerServices;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.designer.domain.SentenseDetail;
import io.vertigo.chatbot.designer.domain.StatCriteria;
import io.vertigo.chatbot.designer.domain.TopIntent;
import io.vertigo.chatbot.domain.DtDefinitions.SentenseDetailFields;
import io.vertigo.chatbot.domain.DtDefinitions.TopIntentFields;
import io.vertigo.database.timeseries.TimedDatas;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@RequestMapping("/analytics")
public class AnalyticsController extends AbstractVSpringMvcController {

	private static final ViewContextKey<TimedDatas> sessionStatsKey = ViewContextKey.of("sessionStats");
	private static final ViewContextKey<TimedDatas> requestsStatsKey = ViewContextKey.of("requestsStats");
	private static final ViewContextKey<SentenseDetail> unknownSentensesKey = ViewContextKey.of("unknownSentenses");
	private static final ViewContextKey<TopIntent> topIntentsKey = ViewContextKey.of("topIntents");
	private static final ViewContextKey<SentenseDetail> intentDetailsKey = ViewContextKey.of("intentDetails");

	private static final ViewContextKey<SmallTalk> smallTalksKey = ViewContextKey.of("smallTalks");

	private static final ViewContextKey<Chatbot> botsKey = ViewContextKey.of("bots");
	private static final ViewContextKey<ChatbotNode> nodesKey = ViewContextKey.of("nodes");

	private static final ViewContextKey<StatCriteria> criteriaKey = ViewContextKey.of("criteria");

	@Inject
	private AnalyticsServices analyticsServices;

	@Inject
	private DesignerServices designerServices;

	@Inject
	private ChatbotServices chatbotServices;

	@Inject
	private NodeServices nodeServices;

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

		if (criteria.getBotId() != null) {
			viewContext.publishDtList(unknownSentensesKey, SentenseDetailFields.smtId, analyticsServices.getSentenseDetails(criteria));
			viewContext.publishDtList(topIntentsKey, TopIntentFields.smtId, analyticsServices.getTopIntents(criteria));

			viewContext.publishDtList(smallTalksKey, designerServices.getAllSmallTalksByBotId(criteria.getBotId()));
		} else {
			viewContext.publishDtList(unknownSentensesKey, SentenseDetailFields.smtId, new DtList<SentenseDetail>(SentenseDetail.class));
			viewContext.publishDtList(topIntentsKey, TopIntentFields.smtId, new DtList<TopIntent>(TopIntent.class));
			viewContext.publishDtList(smallTalksKey, new DtList<SmallTalk>(SmallTalk.class));
		}

		viewContext.publishDtList(intentDetailsKey, SentenseDetailFields.smtId, new DtList<SentenseDetail>(SentenseDetail.class));
	}

	@PostMapping("/_intentDetails")
	public ViewContext doGetIntentDetails(final ViewContext viewContext,
			@ViewAttribute("criteria") final StatCriteria criteria,
			@RequestParam("intentRasa") final String intentRasa) {

		viewContext.publishDtList(intentDetailsKey, SentenseDetailFields.messageId, analyticsServices.getKnownSentensesDetail(criteria, intentRasa)); // "st_1102_blague"

		return viewContext;
	}
}
