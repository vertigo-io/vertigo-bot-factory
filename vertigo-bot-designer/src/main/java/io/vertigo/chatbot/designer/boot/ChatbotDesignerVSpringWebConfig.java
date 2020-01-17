package io.vertigo.chatbot.designer.boot;

import java.util.Optional;

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
		final Optional<Param> devModeOpt = paramManager.getOptionalParam("devMode");
		if (devModeOpt.isPresent()) {
			return devModeOpt.get().getValueAsBoolean();
		}
		return false;
	}
}
