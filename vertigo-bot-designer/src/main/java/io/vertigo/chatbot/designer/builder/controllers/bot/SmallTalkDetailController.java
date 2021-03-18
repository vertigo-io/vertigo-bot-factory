/**
 * vertigo - simple java starter
 *
 * Copyright (C) 2020, Vertigo.io, team@vertigo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vertigo.chatbot.designer.builder.controllers.bot;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.ChatbotUtils;
import io.vertigo.chatbot.commons.domain.NluTrainingSentence;
import io.vertigo.chatbot.commons.domain.ResponseButton;
import io.vertigo.chatbot.commons.domain.ResponseType;
import io.vertigo.chatbot.commons.domain.SmallTalk;
import io.vertigo.chatbot.commons.domain.UtterText;
import io.vertigo.chatbot.designer.builder.services.DesignerServices;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.util.StringUtil;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.validation.UiMessageStack;

@Controller
@RequestMapping("/bot/{botId}/smallTalk")
public class SmallTalkDetailController extends AbstractCommonBotController {

	private static final ViewContextKey<SmallTalk> smallTalkKey = ViewContextKey.of("smallTalk");

	private static final ViewContextKey<ResponseType> responseTypeKey = ViewContextKey.of("responseTypes");

	private static final ViewContextKey<String> newNluTrainingSentenceKey = ViewContextKey.of("newNluTrainingSentence");
	private static final ViewContextKey<NluTrainingSentence> nluTrainingSentencesKey = ViewContextKey
			.of("nluTrainingSentences");
	private static final ViewContextKey<NluTrainingSentence> nluTrainingSentencesToDeleteKey = ViewContextKey
			.of("nluTrainingSentencesToDelete");

	private static final ViewContextKey<UtterText> utterTextsKey = ViewContextKey.of("utterTexts");

	private static final ViewContextKey<ResponseButton> buttonsKey = ViewContextKey.of("buttons");
	private static final ViewContextKey<SmallTalk> smallTalkListKey = ViewContextKey.of("smallTalkList");

	@Inject
	private DesignerServices designerServices;

	@GetMapping("/{intId}")
	public void initContext(final ViewContext viewContext, @PathVariable("botId") final Long botId,
			@PathVariable("intId") final Long intId) {
		initCommonContext(viewContext, botId);
		viewContext.publishMdl(responseTypeKey, ResponseType.class, null); // all

		final SmallTalk smallTalk = designerServices.getSmallTalkById(intId);

		Assertion.check().isTrue(smallTalk.getBotId().equals(botId), "Paramètres incohérents");

		viewContext.publishDto(smallTalkKey, smallTalk);

		viewContext.publishRef(newNluTrainingSentenceKey, "");
		viewContext.publishDtListModifiable(nluTrainingSentencesKey,
				designerServices.getNluTrainingSentenceList(smallTalk));
		viewContext.publishDtList(nluTrainingSentencesToDeleteKey,
				new DtList<NluTrainingSentence>(NluTrainingSentence.class));

		final DtList<UtterText> utterTextList = designerServices.getUtterTextList(smallTalk);
		utterTextList.add(new UtterText()); // add the next for random, or the 1st for rich text if 0 lines
		viewContext.publishDtListModifiable(utterTextsKey, utterTextList);

		viewContext.publishDtListModifiable(buttonsKey, designerServices.getButtonsBySmalltalk(smallTalk));
		viewContext.publishDtList(smallTalkListKey, designerServices.getAllSmallTalksByBotId(botId));

		toModeReadOnly();
	}

	@GetMapping("/new")
	@Secured("BotUser")
	public void initContext(final ViewContext viewContext, @PathVariable("botId") final Long botId) {
		initCommonContext(viewContext, botId);
		viewContext.publishMdl(responseTypeKey, ResponseType.class, null); // all

		viewContext.publishDto(smallTalkKey, designerServices.getNewSmallTalk(botId));

		viewContext.publishRef(newNluTrainingSentenceKey, "");
		viewContext.publishDtListModifiable(nluTrainingSentencesKey,
				new DtList<NluTrainingSentence>(NluTrainingSentence.class));
		viewContext.publishDtList(nluTrainingSentencesToDeleteKey,
				new DtList<NluTrainingSentence>(NluTrainingSentence.class));

		final DtList<UtterText> utterTextList = new DtList<>(UtterText.class);
		utterTextList.add(new UtterText()); // add the first one for initialization, list can't be empty
		viewContext.publishDtListModifiable(utterTextsKey, utterTextList);

		viewContext.publishDtListModifiable(buttonsKey, new DtList<>(ResponseButton.class));
		viewContext.publishDtList(smallTalkListKey, designerServices.getAllSmallTalksByBotId(botId));

		toModeCreate();
	}

	@PostMapping("/_edit")
	@Secured("botContributor")
	public void doEdit() {
		toModeEdit();
	}

	@PostMapping("/_save")
	@Secured("botContributor")
	public String doSave(final ViewContext viewContext, final UiMessageStack uiMessageStack,
			@ViewAttribute("smallTalk") final SmallTalk smallTalk, @PathVariable("botId") final Long botId,
			@ViewAttribute("newNluTrainingSentence") final String newNluTrainingSentence,
			@ViewAttribute("nluTrainingSentences") final DtList<NluTrainingSentence> nluTrainingSentences,
			@ViewAttribute("nluTrainingSentencesToDelete") final DtList<NluTrainingSentence> nluTrainingSentencesToDelete) {

		final DtList<UtterText> utterTexts = ChatbotUtils.getRawDtList(viewContext.getUiListModifiable(utterTextsKey),
				uiMessageStack);

		final DtList<ResponseButton> buttonList = ChatbotUtils.getRawDtList(viewContext.getUiListModifiable(buttonsKey),
				uiMessageStack);

		// add training sentence who is not "validated" by enter and still in the input
		addTrainingSentense(newNluTrainingSentence, nluTrainingSentences);

		designerServices.saveSmallTalk(smallTalk, nluTrainingSentences, nluTrainingSentencesToDelete, utterTexts,
				buttonList);
		return "redirect:/bot/" + botId + "/smallTalk/" + smallTalk.getSmtId();
	}

	@PostMapping("/_delete")
	@Secured("botContributor")
	public String doDelete(final ViewContext viewContext, @ViewAttribute("smallTalk") final SmallTalk smallTalk) {
		designerServices.deleteSmallTalk(smallTalk);
		return "redirect:/bot/" + smallTalk.getBotId() + "/smallTalks/";
	}

	@PostMapping("/_addTrainingSentence")
	@Secured("botContributor")
	public ViewContext doAddTrainingSentence(final ViewContext viewContext,
			@ViewAttribute("newNluTrainingSentence") final String newNluTrainingSentenceIn,
			@ViewAttribute("nluTrainingSentences") final DtList<NluTrainingSentence> nluTrainingSentences) {

		addTrainingSentense(newNluTrainingSentenceIn, nluTrainingSentences);

		viewContext.publishDtListModifiable(nluTrainingSentencesKey, nluTrainingSentences);
		viewContext.publishRef(newNluTrainingSentenceKey, "");

		return viewContext;
	}

	private void addTrainingSentense(final String newNluTrainingSentenceIn,
			final DtList<NluTrainingSentence> nluTrainingSentences) {
		if (StringUtil.isBlank(newNluTrainingSentenceIn)) {
			return;
		}

		final String newNluTrainingSentence = newNluTrainingSentenceIn.trim();

		final boolean exists = nluTrainingSentences.stream()
				.anyMatch(its -> its.getText().equalsIgnoreCase(newNluTrainingSentence));
		if (exists) {
			throw new VUserException("This sentense already exists");
		}

		final NluTrainingSentence newText = new NluTrainingSentence();
		newText.setText(newNluTrainingSentence);

		nluTrainingSentences.add(newText);
	}

	@PostMapping("/_editTrainingSentence")
	@Secured("botContributor")
	public ViewContext doEditTrainingSentence(final ViewContext viewContext, @RequestParam("index") final int index,
			@ViewAttribute("newNluTrainingSentence") final String newNluTrainingSentence,
			@ViewAttribute("nluTrainingSentences") final DtList<NluTrainingSentence> nluTrainingSentences) {

		if (StringUtil.isBlank(newNluTrainingSentence)) {
			// empty edit, rollback modification
			viewContext.markModifiedKeys(nluTrainingSentencesKey);
			return viewContext;
		}

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
	@Secured("botContributor")
	public ViewContext doRemoveTrainingSentence(final ViewContext viewContext, @RequestParam("index") final int index,
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
}
