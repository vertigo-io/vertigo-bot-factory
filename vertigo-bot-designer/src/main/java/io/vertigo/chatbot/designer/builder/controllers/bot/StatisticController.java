package io.vertigo.chatbot.designer.builder.controllers.bot;

import static io.vertigo.chatbot.designer.utils.ListUtils.listLimitReached;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.influxdb.exceptions.InfluxException;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotCustomConfig;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.commons.domain.FontFamily;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TopicCategory;
import io.vertigo.chatbot.commons.domain.topic.TopicLabel;
import io.vertigo.chatbot.commons.influxDb.TimeSerieServices;
import io.vertigo.chatbot.designer.analytics.multilingual.AnalyticsMultilingualResources;
import io.vertigo.chatbot.designer.analytics.services.AnalyticsExportServices;
import io.vertigo.chatbot.designer.analytics.services.AnalyticsServices;
import io.vertigo.chatbot.designer.analytics.services.RatingOptionServices;
import io.vertigo.chatbot.designer.analytics.services.TimeOption;
import io.vertigo.chatbot.designer.analytics.services.TypeExportAnalyticsServices;
import io.vertigo.chatbot.designer.builder.services.FontFamilyServices;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.designer.builder.services.bot.ChatbotCustomConfigServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicCategoryServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicLabelServices;
import io.vertigo.chatbot.designer.commons.ihm.enums.TimeEnum;
import io.vertigo.chatbot.designer.commons.services.EnumIHMManager;
import io.vertigo.chatbot.designer.commons.services.FileServices;
import io.vertigo.chatbot.designer.domain.analytics.CategoryStat;
import io.vertigo.chatbot.designer.domain.analytics.ConversationCriteria;
import io.vertigo.chatbot.designer.domain.analytics.ConversationDetail;
import io.vertigo.chatbot.designer.domain.analytics.ConversationStat;
import io.vertigo.chatbot.designer.domain.analytics.RatingDetail;
import io.vertigo.chatbot.designer.domain.analytics.RatingOption;
import io.vertigo.chatbot.designer.domain.analytics.SentenseDetail;
import io.vertigo.chatbot.designer.domain.analytics.SessionExport;
import io.vertigo.chatbot.designer.domain.analytics.StatCriteria;
import io.vertigo.chatbot.designer.domain.analytics.TopIntent;
import io.vertigo.chatbot.designer.domain.analytics.TopIntentCriteria;
import io.vertigo.chatbot.designer.domain.analytics.TypeExportAnalyticList;
import io.vertigo.chatbot.designer.domain.analytics.TypeExportAnalytics;
import io.vertigo.chatbot.designer.domain.analytics.UnknownSentenseExport;
import io.vertigo.chatbot.designer.domain.commons.SelectionOption;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.locale.MessageText;
import io.vertigo.database.timeseries.TimedDatas;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;
import io.vertigo.vega.webservice.validation.UiMessageStack;

@Controller
@RequestMapping("/bot/{botId}/statistic")
public class StatisticController extends AbstractBotController {

