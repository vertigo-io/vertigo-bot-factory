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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.keycloak.KeycloakPrincipal;

import io.vertigo.account.account.Account;
import io.vertigo.account.authentication.AuthenticationManager;
import io.vertigo.account.authorization.AuthorizationManager;
import io.vertigo.account.authorization.UserAuthorizations;
import io.vertigo.account.authorization.VSecurityException;
import io.vertigo.account.authorization.definitions.Authorization;
import io.vertigo.account.authorization.definitions.AuthorizationName;
import io.vertigo.account.impl.authentication.UsernameAuthenticationToken;
import io.vertigo.account.impl.authentication.UsernamePasswordAuthenticationToken;
import io.vertigo.account.security.VSecurityManager;
import io.vertigo.chatbot.authorization.GlobalAuthorizations;
import io.vertigo.chatbot.authorization.SecuredEntities.ChatbotAuthorizations;
import io.vertigo.chatbot.commons.domain.Person;
import io.vertigo.chatbot.commons.domain.PersonRoleEnum;
import io.vertigo.chatbot.designer.commons.DesignerUserSession;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.connectors.keycloak.KeycloakDeploymentConnector;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.lang.WrappedException;
import io.vertigo.core.locale.MessageText;
import io.vertigo.core.node.Node;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.node.definition.DefinitionSpace;
import io.vertigo.vega.impl.servlet.filter.AbstactKeycloakDelegateAuthenticationHandler;

@Transactional
public class LoginServices extends AbstactKeycloakDelegateAuthenticationHandler implements Component, Activeable {

	@Inject
	private AuthenticationManager authenticationManager;
	@Inject
	private AuthorizationManager authorizationManager;
	@Inject
	private VSecurityManager securityManager;
	@Inject
	private List<KeycloakDeploymentConnector> keycloakDeploymentConnectors;
	@Inject
	private PersonServices personServices;

	public void login(final String login, final String password) {
		final Optional<Account> loggedAccount = authenticationManager.login(new UsernamePasswordAuthenticationToken(login, password));
		if (!loggedAccount.isPresent()) {
			throw new VUserException("Login or Password invalid");
		}
		final Account account = loggedAccount.get();
		final Person person = personServices.getPersonById(Long.valueOf(account.getId()));
		getUserSession().setLoggedPerson(person);

		final UserAuthorizations userAuthorizations = authorizationManager.obtainUserAuthorizations();
		obtainAuthorizationPerRole(person.getRolCd()).stream()
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
		final DefinitionSpace definitionSpace = Node.getNode().getDefinitionSpace();
		final List<Authorization> authorizations = Arrays.stream(authNames)
				.map(name -> definitionSpace.resolve(name.name(), Authorization.class))
				.collect(Collectors.toList());
		return authorizations;
	}

	public boolean isAuthenticated() {
		final Optional<DesignerUserSession> userSession = securityManager.<DesignerUserSession>getCurrentUserSession();
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
		return securityManager.<DesignerUserSession>getCurrentUserSession().orElseThrow(() -> new VSecurityException(MessageText.of("No active session found")));
	}

	@Override
	public boolean doLogin(HttpServletRequest request, HttpServletResponse response) {
		if (!isAuthenticated()) {
			// we should have a Principal
			final KeycloakPrincipal keycloakPrincipal = (KeycloakPrincipal) request.getUserPrincipal();
			final String login = keycloakPrincipal.getKeycloakSecurityContext().getIdToken().getPreferredUsername();
			loginWithPrincipal(login);
			try {
				response.sendRedirect(request.getContextPath() + "/bots/");
			} catch (final IOException e) {
				throw WrappedException.wrap(e);
			}
			//consumed by redirect
			return true;
		}
		//not consumed
		return false;
	}

	private void loginWithPrincipal(final String login) {
		final Account loggedAccount = authenticationManager.login(new UsernameAuthenticationToken(login)).orElseGet(
				() -> {
					// auto provisionning an account when using keycloak
					final Person newPerson = personServices.initPerson(login);
					return authenticationManager.login(new UsernameAuthenticationToken(login)).get();
				});
		final Person person = personServices.getPersonById(Long.valueOf(loggedAccount.getId()));
		getUserSession().setLoggedPerson(person);
		final UserAuthorizations userAuthorizations = authorizationManager.obtainUserAuthorizations();
		obtainAuthorizationPerRole(person.getRolCd()).stream()
				.forEach(auth -> userAuthorizations.addAuthorization(auth));

		person.chatbots().load();
		person.chatbots().get().stream()
				.forEach(chatbot -> userAuthorizations.withSecurityKeys("botId", chatbot.getBotId()));
	}

	@Override
	public void start() {
		Assertion.check().isNotNull(keycloakDeploymentConnectors);
		//---
		final Optional<KeycloakDeploymentConnector> keycloakDeploymentConnectorOpt = keycloakDeploymentConnectors.stream().filter(connector -> "main".equals(connector.getName())).findFirst();
		init(keycloakDeploymentConnectorOpt.get());

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		// do nothing
	}
}
