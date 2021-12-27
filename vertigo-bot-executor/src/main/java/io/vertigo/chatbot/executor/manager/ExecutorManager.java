/**
 * vertigo - simple java starter
 *
 * Copyright (C) 2020, Vertigo.io, team@vertigo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vertigo.chatbot.executor.manager;

import com.google.gson.JsonElement;
import io.vertigo.ai.command.BtCommandManager;
import io.vertigo.chatbot.analytics.AnalyticsSenderServices;
import io.vertigo.chatbot.commons.LogsUtils;
import io.vertigo.chatbot.commons.domain.BotExport;
import io.vertigo.chatbot.commons.domain.ExecutorConfiguration;
import io.vertigo.chatbot.commons.domain.TopicExport;
import io.vertigo.chatbot.engine.BotEngine;
import io.vertigo.chatbot.engine.BotManager;
import io.vertigo.chatbot.engine.model.BotInput;
import io.vertigo.chatbot.engine.model.BotResponse;
import io.vertigo.chatbot.engine.model.TopicDefinition;
import io.vertigo.chatbot.executor.model.ExecutorGlobalConfig;
import io.vertigo.chatbot.executor.model.IncomeRating;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Manager;
import io.vertigo.core.util.StringUtil;
import io.vertigo.vega.engines.webservice.json.JsonEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ExecutorManager implements Manager, Activeable {

	private static final Logger LOGGER = LogManager.getLogger(ExecutorManager.class);

	private final ExecutorConfigManager executorConfigManager;
	private final BotManager botManager;
	private final BtCommandManager btCommandManager;
	private final AnalyticsSenderServices analyticsSenderServices;
	private ExecutorConfiguration executorConfiguration;

	@Inject
	private JsonEngine jsonEngine;

	@Inject
	public ExecutorManager(
			final ExecutorConfigManager executorConfigManager,
			final BotManager botManager,
			final BtCommandManager btCommandManager,
			final AnalyticsSenderServices analyticsSenderServices) {

		Assertion.check()
				.isNotNull(executorConfigManager)
				.isNotNull(botManager)
				.isNotNull(btCommandManager)
				.isNotNull(analyticsSenderServices);
		//--
		this.executorConfigManager = executorConfigManager;
		this.botManager = botManager;
		this.btCommandManager = btCommandManager;
		this.analyticsSenderServices = analyticsSenderServices;
	}

	@Override
	public void start() {
		executorConfiguration = executorConfigManager.getConfig().getExecutorConfiguration();
		final var botExport = executorConfigManager.getConfig().getBot();
		if (botExport == null) {
			// nothing to load
			LOGGER.info("New runner, load a bot to start using it.");
		} else {

			doLoadModel(botExport, new StringBuilder());

		}
	}

	@Override
	public void stop() {
		// nothing
	}

	public void loadModel(final BotExport bot, final ExecutorConfiguration executorConfig, final StringBuilder logs) {
		final var globalConfig = new ExecutorGlobalConfig();
		globalConfig.setBot(bot);
		globalConfig.setExecutorConfiguration(executorConfig);

		executorConfigManager.saveConfig(globalConfig);

		doLoadModel(bot, logs);

	}

	private void doLoadModel(final BotExport botExport, final StringBuilder logs) {

		LogsUtils.addLogs(logs, "Node recovery...");
		final var nluThreshold = executorConfigManager.getConfig().getExecutorConfiguration().getNluThreshold().doubleValue();
		LogsUtils.logOK(logs);
		final List<TopicDefinition> topics = new ArrayList<>();
		LogsUtils.addLogs(logs, "START topic addition...");
		topics.add(TopicDefinition.of(BotEngine.START_TOPIC_NAME, btCommandManager.parse(botExport.getWelcomeBT())));
		LogsUtils.logOK(logs);

		if (!StringUtil.isBlank(botExport.getEndBT())) {
			LogsUtils.addLogs(logs, "END topic addition...");
			topics.add(TopicDefinition.of(BotEngine.END_TOPIC_NAME, btCommandManager.parse(botExport.getEndBT())));
			LogsUtils.logOK(logs);
		}

		if (!StringUtil.isBlank(botExport.getFallbackBT())) {
			LogsUtils.addLogs(logs, "FALLBACK topic addition...");
			topics.add(TopicDefinition.of(BotEngine.FALLBACK_TOPIC_NAME, btCommandManager.parse(botExport.getFallbackBT())));
			LogsUtils.logOK(logs);
		}

		for (final TopicExport topic : botExport.getTopics()) {
			LogsUtils.addLogs(logs, topic.getName(), " topic addition...");
			topics.add(TopicDefinition.of(topic.getName(), btCommandManager.parse(topic.getTopicBT()), topic.getNluTrainingSentences(), nluThreshold));
			LogsUtils.logOK(logs);
		}

		executorConfigManager.updateMapContext(botExport);

		botManager.updateConfig(topics, logs);

	}

	public BotResponse startNewConversation(final BotInput input) {
		Assertion.check()
				.isNull(input.getMessage(), "No message expected");
		//--
		final var newUUID = UUID.randomUUID();

		final var botEngine = botManager.createBotEngine(newUUID);
		botEngine.saveContext(input, executorConfigManager.getContextMap());
		final var botResponse = botEngine.runTick(input);
		analyticsSenderServices.sendEventStartToDb(executorConfigManager.getConfig().getExecutorConfiguration());
		botResponse.getMetadatas().put("sessionId", newUUID);
		botResponse.getMetadatas().put("customConfig", jsonEngine.fromJson(executorConfigManager.getConfig().getExecutorConfiguration().getCustomConfig(), JsonElement.class));
		return botResponse;
	}

	public BotResponse handleUserMessage(final UUID sessionId, final BotInput input) {
		Assertion.check()
				.isNotNull(sessionId, "Please provide sessionId");
		//--
		final var botEngine = botManager.createBotEngine(sessionId);

		botEngine.saveContext(input, executorConfigManager.getContextMap());
		final var botResponse = botEngine.runTick(input);
		botResponse.getMetadatas().put("customConfig", jsonEngine.fromJson(executorConfigManager.getConfig().getExecutorConfiguration().getCustomConfig(), JsonElement.class));
		analyticsSenderServices.sendEventToDb(botResponse.getMetadatas(), executorConfigManager.getConfig().getExecutorConfiguration(), input);

		return botResponse;
	}

	public void rate(final IncomeRating rating) {
		analyticsSenderServices.rate(rating, executorConfigManager.getConfig().getExecutorConfiguration());
	}

	public Map<String, String> getContext() {
		return executorConfigManager.getContextMap();
	}

}
