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
package io.vertigo.chatbot.designer.boot;

import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import io.vertigo.core.node.Node;
import io.vertigo.core.param.Param;
import io.vertigo.core.param.ParamManager;
import io.vertigo.ui.impl.springmvc.config.VSpringWebConfig;
import io.vertigo.ui.impl.springmvc.config.VertigoLocaleResolver;

/**
 * Configures the designer Spring MVC application and its Vertigo UI
 * safeguards.
 *
 * @author Chatbot Team
 */
@ComponentScan({
		"io.vertigo.chatbot.designer.commons.controllers",
		"io.vertigo.chatbot.designer.admin.controllers",
		"io.vertigo.chatbot.designer.builder.controllers",
		"io.vertigo.chatbot.designer.analytics.controllers",
})
public class ChatbotDesignerVSpringWebConfig extends VSpringWebConfig {
	// nothing basic config is enough

	private static final String[] CUSTOM_UI_COMPONENTS_NAME = {
			"chatbotDesigner/popup", "chatbotDesigner/popup-ajax", "chatbotDesigner/column-sample", "chatbotDesigner/search-table"
	};

	/**
	 * Returns the project-specific Vertigo UI components.
	 *
	 * @return custom component names
	 */
	@Override
	protected Set<String> getCustomComponentNames() {
		return Set.of(CUSTOM_UI_COMPONENTS_NAME);
	}

	/**
	 * Determines whether development mode is enabled.
	 *
	 * @return {@code true} when development mode is configured
	 */
	@Override
	protected boolean isDevMode() {
		final ParamManager paramManager = Node.getNode().getComponentSpace().resolve(ParamManager.class);

		return paramManager.getOptionalParam("devMode")
				.map(Param::getValueAsBoolean)
				.orElse(false);
	}

	/**
	 * Registers a guard before Vertigo serializes AJAX ViewContext responses.
	 *
	 * @param handlers return-value handlers
	 */
	@Override
	public void addReturnValueHandlers(final List<HandlerMethodReturnValueHandler> handlers) {
		handlers.add(new ViewContextListLimitReturnValueHandler());
		super.addReturnValueHandlers(handlers);
	}

	/**
	 * Registers a guard before Vertigo renders server-side views.
	 *
	 * @param registry interceptor registry
	 */
	@Override
	public void addInterceptors(final InterceptorRegistry registry) {
		super.addInterceptors(registry);
		registry.addInterceptor(new ViewContextListLimitInterceptor());
	}

	/**
	 * Creates the Vertigo locale resolver.
	 *
	 * @return locale resolver
	 */
	@Bean
	public LocaleResolver localeResolver() {
		return new VertigoLocaleResolver();
	}
}
