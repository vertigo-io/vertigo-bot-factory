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

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.authorization.SecuredEntities.ChatbotOperations;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.commons.domain.topic.KindTopicEnum;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TopicCategory;
import io.vertigo.chatbot.commons.domain.topic.TypeTopic;
import io.vertigo.chatbot.commons.domain.topic.TypeTopicEnum;
import io.vertigo.chatbot.commons.domain.topic.UtterText;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.designer.builder.services.UtterTextServices;
import io.vertigo.chatbot.designer.builder.services.bot.ChatbotServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicCategoryServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicInterfaceServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.chatbot.designer.builder.services.topic.TypeTopicServices;
import io.vertigo.chatbot.designer.utils.AuthorizationUtils;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.stereotype.QueryParam;
import io.vertigo.vega.webservice.validation.UiMessageStack;

@Controller
@RequestMapping("/bot")
public class BotDetailController extends AbstractBotController {

	@Inject
	private UtterTextServices utterTextServices;

	@Inject
	private TopicServices topicServices;

	@Inject
	private NodeServices nodeServices;

	@Inject
	private ChatbotServices chatbotServices;

	@Inject
	private TopicCategoryServices topicCategoryServices;

	@Inject
	private TypeTopicServices typeTopicServices;

	@Inject
	private List<TopicInterfaceServices> topicInterfaceServices;

	private static final ViewContextKey<UtterText> utterTextFailureKey = ViewContextKey.of("utterTextFailure");
	private static final ViewContextKey<UtterText> utterTextStartKey = ViewContextKey.of("utterTextStart");
	private static final ViewContextKey<UtterText> utterTextEndKey = ViewContextKey.of("utterTextEnd");

	private static final ViewContextKey<TypeTopic> typeTopicListKey = ViewContextKey.of("typeTopicList");
	private static final ViewContextKey<String> ttoCdFailureKey = ViewContextKey.of("ttoCdFailure");
	private static final ViewContextKey<String> ttoCdStartKey = ViewContextKey.of("ttoCdStart");
	private static final ViewContextKey<String> ttoCdEndKey = ViewContextKey.of("ttoCdEnd");

	private static final ViewContextKey<ChatbotNode> nodeListKey = ViewContextKey.of("nodeList");
	private static final ViewContextKey<ChatbotNode> nodeEditKey = ViewContextKey.of("nodeEdit");
	private static final ViewContextKey<ChatbotNode> nodeNewKey = ViewContextKey.of("nodeNew"); // template for creation
	private static final ViewContextKey<Boolean> deletePopinKey = ViewContextKey.of("deletePopin");

	private static final ViewContextKey<TopicCategory> topicCategoryKey = ViewContextKey.of("topicCategory");
	private static final ViewContextKey<Topic> topicFailureKey = ViewContextKey.of("topicFailure");
	private static final ViewContextKey<Topic> topicStartKey = ViewContextKey.of("topicStart");
	private static final ViewContextKey<Topic> topicEndKey = ViewContextKey.of("topicEnd");

	@GetMapping("/{botId}")
	public void initContext(final ViewContext viewContext, @PathVariable("botId") final Long botId) {
		final Chatbot bot = initCommonContext(viewContext, botId);

		if (AuthorizationUtils.isAuthorized(bot, ChatbotOperations.botAdm)) {
			viewContext.publishDtList(nodeListKey, nodeServices.getNodesByBot(bot));
		}

		viewContext.publishRef(deletePopinKey, false);
		initNodeEdit(viewContext);

		initBasicTopic(bot, viewContext, KindTopicEnum.FAILURE.name(), topicFailureKey, utterTextFailureKey, ttoCdFailureKey);
		initBasicTopic(bot, viewContext, KindTopicEnum.START.name(), topicStartKey, utterTextStartKey, ttoCdStartKey);
		initBasicTopic(bot, viewContext, KindTopicEnum.END.name(), topicEndKey, utterTextEndKey, ttoCdEndKey);

		final TopicCategory topicCategory = topicCategoryServices.getTechnicalCategoryByBot(bot);
		viewContext.publishDto(topicCategoryKey, topicCategory);
		viewContext.publishDtList(typeTopicListKey, typeTopicServices.getAllTypeTopic());
		toModeReadOnly();
	}

	private void initNodeEdit(final ViewContext viewContext) {
		viewContext.publishDto(nodeEditKey, new ChatbotNode());

		final ChatbotNode templateCreation = new ChatbotNode();
		templateCreation.setColor("#00838f");
		templateCreation.setIsDev(false);
		viewContext.publishDto(nodeNewKey, templateCreation);
	}

