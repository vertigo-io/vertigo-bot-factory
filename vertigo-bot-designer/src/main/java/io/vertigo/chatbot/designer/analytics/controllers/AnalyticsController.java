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
import io.vertigo.chatbot.commons.influxDb.TimeSerieServices;
import io.vertigo.chatbot.designer.analytics.multilingual.AnalyticsMultilingualResources;
import io.vertigo.chatbot.designer.analytics.services.AnalyticsExportServices;
import io.vertigo.chatbot.designer.analytics.services.TimeOption;
import io.vertigo.chatbot.designer.analytics.services.TypeExportAnalyticsServices;
import io.vertigo.chatbot.designer.commons.controllers.AbstractDesignerController;
import io.vertigo.chatbot.designer.commons.ihm.enums.TimeEnum;
import io.vertigo.chatbot.designer.commons.services.EnumIHMManager;
import io.vertigo.chatbot.designer.domain.analytics.SessionExport;
import io.vertigo.chatbot.designer.domain.analytics.StatCriteria;
import io.vertigo.chatbot.designer.domain.analytics.TypeExportAnalytics;
import io.vertigo.chatbot.designer.domain.analytics.UnknownSentenseExport;
import io.vertigo.chatbot.designer.domain.commons.SelectionOption;
import io.vertigo.chatbot.domain.DtDefinitions.SelectionOptionFields;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.locale.LocaleManager;
import io.vertigo.database.timeseries.TimedDatas;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;
import io.vertigo.vega.webservice.validation.UiMessageStack;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Optional;

import static io.vertigo.chatbot.designer.utils.ListUtils.listLimitReached;

@Controller
@RequestMapping("/analytics")
public class AnalyticsController extends AbstractDesignerController {

	private static final ViewContextKey<TimedDatas> sessionStatsKey = ViewContextKey.of("sessionStats");
	private static final ViewContextKey<TimedDatas> requestsStatsKey = ViewContextKey.of("requestsStats");
	private static final ViewContextKey<TimedDatas> userInteractionsStatsKey = ViewContextKey.of("userInteractionsStats");;
	private static final ViewContextKey<SelectionOption> timeOptionsList = ViewContextKey.of("timeOptions");
	private static final ViewContextKey<TypeExportAnalytics> typeExportAnalyticsListKey = ViewContextKey.of("typeExportAnalyticsList");
	private static final ViewContextKey<TypeExportAnalytics> selectTypeExportAnalyticsKey = ViewContextKey.of("selectTypeExportAnalytics");
	private static final ViewContextKey<Chatbot> botsKey = ViewContextKey.of("bots");
	private static final ViewContextKey<StatCriteria> criteriaKey = ViewContextKey.of("criteria");
	private static final ViewContextKey<String> localeKey = ViewContextKey.of("locale");

	@Inject
	private TimeSerieServices timeSerieServices;

	@Inject
	private TypeExportAnalyticsServices typeExportAnalyticsServices;

	@Inject
	private AnalyticsExportServices analyticsExportServices;

	@Inject
	private EnumIHMManager enumIHMManager;

	@Inject
	private LocaleManager localeManager;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext,
			@RequestParam("time") final Optional<TimeOption> timeOption,
			final UiMessageStack uiMessageStack) {
		viewContext.publishDtList(botsKey, chatbotServices.getMySupervisedChatbots());
		final StatCriteria statCriteria = new StatCriteria();
		viewContext.publishDtListModifiable(typeExportAnalyticsListKey, typeExportAnalyticsServices.getNonBotRelatedTypeExportAnalytics());

		statCriteria.setToDate(LocalDate.now());
		statCriteria.setTimeOption(timeOption.orElse(TimeOption.DAY).name());

		viewContext.publishDtList(timeOptionsList, SelectionOptionFields.label, enumIHMManager.getSelectionOptions(TimeEnum.values()));
		viewContext.publishDto(criteriaKey, statCriteria);

		viewContext.publishDto(selectTypeExportAnalyticsKey, new TypeExportAnalytics());

		viewContext.publishRef(localeKey, localeManager.getCurrentLocale().toString());

		updateGraph(viewContext, statCriteria);

		listLimitReached(viewContext, uiMessageStack);
		AbstractVSpringMvcController.toModeEdit();
	}

	@PostMapping("/_updateStats")
	public ViewContext doUpdateStats(final ViewContext viewContext,
			@ViewAttribute("criteria") final StatCriteria criteria, final UiMessageStack uiMessageStack) {

		updateGraph(viewContext, criteria);
		listLimitReached(viewContext, uiMessageStack);
		return viewContext;
	}

	private void updateGraph(final ViewContext viewContext, final StatCriteria criteria) {

		viewContext.publishRef(sessionStatsKey, timeSerieServices.getSessionsStats(criteria));
		viewContext.publishRef(requestsStatsKey, timeSerieServices.getRequestStats(criteria));
		viewContext.publishRef(userInteractionsStatsKey, timeSerieServices.getUserInteractions(criteria));
	}

	/*
	 * Export statistics from criteria and typeExportAnalytics
	 */
	@PostMapping("/_exportStatisticFile")
	public VFile doExportStatisticFile(final ViewContext viewContext,
			@ViewAttribute("criteria") final StatCriteria criteria,
			@ViewAttribute("selectTypeExportAnalytics") final TypeExportAnalytics selectTypeExportAnalytics) {

		if (selectTypeExportAnalytics.getTeaCd() == null) {
			throw new VUserException(AnalyticsMultilingualResources.MANDATORY_TYPE_EXPORT_ANALYTICS);
		}
		switch (selectTypeExportAnalytics.getTeaCd()) {
			case "USER_ACTIONS_CONVERSATIONS":
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
