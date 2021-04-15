package io.vertigo.chatbot.designer.builder.services.topic;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.dao.topic.TopicCategoryDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TopicCategory;
import io.vertigo.chatbot.designer.builder.topicCategory.TopicCategoryPAO;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

@Transactional
@Secured("BotUser")
public class TopicCategoryServices implements Component {

	@Inject
	private TopicCategoryDAO topicCategoryDAO;

	@Inject
	private TopicCategoryPAO topicCategoryPAO;

	@Inject
	private TopicServices topicServices;

	public TopicCategory saveCategory(final TopicCategory category) {
		return topicCategoryDAO.save(category);
	}

	public void deleteCategory(final Long id) {
		topicCategoryDAO.delete(id);
	}

	public TopicCategory getTopicCategoryById(final Long categoryId) {
		return topicCategoryDAO.get(categoryId);
	}

	public DtList<TopicCategory> getCategoryByTopic(final Topic topic) {
		topic.category().load();
		return topic.category().get();
	}

	public DtList<Topic> getAllTopicFromCategory(final TopicCategory category) {
		return topicServices.getTopicFromTopicCategory(category);
	}

	public DtList<TopicCategory> getAllCategories() {
		return topicCategoryDAO.findAll(Criterions.alwaysTrue(), DtListState.of(1000));
	}

	public DtList<TopicCategory> getAllCategoriesByBot(final Chatbot bot) {
		return topicCategoryDAO.getAllCategoriesByBotId(bot.getBotId());
	}

	public DtList<TopicCategory> getAllActiveCategoriesByBot(final Chatbot bot) {
		return topicCategoryDAO.getAllActiveCategoriesByBotId(bot.getBotId());
	}

	public Topic addCategoryToTopic(final TopicCategory category, final Topic topic) {
		final DtList<TopicCategory> categories = getCategoryByTopic(topic);
		if (!categories.contains(category)) {
			categories.add(category);
			topic.category().set(categories);
		}
		return topicServices.save(topic);
	}

	public Topic removeCategoryFromTopic(final TopicCategory category, final Topic topic) {
		final DtList<TopicCategory> categories = getCategoryByTopic(topic);
		if (!categories.contains(category)) {
			throw new VSystemException("Category {0} doesn't remove from {1}", category.getLabel(), topic.getTitle());
		}
		categories.remove(category);
		topic.category().set(categories);
		return topicServices.save(topic);
	}

	public TopicCategory getNewTopicCategory(final Long botId) {
		final TopicCategory category = new TopicCategory();
		category.setBotId(botId);
		// Modify in the futur if sublevel needs
		category.setLevel(1L);
		category.setIsEnabled(true);
		return category;
	}

	public TopicCategory saveCategory(final TopicCategory category, final String[] addTopics) {
		final TopicCategory savedCategory = saveCategory(category);
		final List<Long> topicsIds = Arrays.asList(addTopics).stream().map(id -> Long.parseLong(id)).collect(Collectors.toList());
		topicCategoryPAO.addTopicWithCategory(savedCategory.getTopCatId(), topicsIds);
		return savedCategory;
	}

}
