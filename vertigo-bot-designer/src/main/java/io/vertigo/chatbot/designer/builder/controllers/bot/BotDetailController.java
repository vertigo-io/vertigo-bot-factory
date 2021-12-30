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

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.authorization.SecuredEntities.ChatbotOperations;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotCustomConfig;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.commons.domain.topic.KindTopicEnum;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TypeTopic;
import io.vertigo.chatbot.commons.multilingual.bot.BotMultilingualResources;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.designer.builder.services.UtterTextServices;
import io.vertigo.chatbot.designer.builder.services.bot.ChabotCustomConfigServices;
import io.vertigo.chatbot.designer.builder.services.bot.ChatbotServices;
import io.vertigo.chatbot.designer.builder.services.topic.ITopicService;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.chatbot.designer.builder.services.topic.TypeTopicServices;
import io.vertigo.chatbot.designer.domain.commons.BotPredefinedTopic;
import io.vertigo.chatbot.designer.utils.AuthorizationUtils;
import io.vertigo.chatbot.designer.utils.StringUtils;
import io.vertigo.chatbot.domain.DtDefinitions.BotPredefinedTopicFields;
import io.vertigo.core.locale.MessageText;
import io.vertigo.datamodel.structure.definitions.DtField;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.Entity;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.stereotype.QueryParam;
import io.vertigo.vega.webservice.stereotype.Validate;
import io.vertigo.vega.webservice.validation.AbstractDtObjectValidator;
import io.vertigo.vega.webservice.validation.DtObjectErrors;
import io.vertigo.vega.webservice.validation.UiMessageStack;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/bot")
public class BotDetailController extends AbstractBotCreationController<Chatbot> {

	@Inject
	private TopicServices topicServices;

	@Inject
	private NodeServices nodeServices;

	@Inject
	private ChatbotServices chatbotServices;

	@Inject
	private TypeTopicServices typeTopicServices;

	@Inject
	private ChabotCustomConfigServices chabotCustomConfigServices;

	@Inject
	private List<ITopicService<? extends Entity>> topicInterfaceServices;

	private static final ViewContextKey<BotPredefinedTopic> startTopicKey = ViewContextKey.of("startTopic");
	private static final ViewContextKey<BotPredefinedTopic> failureTopicKey = ViewContextKey.of("failureTopic");
	private static final ViewContextKey<BotPredefinedTopic> endTopicKey = ViewContextKey.of("endTopic");

	private static final ViewContextKey<TypeTopic> typeTopicListKey = ViewContextKey.of("typeTopicList");

	private static final ViewContextKey<ChatbotNode> nodeListKey = ViewContextKey.of("nodeList");
	private static final ViewContextKey<ChatbotNode> nodeEditKey = ViewContextKey.of("nodeEdit");
	private static final ViewContextKey<ChatbotNode> nodeNewKey = ViewContextKey.of("nodeNew"); // template for creation
	private static final ViewContextKey<Boolean> deletePopinKey = ViewContextKey.of("deletePopin");
	private static final ViewContextKey<ChatbotCustomConfig> chatbotCustomConfigKey = ViewContextKey.of("chatbotCustomConfig");

