package io.vertigo.chatbot.executor;

import io.vertigo.chatbot.executor.model.ExecutorGlobalConfig;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Plugin;

public interface ExecutorPlugin extends Plugin, Activeable {

	void refreshConfig(ExecutorGlobalConfig config) throws VSystemException;

	void start();

}
