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

import org.keycloak.KeycloakPrincipal;
import org.keycloak.representations.IDToken;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import io.vertigo.account.account.Account;
import io.vertigo.account.authentication.AuthenticationManager;
import io.vertigo.account.impl.authentication.UsernameAuthenticationToken;
import io.vertigo.account.impl.authentication.UsernamePasswordAuthenticationToken;
import io.vertigo.chatbot.designer.domain.commons.Person;
import io.vertigo.chatbot.designer.utils.UserSessionUtils;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.connectors.keycloak.KeycloakDeploymentConnector;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.lang.WrappedException;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Component;
import io.vertigo.vega.impl.servlet.filter.AbstactKeycloakDelegateAuthenticationHandler;

@Transactional
public class LoginServices extends AbstactKeycloakDelegateAuthenticationHandler implements Component, Activeable {

	@Inject
	private AuthenticationManager authenticationManager;
	@Inject
	private AuthorizationServices authorizationServices;
	@Inject
	private List<KeycloakDeploymentConnector> keycloakDeploymentConnectors;
	@Inject
	private KeycloakPersonServices keycloakPersonServices;

	//don't use anymore
	public void login(final String login, final String password) {
		final Optional<Account> loggedAccount = authenticationManager.login(new UsernamePasswordAuthenticationToken(login, password));
		if (!loggedAccount.isPresent()) {
			throw new VUserException("Login or Password invalid");
		}
		final Account account = loggedAccount.get();
		final Person person = keycloakPersonServices.getPersonToConnect(Long.valueOf(account.getId()));
		UserSessionUtils.getUserSession().setLoggedPerson(person);

		authorizationServices.addUserAuthorization(person);
	}

	public void logout(final HttpSession httpSession) {
		authenticationManager.logout();
		httpSession.invalidate();
	}

	@Override
	public boolean doLogin(final HttpServletRequest request, final HttpServletResponse response) {
		if (!UserSessionUtils.isAuthenticated()) {
			// we should have a Principal
			final KeycloakPrincipal keycloakPrincipal = (KeycloakPrincipal) request.getUserPrincipal();

			loginWithPrincipal(keycloakPrincipal);
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

	private void loginWithPrincipal(final KeycloakPrincipal principal) {
		final IDToken token = principal.getKeycloakSecurityContext().getIdToken();
		final String login = token.getPreferredUsername();
		final Account loggedAccount = authenticationManager.login(new UsernameAuthenticationToken(login)).orElseGet(
				() -> {
					// auto provisionning an account when using keycloak
					final String name = keycloakPersonServices.getNameFromToken(token);
					final String rol = keycloakPersonServices.getRoleFromToken(token);
					final String email = keycloakPersonServices.getEmailFromToken(token);
					keycloakPersonServices.initPerson(login, name, rol);
					return authenticationManager.login(new UsernameAuthenticationToken(login)).get();
				});
		final Person person = keycloakPersonServices.getPersonToConnect(Long.valueOf(loggedAccount.getId()));
		//For migration purpose only TODO remove when done
		if (person.getEmail() == null && keycloakPersonServices.getEmailFromToken(token) != null) {
			person.setEmail(keycloakPersonServices.getEmailFromToken(token));
			keycloakPersonServices.updatePerson(person);
		}
		UserSessionUtils.getUserSession().setLoggedPerson(person);
		UserSessionUtils.getUserSession().setLocale(Locale.FRANCE);
		authorizationServices.addUserAuthorization(person);
	}

	public void reloadAuthorizations() {
		authorizationServices.reloadUserAuthorization(UserSessionUtils.getLoggedPerson());
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
