package io.vertigo.chatbot.designer.builder.controllers.bot;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotCustomConfig;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.influxDb.TimeSerieServices;
import io.vertigo.chatbot.designer.analytics.multilingual.AnalyticsMultilingualResources;
import io.vertigo.chatbot.designer.analytics.services.AnalyticsExportServices;
import io.vertigo.chatbot.designer.analytics.services.AnalyticsServices;
import io.vertigo.chatbot.designer.analytics.services.TimeOption;
import io.vertigo.chatbot.designer.analytics.services.TypeExportAnalyticsServices;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.designer.builder.services.bot.ChabotCustomConfigServices;
import io.vertigo.chatbot.designer.commons.ihm.enums.TimeEnum;
import io.vertigo.chatbot.designer.commons.services.EnumIHMManager;
import io.vertigo.chatbot.designer.domain.analytics.SentenseDetail;
import io.vertigo.chatbot.designer.domain.analytics.SessionExport;
import io.vertigo.chatbot.designer.domain.analytics.StatCriteria;
import io.vertigo.chatbot.designer.domain.analytics.TopIntent;
import io.vertigo.chatbot.designer.domain.analytics.TypeExportAnalytics;
import io.vertigo.chatbot.designer.domain.analytics.UnknownSentenseExport;
import io.vertigo.chatbot.designer.domain.commons.SelectionOption;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.core.lang.VUserException;
import io.vertigo.database.timeseries.TimedDatas;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.validation.UiMessageStack;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Optional;

import static io.vertigo.chatbot.designer.utils.ListUtils.listLimitReached;

@Controller
@RequestMapping("/bot/{botId}/statistic")
public class StatisticController extends AbstractBotController {

	private static final ViewContextKey<ChatbotNode> nodesKey = ViewContextKey.of("nodes");
	private static final ViewContextKey<StatCriteria> criteriaKey = ViewContextKey.of("criteria");
	private static final ViewContextKey<ChatbotCustomConfig> chatbotCustomConfigKey = ViewContextKey.of("chatbotCustomConfig");
	private static final ViewContextKey<TypeExportAnalytics> typeExportAnalyticsListKey = ViewContextKey.of("typeExportAnalyticsList");
	private static final ViewContextKey<TypeExportAnalytics> selectTypeExportAnalyticsKey = ViewContextKey.of("selectTypeExportAnalytics");
	private static final ViewContextKey<SelectionOption> timeOptionsList = ViewContextKey.of("timeOptions");
	private static final ViewContextKey<String> localeKey = ViewContextKey.of("locale");
	private static final ViewContextKey<TimedDatas> sessionStatsKey = ViewContextKey.of("sessionStats");
	private static final ViewContextKey<TimedDatas> requestsStatsKey = ViewContextKey.of("requestsStats");
	private static final ViewContextKey<TimedDatas> ratingStatsKey = ViewContextKey.of("ratingStats");
	private static final ViewContextKey<TimedDatas> userInteractionsStatsKey = ViewContextKey.of("userInteractionsStats");
	private static final ViewContextKey<TopIntent> topIntentsKey = ViewContextKey.of("topIntents");
	private static final ViewContextKey<SentenseDetail> intentDetailsKey = ViewContextKey.of("intentDetails");
	private static final ViewContextKey<Topic> topicsKey = ViewContextKey.of("topics");
	private static final ViewContextKey<SentenseDetail> unknownSentensesKey = ViewContextKey.of("unknownSentenses");

	@Inject
	private NodeServices nodeServices;

	@Inject
	private TypeExportAnalyticsServices typeExportAnalyticsServices;

	@Inject
	private ChabotCustomConfigServices chabotCustomConfigServices;

	@Inject
	private EnumIHMManager enumIHMManager;

	@Inject
	private AnalyticsServices analyticsServices;

	@Inject
	private AnalyticsExportServices analyticsExportServices;

	@Inject
	private TimeSerieServices timeSerieServices;


