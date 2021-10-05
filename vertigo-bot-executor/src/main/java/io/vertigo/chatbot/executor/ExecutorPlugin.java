package io.vertigo.chatbot.executor;

import io.vertigo.core.locale.LocaleManager;
import io.vertigo.core.node.component.Plugin;

public interface ExecutorPlugin extends Plugin {

	void init(final LocaleManager localeManager);

}