	@GetMapping("/{botId}")
	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
		final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);

		if (AuthorizationUtils.isAuthorized(bot, ChatbotOperations.botAdm)) {
			viewContext.publishDtList(nodeListKey, nodeServices.getNodesByBot(bot));
		}

		viewContext.publishRef(deletePopinKey, false);
		initNodeEdit(viewContext);

		loadBotTopic(bot, viewContext, KindTopicEnum.FAILURE.name(), failureTopicKey);
		loadBotTopic(bot, viewContext, KindTopicEnum.START.name(), startTopicKey);
		loadBotTopic(bot, viewContext, KindTopicEnum.END.name(), endTopicKey);

		viewContext.publishDtList(typeTopicListKey, typeTopicServices.getAllTypeTopic());
		viewContext.publishDto(chatbotCustomConfigKey, chabotCustomConfigServices.getChatbotCustomConfigByBotId(botId));
		super.initBreadCrums(viewContext, bot);
		toModeReadOnly();
	}

	private static void initNodeEdit(final ViewContext viewContext) {
		viewContext.publishDto(nodeEditKey, new ChatbotNode());

		final ChatbotNode templateCreation = new ChatbotNode();
		templateCreation.setColor("#00838f");
		templateCreation.setIsDev(false);
		templateCreation.setIsUpToDate(false);
		viewContext.publishDto(nodeNewKey, templateCreation);
	}

	@GetMapping("/new")
	public void initContext(final ViewContext viewContext) {
		initEmptyCommonContext(viewContext);
		viewContext.publishDtList(typeTopicListKey, typeTopicServices.getAllTypeTopic());

		newBotTopic(viewContext, KindTopicEnum.FAILURE.name(), failureTopicKey);
		newBotTopic(viewContext, KindTopicEnum.START.name(), startTopicKey);
		newBotTopic(viewContext, KindTopicEnum.END.name(), endTopicKey);

		viewContext.publishDtList(nodeListKey, new DtList<>(ChatbotNode.class));
		viewContext.publishDto(chatbotCustomConfigKey, chabotCustomConfigServices.getDefaultChatbotCustomConfig());
		initNodeEdit(viewContext);
		super.initEmptyBreadcrums(viewContext);
		toModeCreate();
	}

	private static void newBotTopic(final ViewContext viewContext, final String ktoCd, final ViewContextKey<BotPredefinedTopic> viewModelKey) {
		final BotPredefinedTopic botTopic = new BotPredefinedTopic();
		botTopic.setValue(UtterTextServices.initializeDefaultText(ktoCd));
		botTopic.setTtoCd("SMALLTALK");

		viewContext.publishDto(viewModelKey, botTopic);
	}

	private void loadBotTopic(final Chatbot bot, final ViewContext viewContext, final String ktoCd, final ViewContextKey<BotPredefinedTopic> viewModelKey) {
		final Topic topic = topicServices.getBasicTopicByBotIdKtoCd(bot.getBotId(), ktoCd);
		BotPredefinedTopic predefinedTopic = null;

		for (final ITopicService<? extends Entity> services : topicInterfaceServices) {
			if (services.handleObject(topic)) {
				predefinedTopic = services.getBotPredefinedTopicByTopId(topic.getTopId());
			}
		}

		viewContext.publishDto(viewModelKey, predefinedTopic);
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
			@ViewAttribute("failureTopic") @Validate(BotTopicNotEmptyValidator.class) final BotPredefinedTopic failureBotTopic,
			@ViewAttribute("startTopic") @Validate(BotTopicNotEmptyValidator.class) final BotPredefinedTopic startBotTopic,
			@ViewAttribute("endTopic") @Validate(BotTopicNotEmptyValidator.class) final BotPredefinedTopic endBotTopic,
		 	@ViewAttribute("chatbotCustomConfig") final ChatbotCustomConfig chatbotCustomConfig) {

		final Chatbot savedChatbot = chatbotServices.saveChatbot(bot, personPictureFile, failureBotTopic, startBotTopic, endBotTopic, chatbotCustomConfig);

		return "redirect:/bot/" + savedChatbot.getBotId();
	}

	@PostMapping("/_saveNode")
	@Secured("SuperAdm")
	public ViewContext doSaveNode(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot,
			@ViewAttribute("nodeEdit") final ChatbotNode nodeEdit) {

		//if the node has modification, it is flagged as not uptodate
		if (nodeEdit.getNodId() != null) {
			final ChatbotNode oldNode = nodeServices.getNodeByNodeId(bot, nodeEdit.getNodId());
			if (oldNode == null || !oldNode.getApiKey().equals(nodeEdit.getApiKey()) || !oldNode.getUrl().equals(nodeEdit.getUrl())) {
				nodeEdit.setIsUpToDate(false);
			}
		}
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

	@Override
	protected String getBreadCrums(final Chatbot object) {
		return MessageText.of(BotMultilingualResources.BOT_DETAIL).getDisplay();
	}

	/**
	 * Check if value field is not empty or meaningless html.
	 */
	public static final class BotTopicNotEmptyValidator extends AbstractDtObjectValidator<BotPredefinedTopic> {

		/** {@inheritDoc} */
		@Override
		protected void checkMonoFieldConstraints(final BotPredefinedTopic botTopic, final DtField dtField, final DtObjectErrors dtObjectErrors) {
			if (BotPredefinedTopicFields.value.name().equals(dtField.getName())) {
				final String value = (String) dtField.getDataAccessor().getValue(botTopic);
				if (StringUtils.isHtmlEmpty(value)) {
					dtObjectErrors.addError(dtField.getName(), MessageText.of("Le champ doit être renseigné")); // TODO: use same i18n resource when avaiable in DefaultDtObjectValidator
				}
			}
		}
	}
}
