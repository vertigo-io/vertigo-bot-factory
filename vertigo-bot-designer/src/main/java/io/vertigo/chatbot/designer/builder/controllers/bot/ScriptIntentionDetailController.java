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
import io.vertigo.chatbot.commons.domain.topic.ScriptIntention;
import io.vertigo.chatbot.commons.domain.topic.ScriptIntention;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.UtterText;
import io.vertigo.chatbot.designer.builder.services.ResponsesButtonServices;
import io.vertigo.chatbot.designer.builder.services.ScriptIntentionServices;
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
@RequestMapping("/bot/{botId}/scriptIntention")
@Secured("BotUser")
public class ScriptIntentionDetailController extends AbstractBotController {

	private static final ViewContextKey<ScriptIntention> scriptIntentionKey = ViewContextKey.of("scriptIntention");
	private static final ViewContextKey<Topic> topicKey = ViewContextKey.of("topic");


	@Inject
	private ScriptIntentionServices scriptIntentionServices;

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
	
		final ScriptIntention scriptIntention = scriptIntentionServices.getScriptIntentionById(bot, intId);
		final Topic topic = topicServices.findTopicById(scriptIntention.getTopId());
		Assertion.check().isTrue(topic.getBotId().equals(botId), "Paramètres incohérents");

		viewContext.publishDto(scriptIntentionKey, scriptIntention);
		viewContext.publishDto(topicKey, topic);

	
		toModeReadOnly();
	}

	@GetMapping("/new")
	public void initContext(final ViewContext viewContext, @PathVariable("botId") final Long botId) {
		final Chatbot bot = initCommonContext(viewContext, botId);
	
		viewContext.publishDto(scriptIntentionKey, scriptIntentionServices.getNewScriptIntention(bot));
		viewContext.publishDto(topicKey, topicServices.getNewTopic(bot));

	
	
		toModeCreate();
	}

	@PostMapping("/_edit")
	public void doEdit() {
		toModeEdit();
	}

	@PostMapping("/_save")
	public String doSave(final ViewContext viewContext, final UiMessageStack uiMessageStack,
			@ViewAttribute("scriptIntention") final ScriptIntention scriptIntention,
			@ViewAttribute("topic") final Topic topic,
			@ViewAttribute("bot") final Chatbot chatbot) {
			final Long botId = chatbot.getBotId();
		scriptIntentionServices.saveScriptIntention(chatbot, scriptIntention, topic);
		return "redirect:/bot/" + botId + "/scriptIntention/" + scriptIntention.getSinId();
	}

	@PostMapping("/_delete")
	public String doDelete(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot chatbot, @ViewAttribute("scriptIntention") final ScriptIntention scriptIntention,
			@ViewAttribute("topic") final Topic topic) {
		scriptIntentionServices.deleteScriptIntention(chatbot, scriptIntention, topic);
		return "redirect:/bot/" + topic.getBotId() + "/scriptIntentions/";
	}

	
}
