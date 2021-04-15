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
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TopicIhm;
import io.vertigo.chatbot.commons.domain.topic.TypeTopic;
import io.vertigo.chatbot.commons.domain.topic.TypeTopicEnum;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.chatbot.domain.DtDefinitions.TopicIhmFields;
import io.vertigo.core.lang.VUserException;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;

@Controller
@RequestMapping("/bot/{botId}/topics")
@Secured("BotUser")
public class TopicsListController extends AbstractBotController {

	private static final ViewContextKey<TopicIhm> topicKey = ViewContextKey.of("topics");
	private static final ViewContextKey<Topic> newTopicKey = ViewContextKey.of("newTopic");
	// All the topic types
	private static final ViewContextKey<TypeTopic> typeTopicListKey = ViewContextKey.of("typeTopicList");
	// return of the select
	private static final ViewContextKey<String> selectionList = ViewContextKey.of("selectionList");
	@Inject
	private TopicServices topicServices;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, @PathVariable("botId") final Long botId) {
		final Chatbot bot = initCommonContext(viewContext, botId);
		viewContext.publishDtList(topicKey, TopicIhmFields.topId, topicServices.getAllTopicIhmByBot(bot));
		viewContext.publishDtListModifiable(typeTopicListKey, topicServices.getAllTypeTopic());

		viewContext.publishRef(selectionList, "");
		//		viewContext.publishRef(botIdKey, botId);
		toModeReadOnly();
	}

	@PostMapping("/createTopic")
	public String doCreateTopic(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot, @ViewAttribute("selectionList") final String ttoCd) {
		if (ttoCd.isEmpty()) {
			throw new VUserException("Choose a type of topic");
		}
		final Long botId = bot.getBotId();
		if (TypeTopicEnum.SMALLTALK.name().equals(ttoCd)) {
			return "redirect:/bot/" + botId + "/smallTalk/new";
		} else if (TypeTopicEnum.SCRIPTINTENTION.name().equals(ttoCd)) {
			return "redirect:/bot/" + botId + "/scriptIntention/new";
		}
		return "redirect:/bot/" + botId + "/topics/";
	}

}
