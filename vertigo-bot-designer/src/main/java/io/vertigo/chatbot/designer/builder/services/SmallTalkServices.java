package io.vertigo.chatbot.designer.builder.services;

import java.util.Map;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.topic.SmallTalkDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.SmallTalkExport;
import io.vertigo.chatbot.commons.domain.topic.NluTrainingSentence;
import io.vertigo.chatbot.commons.domain.topic.ResponseButton;
import io.vertigo.chatbot.commons.domain.topic.ResponseTypeEnum;
import io.vertigo.chatbot.commons.domain.topic.SmallTalk;
import io.vertigo.chatbot.commons.domain.topic.SmallTalkIhm;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TypeTopicEnum;
import io.vertigo.chatbot.commons.domain.topic.UtterText;
import io.vertigo.chatbot.designer.builder.smallTalk.SmallTalkPAO;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtList;

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

	public SmallTalk getSmallTalkById(@SecuredOperation("botVisitor") final Chatbot bot, final Long movId) {
		Assertion.check().isNotNull(movId);
		// ---
		return smallTalkDAO.get(movId);
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
				.isNotNull(smallTalk)
				.isNotNull(nluTrainingSentences)
				.isNotNull(nluTrainingSentencesToDelete)
				.isNotNull(utterTexts)
				.isNotNull(buttonList);
		// ---
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

	public DtList<SmallTalkExport> exportSmallTalks(@SecuredOperation("botContributor") final Chatbot bot, final DtList<SmallTalk> smallTalks,
			final Map<Long, DtList<NluTrainingSentence>> trainingSentencesMap,
			final Map<Long, DtList<UtterText>> utterTextsMap,
			final Map<Long, DtList<ResponseButton>> buttonsMap) {
		final DtList<SmallTalkExport> retour = new DtList<>(SmallTalkExport.class);
		for (final SmallTalk smallTalk : smallTalks) {
			final SmallTalkExport newExport = new SmallTalkExport();
			newExport.setSmallTalk(smallTalk);
			newExport.setNluTrainingSentences(trainingSentencesMap.getOrDefault(smallTalk.getSmtId(), new DtList<>(NluTrainingSentence.class)));
			newExport.setUtterTexts(utterTextsMap.getOrDefault(smallTalk.getSmtId(), new DtList<>(UtterText.class)));
			newExport.setButtons(buttonsMap.getOrDefault(smallTalk.getSmtId(), new DtList<>(ResponseButton.class)));

			retour.add(newExport);
		}
		return retour;
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

}
