package io.vertigo.chatbot.designer.builder.services.topic;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.topic.ScriptIntentionDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.NluTrainingSentence;
import io.vertigo.chatbot.commons.domain.topic.ScriptIntention;
import io.vertigo.chatbot.commons.domain.topic.ScriptIntentionIhm;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TypeTopicEnum;
import io.vertigo.chatbot.designer.builder.scriptIntention.ScriptIntentionPAO;
import io.vertigo.chatbot.domain.DtDefinitions.ScriptIntentionFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

@Transactional
@Secured("BotUser")
public class ScriptIntentionServices implements Component, TopicInterfaceServices<ScriptIntention> {

	@Inject
	private TopicServices topicServices;

	@Inject
	private ScriptIntentionDAO scriptIntentionDAO;

	@Inject
	private ScriptIntentionPAO scriptIntentionPAO;

	public ScriptIntention getScriptIntentionById(@SecuredOperation("botAdm") final Chatbot bot, final Long sinId) {
		Assertion.check().isNotNull(sinId);
		// ---
		return scriptIntentionDAO.get(sinId);
	}

	public ScriptIntention getNewScriptIntention(@SecuredOperation("botAdm") final Chatbot bot) {
		final ScriptIntention scriptIntention = new ScriptIntention();
		return scriptIntention;
	}

	public ScriptIntention save(@SecuredOperation("botAdm") final Chatbot chatbot, final ScriptIntention scriptIntention,
			final DtList<NluTrainingSentence> nluTrainingSentences, final DtList<NluTrainingSentence> nluTrainingSentencesToDelete,
			final Topic topic) {

		Assertion.check().isNotNull(scriptIntention)
				.isNotNull(nluTrainingSentences)
				.isNotNull(nluTrainingSentencesToDelete);

		// ---
		topic.setTtoCd(TypeTopicEnum.SCRIPTINTENTION.name());
		final Topic savedTopic = topicServices.save(topic);
		scriptIntention.setTopId(savedTopic.getTopId());
		final ScriptIntention savedSI = this.save(scriptIntention);
		topicServices.save(savedTopic, topic.getIsEnabled(), nluTrainingSentences, nluTrainingSentencesToDelete);
		return savedSI;
	}

	@Override
	public void delete(@SecuredOperation("botAdm") final Chatbot chatbot, final ScriptIntention scriptIntention, final Topic topic) {

		// delete scriptIntention
		delete(scriptIntention);

		topicServices.deleteTopic(chatbot, topic);
	}

	public void removeAllScriptIntentionFromBot(@SecuredOperation("botAdm") final Chatbot bot) {
		scriptIntentionPAO.removeAllScriptIntentionByBotId(bot.getBotId());
	}

	public DtList<ScriptIntention> getAllActiveScriptIntentionsByBot(@SecuredOperation("botAdm") final Chatbot bot) {
		return scriptIntentionDAO.getAllActiveScriptIntentionByBot(bot.getBotId());
	}

	public DtList<ScriptIntentionIhm> getScriptIntentionsIhmByBot(@SecuredOperation("botAdm") final Chatbot bot) {
		return scriptIntentionPAO.getScriptIntentionIHMByBot(bot.getBotId());
	}

	@Override
	public ScriptIntention save(final ScriptIntention scriptIntention) {
		return scriptIntentionDAO.save(scriptIntention);
	}

	@Override
	public void delete(final ScriptIntention scriptIntention) {
		scriptIntentionDAO.delete(scriptIntention.getUID());
	}

	@Override
	public boolean handleObject(final Topic topic) {
		return TypeTopicEnum.SCRIPTINTENTION.name().equals(topic.getTtoCd());
	}

	@Override
	public ScriptIntention findByTopId(final Long topId) {
		if (topId != null) {
			return scriptIntentionDAO.findAll(Criterions.isEqualTo(ScriptIntentionFields.topId, topId), DtListState.of(1)).get(0);
		}
		return null;
	}

}
