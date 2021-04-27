package io.vertigo.chatbot.designer.builder.services.topic;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.topic.TopicCategoryDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TopicCategory;
import io.vertigo.chatbot.designer.builder.topicCategory.TopicCategoryPAO;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtList;

@Transactional
@Secured("BotUser")
public class TopicCategoryServices implements Component {

	@Inject
	private TopicCategoryDAO topicCategoryDAO;

	@Inject
	private TopicCategoryPAO topicCategoryPAO;

	@Inject
	private TopicServices topicServices;

	public TopicCategory saveCategory(@SecuredOperation("botContributor") final Chatbot bot, final TopicCategory category) {
		return topicCategoryDAO.save(category);
	}

	public void deleteCategory(@SecuredOperation("botAdm") final Chatbot bot, final Long id) {
		topicCategoryDAO.delete(id);
	}

	public TopicCategory getTopicCategoryById(@SecuredOperation("botVisitor") final Chatbot bot, final Long categoryId) {
		return topicCategoryDAO.get(categoryId);
	}

	public DtList<Topic> getAllTopicFromCategory(@SecuredOperation("botVisitor") final Chatbot bot, final TopicCategory category) {
		return topicServices.getTopicFromTopicCategory(category);
	}

	public DtList<TopicCategory> getAllCategoriesByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
		return topicCategoryDAO.getAllCategoriesByBotId(bot.getBotId(), Optional.empty(), Optional.empty());
	}

	public DtList<TopicCategory> getAllNonTechnicalCategoriesByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
		return topicCategoryDAO.getAllCategoriesByBotId(bot.getBotId(), Optional.empty(), Optional.of(false));
	}

	public DtList<TopicCategory> getAllActiveCategoriesByBot(@SecuredOperation("botContributor") final Chatbot bot) {
		return topicCategoryDAO.getAllCategoriesByBotId(bot.getBotId(), Optional.of(true), Optional.of(false));
	}

	public TopicCategory getNewTopicCategory(@SecuredOperation("botAdm") final Chatbot bot) {
		final TopicCategory category = new TopicCategory();
		category.setBotId(bot.getBotId());
		// Modify in the futur if sublevel needs
		category.setLevel(1L);
		category.setIsEnabled(true);
		category.setIsTechnical(false);
		return category;
	}

	public TopicCategory saveCategory(@SecuredOperation("botContributor") final Chatbot bot, final TopicCategory category, final String[] addTopics) {
		final TopicCategory savedCategory = saveCategory(bot, category);
		final List<Long> topicsIds = Arrays.asList(addTopics).stream().map(Long::parseLong).collect(Collectors.toList());
		topicCategoryPAO.addTopicWithCategory(savedCategory.getTopCatId(), topicsIds);
		return savedCategory;
	}

	public void removeAllCategoryByBot(@SecuredOperation("botAdm") final Chatbot bot) {
		topicCategoryPAO.removeAllCategoryByBotId(bot.getBotId());
	}

}
