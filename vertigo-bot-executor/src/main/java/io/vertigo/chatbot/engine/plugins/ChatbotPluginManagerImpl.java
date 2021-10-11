package io.vertigo.chatbot.engine.plugins;

import java.util.List;

import javax.inject.Inject;

import io.vertigo.chatbot.executor.ExecutorPlugin;

public class ChatbotPluginManagerImpl implements ChatbotPluginManager {

	@Inject
	public ChatbotPluginManagerImpl(final List<ExecutorPlugin> listPlugins) {
	}

}
