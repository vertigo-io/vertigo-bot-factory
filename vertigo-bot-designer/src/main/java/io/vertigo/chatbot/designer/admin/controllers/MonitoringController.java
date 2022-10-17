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
package io.vertigo.chatbot.designer.admin.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;

import io.vertigo.chatbot.designer.admin.services.MonitoringServices;
import io.vertigo.chatbot.designer.commons.controllers.AbstractDesignerController;
import io.vertigo.chatbot.designer.domain.monitoring.MonitoringAlertingSubscription;
import io.vertigo.chatbot.designer.domain.monitoring.MonitoringDetailIHM;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;

@Controller
@RequestMapping("/monitoring")
public class MonitoringController extends AbstractDesignerController {

	private static final Logger LOGGER = LogManager.getLogger(MonitoringController.class);

	private static final ViewContextKey<MonitoringDetailIHM> monitoringDetailIHMKey = ViewContextKey.of("monitoringDetailIHM");
	private static final ViewContextKey<MonitoringAlertingSubscription> monitoringAlertingSubscriptionKey = ViewContextKey.of("monitoringAlertingSubscription");

	@Inject
	private MonitoringServices monitoringServices;


	@GetMapping("/")
	public void initContext(final ViewContext viewContext) {
		viewContext.publishDto(monitoringDetailIHMKey, monitoringServices.monitore());
	}

	@PostMapping("/refresh")
	public ViewContext refreshMonitoring(final ViewContext viewContext) {
		viewContext.publishDto(monitoringDetailIHMKey, monitoringServices.monitore());
		return viewContext;
	}

	@PostMapping("/subscribeGlobalAlerting")
	public ViewContext subscribeGlobalAlerting(final ViewContext viewContext,
											   @ViewAttribute("monitoringDetailIHM") final MonitoringDetailIHM monitoringDetailIHM) {
		monitoringServices.subscribeToGlobalAlerts(monitoringDetailIHM);
		viewContext.publishDto(monitoringDetailIHMKey, monitoringServices.monitore());
		return viewContext;
	}


	@PostMapping("/subscribeBotAlerting")
	public ViewContext subscribeAlerting(final ViewContext viewContext,
										 @RequestParam("botId") final long botId,
										 @RequestParam("enabled") final String enabled) {

		monitoringServices.subscribeToBot(botId, "true".equals(enabled));
		viewContext.publishDto(monitoringDetailIHMKey, monitoringServices.monitore());
		return viewContext;
	}
}