	private static final Logger LOGGER = LogManager.getLogger(StatisticController.class);
	private static final ViewContextKey<ChatbotNode> nodesKey = ViewContextKey.of("nodes");
	private static final ViewContextKey<StatCriteria> criteriaKey = ViewContextKey.of("criteria");
	private static final ViewContextKey<ChatbotCustomConfig> chatbotCustomConfigKey = ViewContextKey.of("chatbotCustomConfig");
	private static final ViewContextKey<TypeExportAnalytics> typeExportAnalyticsListKey = ViewContextKey.of("typeExportAnalyticsList");
	private static final ViewContextKey<TypeExportAnalyticList> selectTypeExportAnalyticListKey = ViewContextKey.of("selectTypeExportAnalyticList");
	private static final ViewContextKey<SelectionOption> timeOptionsList = ViewContextKey.of("timeOptions");
	private static final ViewContextKey<String> localeKey = ViewContextKey.of("locale");
	private static final ViewContextKey<TimedDatas> sessionStatsKey = ViewContextKey.of("sessionStats");
	private static final ViewContextKey<TimedDatas> requestsStatsKey = ViewContextKey.of("requestsStats");
	private static final ViewContextKey<TimedDatas> ratingStatsKey = ViewContextKey.of("ratingStats");
	private static final ViewContextKey<TimedDatas> userInteractionsStatsKey = ViewContextKey.of("userInteractionsStats");
	private static final ViewContextKey<TopIntent> topIntentsKey = ViewContextKey.of("topIntents");
	private static final ViewContextKey<TopIntent> topIntentsFilteredKey = ViewContextKey.of("topIntentsFiltered");
	private static final ViewContextKey<SentenseDetail> intentDetailsKey = ViewContextKey.of("intentDetails");
	private static final ViewContextKey<ConversationStat> conversationStatKey = ViewContextKey.of("conversationStat");
	private static final ViewContextKey<ConversationDetail> conversationDetailsKey = ViewContextKey.of("conversationDetails");
	private static final ViewContextKey<Topic> topicsKey = ViewContextKey.of("topics");
	private static final ViewContextKey<SentenseDetail> unknownSentensesKey = ViewContextKey.of("unknownSentenses");
	private static final ViewContextKey<ConversationCriteria> conversationCriteriaKey = ViewContextKey.of("conversationCriteria");
	private static final ViewContextKey<RatingOption> ratingOptionsKey = ViewContextKey.of("ratingOptions");
	private static final ViewContextKey<TopicCategory> topicCategoriesKey = ViewContextKey.of("topicCategories");
	private static final ViewContextKey<TopIntentCriteria> topIntentCriteriaKey = ViewContextKey.of("topIntentCriteria");
	private static final ViewContextKey<TopicLabel> topicLabelsKey = ViewContextKey.of("topicLabels");
	private static final ViewContextKey<CategoryStat> categoryStatKey = ViewContextKey.of("categoryStat");
	private static final ViewContextKey<RatingDetail> ratingDetailsKey = ViewContextKey.of("ratingDetails");
	private static final ViewContextKey<Double> totalOfUserActionsKey = ViewContextKey.of("totalOfUserActions");
	private static final ViewContextKey<Double> totalOfUnrecognizedMessageKey = ViewContextKey.of("totalOfUnrecognizedMessage");
	private static final ViewContextKey<Double> totalOfRecognizedMessageKey = ViewContextKey.of("totalOfRecognizedMessage");
	private static final ViewContextKey<Double> totalOfConversationsKey = ViewContextKey.of("totalOfConversations");
	private static final ViewContextKey<FontFamily> fontFamiliesKey = ViewContextKey.of("fontFamilies");

	@Inject
	private NodeServices nodeServices;

	@Inject
	private TypeExportAnalyticsServices typeExportAnalyticsServices;

	@Inject
	private RatingOptionServices ratingOptionServices;

	@Inject
	private TopicCategoryServices topicCategoryServices;

	@Inject
	private TopicLabelServices topicLabelServices;

	@Inject
	private ChatbotCustomConfigServices chatbotCustomConfigServices;

	@Inject
	private FontFamilyServices fontFamilyServices;

	@Inject
	private EnumIHMManager enumIHMManager;

	@Inject
	private AnalyticsServices analyticsServices;

	@Inject
	private AnalyticsExportServices analyticsExportServices;

	@Inject
	private TimeSerieServices timeSerieServices;

