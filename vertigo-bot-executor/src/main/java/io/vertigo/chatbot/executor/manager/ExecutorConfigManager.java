package io.vertigo.chatbot.executor.manager;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.ExecutorConfiguration;
import io.vertigo.commons.transaction.VTransactionManager;
import io.vertigo.commons.transaction.VTransactionWritable;
import io.vertigo.core.component.Activeable;
import io.vertigo.core.component.Manager;
import io.vertigo.dynamo.kvstore.KVStoreManager;

public class ExecutorConfigManager implements Manager, Activeable {

	@Inject
	private KVStoreManager kVStoreManager;

	@Inject
	private VTransactionManager transactionManager;

	private ExecutorConfiguration executorConfiguration;

	@Override
	public void start() {
		try (final VTransactionWritable transaction = transactionManager.createCurrentTransaction()) {
			executorConfiguration = kVStoreManager.find("ExecutorConfig", "executorConfiguration", ExecutorConfiguration.class).orElse(null);
		}
	}

	@Override
	public void stop() {
		// Nothing
	}


	public void loadConfig(final ExecutorConfiguration executorConfiguration) {
		this.executorConfiguration = executorConfiguration;

		kVStoreManager.put("ExecutorConfig", "executorConfiguration", executorConfiguration);
	}

	/**
	 * @return the executorConfiguration
	 */
	public ExecutorConfiguration getExecutorConfiguration() {
		return executorConfiguration;
	}



}
