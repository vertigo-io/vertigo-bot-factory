package io.vertigo.chatbot.designer.builder.controllers.bot;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.TopicCategory;
import io.vertigo.chatbot.designer.builder.services.topic.TopicCategoryServices;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;

@Controller
@RequestMapping("/bot/{botId}/categories")
@Secured("BotUser")
public class TopicCategoryListController extends AbstractBotController {

	private static final ViewContextKey<TopicCategory> topicCategorieskKey = ViewContextKey.of("topicCategories");

	@Inject
	private TopicCategoryServices topicCategoryServices;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, @PathVariable("botId") final Long botId) {
		final Chatbot bot = initCommonContext(viewContext, botId);
		viewContext.publishDtList(topicCategorieskKey, topicCategoryServices.getAllCategoriesByBot(bot));
	}
}
