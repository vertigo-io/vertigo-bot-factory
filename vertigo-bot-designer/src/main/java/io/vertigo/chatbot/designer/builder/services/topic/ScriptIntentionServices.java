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
import io.vertigo.chatbot.designer.builder.services.HistoryServices;
import io.vertigo.chatbot.designer.builder.services.IRecordable;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.designer.domain.History;
import io.vertigo.chatbot.designer.domain.HistoryActionEnum;
import io.vertigo.chatbot.domain.DtDefinitions.ScriptIntentionFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.locale.LocaleMessageText;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.util.StringUtil;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DataObject;

import javax.inject.Inject;
import java.util.Optional;

@Transactional
@Secured("BotUser")
public class ScriptIntentionServices implements Component, ITopicService<ScriptIntention>, IRecordable<ScriptIntention> {

	@Inject
	private ScriptIntentionDAO scriptIntentionDAO;

	@Inject
	private ScriptIntentionPAO scriptIntentionPAO;

	@Inject
	private NodeServices nodeServices;

	@Inject
	private TopicDAO topicDAO;

	@Inject
	private HistoryServices historyServices;

	public ScriptIntention getScriptIntentionById(@SecuredOperation("botVisitor") final Chatbot bot, final Long sinId) {
		Assertion.check().isNotNull(sinId);
		// ---
		return scriptIntentionDAO.get(sinId);
	}

	public ScriptIntention getNewScriptIntention(@SecuredOperation("botVisitor") final Chatbot bot) {
		final ScriptIntention scriptIntention = new ScriptIntention();
		return scriptIntention;
	}

	public ScriptIntention save(@SecuredOperation("botAdm") final Chatbot chatbot,
			final ScriptIntention scriptIntention,
			final Topic topic) {
		final boolean isNew = scriptIntention.getSinId() == null;
		final ScriptIntention oldScriptIntention = findByTopId(topic.getTopId()).orElse(null);
		scriptIntention.setTopId(topic.getTopId());
		final ScriptIntention savedScriptIntention = save(scriptIntention);
		if (oldScriptIntention == null || (oldScriptIntention.getScript() != null && !oldScriptIntention.getScript().equals(scriptIntention.getScript()))) {
			nodeServices.updateNodes(chatbot);
			record(chatbot, savedScriptIntention, isNew ? HistoryActionEnum.ADDED : HistoryActionEnum.UPDATED);
		}
		return savedScriptIntention;

	}

	@Override
	public void deleteIfExists(@SecuredOperation("botContributor") final Chatbot chatbot, final Topic topic) {
		// delete scriptIntention
		findByTopId(topic.getTopId())
				.ifPresent(scriptIntention -> {
					delete(scriptIntention);
					record(chatbot, scriptIntention, HistoryActionEnum.DELETED);
				});

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
	public boolean hasToBeDeactivated(final Topic topic, final DtList<NluTrainingSentence> sentences, final DataObject object, final Chatbot bot) {
		final ScriptIntention scriptIntention = (ScriptIntention) object;
		return (!KindTopicEnum.UNREACHABLE.name().equals(topic.getKtoCd()) && sentences.isEmpty()) || StringUtil.isBlank(scriptIntention.getScript());
	}

	@Override
	public String getDeactivateMessage() {
		return LocaleMessageText.of(TopicsMultilingualResources.DEACTIVATE_TOPIC_SCRIPT_INTENTION).getDisplay();
	}

	@Override
	public void saveTopic(final Topic topic, final Chatbot chatbot, final DataObject dtObject) {
		save(chatbot, (ScriptIntention) dtObject, topic);
	}

	@Override
	public History record(final Chatbot bot, final ScriptIntention scriptIntention, final HistoryActionEnum action) {
		scriptIntention.topic().load();
		final Topic topic = scriptIntention.topic().get();
		final String message = topic.getKtoCd() + " - " + topic.getTitle();
		return historyServices.record(bot, action, scriptIntention.getClass().getSimpleName(), message);
	}
}
