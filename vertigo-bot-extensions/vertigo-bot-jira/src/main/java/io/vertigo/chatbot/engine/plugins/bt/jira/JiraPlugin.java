package io.vertigo.chatbot.engine.plugins.bt.jira;

import javax.inject.Inject;

import io.vertigo.chatbot.engine.plugins.bt.jira.impl.JiraServerService;
import io.vertigo.chatbot.engine.plugins.bt.jira.multilingual.JiraMultilingualResources;
import io.vertigo.chatbot.executor.ExecutorPlugin;
import io.vertigo.chatbot.executor.manager.ExecutorConfigManager;
import io.vertigo.chatbot.executor.model.ExecutorGlobalConfig;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.locale.LocaleManager;
import io.vertigo.core.node.Node;
import io.vertigo.core.param.ParamManager;

public final class JiraPlugin implements ExecutorPlugin {


	private ExecutorConfigManager executorConfigManager;

	private ParamManager paramManager;

	@Inject
	private JiraServerService jiraServerService;

	@Inject
	public JiraPlugin(final LocaleManager localeManager) {
		Assertion.check()
				.isNotNull(localeManager);
		localeManager.add("io.vertigo.chatbot.engine.plugins.bt.jira.multilingual.JiraMultilingualResources", JiraMultilingualResources.values());
	}

	@Override
	public void refreshConfig(final ExecutorGlobalConfig config) throws VSystemException {
		jiraServerService.refreshConfig(config);
	}

	@Override
	public void start() {
		paramManager = Node.getNode().getComponentSpace().resolve(ParamManager.class);
		executorConfigManager = Node.getNode().getComponentSpace().resolve(ExecutorConfigManager.class);
		executorConfigManager.addPlugin(this);
	}

	@Override
	public void stop() {

	}
}
