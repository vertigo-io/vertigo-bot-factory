package io.vertigo.chatbot.designer.builder.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.dao.NluTrainingSentenceDAO;
import io.vertigo.chatbot.commons.dao.SmallTalkDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.NluTrainingSentence;
import io.vertigo.chatbot.commons.domain.ResponseButton;
import io.vertigo.chatbot.commons.domain.ResponseTypeEnum;
import io.vertigo.chatbot.commons.domain.SmallTalk;
import io.vertigo.chatbot.commons.domain.SmallTalkExport;
import io.vertigo.chatbot.commons.domain.UtterText;
import io.vertigo.chatbot.designer.builder.smallTalk.SmallTalkPAO;
import io.vertigo.chatbot.domain.DtDefinitions.NluTrainingSentenceFields;
import io.vertigo.chatbot.domain.DtDefinitions.SmallTalkFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.core.util.StringUtil;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.util.VCollectors;

@Transactional
@Secured("BotUser")
public class SmallTalkServices implements Component {

	@Inject
	private UtterTextServices utterTextServices;

	@Inject
	private ResponsesButtonServices responsesButtonServices;

	@Inject
	private SmallTalkDAO smallTalkDAO;

	@Inject
	private NluTrainingSentenceDAO nluTrainingSentenceDAO;

	@Inject
	private SmallTalkPAO smallTalkPAO;

	public SmallTalk getSmallTalkById(@SecuredOperation("botVisitor") final Chatbot bot, final Long movId) {
		Assertion.check().isNotNull(movId);
		// ---
		return smallTalkDAO.get(movId);
	}

	public DtList<SmallTalk> getAllSmallTalksByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
		return smallTalkDAO.findAll(Criterions.isEqualTo(SmallTalkFields.botId, bot.getBotId()), DtListState.of(1000));
	}

	public DtList<SmallTalk> getAllActiveSmallTalksByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
		return smallTalkDAO.findAll(Criterions.isEqualTo(SmallTalkFields.botId, bot.getBotId()).and(Criterions.isEqualTo(SmallTalkFields.isEnabled, true)), DtListState.of(1000));
	}

	public SmallTalk getNewSmallTalk(@SecuredOperation("botAdm") final Chatbot bot) {
		final SmallTalk smallTalk = new SmallTalk();
		smallTalk.setBotId(bot.getBotId());
		smallTalk.setIsEnabled(true);
		smallTalk.responseType().setEnumValue(ResponseTypeEnum.RICH_TEXT);
		return smallTalk;
	}

	public SmallTalk saveSmallTalk(@SecuredOperation("botContributor") final Chatbot chatbot, final SmallTalk smallTalk,
			final DtList<NluTrainingSentence> nluTrainingSentences, final DtList<NluTrainingSentence> nluTrainingSentencesToDelete,
			final DtList<UtterText> utterTexts, final DtList<ResponseButton> buttonList) {

		Assertion.check()
				.isNotNull(smallTalk)
				.isNotNull(nluTrainingSentences)
				.isNotNull(nluTrainingSentencesToDelete)
				.isNotNull(utterTexts)
				.isNotNull(buttonList);
		// ---

		SmallTalk savedST = smallTalkDAO.save(smallTalk);

		// save and remove NTS
		final DtList<NluTrainingSentence> ntsToSave = saveAllNotBlankNTS(savedST, nluTrainingSentences);
		removeNTS(nluTrainingSentencesToDelete);

		// save utter textes, remove all + create all
		utterTextServices.removeAllUtterTextBySmtId(chatbot, savedST.getSmtId());
		final DtList<UtterText> uttToSave = utterTextServices.createNoBlankUtterTextBySmallTalk(chatbot, savedST, utterTexts);

		if (ntsToSave.isEmpty() || uttToSave.isEmpty()) {
			// no training or response, disable this small talk
			savedST.setIsEnabled(false);
			savedST = smallTalkDAO.save(savedST);
		}

		// remove and create buttons
		responsesButtonServices.removeAllButtonsBySmtId(chatbot, savedST);
		responsesButtonServices.saveAllButtonsBySmtId(chatbot, savedST, buttonList);
		return savedST;
	}

	public void deleteSmallTalk(@SecuredOperation("botContributor") final Chatbot chatbot, final SmallTalk smallTalk) {
		// delete sub elements
		for (final NluTrainingSentence its : getNluTrainingSentenceList(chatbot, smallTalk)) {
			nluTrainingSentenceDAO.delete(its.getUID());
		}

		utterTextServices.deleteUtterTextsBySmallTalk(chatbot, smallTalk);

		responsesButtonServices.deleteResponsesButtonsBySmallTalk(chatbot, smallTalk);

		// delete smallTalk
		smallTalkDAO.delete(smallTalk.getUID());
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

	//********* NTS part ********/

	public DtList<NluTrainingSentence> getNluTrainingSentenceList(@SecuredOperation("botVisitor") final Chatbot bot, final SmallTalk smallTalk) {
		Assertion.check()
				.isNotNull(smallTalk)
				.isNotNull(smallTalk.getSmtId());
		// ---

		return nluTrainingSentenceDAO.findAll(
				Criterions.isEqualTo(NluTrainingSentenceFields.smtId, smallTalk.getSmtId()),
				DtListState.of(1000, 0, NluTrainingSentenceFields.ntsId.name(), false));
	}

	public Map<Long, DtList<NluTrainingSentence>> exportSmallTalkRelativeTrainingSentence(@SecuredOperation("botContributor") final Chatbot bot, final List<Long> smallTalkIds) {
		return nluTrainingSentenceDAO.exportSmallTalkRelativeTrainingSentence(smallTalkIds)
				.stream()
				.collect(Collectors.groupingBy(NluTrainingSentence::getSmtId,
						VCollectors.toDtList(NluTrainingSentence.class)));

	}

	private void removeNTS(final DtList<NluTrainingSentence> nluTrainingSentencesToDelete) {
		nluTrainingSentencesToDelete.stream()
				.filter(itt -> itt.getNtsId() != null)
				.forEach(itt -> nluTrainingSentenceDAO.delete(itt.getNtsId()));
	}

	private DtList<NluTrainingSentence> saveAllNotBlankNTS(final SmallTalk smt, final DtList<NluTrainingSentence> nluTrainingSentences) {
		// save nlu textes
		final DtList<NluTrainingSentence> ntsToSave = nluTrainingSentences.stream()
				.filter(nts -> !StringUtil.isBlank(nts.getText()))
				.collect(VCollectors.toDtList(NluTrainingSentence.class));

		for (final NluTrainingSentence nts : ntsToSave) {
			nts.setSmtId(smt.getSmtId());
			nluTrainingSentenceDAO.save(nts);
		}

		return ntsToSave;
	}

	public void removeAllNTSFromBot(final Chatbot bot) {
		smallTalkPAO.removeAllNluTrainingSentenceByBotId(bot.getBotId());
	}

}
