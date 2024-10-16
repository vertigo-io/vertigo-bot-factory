package io.vertigo.chatbot.designer.builder.services.bot;

import java.time.LocalDate;
import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.authorization.GlobalAuthorizations;
import io.vertigo.chatbot.authorization.SecuredEntities.ChatbotOperations;
import io.vertigo.chatbot.commons.dao.ChatbotDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotCustomConfig;
import io.vertigo.chatbot.commons.domain.topic.TopicCategory;
import io.vertigo.chatbot.designer.analytics.multilingual.AnalyticsMultilingualResources;
import io.vertigo.chatbot.designer.builder.monitoring.MonitoringPAO;
import io.vertigo.chatbot.designer.builder.services.ConfluenceSettingServices;
import io.vertigo.chatbot.designer.builder.services.DocumentaryResourceServices;
import io.vertigo.chatbot.designer.builder.services.HistoryServices;
import io.vertigo.chatbot.designer.builder.services.JiraFieldSettingServices;
import io.vertigo.chatbot.designer.builder.services.JiraSettingServices;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.designer.builder.services.ResponsesButtonServices;
import io.vertigo.chatbot.designer.builder.services.SavedTrainingServices;
import io.vertigo.chatbot.designer.builder.services.TrainingServices;
import io.vertigo.chatbot.designer.builder.services.UnknownSentencesServices;
import io.vertigo.chatbot.designer.builder.services.UtterTextServices;
import io.vertigo.chatbot.designer.builder.services.WelcomeTourServices;
import io.vertigo.chatbot.designer.builder.services.questionanswer.QuestionAnswerCategoryServices;
import io.vertigo.chatbot.designer.builder.services.questionanswer.QuestionAnswerServices;
import io.vertigo.chatbot.designer.builder.services.topic.DictionaryEntityServices;
import io.vertigo.chatbot.designer.builder.services.topic.ScriptIntentionServices;
import io.vertigo.chatbot.designer.builder.services.topic.SmallTalkServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicCategoryServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicLabelServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.chatbot.designer.commons.services.DesignerFileServices;
import io.vertigo.chatbot.designer.dao.monitoring.AlertingEventDAO;
import io.vertigo.chatbot.designer.utils.AuthorizationUtils;
import io.vertigo.chatbot.designer.utils.DateUtils;
import io.vertigo.chatbot.designer.utils.UserSessionUtils;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.chatbot.domain.DtDefinitions.ChatbotFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.daemon.DaemonScheduled;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.locale.LocaleManager;
import io.vertigo.core.locale.MessageText;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criteria;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.datastore.impl.filestore.model.StreamFile;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;


@Transactional
public class ChatbotServices implements Component {

	@Inject
	private ChatbotDAO chatbotDAO;

	@Inject
	private ResponsesButtonServices responsesButtonServices;

	@Inject
	private UtterTextServices utterTextServices;

	@Inject
	private SmallTalkServices smallTalkServices;

	@Inject
	private ScriptIntentionServices scriptIntentionServices;

	@Inject
	private TopicServices topicServices;

	@Inject
	private DesignerFileServices designerFileServices;

	@Inject
	private ChatbotProfilServices chatbotProfilServices;

	@Inject
	private TrainingServices trainingServices;

	@Inject
	private NodeServices nodeServices;

	@Inject
	private TopicCategoryServices topicCategoryServices;

	@Inject
	private TopicLabelServices topicLabelServices;

	@Inject
	private ChatbotCustomConfigServices chatbotCustomConfigServices;

	@Inject
	private HistoryServices historyServices;

	@Inject
	private UnknownSentencesServices unknownSentencesServices;

	@Inject
	private WelcomeTourServices welcomeTourServices;

	@Inject
	private JiraFieldSettingServices jiraFieldSettingServices;

	@Inject
	private SavedTrainingServices savedTrainingServices;

	@Inject
	private ContextValueServices contextValueServices;

	@Inject
	private DictionaryEntityServices dictionaryEntityServices;

