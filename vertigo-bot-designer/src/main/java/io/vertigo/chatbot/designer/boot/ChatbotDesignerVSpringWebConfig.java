package io.vertigo.chatbot.designer.boot;

import org.springframework.context.annotation.ComponentScan;

import io.vertigo.ui.impl.springmvc.config.VSpringWebConfig;

@ComponentScan({
	"io.vertigo.chatbot.designer.commons.controllers",
	"io.vertigo.chatbot.designer.admin.controllers",
	"io.vertigo.chatbot.designer.builder.controllers",
	"io.vertigo.chatbot.designer.analytics.controllers",
})
public class ChatbotDesignerVSpringWebConfig extends VSpringWebConfig {
	// nothing, basic config is enough

}
