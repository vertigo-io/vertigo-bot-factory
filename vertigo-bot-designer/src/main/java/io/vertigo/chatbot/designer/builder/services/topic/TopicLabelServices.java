package io.vertigo.chatbot.designer.builder.services.topic;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.topic.TopicLabelDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TopicLabel;
import io.vertigo.chatbot.designer.builder.topicLabel.TopicLabelPAO;
import io.vertigo.chatbot.domain.DtDefinitions.TopicLabelFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criteria;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

@Transactional
@Secured("BotUser")
public class TopicLabelServices implements Component {

	@Inject
	private TopicLabelDAO topicLabelDAO;

	@Inject
	private TopicLabelPAO topicLabelPAO;

	public TopicLabel save(@SecuredOperation("botAdm") final Chatbot bot, final TopicLabel label) {
		return topicLabelDAO.save(label);
	}

	public TopicLabel save(@SecuredOperation("botAdm") final Chatbot bot, final String label) {
		final TopicLabel topicLabel = new TopicLabel();
		topicLabel.setLabel(label);
		topicLabel.setBotId(bot.getBotId());
		return save(bot, topicLabel);
	}

	public DtList<TopicLabel> getTopicLabelByBotId(@SecuredOperation("botVisitor") final Chatbot bot) {
		return topicLabelDAO.findAll(getBotCriteria(bot), DtListState.of(1000));
	}

	public DtList<TopicLabel> getTopicLabelByBotIdAndTopId(@SecuredOperation("botAdm") final Chatbot bot, final Long topId) {
		return topicLabelDAO.getAllLabelsByBotId(bot.getBotId(), topId);
	}

	public void manageLabels(@SecuredOperation("botAdm") final Chatbot bot, final Topic topic, final DtList<TopicLabel> labels, final DtList<TopicLabel> initialLabels) {
		final List<String> initialLabelsList = initialLabels.stream().map(TopicLabel::getLabel).collect(Collectors.toList());
		final List<String> labelsList = labels.stream().map(TopicLabel::getLabel).collect(Collectors.toList());
		removeLabels(bot, topic, labelsList, initialLabelsList);
		addLabels(bot, topic, labelsList, initialLabelsList);
	}

	private void addLabels(final Chatbot bot, final Topic topic, final List<String> labelsList, final List<String> initialLabelsList) {
		createLabel(bot, labelsList);
		addLabelsToTopics(bot, topic, labelsList);

	}

	private void addLabelsToTopics(final Chatbot bot, final Topic topic, final List<String> labelsList) {
		topic.label().load();
		final List<String> topicLabelsList = topic.label().get().stream().map(TopicLabel::getLabel).collect(Collectors.toList());
		final List<String> toAdd = labelsList.stream().filter(x -> !topicLabelsList.contains(x)).collect(Collectors.toList());
		topicLabelPAO.addInNNTopicLabel(toAdd, topic.getTopId(), bot.getBotId());
	}

	private void createLabel(final Chatbot bot, final List<String> labelsList) {
		DtList<TopicLabel> botLabels = getTopicLabelByBotId(bot);
		List<String> botLabelsString = botLabels.stream().map(TopicLabel::getLabel).collect(Collectors.toList());
		labelsList.stream().filter(x -> !botLabelsString.contains(x)).forEach(x -> save(bot, x));
	}

	private void removeLabels(final Chatbot bot, final Topic topic, final List<String> labelsList, final List<String> initialLabels) {
		final List<String> listToDelete = initialLabels.stream().filter(x -> !labelsList.contains(x)).collect(Collectors.toList());
		removeFromNN(bot, topic, listToDelete);
	}

	private void removeFromNN(final Chatbot bot, final Topic topic, final List<String> listToDelete) {
		if (!listToDelete.isEmpty()) {
			topicLabelPAO.removeFromNNTopicLabel(listToDelete, topic.getTopId(), bot.getBotId());
			final DtList<TopicLabel> topicLabelToDelete = topicLabelDAO.getAllUnusedLabelByBotId(bot.getBotId());
			topicLabelToDelete.stream().forEach(x -> delete(x));
		}

	}

	private void delete(final TopicLabel label) {
		topicLabelDAO.delete(label.getLabelId());
	}

	private static Criteria<TopicLabel> getBotCriteria(final Chatbot bot) {
		return Criterions.isEqualTo(TopicLabelFields.botId, bot.getBotId());
	}
}
