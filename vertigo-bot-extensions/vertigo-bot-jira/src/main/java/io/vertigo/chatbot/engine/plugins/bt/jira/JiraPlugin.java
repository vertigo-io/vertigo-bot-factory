package io.vertigo.chatbot.engine.plugins.bt.jira;

import io.vertigo.chatbot.commons.domain.JiraSettingExport;
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

import javax.inject.Inject;

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
	public void refreshConfig(ExecutorGlobalConfig config) throws VSystemException {
		//TODO This is only for migration purposes, remove this when done
		if (config.getBot().getJiraSetting() == null) {
			JiraSettingExport jiraSettingExport = new JiraSettingExport();
			jiraSettingExport.setUrl(paramManager.getParam("JIRA_URL").getValueAsString());
			jiraSettingExport.setLogin(paramManager.getParam("JIRA_USER").getValueAsString());
			jiraSettingExport.setPassword(paramManager.getParam("JIRA_PWD").getValueAsString());
			jiraSettingExport.setProject(paramManager.getParam("JIRA_PROJECT").getValueAsString());
			config.getBot().setJiraSetting(jiraSettingExport);
		}
		jiraServerService.refreshConfig(config.getBot().getJiraSetting(), config.getBot().getJiraFieldSetting());
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
