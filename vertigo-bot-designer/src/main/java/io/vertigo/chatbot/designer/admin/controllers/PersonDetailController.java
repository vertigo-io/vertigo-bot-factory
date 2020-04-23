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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.Person;
import io.vertigo.chatbot.commons.domain.PersonRole;
import io.vertigo.chatbot.designer.admin.services.PersonServices;
import io.vertigo.chatbot.designer.builder.services.DesignerServices;
import io.vertigo.dynamo.domain.model.UID;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;
import io.vertigo.vega.webservice.validation.UiMessageStack;

@Controller
@RequestMapping("/person")
public class PersonDetailController extends AbstractVSpringMvcController {

	@Inject
	private PersonServices personServices;
	@Inject
	private DesignerServices designerServices;

	private static final ViewContextKey<Person> personKey = ViewContextKey.of("person");
	/** ClÃ© de context du mode changePassword. */
	public static final ViewContextKey<Boolean> MODE_CHANGE_PASSWORD_CONTEXT_KEY = ViewContextKey.of("modeChangePassword");
	public static final ViewContextKey<PersonRole> ROLES_CONTEXT_KEY = ViewContextKey.of("roles");
	public static final ViewContextKey<Chatbot> CHATBOTS_CONTEXT_KEY = ViewContextKey.of("chatbots");
	public static final ViewContextKey<Long[]> CHATBOT_SELECTED_CONTEXT_KEY = ViewContextKey.of("chatbotsSelected");
	public static final ViewContextKey<String> CHATBOT_SELECTED_STR_CONTEXT_KEY = ViewContextKey.of("chatbotsSelectedStr");

	private void initCommonContext(final ViewContext viewContext) {
		viewContext.publishMdl(ROLES_CONTEXT_KEY, PersonRole.class, null); //all
		viewContext.publishDtList(CHATBOTS_CONTEXT_KEY, designerServices.getAllChatbots());
		viewContext.publishRef(CHATBOT_SELECTED_STR_CONTEXT_KEY, "");
	}

	@GetMapping("/{perId}")
	public void initContext(final ViewContext viewContext, @PathVariable("perId") final Long perId) {
		initCommonContext(viewContext);
		final Person person = personServices.getPersonById(perId);
		viewContext.publishDto(personKey, person);
		viewContext.publishRef(MODE_CHANGE_PASSWORD_CONTEXT_KEY, false);

		final List<Long> selectedBotIds = person.chatbots().get().stream()
				.map(Chatbot::getBotId)
				.collect(Collectors.toList());
		viewContext.publishRef(CHATBOT_SELECTED_CONTEXT_KEY, selectedBotIds.toArray(new Long[selectedBotIds.size()]));

		toModeReadOnly();
	}

	@GetMapping("/new")
	public void initContext(final ViewContext viewContext) {
		initCommonContext(viewContext);
		viewContext.publishDto(personKey, new Person());
		viewContext.publishRef(MODE_CHANGE_PASSWORD_CONTEXT_KEY, true);
		viewContext.publishRef(CHATBOT_SELECTED_CONTEXT_KEY, new Long[0]);
		toModeCreate();
	}

	@PostMapping("/_edit")
	public void doEdit() {
		toModeEdit();
	}

	@PostMapping("/_changePassword")
	public void doChangePassword(final ViewContext viewContext) {
		viewContext.publishRef(MODE_CHANGE_PASSWORD_CONTEXT_KEY, true);
		toModeEdit();
	}

	@PostMapping("/_save")
	public String doSave(final ViewContext viewContext, final UiMessageStack uiMessageStack,
			@ViewAttribute("person") final Person person, @ViewAttribute("chatbotsSelectedStr") final String chatbotsSelectedStr) {
		if (!viewContext.getBoolean(MODE_CHANGE_PASSWORD_CONTEXT_KEY)) {
			person.setPasswordNew(null); //can't accept password if not the changePassword mode
		}
		//convert the select result (chatbot's ids comma separated) to list<Uid<Chatbot>>
		final List<UID> chatbotIdsSelected;
		if (chatbotsSelectedStr.isEmpty()) {
			chatbotIdsSelected = Collections.emptyList();
		} else {
			chatbotIdsSelected = Arrays.stream(chatbotsSelectedStr.split(","))
					.map(Long::parseLong)
					.map(id -> UID.of(Chatbot.class, id))
					.collect(Collectors.toList());
		}
		//save person, with its supervised chatbot
		final Person savedPerson = personServices.savePerson(person, chatbotIdsSelected);
		return "redirect:/person/" + savedPerson.getPerId();
	}
}
