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
package io.vertigo.chatbot.designer.builder.controllers.bot;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.SmallTalk;
import io.vertigo.chatbot.designer.builder.services.SmallTalkServices;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;

@Controller
@RequestMapping("/bot/{botId}/smallTalks")
@Secured("BotUser")
public class SmallTalkListController extends AbstractBotController {

	private static final ViewContextKey<SmallTalk> smallTalkKey = ViewContextKey.of("smallTalks");

	@Inject
	private SmallTalkServices smalltalkServices;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, @PathVariable("botId") final Long botId) {
		initCommonContext(viewContext, botId);

		viewContext.publishDtList(smallTalkKey, smalltalkServices.getAllSmallTalksByBotId(botId));
		//		viewContext.publishRef(botIdKey, botId);
		toModeReadOnly();
	}

}
