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
import io.vertigo.datamodel.structure.util.VCollectors;

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

	public DtList<TopicLabel> getTopicLabelByBotId(@SecuredOperation("botVisitor") final Chatbot bot) {
		return topicLabelDAO.findAll(getBotCriteria(bot), DtListState.of(1000));
	}

	public DtList<TopicLabel> getTopicLabelByBotIdAndTopId(@SecuredOperation("botAdm") final Chatbot bot, final Long topId) {
		return topicLabelDAO.getAllLabelsByBotId(bot.getBotId(), topId);
	}

	public void manageLabels(@SecuredOperation("botAdm") final Chatbot bot, final Topic topic, final DtList<TopicLabel> labels, final DtList<TopicLabel> initialLabels) {
		removeLabels(bot, topic, labels, initialLabels);
		addLabels(bot, topic, labels, initialLabels);
	}

	private void addLabels(final Chatbot bot, final Topic topic, final DtList<TopicLabel> labels, final DtList<TopicLabel> initialLabels) {
		final Long botId = bot.getBotId();
		final DtList<TopicLabel> listToAdd = labels.stream()
				.filter(x -> x.getLabelId() != null)
				.filter(x -> !initialLabels.contains(x))
				.collect(VCollectors.toDtList(TopicLabel.class));
		final DtList<TopicLabel> listNotSave = labels.stream().filter(x -> x.getLabelId() == null).collect(VCollectors.toDtList(TopicLabel.class));
		for (TopicLabel toSave : listNotSave) {
			toSave.setBotId(botId);
			listToAdd.add(save(bot, toSave));
		}

		updateNN(topic, listToAdd);

	}

	private void updateNN(final Topic topic, final DtList<TopicLabel> listToAdd) {
		final List<Long> ids = listToAdd.stream().map(x -> x.getLabelId()).collect(Collectors.toList());
		topicLabelPAO.updateNNTopicLabel(ids, topic.getTopId());
	}

	private void removeLabels(final Chatbot bot, final Topic topic, final DtList<TopicLabel> labels, final DtList<TopicLabel> initialLabels) {
		final DtList<TopicLabel> listToDelete = initialLabels.stream().filter(x -> !labels.contains(x)).collect(VCollectors.toDtList(TopicLabel.class));
		removeFromNN(bot, topic, listToDelete);
	}

	private void removeFromNN(final Chatbot bot, final Topic topic, final DtList<TopicLabel> listToDelete) {
		final List<Long> ids = listToDelete.stream().map(TopicLabel::getLabelId).collect(Collectors.toList());
		if (!ids.isEmpty()) {
			topicLabelPAO.removeFromNNTopicLabel(ids, topic.getTopId());
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
