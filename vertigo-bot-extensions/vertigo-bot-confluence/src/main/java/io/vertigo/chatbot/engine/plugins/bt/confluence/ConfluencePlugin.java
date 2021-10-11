package io.vertigo.chatbot.engine.plugins.bt.confluence;

import javax.inject.Inject;

import io.vertigo.chatbot.engine.plugins.bt.confluence.multilingual.ConfluenceMultilingualResources;
import io.vertigo.chatbot.executor.ExecutorPlugin;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.locale.LocaleManager;
import io.vertigo.core.param.ParamManager;

public final class ConfluencePlugin implements ExecutorPlugin {

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

}
