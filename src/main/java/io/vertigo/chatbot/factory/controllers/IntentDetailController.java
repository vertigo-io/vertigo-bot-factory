package io.vertigo.chatbot.factory.controllers;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.vertigo.chatbot.factory.domain.Intent;
import io.vertigo.chatbot.factory.domain.IntentText;
import io.vertigo.chatbot.factory.services.ChatbotServices;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.ui.core.BasicUiListModifiable;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;
import io.vertigo.vega.webservice.model.UiObject;

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
		viewContext.publishDtListModifiable(intentTextsKey, chatbotServices.getIntentTextList(intent));
		viewContext.publishDtListModifiable(intentTextsToDeleteKey, new DtList<IntentText>(IntentText.class));

		toModeReadOnly();
	}

	@GetMapping("/new")
	public void initContext(final ViewContext viewContext) {
		viewContext.publishDto(intentKey, new Intent());
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
			@ViewAttribute("intentTextsToDelete") final DtList<IntentText> intentTextsToDelete) {
		
		// remove from UI list
		final BasicUiListModifiable<IntentText> textes = viewContext.getUiListModifiable(intentTextsKey);
		UiObject<IntentText> removedUi = textes.remove(index);
		viewContext.markModifiedKeys(intentTextsKey);

		// keep track of deleted persisted intentText
		IntentText removed = removedUi.getServerSideObject();
		if (removed.getIttId() != null) {
			intentTextsToDelete.add(removed);
		}
		viewContext.publishDtListModifiable(intentTextsToDeleteKey, intentTextsToDelete);

		return viewContext;
	}

	@PostMapping("/_addText")
	public ViewContext doAddText(final ViewContext viewContext,
			@ViewAttribute("newIntentText") final String newIntentText,
			@ViewAttribute("intent") final Intent intent,
			@ViewAttribute("intentTexts") final DtList<IntentText> intentTexts) {

		IntentText newText = new IntentText();
		newText.setIntId(intent.getIntId());
		newText.setText(newIntentText);

		intentTexts.add(newText);
		viewContext.publishDtListModifiable(intentTextsKey, intentTexts);

		viewContext.publishRef(newIntentTextKey, "");

		return viewContext;
	}
}