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

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.vertigo.account.authorization.AuthorizationManager;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.builder.services.DesignerServices;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;

@Controller
@RequestMapping("/bot/")
public class CommonBotDetailController {

	@Inject
	private DesignerServices designerServices;
	@Inject
	private AuthorizationManager authorizationManager;

	private static final ViewContextKey<String[]> chatBotAuthorizedOperationsKey = ViewContextKey.of("chatBotAuthorizedOperations");
	private static final ViewContextKey<Chatbot> botKey = ViewContextKey.of("bot");

	public Chatbot initCommonContext(final ViewContext viewContext, final Long botId) {
		final Chatbot chatbot = designerServices.getChatbotById(botId);
		viewContext.publishDto(botKey, chatbot);
		final List<String> authorizedOperations = authorizationManager.getAuthorizedOperations(chatbot);
		viewContext.publishRef(chatBotAuthorizedOperationsKey, authorizedOperations.toArray(new String[authorizedOperations.size()]));
		return chatbot;
	}

	public void initEmptyCommonContext(final ViewContext viewContext) {
		viewContext.publishDto(botKey, designerServices.getNewChatbot());
	}

	@GetMapping("/{botId}/avatar")
	public VFile getAvatar(@PathVariable("botId") final Long botId) {
		return designerServices.getAvatar(designerServices.getChatbotById(botId));
	}

	@GetMapping("/avatar")
	public VFile getAvatar() {
		return designerServices.getNoAvatar();
	}

}
