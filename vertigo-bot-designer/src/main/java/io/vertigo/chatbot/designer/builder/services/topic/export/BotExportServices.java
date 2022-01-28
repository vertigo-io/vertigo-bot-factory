package io.vertigo.chatbot.designer.builder.services.topic.export;

import io.vertigo.chatbot.commons.LogsUtils;
import io.vertigo.chatbot.commons.domain.BotExport;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ConfluenceSettingExport;
import io.vertigo.chatbot.commons.domain.JiraSettingExport;
import io.vertigo.chatbot.commons.domain.topic.KindTopicEnum;
import io.vertigo.chatbot.designer.builder.services.ConfluenceSettingServices;
import io.vertigo.chatbot.designer.builder.services.JiraFieldSettingServices;
import io.vertigo.chatbot.designer.builder.services.JiraSettingServices;
import io.vertigo.chatbot.designer.builder.services.WelcomeTourServices;
import io.vertigo.chatbot.designer.builder.services.bot.ContextValueServices;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;

import javax.inject.Inject;
import java.util.Optional;

@Transactional
public class BotExportServices implements Component {

	@Inject
	private TopicExportServices topicExportServices;

	@Inject
	private ContextValueServices contextValueServices;

	@Inject
	private WelcomeTourServices welcomeTourServices;

	@Inject
	private ConfluenceSettingServices confluenceSettingServices;

	@Inject
	private JiraSettingServices jiraSettingServices;

	@Inject
	private JiraFieldSettingServices jiraFieldSettingServices;

	public BotExport exportBot(final Chatbot bot, final StringBuilder logs) {
		final BotExport export = new BotExport();
		export.setBot(bot);
		LogsUtils.addLogs(logs, "Active topics export");
		LogsUtils.breakLine(logs);
		export.setTopics(topicExportServices.exportActiveTopics(bot, logs));
		export.setWelcomeTours(welcomeTourServices.exportBotWelcomeTours(bot, logs));
		export.setFallbackBT(topicExportServices.getBasicBt(bot, KindTopicEnum.FAILURE.name(), logs));
		export.setEndBT(topicExportServices.getBasicBt(bot, KindTopicEnum.END.name(), logs));
		export.setWelcomeBT(topicExportServices.getBasicBt(bot, KindTopicEnum.START.name(), logs));
		export.setIdleBT(topicExportServices.getBasicBt(bot, KindTopicEnum.IDLE.name(), logs));
		export.setMapContext(contextValueServices.exportContextValuesToMapByBot(bot, logs));
		export.setJiraFieldSetting(jiraFieldSettingServices.exportJiraSetting(bot.getBotId()));
		return export;
	}

	public Optional<ConfluenceSettingExport> exportConfluenceSetting(final long botId, final long nodId) {
		return confluenceSettingServices.exportConfluenceSetting(botId, nodId);
	}

	public Optional<JiraSettingExport> exportJiraSetting(final long botId, final long nodId) {
		return jiraSettingServices.exportJiraSetting(botId, nodId);
	}

}
