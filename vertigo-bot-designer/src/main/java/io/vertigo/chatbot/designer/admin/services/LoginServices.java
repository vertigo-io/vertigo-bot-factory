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

import com.nimbusds.oauth2.sdk.AuthorizationSuccessResponse;
import com.nimbusds.openid.connect.sdk.OIDCClaimsRequest;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import io.vertigo.account.account.Account;
import io.vertigo.account.authentication.AuthenticationManager;
import io.vertigo.account.impl.authentication.UsernameAuthenticationToken;
import io.vertigo.account.impl.authentication.UsernamePasswordAuthenticationToken;
import io.vertigo.account.security.VSecurityManager;
import io.vertigo.chatbot.designer.domain.commons.Person;
import io.vertigo.chatbot.designer.utils.UserSessionUtils;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.vega.plugins.authentication.oidc.OIDCAppLoginHandler;

@Transactional
public class LoginServices implements Component, OIDCAppLoginHandler {

	@Inject
	private AuthenticationManager authenticationManager;
	@Inject
	private AuthorizationServices authorizationServices;

	@Inject
	private KeycloakPersonServices keycloakPersonServices;

	@Override
	public String doLogin(HttpServletRequest request, Map<String, Object> claims, AuthorizationSuccessResponse rawResult, Optional<String> requestedUrl) {
		loginWithPrincipal(claims);
		return requestedUrl.orElse("/bots/");
	}

	@Override
	public String doLogout(HttpServletRequest request) {
		return "/";
	}

	public String logout() {
		return "redirect:/OIDC/logout";
	}


	private void loginWithPrincipal(final Map<String, Object> claims) {
		final String login = (String) claims.get("preferred_username");
		final String name = (String) claims.get("name");
		final String email = (String) claims.get("email");
		final String role = keycloakPersonServices.getRoleFromClaims(claims);
		final Account loggedAccount = authenticationManager.login(new UsernameAuthenticationToken(login)).orElseGet(
				() -> {
					keycloakPersonServices.initPerson(login, name, email, role);
					return authenticationManager.login(new UsernameAuthenticationToken(login)).get();
				});
		final Person person = keycloakPersonServices.getPersonToConnect(Long.valueOf(loggedAccount.getId()));
		UserSessionUtils.getUserSession().setLoggedPerson(person);
		UserSessionUtils.getUserSession().setLocale(Locale.FRANCE);
		authorizationServices.addUserAuthorization(person);
	}

	public void reloadAuthorizations() {
		authorizationServices.reloadUserAuthorization(UserSessionUtils.getLoggedPerson());
	}
}
