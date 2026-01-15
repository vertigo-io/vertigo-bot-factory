package io.vertigo.chatbot.designer.builder.services.training;

import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.LogsUtils;
import io.vertigo.chatbot.commons.domain.AttachmentExport;
import io.vertigo.chatbot.commons.domain.BotExport;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotCustomConfig;
import io.vertigo.chatbot.commons.domain.ChatbotCustomConfigExport;
import io.vertigo.chatbot.commons.domain.ConfluenceSettingExport;
import io.vertigo.chatbot.commons.domain.FontFamily;
import io.vertigo.chatbot.commons.domain.FontFamilyEnum;
import io.vertigo.chatbot.commons.domain.JiraSettingExport;
import io.vertigo.chatbot.commons.domain.topic.KindTopicEnum;
import io.vertigo.chatbot.designer.builder.services.*;
import io.vertigo.chatbot.designer.builder.services.bot.AttachmentServices;
import io.vertigo.chatbot.designer.builder.services.bot.ChatbotCustomConfigServices;
import io.vertigo.chatbot.designer.builder.services.bot.ContextValueServices;
import io.vertigo.chatbot.designer.builder.services.questionanswer.QuestionAnswerServices;
import io.vertigo.chatbot.designer.builder.services.topic.export.TopicExportServices;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.data.model.DtList;

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

	@Inject
	private AttachmentServices attachmentServices;

	@Inject
	private ChatbotCustomConfigServices chatbotCustomConfigServices;

	@Inject
	private QuestionAnswerServices questionAnswerServices;

	@Inject
	private DocumentaryResourceServices documentaryResourceServices;

	@Inject
	private GlobalVariableExportService globalVariableExportService;

	@Inject
	private FontFamilyServices fontFamilyServices;

	public BotExport exportBot(final Chatbot bot, final StringBuilder logs,
							   final StringBuilder trainingDataLogs) {
		final BotExport export = new BotExport();
		export.setBot(bot);
		LogsUtils.addLogs(logs, "Active topics export");
		LogsUtils.breakLine(logs);
		export.setTopics(topicExportServices.exportActiveTopics(bot, logs, trainingDataLogs));
		export.setWelcomeTours(welcomeTourServices.exportBotWelcomeTours(bot, logs));
		export.setFallbackBT(topicExportServices.getBasicBt(bot, KindTopicEnum.FAILURE.name(), logs));
		export.setEndBT(topicExportServices.getBasicBt(bot, KindTopicEnum.END.name(), logs));
		export.setWelcomeBT(topicExportServices.getBasicBt(bot, KindTopicEnum.START.name(), logs));
		export.setIdleBT(topicExportServices.getBasicBt(bot, KindTopicEnum.IDLE.name(), logs));
		export.setRatingBT(topicExportServices.getBasicBt(bot, KindTopicEnum.RATING.name(), logs));
		export.setMapContext(contextValueServices.exportContextValuesToMapByBot(bot, logs));
		export.setJiraFieldSetting(jiraFieldSettingServices.exportJiraSetting(bot));
		export.setQuestionAnswerList(questionAnswerServices.exportActiveQuestionsAnswers(bot, logs));
		export.setDocumentaryResources(documentaryResourceServices.exportDocumentaryResourceByBot(bot, logs));
		export.setGlobalVariables(globalVariableExportService.exportGlobalVariables(bot, logs));
		return export;
	}

	public DtList<AttachmentExport> exportBotAttachments(final Chatbot chatbot, final StringBuilder logs) {
		return attachmentServices.exportAttachmentByBot(chatbot, logs);
	}

	public Optional<ConfluenceSettingExport> exportConfluenceSetting(final long botId, final long nodId) {
		return confluenceSettingServices.exportConfluenceSetting(botId, nodId);
	}

	public Optional<JiraSettingExport> exportJiraSetting(final long botId, final long nodId) {
		final ChatbotCustomConfig chatbotCustomConfig = chatbotCustomConfigServices.getChatbotCustomConfigByBotId(botId);
		return jiraSettingServices.exportJiraSetting(botId, nodId, chatbotCustomConfig.getJsmMode());
	}

	public ChatbotCustomConfigExport exportChatbotCustomSettings(final long botId) {
		final ChatbotCustomConfig chatbotCustomConfig = chatbotCustomConfigServices.getChatbotCustomConfigByBotId(botId);
		final FontFamily fontFamily = fontFamilyServices.findByFofCd(chatbotCustomConfig.getFofCd() != null
				? chatbotCustomConfig.getFofCd() : FontFamilyEnum.ARIAL.name());
		final ChatbotCustomConfigExport chatbotCustomConfigExport = new ChatbotCustomConfigExport();
		chatbotCustomConfigExport.setBotEmailAddress(chatbotCustomConfig.getBotEmailAddress());
		chatbotCustomConfigExport.setTotalMaxAttachmentSize(chatbotCustomConfig.getTotalMaxAttachmentSize());
		chatbotCustomConfigExport.setBackgroundColor(chatbotCustomConfig.getBackgroundColor());
		chatbotCustomConfigExport.setFontColor(chatbotCustomConfig.getFontColor());
		chatbotCustomConfigExport.setBotMessageLinkColor(chatbotCustomConfig.getBotMessageLinkColor());
		chatbotCustomConfigExport.setBotMessageBackgroundColor(chatbotCustomConfig.getBotMessageBackgroundColor());
		chatbotCustomConfigExport.setDisableNlu(chatbotCustomConfig.getDisableNlu());
		chatbotCustomConfigExport.setBotMessageFontColor(chatbotCustomConfig.getBotMessageFontColor());
		chatbotCustomConfigExport.setUserMessageBackgroundColor(chatbotCustomConfig.getUserMessageBackgroundColor());
		chatbotCustomConfigExport.setUserMessageFontColor(chatbotCustomConfig.getUserMessageFontColor());
		chatbotCustomConfigExport.setBotRatingColor(chatbotCustomConfig.getBotRatingColor());
		chatbotCustomConfigExport.setDisplayAvatar(chatbotCustomConfig.getDisplayAvatar());
		chatbotCustomConfigExport.setReinitializationButton(chatbotCustomConfig.getReinitializationButton());
		chatbotCustomConfigExport.setFontFamily(fontFamily.getLabel());
		chatbotCustomConfigExport.setJiraCheckBeforeCreate(chatbotCustomConfig.getJiraCheckBeforeCreate());
		chatbotCustomConfigExport.setChatbotDisplay(chatbotCustomConfig.getChatbotDisplay());
		chatbotCustomConfigExport.setQandaDisplay(chatbotCustomConfig.getQandaDisplay());
		chatbotCustomConfigExport.setDocumentaryResourceDisplay(chatbotCustomConfig.getDocumentaryResourceDisplay());
		return chatbotCustomConfigExport;
	}

}
