package io.vertigo.chatbot.designer.builder.services.topic;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.topic.ScriptIntentionDAO;
import io.vertigo.chatbot.commons.dao.topic.TopicDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.KindTopicEnum;
import io.vertigo.chatbot.commons.domain.topic.NluTrainingSentence;
import io.vertigo.chatbot.commons.domain.topic.ScriptIntention;
import io.vertigo.chatbot.commons.domain.topic.ScriptIntentionIhm;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TypeTopicEnum;
import io.vertigo.chatbot.commons.multilingual.topics.TopicsMultilingualResources;
import io.vertigo.chatbot.designer.builder.scriptIntention.ScriptIntentionPAO;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.designer.domain.commons.BotPredefinedTopic;
import io.vertigo.chatbot.domain.DtDefinitions.ScriptIntentionFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.locale.MessageText;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.util.StringUtil;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtObject;

import javax.inject.Inject;
import java.util.Optional;

@Transactional
@Secured("BotUser")
public class ScriptIntentionServices implements Component, ITopicService<ScriptIntention> {

	@Inject
	private ScriptIntentionDAO scriptIntentionDAO;

	@Inject
	private ScriptIntentionPAO scriptIntentionPAO;

	@Inject
	private NodeServices nodeServices;

	@Inject
	private TopicDAO topicDAO;

	public ScriptIntention getScriptIntentionById(@SecuredOperation("botVisitor") final Chatbot bot, final Long sinId) {
		Assertion.check().isNotNull(sinId);
		// ---
		return scriptIntentionDAO.get(sinId);
	}

	public ScriptIntention getNewScriptIntention(@SecuredOperation("botAdm") final Chatbot bot) {
		final ScriptIntention scriptIntention = new ScriptIntention();
		return scriptIntention;
	}

	public ScriptIntention save(@SecuredOperation("botAdm") final Chatbot chatbot,
			final ScriptIntention scriptIntention,
			final Topic topic) {

		ScriptIntention oldScriptIntention = findByTopId(topic.getTopId()).orElse(null);
		if (oldScriptIntention == null || (oldScriptIntention.getScript() != null && !oldScriptIntention.getScript().equals(scriptIntention.getScript()))) {
			nodeServices.updateNodes(chatbot);
		}
		scriptIntention.setTopId(topic.getTopId());
		return this.save(scriptIntention);

	}

	@Override
	public void deleteIfExists(@SecuredOperation("botAdm") final Chatbot chatbot, final Topic topic) {
		// delete scriptIntention
		findByTopId(topic.getTopId())
				.ifPresent(this::delete);
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
	public Optional<ScriptIntention> findByTopId(final Long topId) {
		Assertion.check().isNotNull(topId);

		return scriptIntentionDAO.findOptional(Criterions.isEqualTo(ScriptIntentionFields.topId, topId));
	}

	@Override
	public void createOrUpdateFromTopic(final Chatbot chatbot, final Topic topic, final String text) {
		final ScriptIntention sin = findByTopId(topic.getTopId()).orElse(new ScriptIntention());
		sin.setTopId(topic.getTopId());
		sin.setScript(text);
		save(chatbot, sin, topic);
	}

	@Override
	public BotPredefinedTopic getBotPredefinedTopicByTopId(final Long topId) {
		Assertion.check()
				.isNotNull(topId);
		// ---
		final Topic topic = topicDAO.get(topId);
		final ScriptIntention sin = findByTopId(topId).orElseThrow();

		final BotPredefinedTopic predefinedTopic = new BotPredefinedTopic();
		predefinedTopic.setTopId(topId);
		predefinedTopic.setTtoCd(topic.getTtoCd());
		predefinedTopic.setValue(sin.getScript());
		return predefinedTopic;
	}

	@Override
	public boolean hasToBeDeactivated(final Topic topic, final DtList<NluTrainingSentence> sentences, final DtObject object, final Chatbot bot) {
		ScriptIntention scriptIntention = (ScriptIntention) object;
		return (!KindTopicEnum.UNREACHABLE.name().equals(topic.getKtoCd()) && sentences.isEmpty()) || StringUtil.isBlank(scriptIntention.getScript());
	}

	@Override
	public String getDeactivateMessage() {
		return MessageText.of(TopicsMultilingualResources.DEACTIVATE_TOPIC_SCRIPT_INTENTION).getDisplay();
	}

	@Override
	public void saveTopic(Topic topic, Chatbot chatbot, DtObject dtObject) {
		save(chatbot, (ScriptIntention) dtObject, topic);
	}

}
