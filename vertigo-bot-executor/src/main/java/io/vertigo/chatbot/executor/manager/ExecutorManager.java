/**
 * vertigo - simple java starter
 * <p>
 * Copyright (C) 2020, Vertigo.io, team@vertigo.io
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vertigo.chatbot.executor.manager;

import com.google.gson.JsonElement;
import io.vertigo.ai.command.BtCommandManager;
import io.vertigo.chatbot.analytics.AnalyticsObjectSend;
import io.vertigo.chatbot.analytics.AnalyticsSenderServices;
import io.vertigo.chatbot.commons.LogsUtils;
import io.vertigo.chatbot.commons.domain.*;
import io.vertigo.chatbot.engine.BotEngine;
import io.vertigo.chatbot.engine.BotManager;
import io.vertigo.chatbot.engine.model.BotInput;
import io.vertigo.chatbot.engine.model.BotResponse;
import io.vertigo.chatbot.engine.model.TopicDefinition;
import io.vertigo.chatbot.executor.model.ExecutorGlobalConfig;
import io.vertigo.chatbot.executor.model.IncomeRating;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Manager;
import io.vertigo.core.util.StringUtil;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.vega.engines.webservice.json.JsonEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.*;

import static io.vertigo.chatbot.engine.BotEngine.START_TOPIC_NAME;

public class ExecutorManager implements Manager, Activeable {

    private static final Logger LOGGER = LogManager.getLogger(ExecutorManager.class);

    private final ExecutorConfigManager executorConfigManager;
    private final BotManager botManager;
    private final BtCommandManager btCommandManager;
    private final AnalyticsSenderServices analyticsSenderServices;

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

        executorConfigManager.saveConfig(globalConfig, logs);
        executorConfigManager.updateWelcomeTour(bot.getWelcomeTours());

        doLoadModel(bot, logs);

    }


    private void doLoadModel(final BotExport botExport, final StringBuilder logs) {

        //TODO remove this. Created only for migration of old models without the unreachable tag
        botExport.getTopics().forEach(topicExport -> {
            if (topicExport.getUnreachable() == null) {
                topicExport.setUnreachable(false);
            }
        });

        LogsUtils.addLogs(logs, "Node recovery...");
        final var nluThreshold = executorConfigManager.getConfig().getExecutorConfiguration().getNluThreshold().doubleValue();
        LogsUtils.logOK(logs);
        final List<TopicDefinition> topics = new ArrayList<>();
        LogsUtils.addLogs(logs, "START topic addition...");
        topics.add(TopicDefinition.of(START_TOPIC_NAME, btCommandManager.parse(botExport.getWelcomeBT())));
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

        if (!StringUtil.isBlank(botExport.getIdleBT())) {
            LogsUtils.addLogs(logs, "IDLE topic addition...");
            topics.add(TopicDefinition.of(BotEngine.IDLE_TOPIC_NAME, btCommandManager.parse(botExport.getIdleBT())));
            LogsUtils.logOK(logs);
        }

        if (!StringUtil.isBlank(botExport.getRatingBT())) {
            LogsUtils.addLogs(logs, "RATING topic addition...");
            topics.add(TopicDefinition.of(BotEngine.RATING_TOPIC_NAME, btCommandManager.parse(botExport.getRatingBT())));
            LogsUtils.logOK(logs);
        }

        for (final TopicExport topic : botExport.getTopics()) {
            LogsUtils.addLogs(logs, topic.getName(), " topic addition...");
            topics.add(TopicDefinition.of(topic.getName(), btCommandManager.parse(topic.getTopicBT()), topic.getNluTrainingSentences(), nluThreshold, topic.getUnreachable()));
            LogsUtils.logOK(logs);
        }

        executorConfigManager.updateMapContext(botExport);
        executorConfigManager.updateQuestionAnswerList(botExport);
        executorConfigManager.updateDocumentaryResourceList(botExport);
        botManager.updateConfig(topics, logs);

    }

    public void updateAttachments(final DtList<AttachmentExport> attachmentExports) {
        executorConfigManager.updateAttachments(attachmentExports);
    }

    public BotResponse startNewConversation(final BotInput input) {
        Assertion.check()
                .isNull(input.getMessage(), "No message expected");
        //--
        final var newUUID = UUID.randomUUID();

        final var botEngine = botManager.createBotEngine(newUUID);
        botEngine.saveContext(input, executorConfigManager.getContextMap());
        final var botResponse = botEngine.runTick(input);
        final ExecutorConfiguration executorConfiguration = executorConfigManager.getConfig().getExecutorConfiguration();
        analyticsSenderServices.sendEventStartToDb(newUUID, botResponse, executorConfiguration, input);
        botResponse.getMetadatas().put("sessionId", newUUID);
        if (executorConfiguration.getAvatar() != null) {
            botResponse.getMetadatas().put("avatar", executorConfiguration.getAvatar());
        }
        return botResponse;
    }

    public BotResponse handleUserMessage(final UUID sessionId, final BotInput input) {
        Assertion.check()
                .isNotNull(sessionId, "Please provide sessionId");
        //--
        final var botEngine = botManager.createBotEngine(sessionId);

        botEngine.saveContext(input, executorConfigManager.getContextMap());
        final var botResponse = botEngine.runTick(input);
        final AnalyticsObjectSend analytics = (AnalyticsObjectSend) botResponse.getMetadatas().get("analytics");

        if (analytics.getTopic().getCode().equals(START_TOPIC_NAME)) {
            input.setMessage(null);
            return this.startNewConversation(input);
        }

        final ExecutorConfiguration executorConfiguration = executorConfigManager.getConfig().getExecutorConfiguration();
        if (executorConfiguration.getAvatar() != null) {
            botResponse.getMetadatas().put("avatar", executorConfiguration.getAvatar());
        }
        botResponse.getMetadatas().put("customConfig", jsonEngine.fromJson(executorConfigManager.getConfig().getExecutorConfiguration().getCustomConfig(), JsonElement.class));
        analyticsSenderServices.sendEventToDb(sessionId, botResponse, executorConfigManager.getConfig().getExecutorConfiguration(), input);

        return botResponse;
    }

    public void rate(final UUID sessionId, final IncomeRating rating) {
        analyticsSenderServices.rate(sessionId, rating, executorConfigManager.getConfig().getExecutorConfiguration());
    }

    public Map<String, String> getContext() {
        return executorConfigManager.getContextMap();
    }

    public DtList<QuestionAnswerExport> getQuestionAnswerList() {
        return executorConfigManager.getQuestionAnswerList();
    }

    public String getWelcomeTourTechnicalCode(final String welcomeTourLabel) {
        final WelcomeTourExport welcomeTourExport =
                executorConfigManager.getConfig().getBot().getWelcomeTours().stream()
                        .filter(welcomeTour -> welcomeTour.getLabel().equals(welcomeTourLabel)).findFirst()
                        .orElseThrow(() -> new VSystemException("Welcome tour with label " + welcomeTourLabel + " doesn't exist"));

        return welcomeTourExport.getTechnicalCode();
    }

    public String getBotEmailAddress() {
        final ChatbotCustomConfigExport chatbotCustomConfig = jsonEngine.fromJson(executorConfigManager.getConfig().getExecutorConfiguration().getCustomConfig(),
                ChatbotCustomConfigExport.class);
        return chatbotCustomConfig.getBotEmailAddress();
    }

    public VFile getAttachment(final String label) {
        return executorConfigManager.getAttachment(label);
    }

    public VFile getDocumentaryResourceFile(final String label) {
        return executorConfigManager.getDocumentaryResourceFile(label);
    }

    public Optional<VFile> getWelcomeToursFile() {
        return executorConfigManager.getWelcomeToursFile();
    }

    public DtList<DocumentaryResourceExport> getDocumentaryResourceList() {
        return executorConfigManager.getDocumentaryResourceList();
    }

    public JsonElement getCustomConfig() {
        return executorConfigManager.getCustomConfig();
    }

}
