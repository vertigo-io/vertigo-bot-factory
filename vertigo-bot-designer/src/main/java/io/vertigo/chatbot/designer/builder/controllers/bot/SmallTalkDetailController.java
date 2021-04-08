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
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.NluTrainingSentence;
import io.vertigo.chatbot.commons.domain.topic.ResponseButton;
import io.vertigo.chatbot.commons.domain.topic.ResponseType;
import io.vertigo.chatbot.commons.domain.topic.SmallTalk;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.UtterText;
import io.vertigo.chatbot.designer.builder.services.ResponsesButtonServices;
import io.vertigo.chatbot.designer.builder.services.SmallTalkServices;
import io.vertigo.chatbot.designer.builder.services.TopicServices;
import io.vertigo.chatbot.designer.builder.services.UtterTextServices;
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
@Secured("BotUser")
public class SmallTalkDetailController extends AbstractBotController {

	private static final ViewContextKey<SmallTalk> smallTalkKey = ViewContextKey.of("smallTalk");
	private static final ViewContextKey<Topic> topicKey = ViewContextKey.of("topic");
	private static final ViewContextKey<ResponseType> responseTypeKey = ViewContextKey.of("responseTypes");

	private static final ViewContextKey<String> newNluTrainingSentenceKey = ViewContextKey.of("newNluTrainingSentence");
	private static final ViewContextKey<NluTrainingSentence> nluTrainingSentencesKey = ViewContextKey
			.of("nluTrainingSentences");
	private static final ViewContextKey<NluTrainingSentence> nluTrainingSentencesToDeleteKey = ViewContextKey
			.of("nluTrainingSentencesToDelete");

	private static final ViewContextKey<UtterText> utterTextsKey = ViewContextKey.of("utterTexts");

	private static final ViewContextKey<ResponseButton> buttonsKey = ViewContextKey.of("buttons");
	private static final ViewContextKey<Topic> topicListKey = ViewContextKey.of("topicList");

	@Inject
	private SmallTalkServices smallTalkServices;

	@Inject
	private UtterTextServices utterTextServices;

	@Inject
	private TopicServices topicServices;

	@Inject
	private ResponsesButtonServices responsesButtonServices;

	@GetMapping("/{intId}")
	public void initContext(final ViewContext viewContext, @PathVariable("botId") final Long botId,
			@PathVariable("intId") final Long intId) {
		final Chatbot bot = initCommonContext(viewContext, botId);
		viewContext.publishMdl(responseTypeKey, ResponseType.class, null); // all

		final SmallTalk smallTalk = smallTalkServices.getSmallTalkById(bot, intId);
		final Topic topic = topicServices.findTopicById(smallTalk.getTopId());
		Assertion.check().isTrue(topic.getBotId().equals(botId), "Paramètres incohérents");

		viewContext.publishDto(smallTalkKey, smallTalk);
		viewContext.publishDto(topicKey, topic);

		viewContext.publishRef(newNluTrainingSentenceKey, "");
		viewContext.publishDtListModifiable(nluTrainingSentencesKey,
				topicServices.getNluTrainingSentenceByTopic(bot, topic));
		viewContext.publishDtList(nluTrainingSentencesToDeleteKey,
				new DtList<NluTrainingSentence>(NluTrainingSentence.class));

		final DtList<UtterText> utterTextList = utterTextServices.getUtterTextList(bot, smallTalk);
		utterTextList.add(new UtterText()); // add the next for random, or the 1st for rich text if 0 lines
		viewContext.publishDtListModifiable(utterTextsKey, utterTextList);

		viewContext.publishDtListModifiable(buttonsKey, responsesButtonServices.getButtonsBySmalltalk(bot, smallTalk));
		viewContext.publishDtList(topicListKey, topicServices.getAllTopicByBot(bot));

		toModeReadOnly();
	}

	@GetMapping("/new")
	public void initContext(final ViewContext viewContext, @PathVariable("botId") final Long botId) {
		final Chatbot bot = initCommonContext(viewContext, botId);
		viewContext.publishMdl(responseTypeKey, ResponseType.class, null); // all

		viewContext.publishDto(smallTalkKey, smallTalkServices.getNewSmallTalk(bot));
		viewContext.publishDto(topicKey, topicServices.getNewTopic(bot));

		viewContext.publishRef(newNluTrainingSentenceKey, "");
		viewContext.publishDtListModifiable(nluTrainingSentencesKey,
				new DtList<NluTrainingSentence>(NluTrainingSentence.class));
		viewContext.publishDtList(nluTrainingSentencesToDeleteKey,
				new DtList<NluTrainingSentence>(NluTrainingSentence.class));

		final DtList<UtterText> utterTextList = new DtList<>(UtterText.class);
		utterTextList.add(new UtterText()); // add the first one for initialization, list can't be empty
		viewContext.publishDtListModifiable(utterTextsKey, utterTextList);

		viewContext.publishDtListModifiable(buttonsKey, new DtList<>(ResponseButton.class));
		viewContext.publishDtList(topicListKey, topicServices.getAllTopicByBot(bot));

		toModeCreate();
	}

	@PostMapping("/_edit")
	public void doEdit() {
		toModeEdit();
	}

	@PostMapping("/_save")
	public String doSave(final ViewContext viewContext, final UiMessageStack uiMessageStack,
			@ViewAttribute("smallTalk") final SmallTalk smallTalk,
			@ViewAttribute("topic") final Topic topic,
			@ViewAttribute("bot") final Chatbot chatbot,
			@ViewAttribute("newNluTrainingSentence") final String newNluTrainingSentence,
			@ViewAttribute("nluTrainingSentences") final DtList<NluTrainingSentence> nluTrainingSentences,
			@ViewAttribute("nluTrainingSentencesToDelete") final DtList<NluTrainingSentence> nluTrainingSentencesToDelete) {

		final Long botId = chatbot.getBotId();
		final DtList<UtterText> utterTexts = ChatbotUtils.getRawDtList(viewContext.getUiListModifiable(utterTextsKey),
				uiMessageStack);

		final DtList<ResponseButton> buttonList = ChatbotUtils.getRawDtList(viewContext.getUiListModifiable(buttonsKey),
				uiMessageStack);

		// add training sentence who is not "validated" by enter and still in the input
		addTrainingSentense(newNluTrainingSentence, nluTrainingSentences);

		smallTalkServices.saveSmallTalk(chatbot, smallTalk, nluTrainingSentences, nluTrainingSentencesToDelete, utterTexts,
				buttonList, topic);
		return "redirect:/bot/" + botId + "/smallTalk/" + smallTalk.getSmtId();
	}

	@PostMapping("/_delete")
	public String doDelete(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot chatbot, @ViewAttribute("smallTalk") final SmallTalk smallTalk,
			@ViewAttribute("topic") final Topic topic) {
		smallTalkServices.deleteSmallTalk(chatbot, smallTalk, topic);
		return "redirect:/bot/" + topic.getBotId() + "/smallTalks/";
	}

	@PostMapping("/_addTrainingSentence")
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
