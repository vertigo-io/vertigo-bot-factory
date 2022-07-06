package io.vertigo.chatbot.designer.builder.controllers.bot;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.authorization.SecuredEntities;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.commons.domain.TrainerInfo;
import io.vertigo.chatbot.commons.domain.Training;
import io.vertigo.chatbot.commons.domain.TrainingStatusEnum;
import io.vertigo.chatbot.commons.domain.UnknownSentenceStatusEnum;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TopicCategory;
import io.vertigo.chatbot.commons.domain.topic.TopicFileExport;
import io.vertigo.chatbot.commons.multilingual.bot.BotMultilingualResources;
import io.vertigo.chatbot.commons.multilingual.export.ExportMultilingualResources;
import io.vertigo.chatbot.designer.analytics.multilingual.AnalyticsMultilingualResources;
import io.vertigo.chatbot.designer.analytics.services.TypeBotExportServices;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.designer.builder.services.TrainerInfoServices;
import io.vertigo.chatbot.designer.builder.services.TrainingServices;
import io.vertigo.chatbot.designer.builder.services.UnknownSentencesServices;
import io.vertigo.chatbot.designer.builder.services.topic.DictionaryEntityServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicCategoryServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.chatbot.designer.builder.services.topic.export.file.TopicFileExportServices;
import io.vertigo.chatbot.designer.commons.controllers.AbstractDesignerController;
import io.vertigo.chatbot.designer.commons.services.FileServices;
import io.vertigo.chatbot.designer.domain.DictionaryEntityWrapper;
import io.vertigo.chatbot.designer.domain.topic.export.TypeBotExport;
import io.vertigo.chatbot.designer.domain.topic.export.TypeBotExportList;
import io.vertigo.chatbot.designer.utils.AuthorizationUtils;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.locale.LocaleManager;
import io.vertigo.core.locale.MessageText;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.validation.UiMessageStack;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractBotController extends AbstractDesignerController {

	@Inject
	private NodeServices nodeServices;

	@Inject
	protected TopicServices topicServices;

	@Inject
	protected TrainingServices trainingServices;

	@Inject
	protected TrainerInfoServices trainerInfoServices;

	@Inject
	protected LocaleManager localeManager;

	@Inject
	protected UnknownSentencesServices unknownSentencesServices;

	@Inject
	private TypeBotExportServices typeBotExportServices;

	@Inject
	protected TopicFileExportServices topicFileExportServices;

	@Inject
	protected TopicCategoryServices categoryServices;

	@Inject
	protected DictionaryEntityServices dictionaryEntityServices;

	@Inject
	protected TopicCategoryServices topicCategoryServices;

	@Inject
	protected FileServices fileServices;

	private static final ViewContextKey<Chatbot> botKey = ViewContextKey.of("bot");
	private static final ViewContextKey<String> localeKey = ViewContextKey.of("locale");
	private static final ViewContextKey<Long> enabledTopicsKey = ViewContextKey.of("enabledTopics");
	private static final ViewContextKey<Long> unknownSentencesToTreatKey = ViewContextKey.of("unknownSentencesToTreat");
	private static final ViewContextKey<Integer> totalTopicsKey = ViewContextKey.of("totalTopics");
	protected static final ViewContextKey<Training> trainingKey = ViewContextKey.of("training");
	protected static final ViewContextKey<ChatbotNode> devNodeKey = ViewContextKey.of("devNode");
	protected static final ViewContextKey<String> breadCrumsKey = ViewContextKey.of("breadCrums");
	private static final ViewContextKey<TrainerInfo> trainerStateKey = ViewContextKey.of("trainerState");
	private static final ViewContextKey<TypeBotExport> typeBotExportKey = ViewContextKey.of("typeBotExport");
	private static final ViewContextKey<TypeBotExportList> selectTypeBotExportListKey = ViewContextKey.of("selectTypeBotExportList");

	protected Chatbot initCommonContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, final Long botId) {
		final Chatbot chatbot = chatbotServices.getChatbotById(botId);
		final DtList<Topic> topics = topicServices.getAllTopicByBot(chatbot);
		viewContext.publishDtList(typeBotExportKey, typeBotExportServices.getAllTypeBotExport());
		viewContext.publishDto(selectTypeBotExportListKey, new TypeBotExportList());
		viewContext.publishDto(trainingKey, trainingServices.getDeployedTraining(chatbot).orElse(new Training()));
		viewContext.publishDto(trainerStateKey, trainerInfoServices.createTrainingState(chatbot));
		viewContext.publishRef(enabledTopicsKey, topics.stream().filter(Topic::getIsEnabled).count());
		viewContext.publishRef(unknownSentencesToTreatKey,
		unknownSentencesServices.findAllByBotId(botId).stream().filter(sentence -> sentence.getStatus().equals(UnknownSentenceStatusEnum.TO_TREAT.name())).count());
		viewContext.publishRef(totalTopicsKey, topics.size());
		viewContext.publishDto(botKey, chatbot);
		viewContext.publishDto(devNodeKey, nodeServices.getDevNodeByBotId(botId).orElse(new ChatbotNode()));
		viewContext.publishRef(localeKey, localeManager.getCurrentLocale().toString());
		addKeyConceptSecurityToContext(chatbot, SecuredEntities.ChatbotAuthorizations.values());
		nodeMessageDisplay(chatbot, uiMessageStack);
		return chatbot;
	}

	protected void initBreadCrums(final ViewContext viewContext, final String keyMessage) {
		viewContext.publishRef(breadCrumsKey, MessageText.of(BotMultilingualResources.valueOf(keyMessage)).getDisplay());
	}

	protected void initEmptyCommonContext(final ViewContext viewContext) {
		viewContext.publishDto(botKey, chatbotServices.getNewChatbot());
		viewContext.publishRef(localeKey, localeManager.getCurrentLocale().toString());
		viewContext.publishDto(trainingKey, new Training());
		viewContext.publishDto(trainerStateKey, new TrainerInfo());
		viewContext.publishRef(enabledTopicsKey, 0L);
		viewContext.publishRef(unknownSentencesToTreatKey, 0L);
		viewContext.publishRef(totalTopicsKey, 0);
		viewContext.publishDto(devNodeKey, new ChatbotNode());
	}

	@GetMapping("/{botId}/avatar")
	public VFile getAvatar(@PathVariable("botId") final Long botId) {
		return chatbotServices.getAvatar(chatbotServices.getChatbotById(botId));
	}

	@GetMapping("/avatar")
	public VFile getAvatar() {
		return chatbotServices.getNoAvatar();
	}

	public void nodeMessageDisplay(final Chatbot chatbot, final UiMessageStack uiMessageStack) {
		if (AuthorizationUtils.isAuthorized(chatbot, SecuredEntities.ChatbotOperations.botAdm)) {
			final Optional<ChatbotNode> devNode = nodeServices.getDevNodeByBotId(chatbot.getBotId());
			if (devNode.isEmpty() || !devNode.get().getIsUpToDate()) {
				uiMessageStack.info(MessageText.of(BotMultilingualResources.NODE_NOT_UP_TO_DATE).getDisplay());
			}
		}
	}

	@PostMapping("/train")
	public ViewContext doTrain(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot) {
		final Training newTraining = trainingServices.trainAgent(bot, nodeServices.getDevNodeByBotId(bot.getBotId()).orElseThrow().getNodId());
		viewContext.publishDto(trainingKey, newTraining);
		viewContext.publishDto(trainerStateKey, trainerInfoServices.createTrainingState(newTraining));
		return viewContext;
	}

	@PostMapping("/refreshTrainer")
	public ViewContext refreshTrainerState(final ViewContext viewContext,
										   final UiMessageStack uiMessageStack,
										   @ViewAttribute("bot") final Chatbot bot,
										   @ViewAttribute("trainerState") final TrainerInfo trainerInfo) {
		final TrainerInfo state = trainerInfoServices.getTrainingState(bot, trainerInfo);
		if (!state.getTrainingInProgress()) {
			final Training training = trainingServices.getTrainingByTraIdAndBotId(bot.getBotId(), state.getTraId()).orElseThrow();
			viewContext.publishDto(trainingKey, training);
			if (TrainingStatusEnum.OK == training.trainingStatus().getEnumValue()) {
				uiMessageStack.success(MessageText.of(BotMultilingualResources.TRAINING_STATE_MESSAGE_OK, training.getVersionNumber()).getDisplay());
			} else {
				uiMessageStack.error(MessageText.of(BotMultilingualResources.TRAINING_STATE_MESSAGE_KO, training.getVersionNumber(), training.getWarnings()).getDisplay());
			}

		}
		viewContext.publishDto(trainerStateKey, state);

		return viewContext;
	}

	@PostMapping("/delete")
	@Secured("BotUser")
	public String doDelete(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot) {
		chatbotServices.deleteChatbot(bot);
		return "redirect:/bots/";
	}

	@PostMapping("/_exportBot")
	public VFile exportBot(final ViewContext viewContext,
						   @ViewAttribute("bot") final Chatbot bot,
						   @ViewAttribute("selectTypeBotExportList") final TypeBotExportList typeBotExportList) {
		if (typeBotExportList.getTbeCd().isEmpty()) {
			throw new VUserException(AnalyticsMultilingualResources.MANDATORY_TYPE_EXPORT_ANALYTICS);
		}
		final List<VFile> fileList = new ArrayList<>();
		typeBotExportList.getTbeCd().forEach(typeBotExport -> {
			switch (typeBotExport) {
				case "CATEGORIES":
					final DtList<TopicCategory> topicCategories = topicCategoryServices.getAllNonTechnicalCategoriesByBot(bot);
					fileList.add(topicCategoryServices.exportCategories(bot, topicCategories));
					break;
				case "TOPICS":
					final DtList<TopicFileExport> listTopics = topicFileExportServices.getTopicFileExport(bot.getBotId(),
							categoryServices.getAllCategoriesByBot(bot).stream().map(TopicCategory::getTopCatId).collect(Collectors.toList()));
					fileList.add(topicFileExportServices.exportTopicFile(bot, listTopics));
					break;
				case "DICTIONARY":
					final DtList<DictionaryEntityWrapper> listDictionaryEntitiesToExport = dictionaryEntityServices.getDictionaryExportByBotId(bot.getBotId(), "|");
					fileList.add(dictionaryEntityServices.exportDictionary(bot, listDictionaryEntitiesToExport));
					break;
				default:
					throw new VUserException(ExportMultilingualResources.MANDATORY_TYPE_BOT_EXPORT);
			}
		});
		if (fileList.size() == 1) {
			return fileList.get(0);
		} else {
			final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			return fileServices.zipMultipleFiles(fileList,
					MessageText.of(BotMultilingualResources.EXPORT_ZIP_FILENAME, bot.getName(), dateFormat.format(new Date())).getDisplay());
		}

	}

	protected Long getBotId(final ViewContext viewContext) {
		return viewContext.getUiObject(botKey).getLong("botId");
	}

}
