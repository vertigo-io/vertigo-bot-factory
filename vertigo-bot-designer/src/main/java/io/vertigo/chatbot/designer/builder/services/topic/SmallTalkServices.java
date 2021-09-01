package io.vertigo.chatbot.designer.builder.services.topic;

import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.topic.SmallTalkDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.ResponseButton;
import io.vertigo.chatbot.commons.domain.topic.ResponseTypeEnum;
import io.vertigo.chatbot.commons.domain.topic.SmallTalk;
import io.vertigo.chatbot.commons.domain.topic.SmallTalkIhm;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TypeTopicEnum;
import io.vertigo.chatbot.commons.domain.topic.UtterText;
import io.vertigo.chatbot.commons.multilingual.topics.TopicsMultilingualResources;
import io.vertigo.chatbot.designer.builder.model.topic.SaveTopicObject;
import io.vertigo.chatbot.designer.builder.services.ResponsesButtonServices;
import io.vertigo.chatbot.designer.builder.services.UtterTextServices;
import io.vertigo.chatbot.designer.builder.smallTalk.SmallTalkPAO;
import io.vertigo.chatbot.domain.DtDefinitions.SmallTalkFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.locale.MessageText;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;

@Transactional
@Secured("BotUser")
public class SmallTalkServices implements Component, TopicInterfaceServices<SmallTalk> {

	@Inject
	private UtterTextServices utterTextServices;

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

	@Override
	public SmallTalk save(final SmallTalk smt) {
		return smallTalkDAO.save(smt);
	}

	public SmallTalk saveSmallTalk(@SecuredOperation("botContributor") final Chatbot chatbot, final SmallTalk smallTalk,

			final DtList<UtterText> utterTexts, final DtList<ResponseButton> buttonList, final Topic topic) {

		Assertion.check()
				.isNotNull(smallTalk).isNotNull(topic)
				.isNotNull(utterTexts)
				.isNotNull(buttonList);
		// ---

		smallTalk.setTopId(topic.getTopId());
		final SmallTalk savedST = save(smallTalk);

		// save utter textes, remove all + create all
		utterTextServices.removeAllUtterTextBySmtId(chatbot, savedST.getSmtId());
		utterTextServices.createNoBlankUtterTextBySmallTalk(chatbot, savedST, utterTexts);

		// remove and create buttons

		for (final ResponseButton button : buttonList) {
			if (TopicsUtils.checkSpecialCharacters(button.getText())) {
				throw new VUserException("The button cannot contain the following characters : '[', ']', '|', '¤'. ");
			}
		}
		responsesButtonServices.removeAllButtonsBySmtId(chatbot, savedST);
		responsesButtonServices.saveAllButtonsBySmtId(chatbot, savedST, buttonList);
		return savedST;
	}

	@Override
	public void delete(@SecuredOperation("botContributor") final Chatbot chatbot, final SmallTalk smallTalk, final Topic topic) {

		utterTextServices.deleteUtterTextsBySmallTalk(chatbot, smallTalk);

		responsesButtonServices.deleteResponsesButtonsBySmallTalk(chatbot, smallTalk);

		// delete smallTalk
		delete(smallTalk);
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

	@Override
	public void initializeBasic(final Chatbot chatbot, final Topic topic, final String text) {
		final SmallTalk smt = new SmallTalk();
		smt.setTopId(topic.getTopId());
		smt.setRtyId(ResponseTypeEnum.RICH_TEXT.name());
		smt.setIsEnd(false);
		final UtterText utt = new UtterText();
		utt.setText(text);
		final DtList<UtterText> utterTexts = new DtList<UtterText>(UtterText.class);
		utterTexts.add(utt);

		saveSmallTalk(chatbot, smt, utterTexts,
				new DtList<>(ResponseButton.class), topic);
	}

	@Override
	public void delete(final SmallTalk smallTalk) {
		if (smallTalk != null) {
			smallTalkDAO.delete(smallTalk.getUID());
		}
	}

	@Override
	public boolean handleObject(final Topic topic) {
		return TypeTopicEnum.SMALLTALK.name().equals(topic.getTtoCd());
	}

	@Override
	public SmallTalk findByTopId(final Long topId) {
		if (topId != null) {
			final Optional<SmallTalk> result = smallTalkDAO.findOptional(Criterions.isEqualTo(SmallTalkFields.topId, topId));
			return result.isPresent() ? result.get() : null;
		}
		return null;
	}

	@Override
	public boolean isEnabled(final SmallTalk object, final boolean isEnabled, final Chatbot bot) {
		return !(hasToBeDeactivated(object, bot)) && isEnabled;
	}

	@Override
	public UtterText getBasicUtterTextByTopId(final Long topId) {
		return utterTextServices.getBasicUtterTextByTopId(topId);

	}

	@Override
	public boolean hasToBeDeactivated(final SmallTalk object, final Chatbot bot) {
		final DtList<UtterText> utt = utterTextServices.getUtterTextList(bot, object);
		final DtList<ResponseButton> buttonList = responsesButtonServices.getResponsesButtonList(bot, object);
		return utt.isEmpty() && buttonList.isEmpty();
	}

	@Override
	public String getDeactivateMessage() {
		return MessageText.of(TopicsMultilingualResources.DEACTIVATE_TOPIC_SMALL_TALK).getDisplay();
	}

	@Override
	public SmallTalk saveFromSaveTopicObject(final SaveTopicObject<SmallTalk> saveObject) {
		return saveSmallTalk(saveObject.getBot(), saveObject.getObject(), saveObject.getUtters(), saveObject.getButtons(), saveObject.getTopic());
	}
}
