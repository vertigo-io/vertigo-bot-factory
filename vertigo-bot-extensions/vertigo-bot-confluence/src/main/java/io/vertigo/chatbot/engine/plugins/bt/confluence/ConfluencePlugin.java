package io.vertigo.chatbot.engine.plugins.bt.confluence;

import javax.inject.Inject;

import io.vertigo.chatbot.engine.plugins.bt.confluence.multilingual.ConfluenceMultilingualResources;
import io.vertigo.chatbot.executor.ExecutorPlugin;
import io.vertigo.core.locale.LocaleManager;

public final class ConfluencePlugin implements ExecutorPlugin {

	@Inject
	public LocaleManager localeManager;
	//String locale = paramManager.getParam("LOCALE").getValueAsString();

	@Override
	public void init() {
		//TODO : initialize Locale
		localeManager.add("io.vertigo.chatbot.engine.plugins.bt.confluence.multilingual.ConfluenceMultilingualResources", ConfluenceMultilingualResources.values());

	}

}
