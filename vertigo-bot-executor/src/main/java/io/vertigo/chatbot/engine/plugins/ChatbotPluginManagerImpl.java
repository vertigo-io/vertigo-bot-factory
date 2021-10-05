package io.vertigo.chatbot.engine.plugins;

import java.util.List;

import javax.inject.Inject;

import io.vertigo.chatbot.executor.ExecutorPlugin;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.locale.LocaleManager;

public class ChatbotPluginManagerImpl implements ChatbotPluginManager {

	@Inject
	public ChatbotPluginManagerImpl(
			final LocaleManager localeManager,
			final List<ExecutorPlugin> listPlugins) {
		Assertion.check()
				.isNotNull(localeManager);
		//---

		listPlugins.forEach(x -> x.init(localeManager));
	}

}
