package io.vertigo.chatbot.designer.builder.services.topic;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.topic.SmallTalkDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.KindTopicEnum;
import io.vertigo.chatbot.commons.domain.topic.NluTrainingSentence;
import io.vertigo.chatbot.commons.domain.topic.ResponseButton;
import io.vertigo.chatbot.commons.domain.topic.ResponseTypeEnum;
import io.vertigo.chatbot.commons.domain.topic.SmallTalk;
import io.vertigo.chatbot.commons.domain.topic.SmallTalkIhm;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TypeTopicEnum;
import io.vertigo.chatbot.commons.domain.topic.UtterText;
import io.vertigo.chatbot.designer.builder.services.ResponsesButtonServices;
import io.vertigo.chatbot.designer.builder.services.UtterTextServices;
import io.vertigo.chatbot.designer.builder.smallTalk.SmallTalkPAO;
import io.vertigo.chatbot.domain.DtDefinitions.SmallTalkFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

@Transactional
@Secured("BotUser")
public class SmallTalkServices implements Component {

	@Inject
	private UtterTextServices utterTextServices;

	@Inject
	private TopicServices topicServices;

	@Inject
	private ResponsesButtonServices responsesButtonServices;

	@Inject
	private SmallTalkDAO smallTalkDAO;

	@Inject
	private SmallTalkPAO smallTalkPAO;

	public SmallTalk getSmallTalkById(@SecuredOperation("botVisitor") final Chatbot bot, final Long smtId) {
		Assertion.check().isNotNull(smtId);
		// ---
		return smallTalkDAO.get(smtId);
	}

	public SmallTalk getNewSmallTalk(@SecuredOperation("botAdm") final Chatbot bot) {
		final SmallTalk smallTalk = new SmallTalk();
		smallTalk.responseType().setEnumValue(ResponseTypeEnum.RICH_TEXT);
		return smallTalk;
	}

	public SmallTalk saveSmallTalk(@SecuredOperation("botContributor") final Chatbot chatbot, final SmallTalk smallTalk,
			final DtList<NluTrainingSentence> nluTrainingSentences, final DtList<NluTrainingSentence> nluTrainingSentencesToDelete,
			final DtList<UtterText> utterTexts, final DtList<ResponseButton> buttonList, final Topic topic) {

		Assertion.check()
				.isNotNull(smallTalk).isNotNull(topic)
				.isNotNull(utterTexts)
				.isNotNull(buttonList);
		// ---
		if (KindTopicEnum.NORMAL.name().equals(topic.getKtoCd())) {
			Assertion.check().isNotNull(nluTrainingSentences)
					.isNotNull(nluTrainingSentencesToDelete);
		}

		topic.setTtoCd(TypeTopicEnum.SMALLTALK.name());
		final Topic savedTopic = topicServices.save(topic);
		smallTalk.setTopId(savedTopic.getTopId());
		final SmallTalk savedST = smallTalkDAO.save(smallTalk);

		// save utter textes, remove all + create all
		utterTextServices.removeAllUtterTextBySmtId(chatbot, savedST.getSmtId());
		final DtList<UtterText> uttToSave = utterTextServices.createNoBlankUtterTextBySmallTalk(chatbot, savedST, utterTexts);

		topicServices.save(savedTopic, !uttToSave.isEmpty(), nluTrainingSentences, nluTrainingSentencesToDelete);

		// remove and create buttons
		responsesButtonServices.removeAllButtonsBySmtId(chatbot, savedST);
		responsesButtonServices.saveAllButtonsBySmtId(chatbot, savedST, buttonList);
		return savedST;
	}

	public void deleteSmallTalk(@SecuredOperation("botContributor") final Chatbot chatbot, final SmallTalk smallTalk, final Topic topic) {

		utterTextServices.deleteUtterTextsBySmallTalk(chatbot, smallTalk);

		responsesButtonServices.deleteResponsesButtonsBySmallTalk(chatbot, smallTalk);

		// delete smallTalk
		smallTalkDAO.delete(smallTalk.getUID());

		topicServices.deleteTopic(chatbot, topic);
	}

	public void removeAllSmallTalkFromBot(@SecuredOperation("botAdm") final Chatbot bot) {
		smallTalkPAO.removeAllSmallTalkByBotId(bot.getBotId());
	}

	public DtList<SmallTalk> getAllActiveSmallTalksByBot(final Chatbot bot) {
		return smallTalkDAO.getAllActiveSmallTalkByBot(bot.getBotId());
	}

	public DtList<SmallTalkIhm> getSmallTalksIhmByBot(final Chatbot bot) {
		return smallTalkPAO.getSmallTalkIHMByBot(bot.getBotId());
	}

	@Secured("SuperAdm")
	public SmallTalk getSmallTalkByTopId(final Long topId) {
		if (topId != null) {
			return smallTalkDAO.findAll(Criterions.isEqualTo(SmallTalkFields.topId, topId), DtListState.of(1)).get(0);
		}
		return null;
	}

}
