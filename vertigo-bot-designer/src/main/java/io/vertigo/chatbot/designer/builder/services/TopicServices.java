package io.vertigo.chatbot.designer.builder.services;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.topic.NluTrainingSentenceDAO;
import io.vertigo.chatbot.commons.dao.topic.TopicDAO;
import io.vertigo.chatbot.commons.dao.topic.TypeTopicDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.NluTrainingSentence;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TopicIhm;
import io.vertigo.chatbot.commons.domain.topic.TypeTopic;
import io.vertigo.chatbot.designer.builder.topic.TopicPAO;
import io.vertigo.chatbot.domain.DtDefinitions.NluTrainingSentenceFields;
import io.vertigo.chatbot.domain.DtDefinitions.TopicFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.util.StringUtil;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.util.VCollectors;

@Transactional
public class TopicServices implements Component {

	@Inject
	private TopicDAO topicDAO;

	@Inject
	private TopicPAO topicPAO;
	
	@Inject
	private TypeTopicDAO typeTopicDAO;
	

	@Inject
	private NluTrainingSentenceDAO nluTrainingSentenceDAO;

	public Topic findTopicById(final Long id) {
		return topicDAO.get(id);
	}

	public Topic save(final Topic topic) {
		return topicDAO.save(topic);
	}

	public Topic save(final Topic topic, final Boolean isEnabled, final DtList<NluTrainingSentence> nluTrainingSentences, final DtList<NluTrainingSentence> nluTrainingSentencesToDelete) {

		// save and remove NTS
		final DtList<NluTrainingSentence> ntsToSave = saveAllNotBlankNTS(topic, nluTrainingSentences);
		removeNTS(nluTrainingSentencesToDelete);
		topic.setIsEnabled(!ntsToSave.isEmpty() || isEnabled);

		return topicDAO.save(topic);
	}

	public Topic createTopic(final Topic topic) {
		return topicDAO.create(topic);
	}

	public Topic createTopic(final String title, final String ttoCd, final String description, final Boolean isEnabled) {
		final Topic toCreate = new Topic();
		toCreate.setTitle(title);
		toCreate.setTtoCd(ttoCd);
		toCreate.setDescription(description);
		toCreate.setIsEnabled(isEnabled);
		return topicDAO.create(toCreate);
	}

	public Topic getNewTopic(final Chatbot bot) {
		final Topic topic = new Topic();
		topic.setBotId(bot.getBotId());
		topic.setIsEnabled(true);
		return topic;
	}

	public void deleteTopic(final Chatbot bot, final Topic topic) {
		// delete sub elements
		for (final NluTrainingSentence its : getNluTrainingSentenceByTopic(bot, topic)) {
			nluTrainingSentenceDAO.delete(its.getUID());
		}

		topicDAO.delete(topic.getTopId());
	}

	public DtList<Topic> getAllTopicByBot(final Chatbot bot) {
		return topicDAO.findAll(Criterions.isEqualTo(TopicFields.botId, bot.getBotId()), DtListState.of(1000));
	}

	public DtList<TopicIhm> getAllTopicIhmByBot(final Chatbot bot) {
		return topicPAO.getAllTopicsIhmFromBot(bot.getBotId());
	}
	
	public DtList<Topic> getAllTopicEnableByBot(final Chatbot bot) {
		return topicDAO.findAll(Criterions.isEqualTo(TopicFields.botId, bot.getBotId()).and(Criterions.isEqualTo(TopicFields.isEnabled, true)), DtListState.of(1000));
	}

	public void removeAllTopicsFromBot(final Chatbot bot) {
		topicPAO.removeAllTopicsFromBot(bot.getBotId());
	}
	
	public DtList<TypeTopic> getAllTypeTopic() {
		return typeTopicDAO.findAll(Criterions.alwaysTrue(), DtListState.of(100));
	}

	//********* NTS part ********/

	public DtList<NluTrainingSentence> getNluTrainingSentenceByTopic(@SecuredOperation("botVisitor") final Chatbot bot, final Topic topic) {
		Assertion.check()
				.isNotNull(topic.getTopId());
		// ---

		return nluTrainingSentenceDAO.findAll(
				Criterions.isEqualTo(NluTrainingSentenceFields.topId, topic.getTopId()),
				DtListState.of(1000, 0, NluTrainingSentenceFields.ntsId.name(), false));
	}

	public void removeNTS(final DtList<NluTrainingSentence> nluTrainingSentencesToDelete) {
		nluTrainingSentencesToDelete.stream()
				.filter(itt -> itt.getNtsId() != null)
				.forEach(itt -> nluTrainingSentenceDAO.delete(itt.getNtsId()));
	}

	protected DtList<NluTrainingSentence> saveAllNotBlankNTS(final Topic topic, final DtList<NluTrainingSentence> nluTrainingSentences) {
		// save nlu textes
		final DtList<NluTrainingSentence> ntsToSave = nluTrainingSentences.stream()
				.filter(nts -> !StringUtil.isBlank(nts.getText()))
				.collect(VCollectors.toDtList(NluTrainingSentence.class));

		for (final NluTrainingSentence nts : ntsToSave) {
			nts.setTopId(topic.getTopId());
			nluTrainingSentenceDAO.save(nts);
		}

		return ntsToSave;
	}

	public void removeAllNTSFromBot(final Chatbot bot) {
		topicPAO.removeAllNluTrainingSentenceByBotId(bot.getBotId());
	}
}
