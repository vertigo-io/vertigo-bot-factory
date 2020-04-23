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

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import io.vertigo.account.authorization.AuthorizationManager;
import io.vertigo.account.authorization.metamodel.AuthorizationName;
import io.vertigo.chatbot.authorization.GlobalAuthorizations;
import io.vertigo.chatbot.authorization.SecuredEntities.ChatbotAuthorizations;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@RequestMapping("/commons")
@ControllerAdvice(assignableTypes = { AbstractVSpringMvcController.class })
public class CommonDesignerController extends AbstractVSpringMvcController {

	@Inject
	private AuthorizationManager authorizationManager;

	private static final ViewContextKey<HashMap<String, Boolean>> userAuthorizationsKey = ViewContextKey.of("userAuthorizations");

	@ModelAttribute
	public void initContext(final ViewContext viewContext) {
		if (isNewContext()) {
			final HashMap<String, Boolean> userAuthorizations = new HashMap<>();
			appendAuthorizations(userAuthorizations, GlobalAuthorizations.values());
			appendAuthorizations(userAuthorizations, ChatbotAuthorizations.values());
			viewContext.publishRef(userAuthorizationsKey, userAuthorizations);
		}
	}

	private void appendAuthorizations(final Map<String, Boolean> userAuthorizations, final AuthorizationName[] values) {
		for (final AuthorizationName authorization : values) {
			userAuthorizations.put(authorization.name(), authorizationManager.hasAuthorization(authorization));
		}
	}
}
