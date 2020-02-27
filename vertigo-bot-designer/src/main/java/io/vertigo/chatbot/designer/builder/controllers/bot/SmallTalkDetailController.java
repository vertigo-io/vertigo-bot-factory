package io.vertigo.chatbot.designer.builder.controllers.bot;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.vertigo.chatbot.commons.domain.NluTrainingSentence;
import io.vertigo.chatbot.commons.domain.SmallTalk;
import io.vertigo.chatbot.commons.domain.UtterText;
import io.vertigo.chatbot.designer.builder.services.DesignerServices;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.lang.Assertion;
import io.vertigo.lang.VUserException;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@RequestMapping("/bot/{botId}/smallTalk")
public class SmallTalkDetailController extends AbstractVSpringMvcController {

	private static final ViewContextKey<SmallTalk> smallTalkKey = ViewContextKey.of("smallTalk");

	private static final ViewContextKey<String> newNluTrainingSentenceKey = ViewContextKey.of("newNluTrainingSentence");
	private static final ViewContextKey<NluTrainingSentence> nluTrainingSentencesKey = ViewContextKey.of("nluTrainingSentences");
	private static final ViewContextKey<NluTrainingSentence> nluTrainingSentencesToDeleteKey = ViewContextKey.of("nluTrainingSentencesToDelete");

	private static final ViewContextKey<String> newUtterTextKey = ViewContextKey.of("newUtterText");
	private static final ViewContextKey<UtterText> utterTextsKey = ViewContextKey.of("utterTexts");
	private static final ViewContextKey<UtterText> utterTextsToDeleteKey = ViewContextKey.of("utterTextsToDelete");

	@Inject
	private DesignerServices designerServices;

	@Inject
	private CommonBotDetailController commonBotDetailController;

	@GetMapping("/{intId}")
	public void initContext(final ViewContext viewContext, @PathVariable("botId") final Long botId, @PathVariable("intId") final Long intId) {
		commonBotDetailController.initCommonContext(viewContext, botId);

		final SmallTalk smallTalk = designerServices.getSmallTalkById(intId);

		Assertion.checkState(smallTalk.getBotId().equals(botId), "Paramètres incohérents");

		viewContext.publishDto(smallTalkKey, smallTalk);

		viewContext.publishRef(newNluTrainingSentenceKey, "");
		viewContext.publishDtListModifiable(nluTrainingSentencesKey, designerServices.getNluTrainingSentenceList(smallTalk));
		viewContext.publishDtList(nluTrainingSentencesToDeleteKey, new DtList<NluTrainingSentence>(NluTrainingSentence.class));

		viewContext.publishRef(newUtterTextKey, "");
		viewContext.publishDtList(utterTextsKey, designerServices.getUtterTextList(smallTalk));
		viewContext.publishDtList(utterTextsToDeleteKey, new DtList<UtterText>(UtterText.class));

		toModeReadOnly();
	}

	@GetMapping("/new")
	public void initContext(final ViewContext viewContext, @PathVariable("botId") final Long botId) {
		commonBotDetailController.initCommonContext(viewContext, botId);

		viewContext.publishDto(smallTalkKey, designerServices.getNewSmallTalk(botId));

		viewContext.publishRef(newNluTrainingSentenceKey, "");
		viewContext.publishDtListModifiable(nluTrainingSentencesKey, new DtList<NluTrainingSentence>(NluTrainingSentence.class));
		viewContext.publishDtList(nluTrainingSentencesToDeleteKey, new DtList<NluTrainingSentence>(NluTrainingSentence.class));

		viewContext.publishRef(newUtterTextKey, "");
		viewContext.publishDtList(utterTextsKey, new DtList<UtterText>(UtterText.class));
		viewContext.publishDtList(utterTextsToDeleteKey, new DtList<UtterText>(UtterText.class));

		toModeCreate();
	}

	@PostMapping("/_edit")
	public void doEdit() {
		toModeEdit();
	}

	@PostMapping("/_save")
	public String doSave(@ViewAttribute("smallTalk") final SmallTalk smallTalk,
			@PathVariable("botId") final Long botId,
			@ViewAttribute("nluTrainingSentences") final DtList<NluTrainingSentence> nluTrainingSentences,
			@ViewAttribute("nluTrainingSentencesToDelete") final DtList<NluTrainingSentence> nluTrainingSentencesToDelete,
			@ViewAttribute("utterTexts") final DtList<UtterText> utterTexts,
			@ViewAttribute("utterTextsToDelete") final DtList<UtterText> utterTextsToDelete) {

		designerServices.saveSmallTalk(smallTalk, nluTrainingSentences, nluTrainingSentencesToDelete, utterTexts, utterTextsToDelete);
		return "redirect:/bot/" + botId + "/smallTalk/" + smallTalk.getSmtId();
	}

	@PostMapping("/_delete")
	public String doDelete(@ViewAttribute("smallTalk") final SmallTalk smallTalk) {
		designerServices.deleteSmallTalk(smallTalk);
		return "redirect:/bot/" + smallTalk.getBotId() + "/smallTalks/";
	}

