package io.vertigo.chatbot.engine.plugins.bt.confluence;

import io.vertigo.chatbot.commons.domain.ConfluenceSettingExport;
import io.vertigo.chatbot.engine.plugins.bt.confluence.impl.ConfluenceServerServices;
import io.vertigo.chatbot.engine.plugins.bt.confluence.multilingual.ConfluenceMultilingualResources;
import io.vertigo.chatbot.executor.ExecutorPlugin;
import io.vertigo.chatbot.executor.manager.ExecutorConfigManager;
import io.vertigo.chatbot.executor.model.ExecutorGlobalConfig;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.locale.LocaleManager;
import io.vertigo.core.node.Node;
import io.vertigo.core.param.ParamManager;

import javax.inject.Inject;

public final class ConfluencePlugin implements ExecutorPlugin {

	@Inject
	private ConfluenceServerServices confluenceServerServices;

	private ExecutorConfigManager executorConfigManager;

	private ParamManager paramManager;

	@Inject
	public ConfluencePlugin(final LocaleManager localeManager, final ParamManager paramManager) {
		Assertion.check()
				.isNotNull(localeManager)
				.isNotNull(paramManager);
		//-----
		//String locale = paramManager.getParam("LOCALE").getValueAsString();
		//TODO : initialize Locale
		localeManager.add("io.vertigo.chatbot.engine.plugins.bt.confluence.multilingual.ConfluenceMultilingualResources", ConfluenceMultilingualResources.values());
	}

	@Override
	public void refreshConfig(ExecutorGlobalConfig config) throws VSystemException {
		//TODO This is only for migration purposes, remove this when done
		if (config.getBot().getConfluenceSetting() == null) {
			ConfluenceSettingExport confluenceSettingExport = new ConfluenceSettingExport();
			confluenceSettingExport.setUrl(paramManager.getParam("CONFLUENCE_URL").getValueAsString());
			confluenceSettingExport.setLogin(paramManager.getParam("CONFLUENCE_USER").getValueAsString());
			confluenceSettingExport.setPassword(paramManager.getParam("CONFLUENCE_PWD").getValueAsString());
			confluenceSettingExport.setNumberOfResults(paramManager.getParam("CONFLUENCE_LIMIT").getValueAsLong());
			config.getBot().setConfluenceSetting(confluenceSettingExport);
		}
		confluenceServerServices.refreshConfig(config);
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
