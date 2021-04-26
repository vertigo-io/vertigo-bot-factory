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

	@Inject
	private TopicCategoryServices topicCategoryServices;

	@GetMapping("/{topCatId}")
	public void initContext(final ViewContext viewContext, @PathVariable("botId") final Long botId,
			@PathVariable("topCatId") final Long topCatId) {
		final Chatbot bot = initCommonContext(viewContext, botId);
		final TopicCategory topicCategory = topicCategoryServices.getTopicCategoryById(bot, topCatId);
		final DtList<Topic> topics = topicCategoryServices.getAllTopicFromCategory(bot, topicCategory);
		viewContext.publishDto(topicCategoryKey, topicCategory);
		viewContext.publishDtList(topicsKey, topics);
		toModeReadOnly();
	}

	@GetMapping("/new")
	public void getNewCategory(final ViewContext viewContext, @PathVariable("botId") final Long botId) {
		final Chatbot bot = initCommonContext(viewContext, botId);
		viewContext.publishDto(topicCategoryKey, topicCategoryServices.getNewTopicCategory(bot));
		viewContext.publishDtList(topicsKey, new DtList<Topic>(Topic.class));
		toModeCreate();
	}

	@PostMapping("/_save")
	public String saveTopicCategory(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot, @ViewAttribute("topicCategory") final TopicCategory category) {
		topicCategoryServices.saveCategory(bot, category);
		return "redirect:/bot/" + category.getBotId() + "/category/" + category.getTopCatId();
	}

	@PostMapping("/_delete")
	public String deleteTopicCategory(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot, @ViewAttribute("topicCategory") final TopicCategory category) {
		topicCategoryServices.deleteCategory(bot, category.getTopCatId());
		return "redirect:/bot/" + bot.getBotId() + "/categories/";
	}

	@PostMapping("/_edit")
	public void doEdit() {
		toModeEdit();
	}
}
