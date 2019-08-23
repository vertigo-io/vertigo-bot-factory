package io.vertigo.chatbot.designer.controllers;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.vertigo.chatbot.designer.domain.Intent;
import io.vertigo.chatbot.designer.domain.IntentText;
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
	private static final ViewContextKey<String> newIntentTextKey = ViewContextKey.of("newIntentText");
	private static final ViewContextKey<IntentText> intentTextsKey = ViewContextKey.of("intentTexts");
	private static final ViewContextKey<IntentText> intentTextsToDeleteKey = ViewContextKey.of("intentTextsToDelete");

	@Inject
	private ChatbotServices chatbotServices;

	@GetMapping("/{intId}")
	public void initContext(final ViewContext viewContext, @PathVariable("intId") final Long intId) {
		final Intent intent = chatbotServices.getIntentById(intId);

		viewContext.publishDto(intentKey, intent);
		viewContext.publishRef(newIntentTextKey, "");
		viewContext.publishDtList(intentTextsKey, chatbotServices.getIntentTextList(intent));
		viewContext.publishDtList(intentTextsToDeleteKey, new DtList<IntentText>(IntentText.class));

		toModeReadOnly();
	}

	@GetMapping("/new")
	public void initContext(final ViewContext viewContext) {
		viewContext.publishDto(intentKey, new Intent());
		viewContext.publishRef(newIntentTextKey, "");
		viewContext.publishDtList(intentTextsKey, new DtList<IntentText>(IntentText.class));
		viewContext.publishDtList(intentTextsToDeleteKey, new DtList<IntentText>(IntentText.class));
		
		toModeCreate();
	}

	@PostMapping("/_edit")
	public void doEdit() {
		toModeEdit();
	}

	@PostMapping("/_save")
	public String doSave(@ViewAttribute("intent") final Intent intent,
			@ViewAttribute("intentTexts") final DtList<IntentText> intentTexts,
			@ViewAttribute("intentTextsToDelete") final DtList<IntentText> intentTextsToDelete
			) {

		chatbotServices.save(intent, intentTexts, intentTextsToDelete);
		return "redirect:/intent/" + intent.getIntId();
	}

	@PostMapping("/_removeText")
	public ViewContext doRemoveText(final ViewContext viewContext,
			@RequestParam("index") final int index,
			@ViewAttribute("intentTextsToDelete") final DtList<IntentText> intentTextsToDelete,
			@ViewAttribute("intentTexts") final DtList<IntentText> intentTexts
			) {
		
		// remove from UI list
		IntentText removed = intentTexts.remove(index);
		viewContext.publishDtList(intentTextsKey, intentTexts);

		// keep track of deleted persisted intentText
		if (removed.getIttId() != null) {
			intentTextsToDelete.add(removed);
		}
		viewContext.publishDtList(intentTextsToDeleteKey, intentTextsToDelete);

		return viewContext;
	}

	@PostMapping("/_addText")
	public ViewContext doAddText(final ViewContext viewContext,
			@ViewAttribute("newIntentText") final String newIntentText,
			@ViewAttribute("intent") final Intent intent,
			@ViewAttribute("intentTexts") final DtList<IntentText> intentTexts
			) {

		IntentText newText = new IntentText();
		newText.setText(newIntentText);

		intentTexts.add(newText);
		viewContext.publishDtList(intentTextsKey, intentTexts);

		viewContext.publishRef(newIntentTextKey, "");

		return viewContext;
	}
}