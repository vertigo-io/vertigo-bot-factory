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
import io.vertigo.chatbot.designer.builder.services.UtterTextServices;
import io.vertigo.chatbot.designer.builder.services.topic.SmallTalkServices;
import io.vertigo.core.lang.VUserException;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.validation.UiMessageStack;

@Controller
@RequestMapping("/bot/{botId}/smallTalk")
@Secured("BotUser")
public class SmallTalkDetailController extends AbstractTopicController<SmallTalk> {

	private static final ViewContextKey<SmallTalk> smallTalkKey = ViewContextKey.of("smallTalk");

	private static final ViewContextKey<ResponseType> responseTypeKey = ViewContextKey.of("responseTypes");

	private static final ViewContextKey<UtterText> utterTextsKey = ViewContextKey.of("utterTexts");

	private static final ViewContextKey<ResponseButton> buttonsKey = ViewContextKey.of("buttons");

	@Inject
	private SmallTalkServices smallTalkServices;

	@Inject
	private UtterTextServices utterTextServices;

	@Inject
	private ResponsesButtonServices responsesButtonServices;

	@GetMapping("/{smtId}")
	public void initContext(final ViewContext viewContext, @PathVariable("botId") final Long botId,
			@PathVariable("smtId") final Long smtId) {

		final Chatbot bot = initCommonContext(viewContext, botId);
		final SmallTalk smallTalk = smallTalkServices.getSmallTalkById(bot, smtId);
		final Topic topic = getTopic(smallTalk);

		initContext(viewContext, bot, topic);

		viewContext.publishDto(smallTalkKey, smallTalk);
		viewContext.publishMdl(responseTypeKey, ResponseType.class, null);
		viewContext.publishDtListModifiable(buttonsKey, responsesButtonServices.getButtonsBySmalltalk(bot, smallTalk.getSmtId()));
		final DtList<UtterText> utterTextList = utterTextServices.getUtterTextList(bot, smallTalk);
		utterTextList.add(new UtterText()); // add the next for random, or the 1st for rich text if 0 lines
		viewContext.publishDtListModifiable(utterTextsKey, utterTextList);

		toModeReadOnly();
	}

	@GetMapping("/new")
	public void initContext(final ViewContext viewContext, @PathVariable("botId") final Long botId) {

		final Chatbot bot = initCommonContext(viewContext, botId);

		initContextNew(viewContext, bot);

		viewContext.publishDto(smallTalkKey, smallTalkServices.getNewSmallTalk(bot));
		viewContext.publishMdl(responseTypeKey, ResponseType.class, null);

		final DtList<UtterText> utterTextList = new DtList<>(UtterText.class);
		utterTextList.add(new UtterText()); // add the first one for initialization, list can't be empty
		viewContext.publishDtListModifiable(utterTextsKey, utterTextList);

		viewContext.publishDtListModifiable(buttonsKey, new DtList<>(ResponseButton.class));

		toModeCreate();
	}

	@Override
	Topic getTopic(final SmallTalk smallTalk) {
		return topicServices.findTopicById(smallTalk.getTopId());
	}

	@PostMapping("/_save")
	@Override
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

	@Override
	@PostMapping("/_delete")
	public String doDelete(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot chatbot, @ViewAttribute("smallTalk") final SmallTalk smallTalk,
			@ViewAttribute("topic") final Topic topic) {
		final DtList<Topic> listTopicRef = topicServices.getTopicReferencingTopId(topic.getTopId());
		if (!listTopicRef.isEmpty()) {
			final StringBuilder errorMessage = new StringBuilder("This topic cannot be removed because it is referenced in response button in the following topics : ");
			String prefix = "";
			for (final Topic topicRef : listTopicRef) {
				errorMessage.append(prefix);
				errorMessage.append(topicRef.getTitle());
				prefix = ", ";
			}
			errorMessage.append(".");
			throw new VUserException(errorMessage.toString());
		}
		smallTalkServices.deleteSmallTalk(chatbot, smallTalk, topic);
		return "redirect:/bot/" + topic.getBotId() + "/topics/";
	}

}
