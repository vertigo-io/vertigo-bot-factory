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

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.vertigo.chatbot.designer.admin.services.PersonServices;
import io.vertigo.chatbot.designer.commons.utils.UserSessionUtils;
import io.vertigo.chatbot.designer.domain.commons.Person;
import io.vertigo.chatbot.designer.domain.commons.PersonRole;
import io.vertigo.chatbot.designer.domain.commons.PersonRoleEnum;
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
	private UserSessionUtils userSessionUtils;

	private static final ViewContextKey<Person> personKey = ViewContextKey.of("person");
	public static final ViewContextKey<PersonRole> ROLES_CONTEXT_KEY = ViewContextKey.of("roles");
	public static final ViewContextKey<Boolean> IS_SAME_USER_KEY = ViewContextKey.of("isSameUser");
	public static final ViewContextKey<Boolean> IS_LAST_ADMIN = ViewContextKey.of("isLastAdmin");

	@GetMapping("/{perId}")
	public void initContext(final ViewContext viewContext, @PathVariable("perId") final Long perId) {
		final Person person = personServices.getPersonById(perId);
		viewContext.publishDto(personKey, person);
		viewContext.publishRef(IS_SAME_USER_KEY, isPersonConnected(person));
		viewContext.publishMdl(ROLES_CONTEXT_KEY, PersonRole.class, null); // all
		viewContext.publishRef(IS_LAST_ADMIN, isLastAdmin(person));
		toModeReadOnly();
	}

	@PostMapping("/_edit")
	public void doEdit() {
		toModeEdit();
	}

	@PostMapping("/_save")
	public String doSave(final ViewContext viewContext, final UiMessageStack uiMessageStack,
			@ViewAttribute("person") final Person person) {
		//save person, with its supervised chatbot
		final Person savedPerson = personServices.savePerson(person);
		return "redirect:/person/" + savedPerson.getPerId();
	}

	private Boolean isPersonConnected(final Person person) {
		final Person personConnected = userSessionUtils.getLoggedPerson();
		return person.getPerId().equals(personConnected.getPerId());
	}

	private Boolean isLastAdmin(final Person person) {
		return personServices.getAdminPerNumber().equals(1L) && person.getRolCd().equals(PersonRoleEnum.RAdmin.name());
	}

	@PostMapping("/_delete")
	private String deletePerson(@ViewAttribute("person") final Person person) {
		personServices.deletePerson(person);
		return "redirect:/persons/";
	}

}