	@GetMapping("/")
	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId,
							@RequestParam("nodId") final Optional<Long> nodId,
							@RequestParam("time") final Optional<TimeOption> timeOption) {
		final Chatbot bot = super.initCommonContext(viewContext, uiMessageStack, botId);
		final StatCriteria statCriteria = new StatCriteria();
		viewContext.publishDtList(nodesKey, nodeServices.getNodesByBot(bot));
		statCriteria.setBotId(botId);
		viewContext.publishDtListModifiable(typeExportAnalyticsListKey, typeExportAnalyticsServices.getBotRelatedTypeExportAnalytics());
		viewContext.publishDto(chatbotCustomConfigKey, chabotCustomConfigServices.getChatbotCustomConfigByBotId(botId));
		statCriteria.setToDate(LocalDate.now());
		statCriteria.setTimeOption(timeOption.orElse(TimeOption.DAY).name());

		nodId.ifPresent(statCriteria::setNodId);

		viewContext.publishDtList(timeOptionsList, DtDefinitions.SelectionOptionFields.label, enumIHMManager.getSelectionOptions(TimeEnum.values()));
		viewContext.publishDto(criteriaKey, statCriteria);

		viewContext.publishDto(selectTypeExportAnalyticsKey, new TypeExportAnalytics());

		viewContext.publishRef(localeKey, localeManager.getCurrentLocale().toString());

		updateGraph(viewContext, statCriteria, bot);

		super.initBreadCrums(viewContext, "STATISTIC");
		listLimitReached(viewContext, uiMessageStack);
	}

	private void updateGraph(final ViewContext viewContext, final StatCriteria criteria, final Chatbot bot) {
		viewContext.publishRef(sessionStatsKey, timeSerieServices.getSessionsStats(criteria));
		viewContext.publishRef(requestsStatsKey, timeSerieServices.getRequestStats(criteria));
		viewContext.publishRef(userInteractionsStatsKey, timeSerieServices.getUserInteractions(criteria));
		viewContext.publishDtList(unknownSentensesKey, DtDefinitions.SentenseDetailFields.topId, analyticsServices.getSentenseDetails(criteria));
		viewContext.publishDtList(topIntentsKey, DtDefinitions.TopIntentFields.topId, analyticsServices.getTopIntents(bot, localeManager.getCurrentLocale().toString(), criteria));
		viewContext.publishDtList(topicsKey, topicServices.getAllTopicByBot(bot));
		viewContext.publishRef(ratingStatsKey, timeSerieServices.getRatingStats(criteria));
		viewContext.publishDto(chatbotCustomConfigKey, chabotCustomConfigServices.getChatbotCustomConfigByBotId(bot.getBotId()));
		viewContext.publishDtList(nodesKey, nodeServices.getNodesByBot(bot));
		viewContext.publishDtListModifiable(typeExportAnalyticsListKey, typeExportAnalyticsServices.getAllTypeExportAnalytics());
		viewContext.publishDtList(intentDetailsKey, DtDefinitions.SentenseDetailFields.topId, new DtList<SentenseDetail>(SentenseDetail.class));
	}

	@PostMapping("/_updateStats")
	public ViewContext doUpdateStats(final ViewContext viewContext,
									 @ViewAttribute("bot") final Chatbot bot,
									 @ViewAttribute("criteria") final StatCriteria criteria, final UiMessageStack uiMessageStack) {

		updateGraph(viewContext, criteria, bot);
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

	@PostMapping("/_exportStatisticFile")
	public VFile doExportStatisticFile(final ViewContext viewContext,
									   @ViewAttribute("criteria") final StatCriteria criteria,
									   @ViewAttribute("selectTypeExportAnalytics") final String selectTypeExportAnalytics) {

		if (selectTypeExportAnalytics.isEmpty()) {
			throw new VUserException(AnalyticsMultilingualResources.MANDATORY_TYPE_EXPORT_ANALYTICS);
		}
		switch (selectTypeExportAnalytics) {
			case "SESSIONS":
				final DtList<SessionExport> listSessionExport = analyticsExportServices.getSessionExport(criteria);
				return analyticsExportServices.exportSessions(listSessionExport);
			case "UNKNOWN_MESSAGES":
				final DtList<UnknownSentenseExport> listUnknownSentenseExport = analyticsExportServices.getUnknownSentenseExport(criteria);
				return analyticsExportServices.exportUnknownMessages(listUnknownSentenseExport);
			default:
				throw new VUserException(AnalyticsMultilingualResources.MANDATORY_TYPE_EXPORT_ANALYTICS);
		}

	}
}
