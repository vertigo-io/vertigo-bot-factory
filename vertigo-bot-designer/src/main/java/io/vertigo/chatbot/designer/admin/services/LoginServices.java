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
package io.vertigo.chatbot.designer.admin.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import io.vertigo.account.account.Account;
import io.vertigo.account.authentication.AuthenticationManager;
import io.vertigo.account.authorization.AuthorizationManager;
import io.vertigo.account.authorization.UserAuthorizations;
import io.vertigo.account.authorization.VSecurityException;
import io.vertigo.account.authorization.metamodel.Authorization;
import io.vertigo.account.authorization.metamodel.AuthorizationName;
import io.vertigo.account.impl.authentication.UsernamePasswordAuthenticationToken;
import io.vertigo.account.security.VSecurityManager;
import io.vertigo.app.Home;
import io.vertigo.chatbot.authorization.GlobalAuthorizations;
import io.vertigo.chatbot.authorization.SecuredEntities.ChatbotAuthorizations;
import io.vertigo.chatbot.commons.dao.PersonDAO;
import io.vertigo.chatbot.commons.domain.Person;
import io.vertigo.chatbot.commons.domain.PersonRoleEnum;
import io.vertigo.chatbot.designer.commons.DesignerUserSession;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.component.Component;
import io.vertigo.core.definition.DefinitionSpace;
import io.vertigo.core.locale.MessageText;
import io.vertigo.lang.VUserException;

@Transactional
public class LoginServices implements Component {

	@Inject
	private AuthenticationManager authenticationManager;
	@Inject
	private AuthorizationManager authorizationManager;
	@Inject
	private VSecurityManager securityManager;

	@Inject
	private PersonDAO personDao;

	public void login(final String login, final String password) {
		final Optional<Account> loggedAccount = authenticationManager.login(new UsernamePasswordAuthenticationToken(login, password));
		if (!loggedAccount.isPresent()) {
			throw new VUserException("Login or Password invalid");
		}
		final Account account = loggedAccount.get();
		final Person person = personDao.get(Long.valueOf(account.getId()));
		getUserSession().setLoggedPerson(person);

		final UserAuthorizations userAuthorizations = authorizationManager.obtainUserAuthorizations();
		obtainAuthorizationPerRole(person.getRole()).stream()
				.forEach(auth -> userAuthorizations.addAuthorization(auth));

		person.chatbots().load();
		person.chatbots().get().stream()
				.forEach(chatbot -> userAuthorizations.withSecurityKeys("botId", chatbot.getBotId()));
	}

	private List<Authorization> obtainAuthorizationPerRole(final String role) {
		if (PersonRoleEnum.RAdmin.name().equals(role)) {
			return resolveAuthorizations(GlobalAuthorizations.AtzAdmPer, GlobalAuthorizations.AtzSuperAdmBot, GlobalAuthorizations.AtzAdmBot, ChatbotAuthorizations.AtzChatbot$admin);
		} else if (PersonRoleEnum.RUser.name().equals(role)) {
			return resolveAuthorizations(GlobalAuthorizations.AtzAdmBot, ChatbotAuthorizations.AtzChatbot$read, ChatbotAuthorizations.AtzChatbot$write);
		}
		throw new IllegalArgumentException("Unsupported role " + role);
	}

	private static List<Authorization> resolveAuthorizations(final AuthorizationName... authNames) {
		final DefinitionSpace definitionSpace = Home.getApp().getDefinitionSpace();
		final List<Authorization> authorizations = Arrays.stream(authNames)
				.map(name -> definitionSpace.resolve(name.name(), Authorization.class))
				.collect(Collectors.toList());
		return authorizations;
	}

	public boolean isAuthenticated() {
		final Optional<DesignerUserSession> userSession = securityManager.<DesignerUserSession> getCurrentUserSession();
		return !userSession.isPresent() ? false : userSession.get().isAuthenticated();
	}

	public Person getLoggedPerson() {
		return getUserSession().getLoggedPerson();
	}

	public void logout(final HttpSession httpSession) {
		authenticationManager.logout();
		httpSession.invalidate();
	}

	private DesignerUserSession getUserSession() {
		return securityManager.<DesignerUserSession> getCurrentUserSession().orElseThrow(() -> new VSecurityException(MessageText.of("No active session found")));
	}
}
