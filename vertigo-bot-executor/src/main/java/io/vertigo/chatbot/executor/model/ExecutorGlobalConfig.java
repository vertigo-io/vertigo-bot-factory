package io.vertigo.chatbot.executor.model;

import io.vertigo.chatbot.commons.domain.BotExport;
import io.vertigo.chatbot.commons.domain.ExecutorConfiguration;

public class ExecutorGlobalConfig {

	private ExecutorConfiguration executorConfiguration;
	private BotExport bot;

	/**
	 * @return the executorConfiguration
	 */
	public ExecutorConfiguration getExecutorConfiguration() {
		return executorConfiguration;
	}

	/**
	 * @param executorConfiguration the executorConfiguration to set
	 */
	public void setExecutorConfiguration(final ExecutorConfiguration executorConfiguration) {
		this.executorConfiguration = executorConfiguration;
	}

	/**
	 * @return the bot
	 */
	public BotExport getBot() {
		return bot;
	}

	/**
	 * @param bot the bot to set
	 */
	public void setBot(final BotExport bot) {
		this.bot = bot;
	}
}