	@Inject
	private ConfluenceSettingServices confluenceSettingServices;

	@Inject
	private JiraSettingServices jiraSettingServices;

	@Inject
	private DocumentaryResourceServices documentaryResourceServices;

	@Inject
	private QuestionAnswerServices questionAnswerServices;

	@Inject
	private QuestionAnswerCategoryServices questionAnswerCategoryServices;

	@Inject
	private ContextPossibleValueServices contextPossibleValueServices;

	@Inject
	private ContextEnvironmentServices contextEnvironmentServices;

	@Inject
	private MonitoringPAO monitoringPAO;

	@Inject
	private AlertingEventDAO alertingEventDAO;

	@Inject
	private LocaleManager localeManager;

	@Secured("BotUser")
	public Chatbot saveChatbot(@SecuredOperation("botAdm") final Chatbot chatbot, final Optional<FileInfoURI> personPictureFile,
		   	final ChatbotCustomConfig chatbotCustomConfig) {

		Assertion.check().isNotNull(chatbot);
		// ---

		final boolean newBot = chatbot.getBotId() == null;

		// Avatar
		Long oldAvatar = null;
		if (personPictureFile.isPresent()) {
			oldAvatar = chatbot.getFilIdAvatar();
			final VFile fileTmp = designerFileServices.getFileTmp(personPictureFile.get());
			final FileInfoURI fileInfoUri = designerFileServices.saveFile(fileTmp);
			chatbot.setFilIdAvatar((Long) fileInfoUri.getKey());
		}

		// chatbot save
		chatbot.setStatus("OK");
		final Chatbot savedChatbot = chatbotDAO.save(chatbot);

		// clean old avatar
		if (oldAvatar != null) {
			designerFileServices.deleteFile(oldAvatar);
		}

		if (newBot) {
			final TopicCategory topicCategory = topicCategoryServices.initializeBasicCategory(savedChatbot);
			contextValueServices.initContextValueURL(chatbot);
			topicServices.initTechnicalTopics(savedChatbot, topicCategory.getTopCatId(), localeManager.getCurrentLocale().toString());
		}

		chatbotCustomConfigServices.save(savedChatbot, chatbotCustomConfig);

		return savedChatbot;
	}

	@Secured("BotUser")
	public Boolean deleteChatbot(@SecuredOperation("botAdm") final Chatbot bot) {

		// Delete node
		deleteMonitoringSubscriptions(bot);
		confluenceSettingServices.deleteAllByBotId(bot);
		jiraSettingServices.deleteAllByBotId(bot);
		jiraFieldSettingServices.deleteAllByBotId(bot);
		nodeServices.deleteChatbotNodeByBot(bot);
		// Delete training and all media file
		savedTrainingServices.deleteAllByBotId(bot);
		trainingServices.removeAllTraining(bot);
		utterTextServices.removeAllUtterTextByBotId(bot);
		responsesButtonServices.removeAllSMTButtonsByBot(bot);
		dictionaryEntityServices.deleteAllByBot(bot);
		// Delete training, reponsetype and smallTalk
		topicServices.removeAllNTSFromBot(bot);
		topicLabelServices.cleanLabelFromBot(bot);
		scriptIntentionServices.removeAllScriptIntentionFromBot(bot);
		smallTalkServices.removeAllSmallTalkFromBot(bot);
		topicServices.removeAllTopicsFromBot(bot);
		topicCategoryServices.removeAllCategoryByBot(bot);

		// Delete questions/answers and documentary resources
		documentaryResourceServices.deleteAllDocumentaryResourceByBot(bot);
		questionAnswerServices.deleteAllQueAnsByBot(bot);
		questionAnswerCategoryServices.deleteAllQuestionAnswerCategoryByBot(bot);

		chatbotProfilServices.deleteAllProfilByBot(bot);
		chatbotCustomConfigServices.deleteChatbotCustomConfig(bot);
		contextPossibleValueServices.deleteAllContextPossibleValueByBot(bot);
		contextEnvironmentServices.deleteAllContextEnvironmentByBot(bot);
		contextValueServices.deleteAllByBotId(bot);
		historyServices.deleteAllByBotId(bot.getBotId());
		unknownSentencesServices.deleteAllByBotId(bot.getBotId());
		welcomeTourServices.deleteAllByBotId(bot.getBotId());
		chatbotDAO.delete(bot.getBotId());

		// Delete avatar file reference in bot
		if (bot.getFilIdAvatar() != null) {
			designerFileServices.deleteChatbotFile(bot, bot.getFilIdAvatar());
		}
		return true;
	}

