package io.vertigo.chatbot.boot;

import org.springframework.context.annotation.ComponentScan;

import io.vertigo.ui.impl.springmvc.config.VSpringWebConfig;

@ComponentScan({"io.vertigo.chatbot.factory.controllers" })
public class ChatbotFactoryVSpringWebConfig extends VSpringWebConfig {
	// nothing basic config is enough

}
