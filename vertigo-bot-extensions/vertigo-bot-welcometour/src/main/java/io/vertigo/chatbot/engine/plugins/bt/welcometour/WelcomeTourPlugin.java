package io.vertigo.chatbot.engine.plugins.bt.welcometour;

import io.vertigo.chatbot.executor.ExecutorPlugin;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.locale.LocaleManager;
import io.vertigo.core.param.ParamManager;

import javax.inject.Inject;

public class WelcomeTourPlugin implements ExecutorPlugin {

	@Inject
	public WelcomeTourPlugin(final LocaleManager localeManager, final ParamManager paramManager) {
		Assertion.check()
				.isNotNull(localeManager)
				.isNotNull(paramManager);
	}
}
