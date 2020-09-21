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

import org.springframework.context.annotation.ComponentScan;

import io.vertigo.app.Home;
import io.vertigo.core.param.Param;
import io.vertigo.core.param.ParamManager;
import io.vertigo.ui.impl.springmvc.config.VSpringWebConfig;

@ComponentScan({
		"io.vertigo.chatbot.designer.commons.controllers",
		"io.vertigo.chatbot.designer.admin.controllers",
		"io.vertigo.chatbot.designer.builder.controllers",
		"io.vertigo.chatbot.designer.analytics.controllers",
})
public class ChatbotDesignerVSpringWebConfig extends VSpringWebConfig {
	// nothing basic config is enough

	@Override
	protected boolean isDevMode() {
		final ParamManager paramManager = Home.getApp().getComponentSpace().resolve(ParamManager.class);

		return paramManager.getOptionalParam("devMode")
				.map(Param::getValueAsBoolean)
				.orElse(false);
	}
}
