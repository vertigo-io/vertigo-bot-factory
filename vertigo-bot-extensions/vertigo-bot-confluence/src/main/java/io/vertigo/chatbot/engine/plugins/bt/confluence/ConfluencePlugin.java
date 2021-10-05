package io.vertigo.chatbot.engine.plugins.bt.confluence;

import io.vertigo.chatbot.engine.plugins.bt.confluence.multilingual.ConfluenceMultilingualResources;
import io.vertigo.chatbot.executor.ExecutorPlugin;
import io.vertigo.core.locale.LocaleManager;

public final class ConfluencePlugin implements ExecutorPlugin {

	@Override
	public void init(final LocaleManager localeManager) {
		localeManager.add("io.vertigo.chatbot.engine.plugins.bt.confluence.multilingual.ConfluenceMultilingualResources", ConfluenceMultilingualResources.values());

	}

}
