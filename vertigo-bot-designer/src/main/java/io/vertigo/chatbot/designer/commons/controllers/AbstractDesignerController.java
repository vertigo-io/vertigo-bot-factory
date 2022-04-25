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
package io.vertigo.chatbot.designer.commons.controllers;

import io.vertigo.account.authorization.AuthorizationManager;
import io.vertigo.account.authorization.definitions.AuthorizationName;
import io.vertigo.chatbot.authorization.GlobalAuthorizations;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.designer.builder.services.bot.ChatbotServices;
import io.vertigo.chatbot.designer.utils.UserSessionUtils;
import io.vertigo.core.param.ParamManager;
import io.vertigo.datamodel.structure.model.KeyConcept;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.inject.Inject;
import java.util.HashMap;

public abstract class AbstractDesignerController extends AbstractVSpringMvcController {

	@Inject
	private AuthorizationManager authorizationManager;

	@Inject
	protected ChatbotServices chatbotServices;

	@Inject
	private ParamManager paramManager;

	private static final ViewContextKey<HashMap<String, Boolean>> userAuthorizationsKey = ViewContextKey.of("userAuthorizations");

	private static final ViewContextKey<String> versionNumberKey = ViewContextKey.of("versionNumber");

	private static final ViewContextKey<String> personNameKey = ViewContextKey.of("personName");

	private static final ViewContextKey<Chatbot> supervisedBotsKey = ViewContextKey.of("bots");

	private final HashMap<String, Boolean> userAuthorizations = new HashMap<>();

	@ModelAttribute
	public void initSecurityContext(final ViewContext viewContext) {
		// Automatically initialize global authorizations
		if (isNewContext()) {
			for (final AuthorizationName authorization : GlobalAuthorizations.values()) {
				userAuthorizations.put(authorization.name(), authorizationManager.hasAuthorization(authorization));
			}
			viewContext.publishRef(userAuthorizationsKey, userAuthorizations);
		}
	}

	@ModelAttribute
	public void initVersionNumber(final ViewContext viewContext) {
		viewContext.publishRef(versionNumberKey, paramManager.getParam("VERSION").getValueAsString());
	}

	@ModelAttribute
	public void initPersonName(final ViewContext viewContext) {
		viewContext.publishRef(personNameKey, UserSessionUtils.getLoggedPerson().getName());
	}

	@ModelAttribute
	public void initSupervisedBots(final ViewContext viewContext) {
		viewContext.publishDtList(supervisedBotsKey, chatbotServices.getMySupervisedChatbots());
	}

	protected <K extends KeyConcept> void addKeyConceptSecurityToContext(final K keyConcept, final AuthorizationName[] authorizations) {
		// Initialize KeyConcept dependent authorizations (must be called by controller)
		if (isNewContext()) {
			final var authorizedOperationList = authorizationManager.getAuthorizedOperations(keyConcept);

			for (final AuthorizationName authorization : authorizations) {
				final var operationName = getOperationName(authorization.name());

				userAuthorizations.put(authorization.name(), authorizedOperationList.contains(operationName));
			}
		}
	}

	private static String getOperationName(final String authorizationCode) {
		return authorizationCode.substring(authorizationCode.indexOf('$') + 1);
	}
}
