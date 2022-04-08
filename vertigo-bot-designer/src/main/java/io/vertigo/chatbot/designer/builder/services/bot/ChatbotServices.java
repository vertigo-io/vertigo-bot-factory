package io.vertigo.chatbot.designer.builder.services.bot;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.authorization.GlobalAuthorizations;
import io.vertigo.chatbot.authorization.SecuredEntities.ChatbotOperations;
import io.vertigo.chatbot.commons.dao.ChatbotDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotCustomConfig;
import io.vertigo.chatbot.commons.domain.topic.TopicCategory;
import io.vertigo.chatbot.designer.analytics.multilingual.AnalyticsMultilingualResources;
import io.vertigo.chatbot.designer.builder.services.HistoryServices;
import io.vertigo.chatbot.designer.builder.services.JiraFieldSettingServices;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.designer.builder.services.ResponsesButtonServices;
import io.vertigo.chatbot.designer.builder.services.SavedTrainingServices;
import io.vertigo.chatbot.designer.builder.services.TrainingServices;
import io.vertigo.chatbot.designer.builder.services.UnknownSentencesServices;
import io.vertigo.chatbot.designer.builder.services.UtterTextServices;
import io.vertigo.chatbot.designer.builder.services.WelcomeTourServices;
import io.vertigo.chatbot.designer.builder.services.topic.ScriptIntentionServices;
import io.vertigo.chatbot.designer.builder.services.topic.SmallTalkServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicCategoryServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicLabelServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.chatbot.designer.commons.services.FileServices;
import io.vertigo.chatbot.designer.utils.AuthorizationUtils;
import io.vertigo.chatbot.designer.utils.DateUtils;
import io.vertigo.chatbot.designer.utils.UserSessionUtils;
import io.vertigo.chatbot.domain.DtDefinitions.ChatbotFields;
import io.vertigo.commons.transaction.Transactional;
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

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Optional;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;


@Transactional
@Secured("BotUser")
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
	private FileServices fileServices;

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
	private ChabotCustomConfigServices chabotCustomConfigServices;

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
	private LocaleManager localeManager;

	public Chatbot saveChatbot(@SecuredOperation("botAdm") final Chatbot chatbot, final Optional<FileInfoURI> personPictureFile,
		   	final ChatbotCustomConfig chatbotCustomConfig) {

		Assertion.check().isNotNull(chatbot);
		// ---

		final boolean newBot = chatbot.getBotId() == null;

		// Avatar
		Long oldAvatar = null;
		if (personPictureFile.isPresent()) {
			oldAvatar = chatbot.getFilIdAvatar();
			final VFile fileTmp = fileServices.getFileTmp(personPictureFile.get());
			final FileInfoURI fileInfoUri = fileServices.saveFile(fileTmp);
			chatbot.setFilIdAvatar((Long) fileInfoUri.getKey());
		}

		// chatbot save
		chatbot.setStatus("OK");
		final Chatbot savedChatbot = chatbotDAO.save(chatbot);

		// clean old avatar
		if (oldAvatar != null) {
			fileServices.deleteFile(oldAvatar);
		}

		if (newBot) {
			final TopicCategory topicCategory = topicCategoryServices.initializeBasicCategory(savedChatbot);
			topicServices.initTechnicalTopics(savedChatbot, topicCategory.getTopCatId(), localeManager.getCurrentLocale().toString());
		}

		chabotCustomConfigServices.save(savedChatbot, chatbotCustomConfig);

		return savedChatbot;
	}

	public Boolean deleteChatbot(@SecuredOperation("botAdm") final Chatbot bot) {

		// Delete node
		nodeServices.deleteChatbotNodeByBot(bot);
		// Delete training and all media file
		savedTrainingServices.deleteAllByBotId(bot.getBotId());
		trainingServices.removeAllTraining(bot);
		utterTextServices.removeAllUtterTextByBotId(bot);
		responsesButtonServices.removeAllSMTButtonsByBot(bot);
		// Delete training, reponsetype and smallTalk
		topicServices.removeAllNTSFromBot(bot);
		topicLabelServices.cleanLabelFromBot(bot);
		scriptIntentionServices.removeAllScriptIntentionFromBot(bot);
		smallTalkServices.removeAllSmallTalkFromBot(bot);
		topicServices.removeAllTopicsFromBot(bot);
		topicCategoryServices.removeAllCategoryByBot(bot);

		chatbotProfilServices.deleteAllProfilByBot(bot);
		chabotCustomConfigServices.deleteChatbotCustomConfig(bot);
		contextValueServices.deleteAllByBotId(bot.getBotId());
		historyServices.deleteAllByBotId(bot.getBotId());
		unknownSentencesServices.deleteAllByBotId(bot.getBotId());
		welcomeTourServices.deleteAllByBotId(bot.getBotId());
		jiraFieldSettingServices.deleteAllByBotId(bot.getBotId());
		chatbotDAO.delete(bot.getBotId());

		// Delete avatar file reference in bot
		if (bot.getFilIdAvatar() != null) {
			fileServices.deleteChatbotFile(bot, bot.getFilIdAvatar());
		}
		return true;
	}

	public DtList<Chatbot> getMySupervisedChatbots() {
		if (AuthorizationUtils.hasAuthorization(GlobalAuthorizations.AtzSuperAdm)) {
			return getAllChatbots();
		}
		return chatbotDAO.getChatbotByPerId(UserSessionUtils.getLoggedPerson().getPerId());
	}

	@Secured("SuperAdm")
	public DtList<Chatbot> getAllChatbots() {
		return chatbotDAO.findAll(Criterions.alwaysTrue(), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
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

	public VFile getAvatar(@SecuredOperation("botVisitor") final Chatbot bot) {
		if (bot.getFilIdAvatar() == null) {
			return getNoAvatar();
		}
		return fileServices.getFile(bot.getFilIdAvatar());
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

}
