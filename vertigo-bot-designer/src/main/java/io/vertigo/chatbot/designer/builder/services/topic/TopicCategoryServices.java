package io.vertigo.chatbot.designer.builder.services.topic;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.topic.TopicCategoryDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TopicCategory;
import io.vertigo.chatbot.commons.multilingual.topics.TopicsMultilingualResources;
import io.vertigo.chatbot.designer.builder.topicCategory.TopicCategoryPAO;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.locale.MessageText;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtList;

import javax.inject.Inject;
import java.util.Optional;

import static io.vertigo.chatbot.designer.builder.services.topic.TopicsUtils.DEFAULT_TOPIC_CAT_CODE;

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

	public void deleteCategory(@SecuredOperation("botAdm") final Chatbot bot, final TopicCategory category) {

		for (final Topic topic : getAllTopicFromCategory(bot, category)) {
			topicServices.deleteCompleteTopic(bot, topic);
		}
		topicCategoryDAO.delete(category.getTopCatId());
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

	public DtList<TopicCategory> getAllActiveCategoriesByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
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

	public void removeAllCategoryByBot(@SecuredOperation("botAdm") final Chatbot bot) {
		topicCategoryPAO.removeAllCategoryByBotId(bot.getBotId());
	}

	public TopicCategory initializeBasicCategory(final Chatbot chatbot) {
		final TopicCategory topicCategory = new TopicCategory();
		topicCategory.setIsEnabled(true);
		topicCategory.setLabel(MessageText.of(TopicsMultilingualResources.DEFAULT_TOPICS).getDisplay());
		topicCategory.setCode(DEFAULT_TOPIC_CAT_CODE);
		topicCategory.setIsTechnical(true);
		topicCategory.setLevel(1L);
		topicCategory.setBotId(chatbot.getBotId());
		return topicCategoryDAO.save(topicCategory);
	}

}
