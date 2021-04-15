package io.vertigo.chatbot.designer.builder.controllers.bot;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TopicCategory;
import io.vertigo.chatbot.designer.builder.services.topic.TopicCategoryServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;

@Controller
@RequestMapping("/bot/{botId}/category")
@Secured("BotUser")
public class TopicCategoryDetailController extends AbstractBotController {

	private static final ViewContextKey<TopicCategory> topicCategoryKey = ViewContextKey.of("topicCategory");
	private static final ViewContextKey<Topic> topicsKey = ViewContextKey.of("topics");
	private static final ViewContextKey<Topic> notAssociatedTopicsKey = ViewContextKey.of("notAssociatedTopics");
	private static final ViewContextKey<String[]> addTopicsKey = ViewContextKey.of("addTopics");

	@Inject
	private TopicCategoryServices topicCategoryServices;

	@Inject
	private TopicServices topicServices;

	@GetMapping("/{topCatId}")
	public void initContext(final ViewContext viewContext, @PathVariable("botId") final Long botId,
			@PathVariable("topCatId") final Long topCatId) {
		final Chatbot bot = initCommonContext(viewContext, botId);
		final TopicCategory topicCategory = topicCategoryServices.getTopicCategoryById(topCatId);
		final DtList<Topic> topics = topicCategoryServices.getAllTopicFromCategory(topicCategory);
		viewContext.publishDto(topicCategoryKey, topicCategory);
		viewContext.publishDtListModifiable(topicsKey, topics);
		viewContext.publishRef(addTopicsKey, new String[0]);
		viewContext.publishDtList(notAssociatedTopicsKey, topicServices.getAllTopicByBot(bot));
		toModeReadOnly();
	}

	@GetMapping("/new")
	public void getNewCategory(final ViewContext viewContext, @PathVariable("botId") final Long botId) {
		final Chatbot bot = initCommonContext(viewContext, botId);
		viewContext.publishDto(topicCategoryKey, topicCategoryServices.getNewTopicCategory(botId));
		viewContext.publishDtListModifiable(topicsKey, new DtList<Topic>(Topic.class));
		viewContext.publishRef(addTopicsKey, new String[0]);
		viewContext.publishDtList(notAssociatedTopicsKey, topicServices.getAllTopicByBot(bot));
		toModeCreate();
	}

	@PostMapping("/_save")
	public String saveTopicCategory(final ViewContext viewContext, @ViewAttribute("topicCategory") final TopicCategory category,
			@ViewAttribute("addTopics") final String[] addTopics) {
		topicCategoryServices.saveCategory(category, addTopics);
		return "redirect:/bot/" + category.getBotId() + "/category/" + category.getTopCatId();
	}

	@PostMapping("/_delete")
	public String deleteTopicCategory(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot, @ViewAttribute("topicCategory") final TopicCategory category) {
		topicCategoryServices.deleteCategory(category.getTopCatId());
		return "redirect:/bot/" + bot.getBotId() + "/categories/";
	}
}
