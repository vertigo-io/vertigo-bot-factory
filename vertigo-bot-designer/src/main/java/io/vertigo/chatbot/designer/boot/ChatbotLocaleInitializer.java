package io.vertigo.chatbot.designer.boot;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.DtResources;
import io.vertigo.chatbot.commons.multilingual.topics.TopicsMultilingualResources;
import io.vertigo.chatbot.designer.domain.commons.EnumResource;
import io.vertigo.core.locale.LocaleManager;
import io.vertigo.core.node.component.ComponentInitializer;

public class ChatbotLocaleInitializer implements ComponentInitializer {

	@Inject
	private LocaleManager localeManager;

	@Override
	public void init() {
		localeManager.add("io.vertigo.chatbot.commons.domain.DtResources", DtResources.values());
		localeManager.add("io.vertigo.chatbot.designer.domain.DtResources", io.vertigo.chatbot.designer.domain.DtResources.values());
		localeManager.add("io.vertigo.chatbot.designer.domain.admin.DtResources", io.vertigo.chatbot.designer.domain.admin.DtResources.values());
		localeManager.add("io.vertigo.chatbot.designer.domain.commons.DtResources", io.vertigo.chatbot.designer.domain.commons.DtResources.values());
		localeManager.add("io.vertigo.chatbot.commons.domain.topic.DtResources", io.vertigo.chatbot.commons.domain.topic.DtResources.values());
		localeManager.add("io.vertigo.chatbot.designer.domain.commons.EnumResources", EnumResource.values());
		localeManager.add("io.vertigo.chatbot.commons.multilingual.topics.TopicsMultilingualResources", TopicsMultilingualResources.values());
	}

}
