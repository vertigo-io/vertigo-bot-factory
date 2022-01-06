package io.vertigo.chatbot.designer.builder.controllers.bot;

import com.google.gson.reflect.TypeToken;
import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.UnknownSentenceDetail;
import io.vertigo.chatbot.commons.domain.UnknownSentenceStatus;
import io.vertigo.chatbot.commons.domain.UnknownSentenceStatusEnum;
import io.vertigo.chatbot.commons.domain.UnknownSentenceToUpdateIhm;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TopicCategory;
import io.vertigo.chatbot.designer.builder.services.UnknownSentencesServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicCategoryServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.engines.webservice.json.JsonEngine;
import io.vertigo.vega.webservice.validation.UiMessageStack;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import java.util.List;

import static io.vertigo.chatbot.designer.utils.ListUtils.listLimitReached;

@Controller
@RequestMapping("/bot/{botId}/followup")
@Secured("BotUser")
public class UnknownSentenceListController extends AbstractBotListController<UnknownSentenceDetail> {

	@Inject
	private UnknownSentencesServices unknownSentencesServices;

	@Inject
	private TopicCategoryServices topicCategoryServices;

	@Inject
	private TopicServices topicServices;

	@Inject
	private JsonEngine jsonEngine;

	@Inject
	private UnknownSentenceStatusServices unknownSentenceStatusServices;

	private static final ViewContextKey<UnknownSentenceDetail> unknownSentenceListKey = ViewContextKey.of("unknownSentenceList");
	private static final ViewContextKey<UnknownSentenceDetail> unknownSentenceFilteredListKey = ViewContextKey.of("unknownSentenceFilteredList");

	private static final ViewContextKey<TopicCategory> topicCategorieskKey = ViewContextKey.of("topicCategories");
	private static final ViewContextKey<Topic> topickKey = ViewContextKey.of("topics");
	private static final ViewContextKey<UnknownSentenceStatus> statusKey = ViewContextKey.of("statuses");

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
		final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);
		viewContext.publishDtList(topicCategorieskKey, topicCategoryServices.getAllNonTechnicalCategoriesByBot(bot));
		DtList<UnknownSentenceDetail> unknownSentenceDetailDtList = unknownSentencesServices.findUnknownSentences(botId);
		viewContext.publishDtList(unknownSentenceListKey, unknownSentenceDetailDtList);
		viewContext.publishDtList(unknownSentenceFilteredListKey, unknownSentenceDetailDtList);
		viewContext.publishDtList(topickKey, topicServices.getAllTopicByBot(bot));

		viewContext.publishDtList(statusKey, unknownSentenceStatusServices.findAll());
		listLimitReached(viewContext, uiMessageStack);
		super.initBreadCrums(viewContext, UnknownSentenceDetail.class);
		toModeReadOnly();
	}

	@PostMapping("/_treatUnknownSentence")
	public ViewContext treatUnknownSentence(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot, UiMessageStack uiMessageStack,
											@RequestParam("unknownSentencesToUpdate") final String unknownSentencesToUpdate,
											@RequestParam("topId") final Long topId) {

		topicServices.addTrainingSentence(bot,  jsonEngine.fromJson(unknownSentencesToUpdate, new TypeToken<List<UnknownSentenceToUpdateIhm>>(){}.getType()), topId);
		viewContext.publishDtList(unknownSentenceListKey, unknownSentencesServices.findUnknownSentences(bot.getBotId()));
		listLimitReached(viewContext, uiMessageStack);
		this.nodeMessageDisplay(bot, uiMessageStack);
		return viewContext;
	}

	@PostMapping("/_rejectUnknownSentence")
	public ViewContext rejectUnknownSentence(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot, UiMessageStack uiMessageStack,
											@RequestParam("unknownSentenceId") final Long unknownSentenceId) {
		unknownSentencesServices.updateStatus(unknownSentencesServices.findById(unknownSentenceId), UnknownSentenceStatusEnum.REJECTED);
		viewContext.publishDtList(unknownSentenceListKey, unknownSentencesServices.findUnknownSentences(bot.getBotId()));
		listLimitReached(viewContext, uiMessageStack);
		this.nodeMessageDisplay(bot, uiMessageStack);
		return viewContext;
	}

	@PostMapping("/_refreshUnknownSentences")
	public ViewContext refreshUnknownSentences(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot, UiMessageStack uiMessageStack) {
		DtList<UnknownSentenceDetail> unknownSentenceDetailDtList = unknownSentencesServices.findUnknownSentences(bot.getBotId());
		viewContext.publishDtList(unknownSentenceListKey, unknownSentenceDetailDtList);
		viewContext.publishDtList(unknownSentenceFilteredListKey, unknownSentenceDetailDtList);
		listLimitReached(viewContext, uiMessageStack);
		this.nodeMessageDisplay(bot, uiMessageStack);
		return viewContext;
	}
}