	@PostMapping("/_addTrainingSentence")
	public ViewContext doAddTrainingSentence(final ViewContext viewContext,
			@ViewAttribute("newNluTrainingSentence") final String newNluTrainingSentenceIn,
			@ViewAttribute("nluTrainingSentences") final DtList<NluTrainingSentence> nluTrainingSentences) {

		final String newNluTrainingSentence = newNluTrainingSentenceIn.trim();

		final boolean exists = nluTrainingSentences.stream()
				.anyMatch(its -> its.getText().equalsIgnoreCase(newNluTrainingSentence));
		if (exists) {
			throw new VUserException("This sentense already exists");
		}

		final NluTrainingSentence newText = new NluTrainingSentence();
		newText.setText(newNluTrainingSentence);

		nluTrainingSentences.add(newText);
		viewContext.publishDtListModifiable(nluTrainingSentencesKey, nluTrainingSentences);

		viewContext.publishRef(newNluTrainingSentenceKey, "");

		return viewContext;
	}

	@PostMapping("/_editTrainingSentence")
	public ViewContext doEditTrainingSentence(final ViewContext viewContext,
			@RequestParam("index") final int index,
			@ViewAttribute("newNluTrainingSentence") final String newNluTrainingSentence,
			@ViewAttribute("nluTrainingSentences") final DtList<NluTrainingSentence> nluTrainingSentences) {

		int curIdx = 0;
		for (final NluTrainingSentence nts : nluTrainingSentences) {
			if (curIdx == index) {
				nts.setText(newNluTrainingSentence);
			} else if (newNluTrainingSentence.equalsIgnoreCase(nts.getText())) {
				throw new VUserException("This sentense already exists");
			}
			curIdx++;
		}

		viewContext.publishDtListModifiable(nluTrainingSentencesKey, nluTrainingSentences);

		return viewContext;
	}

	@PostMapping("/_removeTrainingSentence")
	public ViewContext doRemoveTrainingSentence(final ViewContext viewContext,
			@RequestParam("index") final int index,
			@ViewAttribute("nluTrainingSentencesToDelete") final DtList<NluTrainingSentence> nluTrainingSentencesToDelete,
			@ViewAttribute("nluTrainingSentences") final DtList<NluTrainingSentence> nluTrainingSentences) {

		// remove from list
		final NluTrainingSentence removed = nluTrainingSentences.remove(index);
		viewContext.publishDtListModifiable(nluTrainingSentencesKey, nluTrainingSentences);

		// keep track of deleted persisted NluTrainingSentence
		if (removed.getNtsId() != null) {
			nluTrainingSentencesToDelete.add(removed);
		}
		viewContext.publishDtList(nluTrainingSentencesToDeleteKey, nluTrainingSentencesToDelete);

		return viewContext;
	}

	@PostMapping("/_addUtterText")
	public ViewContext doAddUtterText(final ViewContext viewContext,
			@ViewAttribute("newUtterText") final String newUtterTextIn,
			@ViewAttribute("utterTexts") final DtList<UtterText> utterTexts) {

		final String newUtterText = newUtterTextIn.trim();

		final boolean exists = utterTexts.stream()
				.anyMatch(ut -> ut.getText().equalsIgnoreCase(newUtterText));

		if (exists) {
			throw new VUserException("This sentense already exists");
		}

		final UtterText newText = new UtterText();
		newText.setText(newUtterText);

		utterTexts.add(newText);
		viewContext.publishDtList(utterTextsKey, utterTexts);

		viewContext.publishRef(newUtterTextKey, "");

		return viewContext;
	}

	@PostMapping("/_editUtterText")
	public ViewContext doEditUtterText(final ViewContext viewContext,
			@RequestParam("index") final int index,
			@ViewAttribute("newUtterText") final String newUtterText,
			@ViewAttribute("utterTexts") final DtList<UtterText> utterTexts) {

		int curIdx = 0;
		for (final UtterText utt : utterTexts) {
			if (curIdx == index) {
				utt.setText(newUtterText);
			} else if (newUtterText.equalsIgnoreCase(utt.getText())) {
				throw new VUserException("This sentense already exists");
			}
			curIdx++;
		}

		viewContext.publishDtListModifiable(utterTextsKey, utterTexts);

		return viewContext;
	}

	@PostMapping("/_removeUtterText")
	public ViewContext doRemoveUtterText(final ViewContext viewContext,
			@RequestParam("index") final int index,
			@ViewAttribute("utterTextsToDelete") final DtList<UtterText> utterTextsToDelete,
			@ViewAttribute("utterTexts") final DtList<UtterText> utterTexts) {

		// remove from list
		final UtterText removed = utterTexts.remove(index);
		viewContext.publishDtList(utterTextsKey, utterTexts);

		// keep track of deleted persisted UtterText
		if (removed.getUttId() != null) {
			utterTextsToDelete.add(removed);
		}
		viewContext.publishDtList(utterTextsToDeleteKey, utterTextsToDelete);

		return viewContext;
	}
}