	@Inject
	private FileServices fileServices;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId,
			@RequestParam("nodId") final Optional<Long> nodId,
			@RequestParam("time") final Optional<TimeOption> timeOption) {
		final Chatbot bot = super.initCommonContext(viewContext, uiMessageStack, botId);
		final StatCriteria statCriteria = new StatCriteria();
		viewContext.publishDtList(ratingOptionsKey, ratingOptionServices.getAllRatingOptions());
		viewContext.publishDtList(nodesKey, nodeServices.getAllNodesByBot(bot));
		viewContext.publishDtList(topicCategoriesKey, topicCategoryServices.getAllCategoriesByBot(bot));
		viewContext.publishDtList(topicLabelsKey, topicLabelServices.getTopicLabelByBotId(bot));
		viewContext.publishDto(chatbotCustomConfigKey, chatbotCustomConfigServices.getChatbotCustomConfigByBotId(bot.getBotId()));
		viewContext.publishDtList(fontFamiliesKey, fontFamilyServices.findAll());
		viewContext.publishDtList(typeExportAnalyticsListKey, typeExportAnalyticsServices.getAllTypeExportAnalytics());
		viewContext.publishDtList(topicsKey, topicServices.getAllTopicByBot(bot));
		statCriteria.setBotId(botId);
		viewContext.publishDto(chatbotCustomConfigKey, chatbotCustomConfigServices.getChatbotCustomConfigByBotId(botId));
		statCriteria.setToDate(LocalDate.now());
		statCriteria.setTimeOption(timeOption.orElse(TimeOption.DAY).name());

		nodId.ifPresent(statCriteria::setNodId);

		viewContext.publishDtList(timeOptionsList, DtDefinitions.SelectionOptionFields.label, enumIHMManager.getSelectionOptions(TimeEnum.values()));
		viewContext.publishDto(criteriaKey, statCriteria);
		viewContext.publishDto(conversationCriteriaKey, new ConversationCriteria());
		viewContext.publishDto(topIntentCriteriaKey, new TopIntentCriteria());

		viewContext.publishDto(selectTypeExportAnalyticListKey, new TypeExportAnalyticList());

		viewContext.publishRef(localeKey, localeManager.getCurrentLocale().toString());

		updateGraph(viewContext, statCriteria, bot, uiMessageStack);

		super.initBreadCrums(viewContext, "STATISTIC");
		listLimitReached(viewContext, uiMessageStack);
	}

	private void updateGraph(final ViewContext viewContext, final StatCriteria criteria, final Chatbot bot, final UiMessageStack uiMessageStack) {
		final TimedDatas sessionStat = timeSerieServices.getSessionsStats(criteria);
		viewContext.publishRef(sessionStatsKey, sessionStat);
		viewContext.publishRef(totalOfConversationsKey, sessionStat.getTimedDataSeries().stream()
				.mapToDouble(it -> (Double) it.getValues().get("isSessionStart:count")).sum());

		final TimedDatas requestsStat = timeSerieServices.getRequestStats(criteria);
		viewContext.publishRef(requestsStatsKey, requestsStat);
		viewContext.publishRef(totalOfUnrecognizedMessageKey, requestsStat.getTimedDataSeries().stream()
				.mapToDouble(it -> (Double) it.getValues().get("isFallback:count")).sum());
		viewContext.publishRef(totalOfRecognizedMessageKey, requestsStat.getTimedDataSeries().stream()
				.mapToDouble(it -> (Double) it.getValues().get("isNlu:count")).sum());

		final TimedDatas userInteractions = timeSerieServices.getUserInteractions(criteria);
		viewContext.publishRef(userInteractionsStatsKey, userInteractions);
		viewContext.publishRef(totalOfUserActionsKey, userInteractions.getTimedDataSeries().stream()
				.mapToDouble(it -> (Double) it.getValues().get("name:count")).sum());

		viewContext.publishDtList(ratingDetailsKey, analyticsServices.getRatingDetails(criteria));
		viewContext.publishDtList(unknownSentensesKey, DtDefinitions.SentenseDetailFields.topId, analyticsServices.getSentenseDetails(criteria));
		final DtList<TopIntent> topIntents = analyticsServices.getTopIntents(bot, localeManager.getCurrentLocale().toString(), criteria);
		viewContext.publishDtList(topIntentsKey, DtDefinitions.TopIntentFields.topId, topIntents);
		viewContext.publishDtList(topIntentsFilteredKey, DtDefinitions.TopIntentFields.topId, topIntents);
		viewContext.publishDtList(categoryStatKey, analyticsServices.buildCategoryStats(viewContext.readDtList(topicCategoriesKey, AbstractVSpringMvcController.getUiMessageStack()), topIntents));
		viewContext.publishRef(ratingStatsKey, timeSerieServices.getRatingStats(criteria));
		viewContext.publishDtList(intentDetailsKey, DtDefinitions.SentenseDetailFields.topId, new DtList<SentenseDetail>(SentenseDetail.class));
		viewContext.publishDtList(conversationDetailsKey, DtDefinitions.ConversationDetailFields.sessionId, new DtList<ConversationDetail>(ConversationDetail.class));
		viewContext.publishDtList(conversationStatKey, DtDefinitions.ConversationStatFields.sessionId,
				analyticsServices.getConversationsStats(criteria, viewContext.readDto(conversationCriteriaKey, AbstractVSpringMvcController.getUiMessageStack())));
	}

	@PostMapping("/_updateStats")
	public ViewContext doUpdateStats(final ViewContext viewContext,
			@ViewAttribute("bot") final Chatbot bot,
			@ViewAttribute("criteria") final StatCriteria criteria, final UiMessageStack uiMessageStack) {
		try {
			updateGraph(viewContext, criteria, bot, uiMessageStack);
		} catch (final InfluxException influxException) {
			LOGGER.error("Error when trying to update statistic graph, ignoring it for now...", influxException);
		}
		listLimitReached(viewContext, uiMessageStack);
		return viewContext;
	}

	@PostMapping("/_intentDetails")
	public ViewContext doGetIntentDetails(final ViewContext viewContext,
			@ViewAttribute("criteria") final StatCriteria criteria,
			@RequestParam("intentRasa") final String intentRasa, final UiMessageStack uiMessageStack) {

		viewContext.publishDtList(intentDetailsKey, DtDefinitions.SentenseDetailFields.text, analyticsServices.getKnownSentensesDetail(criteria, intentRasa));
		listLimitReached(viewContext, uiMessageStack);
		return viewContext;
	}

	@PostMapping("/_conversationDetails")
	public ViewContext doGetConversationDetails(final ViewContext viewContext,
			@ViewAttribute("criteria") final StatCriteria criteria,
			@RequestParam("sessionId") final String sessionId, final UiMessageStack uiMessageStack) {

		viewContext.publishDtList(conversationDetailsKey, DtDefinitions.ConversationDetailFields.sessionId, analyticsServices.getConversationDetails(criteria, sessionId));
		listLimitReached(viewContext, uiMessageStack);
		return viewContext;
	}

	@PostMapping("/_filterConversation")
	public ViewContext filterConversation(final ViewContext viewContext, final UiMessageStack uiMessageStack,
			@ViewAttribute("criteria") final StatCriteria criteria,
			@ViewAttribute("conversationCriteria") final ConversationCriteria conversationCriteria) {

		viewContext.publishDtList(conversationStatKey, analyticsServices.getConversationsStats(criteria, conversationCriteria));
		listLimitReached(viewContext, uiMessageStack);
		return viewContext;
	}

	@PostMapping("/_exportStatisticFile")
	public VFile doExportStatisticFile(final ViewContext viewContext,
			@ViewAttribute("criteria") final StatCriteria criteria,
			@ViewAttribute("conversationCriteria") final ConversationCriteria conversationCriteria,
			@ViewAttribute("bot") final Chatbot bot,
			@ViewAttribute("selectTypeExportAnalyticList") final TypeExportAnalyticList typeExportAnalyticList) {

		if (typeExportAnalyticList.getTeaCd().isEmpty()) {
			throw new VUserException(AnalyticsMultilingualResources.MANDATORY_TYPE_EXPORT_ANALYTICS);
		}
		final List<VFile> fileList = new ArrayList<>();
		typeExportAnalyticList.getTeaCd().forEach(typeExportAnalytic -> {
			switch (typeExportAnalytic) {
				case "USER_ACTIONS_CONVERSATIONS":
					final DtList<SessionExport> listSessionExport = analyticsExportServices.getSessionExport(criteria);
					fileList.add(analyticsExportServices.exportSessions(listSessionExport));
					break;
				case "UNKNOWN_MESSAGES":
					final DtList<UnknownSentenseExport> listUnknownSentenseExport = analyticsExportServices.getUnknownSentenseExport(criteria);
					fileList.add(analyticsExportServices.exportUnknownMessages(listUnknownSentenseExport));
					break;
				case "CONVERSATIONS":
					final DtList<ConversationStat> conversationStats = analyticsServices.getConversationsStats(criteria, conversationCriteria);
					fileList.add(analyticsExportServices.exportConversations(conversationStats));
					break;
				case "CATEGORIES":
					final DtList<CategoryStat> categoryStats = analyticsServices.buildCategoryStats(viewContext.readDtList(topicCategoriesKey, AbstractVSpringMvcController.getUiMessageStack()),
							analyticsServices.getTopIntents(bot, localeManager.getCurrentLocale().toString(), criteria));
					fileList.add(analyticsExportServices.exportCategories(categoryStats));
					break;
				case "RATING":
					final DtList<RatingDetail> ratingDetails = analyticsServices.getRatingDetails(criteria);
					fileList.add(analyticsExportServices.exportRatingDetails(ratingDetails));
					break;
				case "TOPIC_USAGE":
					final DtList<TopIntent> topIntents = analyticsServices.getTopIntents(bot, localeManager.getCurrentLocale().toString(), criteria);
					fileList.add(analyticsExportServices.exportTopIntents(topIntents));
					break;
				default:
					throw new VUserException(AnalyticsMultilingualResources.MANDATORY_TYPE_EXPORT_ANALYTICS);
			}
		});
		if (fileList.size() == 1) {
			return fileList.get(0);
		} else {
			final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			return fileServices.zipMultipleFiles(fileList,
					MessageText.of(AnalyticsMultilingualResources.ZIP_EXPORT_FILENAME).getDisplay() + dateFormat.format(new Date()));
		}
	}
}
