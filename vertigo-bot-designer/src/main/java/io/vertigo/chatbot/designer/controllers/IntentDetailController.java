package io.vertigo.chatbot.designer.controllers;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.vertigo.chatbot.commons.domain.Intent;
import io.vertigo.chatbot.commons.domain.IntentTrainingSentence;
import io.vertigo.chatbot.commons.domain.UtterText;
import io.vertigo.chatbot.designer.services.ChatbotServices;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;

@Controller
@RequestMapping("/intent")
public class IntentDetailController extends AbstractVSpringMvcController {

	private static final ViewContextKey<Intent> intentKey = ViewContextKey.of("intent");
	
	private static final ViewContextKey<String> newIntentTrainingSentenceKey = ViewContextKey.of("newIntentTrainingSentence");
	private static final ViewContextKey<IntentTrainingSentence> intentTrainingSentencesKey = ViewContextKey.of("intentTrainingSentences");
	private static final ViewContextKey<IntentTrainingSentence> intentTrainingSentencesToDeleteKey = ViewContextKey.of("intentTrainingSentencesToDelete");
	
	private static final ViewContextKey<String> newUtterTextKey = ViewContextKey.of("newUtterText");
	private static final ViewContextKey<UtterText> utterTextsKey = ViewContextKey.of("utterTexts");
	private static final ViewContextKey<UtterText> utterTextsToDeleteKey = ViewContextKey.of("utterTextsToDelete");

	@Inject
	private ChatbotServices chatbotServices;

	@GetMapping("/{intId}")
	public void initContext(final ViewContext viewContext, @PathVariable("intId") final Long intId) {
		final Intent intent = chatbotServices.getIntentById(intId);

		viewContext.publishDto(intentKey, intent);
		
		viewContext.publishRef(newIntentTrainingSentenceKey, "");
		viewContext.publishDtList(intentTrainingSentencesKey, chatbotServices.getIntentTrainingSentenceList(intent));
		viewContext.publishDtList(intentTrainingSentencesToDeleteKey, new DtList<IntentTrainingSentence>(IntentTrainingSentence.class));
		
		viewContext.publishRef(newUtterTextKey, "");
		viewContext.publishDtList(utterTextsKey, chatbotServices.getIntentUtterTextList(intent));
		viewContext.publishDtList(utterTextsToDeleteKey, new DtList<UtterText>(UtterText.class));

		toModeReadOnly();
	}

	@GetMapping("/new")
	public void initContext(final ViewContext viewContext) {
		viewContext.publishDto(intentKey, chatbotServices.getNewIntent());
		
		viewContext.publishRef(newIntentTrainingSentenceKey, "");
		viewContext.publishDtList(intentTrainingSentencesKey, new DtList<IntentTrainingSentence>(IntentTrainingSentence.class));
		viewContext.publishDtList(intentTrainingSentencesToDeleteKey, new DtList<IntentTrainingSentence>(IntentTrainingSentence.class));
		
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
	public String doSave(@ViewAttribute("intent") final Intent intent,
			@ViewAttribute("intentTrainingSentences") final DtList<IntentTrainingSentence> intentTrainingSentences,
			@ViewAttribute("intentTrainingSentencesToDelete") final DtList<IntentTrainingSentence> intentTrainingSentencesToDelete,
			@ViewAttribute("utterTexts") final DtList<UtterText> utterTexts,
			@ViewAttribute("utterTextsToDelete") final DtList<UtterText> utterTextsToDelete
			) {

		chatbotServices.save(intent, intentTrainingSentences, intentTrainingSentencesToDelete, utterTexts, utterTextsToDelete);
		return "redirect:/intent/" + intent.getIntId();
	}

	@PostMapping("/_addTrainingSentence")
	public ViewContext doAddTrainingSentence(final ViewContext viewContext,
			@ViewAttribute("newIntentTrainingSentence") final String newIntentTrainingSentence,
			@ViewAttribute("intent") final Intent intent,
			@ViewAttribute("intentTrainingSentences") final DtList<IntentTrainingSentence> intentTrainingSentences
			) {

		IntentTrainingSentence newText = new IntentTrainingSentence();
		newText.setText(newIntentTrainingSentence);

		intentTrainingSentences.add(newText);
		viewContext.publishDtList(intentTrainingSentencesKey, intentTrainingSentences);

		viewContext.publishRef(newIntentTrainingSentenceKey, "");

		return viewContext;
	}

	@PostMapping("/_removeTrainingSentence")
	public ViewContext doRemoveTrainingSentence(final ViewContext viewContext,
			@RequestParam("index") final int index,
			@ViewAttribute("intentTrainingSentencesToDelete") final DtList<IntentTrainingSentence> intentTrainingSentencesToDelete,
			@ViewAttribute("intentTrainingSentences") final DtList<IntentTrainingSentence> intentTrainingSentences
			) {
		
		// remove from list
		IntentTrainingSentence removed = intentTrainingSentences.remove(index);
		viewContext.publishDtList(intentTrainingSentencesKey, intentTrainingSentences);
	
		// keep track of deleted persisted IntentTrainingSentence
		if (removed.getItsId() != null) {
			intentTrainingSentencesToDelete.add(removed);
		}
		viewContext.publishDtList(intentTrainingSentencesToDeleteKey, intentTrainingSentencesToDelete);
	
		return viewContext;
	}
	
	@PostMapping("/_addUtterText")
	public ViewContext doAddUtterText(final ViewContext viewContext,
			@ViewAttribute("newUtterText") final String newUtterText,
			@ViewAttribute("intent") final Intent intent,
			@ViewAttribute("utterTexts") final DtList<UtterText> utterTexts
			) {

		UtterText newText = new UtterText();
		newText.setText(newUtterText);

		utterTexts.add(newText);
		viewContext.publishDtList(utterTextsKey, utterTexts);

		viewContext.publishRef(newUtterTextKey, "");

		return viewContext;
	}

	@PostMapping("/_removeUtterText")
	public ViewContext doRemoveUtterText(final ViewContext viewContext,
			@RequestParam("index") final int index,
			@ViewAttribute("utterTextsToDelete") final DtList<UtterText> utterTextsToDelete,
			@ViewAttribute("utterTexts") final DtList<UtterText> utterTexts
			) {
		
		// remove from list
		UtterText removed = utterTexts.remove(index);
		viewContext.publishDtList(utterTextsKey, utterTexts);
	
		// keep track of deleted persisted UtterText
		if (removed.getUtxId() != null) {
			utterTextsToDelete.add(removed);
		}
		viewContext.publishDtList(utterTextsToDeleteKey, utterTextsToDelete);
	
		return viewContext;
	}
}