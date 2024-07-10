package io.vertigo.chatbot.engine.plugins.bt.jira;

import jakarta.inject.Inject;

import io.vertigo.chatbot.engine.plugins.bt.jira.impl.JiraServerService;
import io.vertigo.chatbot.engine.plugins.bt.jira.multilingual.JiraMultilingualResources;
import io.vertigo.chatbot.executor.ExecutorPlugin;
import io.vertigo.chatbot.executor.manager.ExecutorConfigManager;
import io.vertigo.chatbot.executor.model.ExecutorGlobalConfig;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.locale.LocaleManager;
import io.vertigo.core.node.Node;

public final class JiraPlugin implements ExecutorPlugin {


	private ExecutorConfigManager executorConfigManager;

	@Inject
	private JiraServerService jiraServerService;

	@Inject
	public JiraPlugin(final LocaleManager localeManager) {
		Assertion.check()
				.isNotNull(localeManager);
		localeManager.add("io.vertigo.chatbot.engine.plugins.bt.jira.multilingual.JiraMultilingualResources", JiraMultilingualResources.values());
	}

	@Override
	public void refreshConfig(final ExecutorGlobalConfig config, final StringBuilder logs) throws VSystemException {
		jiraServerService.refreshConfig(config, logs);
	}

	@Override
	public void start() {
		executorConfigManager = Node.getNode().getComponentSpace().resolve(ExecutorConfigManager.class);
		executorConfigManager.addPlugin(this);
	}

	@Override
	public void stop() {

	}
}
