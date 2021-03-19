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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.vertigo.chatbot.designer.admin.services.LoginServices;
import io.vertigo.chatbot.designer.utils.UserSessionUtils;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.lang.WrappedException;
import io.vertigo.core.util.StringUtil;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;
import io.vertigo.vega.webservice.validation.UiMessageStack;

@Controller
@RequestMapping("/login")
public class LoginController extends AbstractVSpringMvcController {

	private static final String REDIRECT_HOME = "redirect:/";
	private static final String REDIRECT_LOGIN = "redirect:/login/";

	private static final ViewContextKey<String> loginKey = ViewContextKey.of("login");
	private static final ViewContextKey<String> passwordKey = ViewContextKey.of("password");

	@Inject
	private LoginServices loginServices;

	@GetMapping("/")
	public String initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @RequestParam(name = "code", required = false) final Integer code) {
		if (!UserSessionUtils.isAuthenticated()) {
			if (code != null && code.equals(401)) {
				uiMessageStack.warning("You have been disconnected");
				return REDIRECT_LOGIN;
			} else if (code != null && code.equals(400)) {
				uiMessageStack.warning("You have been inactive for too long, your login has expired");
				return REDIRECT_LOGIN;
			}
			viewContext.publishRef(loginKey, "");
			viewContext.publishRef(passwordKey, "");
			return null;
		}
		return REDIRECT_HOME;
	}

	@PostMapping("/_login")
	public String doLogin(@RequestParam("login") final String login, @RequestParam("password") final String password) {
		if (StringUtil.isBlank(login) || StringUtil.isBlank(password)) {
			throw new VUserException("Login and Password are mandatory");
		}
		loginServices.login(login, password);
		return REDIRECT_HOME;
	}

	@GetMapping("/_logout")
	public String logout(final HttpServletRequest request, final HttpSession httpSession) {
		try {
			request.logout();
		} catch (final ServletException e) {
			throw WrappedException.wrap(e);
		}
		loginServices.logout(httpSession);
		return REDIRECT_LOGIN;
	}

	@GetMapping("/_reloadAuthorizations")
	public String reloadAuthorizations() {
		loginServices.reloadAuthorizations();
		return REDIRECT_HOME;
	}

}
