package io.vertigo.chatbot.designer.builder.services.topic;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.topic.SmallTalkDAO;
import io.vertigo.chatbot.commons.dao.topic.TopicDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.KindTopicEnum;
import io.vertigo.chatbot.commons.domain.topic.NluTrainingSentence;
import io.vertigo.chatbot.commons.domain.topic.ResponseButton;
import io.vertigo.chatbot.commons.domain.topic.ResponseButtonUrl;
import io.vertigo.chatbot.commons.domain.topic.ResponseTypeEnum;
import io.vertigo.chatbot.commons.domain.topic.SmallTalk;
import io.vertigo.chatbot.commons.domain.topic.SmallTalkIhm;
import io.vertigo.chatbot.commons.domain.topic.SmallTalkWrapper;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TypeTopicEnum;
import io.vertigo.chatbot.commons.domain.topic.UtterText;
import io.vertigo.chatbot.commons.multilingual.topics.TopicsMultilingualResources;
import io.vertigo.chatbot.designer.builder.services.HistoryServices;
import io.vertigo.chatbot.designer.builder.services.IRecordable;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.designer.builder.services.ResponsesButtonServices;
import io.vertigo.chatbot.designer.builder.services.ResponsesButtonUrlServices;
import io.vertigo.chatbot.designer.builder.services.UtterTextServices;
import io.vertigo.chatbot.designer.builder.smallTalk.SmallTalkPAO;
import io.vertigo.chatbot.designer.domain.History;
import io.vertigo.chatbot.designer.domain.HistoryActionEnum;
import io.vertigo.chatbot.designer.domain.commons.BotPredefinedTopic;
import io.vertigo.chatbot.designer.utils.HashUtils;
import io.vertigo.chatbot.domain.DtDefinitions.SmallTalkFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.locale.MessageText;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtObject;

import javax.inject.Inject;
import java.util.Optional;

@Transactional
@Secured("BotUser")
public class SmallTalkServices implements Component, ITopicService<SmallTalk>, IRecordable<SmallTalk> {

	@Inject
	private UtterTextServices utterTextServices;

	@Inject
	private ResponsesButtonServices responsesButtonServices;

	@Inject
	private ResponsesButtonUrlServices responsesButtonUrlServices;

	@Inject
	private SmallTalkDAO smallTalkDAO;

	@Inject
	private SmallTalkPAO smallTalkPAO;

	@Inject
	private NodeServices nodeServices;

	@Inject
	private TopicDAO topicDAO;

	@Inject
	private HistoryServices historyServices;

	public SmallTalk getSmallTalkById(@SecuredOperation("botVisitor") final Chatbot bot, final Long smtId) {
		Assertion.check().isNotNull(smtId);
		// ---
		return smallTalkDAO.get(smtId);
	}

	public SmallTalk getNewSmallTalk(@SecuredOperation("botContributor") final Chatbot bot) {
		final SmallTalk smallTalk = new SmallTalk();
		smallTalk.responseType().setEnumValue(ResponseTypeEnum.RICH_TEXT);
		return smallTalk;
	}

	@Override
	public SmallTalk save(final SmallTalk smt) {
		return smallTalkDAO.save(smt);
	}

	public SmallTalk saveSmallTalk(@SecuredOperation("botContributor") final Chatbot chatbot, final SmallTalk smallTalk,

								   final DtList<UtterText> utterTexts, final DtList<ResponseButton> buttonList, final DtList<ResponseButtonUrl> buttonUrlList, final Topic topic) {

		Assertion.check()
				.isNotNull(smallTalk).isNotNull(topic)
				.isNotNull(utterTexts)
				.isNotNull(buttonList);
		// ---
		boolean isNew = smallTalk.getSmtId() == null;
		boolean updated = isNew;
		smallTalk.setTopId(topic.getTopId());
		final SmallTalk savedST = save(smallTalk);

		// save utter textes, remove all + create all
		DtList<UtterText> oldUtterText = utterTextServices.getUtterTextList(chatbot, smallTalk);
		if (!HashUtils.generateHashCodeForUtterTexts(oldUtterText).equals(HashUtils.generateHashCodeForUtterTexts(utterTexts))) {
			utterTextServices.removeAllUtterTextBySmtId(chatbot, savedST.getSmtId());
			utterTextServices.createNoBlankUtterTextBySmallTalk(chatbot, savedST, utterTexts);
			nodeServices.updateNodes(chatbot);
			updated = true;
		}

		// remove and create buttons
		DtList<ResponseButton> oldResponseButtons = responsesButtonServices.getResponsesButtonList(chatbot, smallTalk);
		if (!HashUtils.generateHashCodeForResponseButtons(oldResponseButtons).equals(HashUtils.generateHashCodeForResponseButtons(buttonList))) {
			for (final ResponseButton button : buttonList) {
				if (TopicsUtils.checkSpecialCharacters(button.getText())) {
					throw new VUserException("The button cannot contain the following characters : '[', ']', '|', '¤'. ");
				}
			}
			responsesButtonServices.removeAllButtonsBySmtId(chatbot, savedST);
			responsesButtonServices.saveAllButtonsBySmtId(chatbot, savedST, buttonList);
			nodeServices.updateNodes(chatbot);
			updated = true;
		}

		DtList<ResponseButtonUrl> oldResponseUrlButtons = responsesButtonUrlServices.getResponsesButtonUrlList(chatbot, smallTalk);
		if (!HashUtils.generateHashCodeForResponseButtonsUrl(oldResponseUrlButtons).equals(HashUtils.generateHashCodeForResponseButtonsUrl(buttonUrlList))) {
			for (final ResponseButtonUrl button : buttonUrlList) {
				if (TopicsUtils.checkSpecialCharacters(button.getText())) {
					throw new VUserException("The button cannot contain the following characters : '[', ']', '|', '¤'. ");
				}
			}
			responsesButtonUrlServices.removeAllButtonsUrlBySmtId(chatbot, savedST);
			responsesButtonUrlServices.saveAllButtonsUrlBySmtId(chatbot, savedST, buttonUrlList);
			nodeServices.updateNodes(chatbot);
			updated = true;
		}

		if (updated) {
			record(chatbot, smallTalk, isNew ? HistoryActionEnum.ADDED : HistoryActionEnum.UPDATED);
		}

		return savedST;
	}

