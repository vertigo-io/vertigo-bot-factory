package io.vertigo.chatbot.designer.builder.services.topic;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.topic.TopicLabelDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TopicFileExport;
import io.vertigo.chatbot.commons.domain.topic.TopicLabel;
import io.vertigo.chatbot.designer.builder.topicLabel.TopicLabelPAO;
import io.vertigo.chatbot.domain.DtDefinitions.TopicLabelFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criteria;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;


@Transactional
@Secured("BotUser")
public class TopicLabelServices implements Component {

	@Inject
	private TopicLabelDAO topicLabelDAO;

	@Inject
	private TopicLabelPAO topicLabelPAO;

	public TopicLabel save(@SecuredOperation("botContributor") final Chatbot bot, final TopicLabel label) {
		return topicLabelDAO.save(label);
	}

	public TopicLabel save(@SecuredOperation("botContributor") final Chatbot bot, final String label) {
		final TopicLabel topicLabel = new TopicLabel();
		topicLabel.setLabel(label);
		topicLabel.setBotId(bot.getBotId());
		return save(bot, topicLabel);
	}

	public DtList<TopicLabel> getTopicLabelByBotId(@SecuredOperation("botVisitor") final Chatbot bot) {
		return topicLabelDAO.findAll(getBotCriteria(bot), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}

	public DtList<TopicLabel> getTopicLabelByBotIdAndTopId(@SecuredOperation("botContributor") final Chatbot bot, final Long topId) {
		return topicLabelDAO.getAllLabelsByBotId(bot.getBotId(), topId);
	}

	public void manageLabels(@SecuredOperation("botContributor") final Chatbot bot, final Topic topic, final DtList<TopicLabel> labels, final DtList<TopicLabel> initialLabels) {
		final List<String> initialLabelsList = initialLabels.stream().map(TopicLabel::getLabel).collect(Collectors.toList());
		final List<String> labelsList = labels.stream().map(TopicLabel::getLabel).collect(Collectors.toList());
		removeLabels(bot.getBotId(), topic, labelsList, initialLabelsList);
		addLabels(bot.getBotId(), topic, labelsList);
	}

	private void addLabels(final Long botId, final Topic topic, final List<String> labelsList) {
		createLabel(botId, labelsList);
		addLabelsToTopics(botId, topic, labelsList);

	}

	private void addLabelsToTopics(final Long botId, final Topic topic, final List<String> labelsList) {
		topic.label().load();
		final List<String> topicLabelsList = topic.label().get().stream().map(TopicLabel::getLabel).collect(Collectors.toList());
		final List<String> toAdd = labelsList.stream().filter(x -> !topicLabelsList.contains(x)).collect(Collectors.toList());
		topicLabelPAO.addInNNTopicLabel(toAdd, topic.getTopId(), botId);
	}

	private void createLabel(final Long botId, final List<String> labelsList) {
		final DtList<TopicLabel> botLabels = getTopicLabelByBotId(botId);
		final List<String> botLabelsString = botLabels.stream().map(TopicLabel::getLabel).collect(Collectors.toList());
		labelsList.stream().filter(x -> !botLabelsString.contains(x)).forEach(x -> save(botId, x));
	}

	private void removeLabels(final Long botId, final Topic topic, final List<String> labelsList, final List<String> initialLabels) {
		final List<String> listToDelete = initialLabels.stream().filter(x -> !labelsList.contains(x)).collect(Collectors.toList());
		removeFromNN(botId, topic, listToDelete);
	}

	private void removeFromNN(final Long botId, final Topic topic, final List<String> listToDelete) {
		if (!listToDelete.isEmpty()) {
			topicLabelPAO.removeFromNNTopicLabel(listToDelete, topic.getTopId(), botId);
			removeUnusedLabel(botId);
		}

	}

	private void delete(final TopicLabel label) {
		topicLabelDAO.delete(label.getLabelId());
	}

	private static Criteria<TopicLabel> getBotCriteria(final Chatbot bot) {
		return Criterions.isEqualTo(TopicLabelFields.botId, bot.getBotId());
	}

	public void replaceLabel(final TopicFileExport tfe, final Topic topicSaved) {
		final Long botId = topicSaved.getBotId();
		topicLabelPAO.resetNNTopicLabel(topicSaved.getTopId());
		final String labels = tfe.getLabels();
		if (!labels.isBlank()) {
			final List<String> listLabels = Arrays.asList(tfe.getLabels().split(","));
			addLabels(botId, topicSaved, listLabels);
			removeUnusedLabel(botId);
		}
	}

	private DtList<TopicLabel> getTopicLabelByBotId(final Long botId) {
		return topicLabelDAO.findAll(Criterions.isEqualTo(TopicLabelFields.botId, botId), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}

	private TopicLabel save(final Long botId, final String label) {
		final TopicLabel topicLabel = new TopicLabel();
		topicLabel.setLabel(label);
		topicLabel.setBotId(botId);
		return topicLabelDAO.save(topicLabel);
	}

	private void removeUnusedLabel(final Long botId) {
		final DtList<TopicLabel> topicLabelToDelete = topicLabelDAO.getAllUnusedLabelByBotId(botId);
		topicLabelToDelete.stream().forEach(x -> delete(x));
	}

	public void cleanLabelFromTopic(@SecuredOperation("botContributor") final Chatbot bot, final Long topId) {
		topicLabelPAO.removeAllLabelByTopicId(topId);
		removeUnusedLabel(bot.getBotId());
	}

	public void cleanLabelFromBot(@SecuredOperation("botAdm") final Chatbot bot) {
		topicLabelPAO.removeAllLabelFromBotId(bot.getBotId());
		topicLabelPAO.removeAllLabelByBotId(bot.getBotId());
	}
}