	public DtList<Chatbot> getMySupervisedChatbots() {
		if (AuthorizationUtils.hasAuthorization(GlobalAuthorizations.AtzSuperAdm)) {
			return getAllChatbots();
		}
		return chatbotDAO.getChatbotByPerId(UserSessionUtils.getLoggedPerson().getPerId());
	}

	private void deleteMonitoringSubscriptions(Chatbot chatbot) {
		alertingEventDAO.findAll(Criterions.isEqualTo(DtDefinitions.AlertingEventFields.botId, chatbot.getBotId()),
				DtListState.of(MAX_ELEMENTS_PLUS_ONE)).forEach(alertingEvent -> alertingEventDAO.delete(alertingEvent.getAgeId()));
		monitoringPAO.removeAllFromNNAlertingSubscriptionChatbot(chatbot.getBotId());
	}

	@Secured("SuperAdm")
	public DtList<Chatbot> getAllChatbots() {
		return chatbotDAO.findAll(Criterions.alwaysTrue(), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}

	public DtList<Chatbot> getAllChatbotsForDaemons() {
		return chatbotDAO.findAll(Criterions.alwaysTrue(), DtListState.of(null));
	}

	@Secured("SuperAdm")
	public Chatbot getNewChatbot() {
		final Chatbot newChatbot = new Chatbot();
		newChatbot.setCreationDate(LocalDate.now());

		return newChatbot;
	}

	public Chatbot getChatbotById(final Long botId) {
		Assertion.check().isNotNull(botId);
		// ---
		final Chatbot chatbot = chatbotDAO.get(botId);
		AuthorizationUtils.checkRights(chatbot, ChatbotOperations.botVisitor, "can't get the chatbot : not enough right");
		return chatbot;
	}

	@Secured("BotUser")
	public VFile getAvatar(@SecuredOperation("botVisitor") final Chatbot bot) {
		if (bot.getFilIdAvatar() == null) {
			return getNoAvatar();
		}
		return designerFileServices.getFile(bot.getFilIdAvatar());
	}

	public VFile getNoAvatar() {
		return StreamFile.of(
				"noAvatar.png",
				"image/png",
				ChatbotServices.class.getResource("/noAvatar.png"));
	}

	public Optional<Chatbot> getChatbotByBotId(final Long botId) {
		final Criteria<Chatbot> criteria = Criterions.isEqualTo(ChatbotFields.botId, botId);
		return chatbotDAO.findOptional(criteria);
	}

	public String getBotNameDisplay(final Optional<Chatbot> bot) {
		return bot.isPresent() ? bot.get().getName() : MessageText.of(AnalyticsMultilingualResources.DELETED_BOT).getDisplay();
	}

	public String getNodeName(final Optional<Chatbot> bot, final Long nodId) {
		return bot.isPresent() ? nodeServices.getNodeByNodeId(bot.get(), nodId).getName()
				: MessageText.of(AnalyticsMultilingualResources.DELETED_BOT).getDisplay();
	}

	public String getBotDateDisplay(final Optional<Chatbot> bot) {
		return bot.isPresent() ? DateUtils.toStringJJMMAAAA(bot.get().getCreationDate()) : null;
	}

	@DaemonScheduled(name = "DmnUnknownSentences", periodInSeconds = 600)
	public void updateUnknownSentencesDaemon() {
		getAllChatbotsForDaemons().forEach(chatbot -> {
			unknownSentencesServices.saveLatestUnknownSentences(chatbot.getBotId());
		});
	}
}