	@Override
	public void deleteIfExists(@SecuredOperation("botContributor") final Chatbot chatbot, final Topic topic) {
		findByTopId(topic.getTopId())
				.ifPresent(smallTalk -> {
					utterTextServices.deleteUtterTextsBySmallTalk(chatbot, smallTalk);

					responsesButtonServices.deleteResponsesButtonsBySmallTalk(chatbot, smallTalk);

					// delete smallTalk
					delete(smallTalk);
					record(chatbot, smallTalk, HistoryActionEnum.DELETED);
				});
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
	public void createOrUpdateFromTopic(final Chatbot chatbot, final Topic topic, final String text) {
		final SmallTalk smt = findByTopId(topic.getTopId()).orElse(new SmallTalk());
		smt.setTopId(topic.getTopId());
		smt.setRtyId(ResponseTypeEnum.RICH_TEXT.name());
		smt.setIsEnd(false);
		final UtterText utt = new UtterText();
		utt.setText(text);
		final DtList<UtterText> utterTexts = new DtList<>(UtterText.class);
		utterTexts.add(utt);

		saveSmallTalk(chatbot, smt, utterTexts,
				new DtList<>(ResponseButton.class), new DtList<>(ResponseButtonUrl.class), topic);
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
	public Optional<SmallTalk> findByTopId(final Long topId) {
		Assertion.check().isNotNull(topId);

		return smallTalkDAO.findOptional(Criterions.isEqualTo(SmallTalkFields.topId, topId));
	}

	public UtterText getBasicUtterTextByTopId(final Long topId) {
		return utterTextServices.getBasicUtterTextByTopId(topId);
	}

	@Override
	public BotPredefinedTopic getBotPredefinedTopicByTopId(final Long topId) {
		Assertion.check()
				.isNotNull(topId);
		// ---
		final Topic topic = topicDAO.get(topId);
		final UtterText utter = getBasicUtterTextByTopId(topId);

		final BotPredefinedTopic predefinedTopic = new BotPredefinedTopic();
		predefinedTopic.setTopId(topId);
		predefinedTopic.setTtoCd(topic.getTtoCd());
		predefinedTopic.setValue(utter.getText());
		return predefinedTopic;
	}

	@Override
	public boolean hasToBeDeactivated(final Topic topic, final DtList<NluTrainingSentence> sentences, final DtObject object, final Chatbot bot) {
		SmallTalkWrapper smallTalkWrapper = (SmallTalkWrapper) object;
		final DtList<UtterText> utt = utterTextServices.getUtterTextList(bot, smallTalkWrapper.getSmallTalk());
		final DtList<ResponseButton> buttonList = responsesButtonServices.getResponsesButtonList(bot, smallTalkWrapper.getSmallTalk());
		return (!KindTopicEnum.UNREACHABLE.name().equals(topic.getKtoCd())  && sentences.isEmpty()) || (utt.isEmpty() && buttonList.isEmpty());
	}

	@Override
	public String getDeactivateMessage() {
		return MessageText.of(TopicsMultilingualResources.DEACTIVATE_TOPIC_SMALL_TALK).getDisplay();
	}

	@Override
	public void saveTopic(Topic topic, Chatbot chatbot, DtObject dtObject) {
		SmallTalkWrapper smallTalkWrapper = (SmallTalkWrapper) dtObject;
		saveSmallTalk(chatbot, smallTalkWrapper.getSmallTalk(), smallTalkWrapper.getUtterTexts(), smallTalkWrapper.getButtons(), smallTalkWrapper.getButtonsUrl(), topic);
	}

	@Override
	public History record(Chatbot bot, SmallTalk smallTalk, HistoryActionEnum action) {
		smallTalk.topic().load();
		Topic topic = smallTalk.topic().get();
		String message = topic.getKtoCd() + " - " + topic.getTitle();
		return historyServices.record(bot, action, smallTalk.getClass().getSimpleName(), message);
	}
}
