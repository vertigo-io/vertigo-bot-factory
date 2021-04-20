package io.vertigo.chatbot.designer.builder.services;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.topic.ScriptIntentionDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.ScriptIntention;
import io.vertigo.chatbot.commons.domain.topic.ScriptIntentionIhm;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TypeTopicEnum;
import io.vertigo.chatbot.designer.builder.scriptIntention.ScriptIntentionPAO;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtList;

@Transactional
@Secured("BotUser")
public class ScriptIntentionServices implements Component {

	@Inject
	private UtterTextServices utterTextServices;

	@Inject
	private TopicServices topicServices;

	@Inject
	private ResponsesButtonServices responsesButtonServices;

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

	public ScriptIntention saveScriptIntention(@SecuredOperation("botAdm") final Chatbot chatbot, final ScriptIntention scriptIntention,
			final Topic topic) {

		Assertion.check().isNotNull(scriptIntention);
		// ---
		topic.setTtoCd(TypeTopicEnum.SCRIPTINTENTION.name());
		final Topic savedTopic = topicServices.save(topic);
		scriptIntention.setTopId(savedTopic.getTopId());
		final ScriptIntention savedSI = scriptIntentionDAO.save(scriptIntention);

		return savedSI;
	}

	public void deleteScriptIntention(@SecuredOperation("botAdm") final Chatbot chatbot, final ScriptIntention scriptIntention, final Topic topic) {

		// delete scriptIntention
		scriptIntentionDAO.delete(scriptIntention.getUID());

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

}