	@GetMapping("/new")
	public void initContext(final ViewContext viewContext) {
		initEmptyCommonContext(viewContext);
		viewContext.publishDtList(typeTopicListKey, typeTopicServices.getAllTypeTopic());
		//Init topic failure

		initNewBasicTopic(viewContext, KindTopicEnum.FAILURE.name(), topicFailureKey, utterTextFailureKey);
		initNewBasicTopic(viewContext, KindTopicEnum.START.name(), topicStartKey, utterTextStartKey);
		initNewBasicTopic(viewContext, KindTopicEnum.END.name(), topicEndKey, utterTextEndKey);
		final TopicCategory topicCategory = topicCategoryServices.initializeBasicCategory();
		viewContext.publishDto(topicCategoryKey, topicCategory);
		viewContext.publishDtList(nodeListKey, new DtList<>(ChatbotNode.class));
		viewContext.publishRef(ttoCdStartKey, TypeTopicEnum.SMALLTALK.name());
		viewContext.publishRef(ttoCdEndKey, TypeTopicEnum.SMALLTALK.name());
		viewContext.publishRef(ttoCdFailureKey, TypeTopicEnum.SMALLTALK.name());
		initNodeEdit(viewContext);

		toModeCreate();
	}

	private void initNewBasicTopic(final ViewContext viewContext, final String ktoCd, final ViewContextKey<Topic> topicBasicKey,
			final ViewContextKey<UtterText> uttertextkey) {
		viewContext.publishDto(topicBasicKey, topicServices.initNewBasicTopic(ktoCd));
		viewContext.publishDto(uttertextkey, utterTextServices.initNewBasicUttText(ktoCd));
	}

	private void initBasicTopic(final Chatbot bot, final ViewContext viewContext, final String ktoCd, final ViewContextKey<Topic> topicBasicKey,
			final ViewContextKey<UtterText> uttertextkey, final ViewContextKey<String> ttoCdkey) {
		final Topic topic = topicServices.getBasicTopicByBotIdKtoCd(bot.getBotId(), ktoCd);
		UtterText utterText = new UtterText();

		for (final TopicInterfaceServices services : topicInterfaceServices) {
			if (services.handleObject(topic)) {
				utterText = services.getBasicUtterTextByTopId(topic.getTopId());
			}
		}

		viewContext.publishDto(uttertextkey, utterText);
		viewContext.publishDto(topicBasicKey, topic);
		viewContext.publishRef(ttoCdkey, topic.getTtoCd());
	}

	@PostMapping("/_edit")
	public void doEdit() {
		toModeEdit();
	}

	@PostMapping("/_delete")
	@Secured("BotUser")
	public String doDelete(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot) {
		chatbotServices.deleteChatbot(bot);
		return "redirect:/bots/";
	}

	@PostMapping("/_save")
	@Secured("BotUser")
	public String doSave(final ViewContext viewContext, final UiMessageStack uiMessageStack,
			@ViewAttribute("bot") final Chatbot bot,
			@QueryParam("botTmpPictureUri") final Optional<FileInfoURI> personPictureFile,
			@ViewAttribute("utterTextFailure") final UtterText utterTextFailure,
			@ViewAttribute("utterTextStart") final UtterText utterTextStart,
			@ViewAttribute("utterTextEnd") final UtterText utterTextEnd,
			@ViewAttribute("topicFailure") final Topic topicFailure,
			@ViewAttribute("topicStart") final Topic topicStart,
			@ViewAttribute("topicEnd") final Topic topicEnd,
			@ViewAttribute("ttoCdFailure") final String ttoCdFailure,
			@ViewAttribute("ttoCdStart") final String ttoCdStart,
			@ViewAttribute("ttoCdEnd") final String ttoCdEnd,
			@ViewAttribute("topicCategory") final TopicCategory topicCategory) {

		topicStart.setTtoCd(ttoCdStart);
		topicFailure.setTtoCd(ttoCdFailure);
		topicEnd.setTtoCd(ttoCdEnd);
		final Chatbot savedChatbot = chatbotServices.saveChatbot(bot, personPictureFile, utterTextFailure,
				utterTextStart, utterTextEnd, topicFailure, topicStart, topicEnd, topicCategory);

		return "redirect:/bot/" + savedChatbot.getBotId();
	}

	@PostMapping("/_saveNode")
	@Secured("SuperAdm")
	public ViewContext doSaveNode(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot,
			@ViewAttribute("nodeEdit") final ChatbotNode nodeEdit) {

		nodeEdit.setBotId(bot.getBotId());

		nodeServices.saveNode(nodeEdit);

		viewContext.publishDtList(nodeListKey, nodeServices.getNodesByBot(bot));
		viewContext.publishDto(nodeEditKey, new ChatbotNode()); // reset nodeEdit so previous values are not used for
																// subsequent requests

		return viewContext;
	}

	@PostMapping("/_deleteNode")
	@Secured("SuperAdm")
	public ViewContext doDeleteNode(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot,
			@RequestParam("nodId") final Long nodId) {

		nodeServices.deleteNode(nodId);

		viewContext.publishDtList(nodeListKey, nodeServices.getNodesByBot(bot));

		return viewContext;
	}

}
