package io.vertigo.chatbot.boot;

import io.vertigo.ui.impl.springmvc.config.AbstractVSpringMvcWebApplicationInitializer;

public class ChatbotDesignerVSpringWebApplicationInitializer extends AbstractVSpringMvcWebApplicationInitializer {

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[] { ChatbotDesignerVSpringWebConfig.class };
	}
}
