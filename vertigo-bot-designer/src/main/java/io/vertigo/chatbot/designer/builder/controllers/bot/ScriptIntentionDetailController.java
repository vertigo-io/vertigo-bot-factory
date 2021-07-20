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
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.NluTrainingSentence;
import io.vertigo.chatbot.commons.domain.topic.ScriptIntention;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TypeTopicEnum;
import io.vertigo.chatbot.designer.builder.services.topic.ScriptIntentionServices;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.validation.UiMessageStack;

@Controller
@RequestMapping("/bot/{botId}/scriptIntention")
@Secured("BotUser")
public class ScriptIntentionDetailController extends AbstractTopicController<ScriptIntention> {

	private static final ViewContextKey<ScriptIntention> scriptIntentionKey = ViewContextKey.of("object");

	@Inject
	private ScriptIntentionServices scriptIntentionServices;

	@GetMapping("/{sinId}")
	@Secured("BotVisitor")
	public void initContext(final ViewContext viewContext, @PathVariable("botId") final Long botId,
			@PathVariable("sinId") final Long sinId) {

		final Chatbot bot = initCommonContext(viewContext, botId);
		final ScriptIntention scriptIntention = scriptIntentionServices.getScriptIntentionById(bot, sinId);
		final Topic topic = getTopic(scriptIntention);

		initContext(viewContext, bot, topic);

		viewContext.publishDto(scriptIntentionKey, scriptIntention);

		toModeReadOnly();
	}

	@GetMapping("/new")
	@Secured("BotAdm")
	public void initContext(final ViewContext viewContext, @PathVariable("botId") final Long botId) {

		final Chatbot bot = initCommonContext(viewContext, botId);

		initContextNew(viewContext, bot);

		viewContext.publishDto(scriptIntentionKey, scriptIntentionServices.getNewScriptIntention(bot));

		toModeCreate();
	}

	@Override
	@PostMapping("/_save")
	@Secured("BotAdm")
	public String doSave(final ViewContext viewContext, final UiMessageStack uiMessageStack,
			@ViewAttribute("object") final ScriptIntention scriptIntention,
			@ViewAttribute("topic") final Topic topic,
			@ViewAttribute("bot") final Chatbot chatbot,
			@ViewAttribute("newNluTrainingSentence") final String newNluTrainingSentence,
			@ViewAttribute("nluTrainingSentences") final DtList<NluTrainingSentence> nluTrainingSentences,
			@ViewAttribute("nluTrainingSentencesToDelete") final DtList<NluTrainingSentence> nluTrainingSentencesToDelete) {

		checkCategory(topic);

		final Long botId = chatbot.getBotId();

		// add training sentence who is not "validated" by enter and still in the input
		nluTrainingSentenceServices.addTrainingSentense(newNluTrainingSentence, nluTrainingSentences);
		topicServices.saveTtoCd(topic, TypeTopicEnum.SCRIPTINTENTION.name());
		scriptIntentionServices.save(chatbot, scriptIntention, topic);
		topicServices.save(topic, scriptIntentionServices.isEnabled(scriptIntention, topic.getIsEnabled(), chatbot), nluTrainingSentences, nluTrainingSentencesToDelete);
		return "redirect:/bot/" + botId + "/scriptIntention/" + scriptIntention.getSinId();
	}

	@Override
	Topic getTopic(final ScriptIntention object) {
		return topicServices.findTopicById(object.getTopId());
	}

}
