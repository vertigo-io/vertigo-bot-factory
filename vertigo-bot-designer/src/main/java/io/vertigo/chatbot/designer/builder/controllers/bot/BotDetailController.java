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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotCustomConfig;
import io.vertigo.chatbot.commons.domain.FontFamily;
import io.vertigo.chatbot.commons.domain.topic.TypeTopic;
import io.vertigo.chatbot.commons.multilingual.ConstraintResources;
import io.vertigo.chatbot.commons.multilingual.bot.BotMultilingualResources;
import io.vertigo.chatbot.designer.builder.services.FontFamilyServices;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.chatbot.designer.builder.services.bot.ChatbotCustomConfigServices;
import io.vertigo.chatbot.designer.builder.services.bot.ChatbotServices;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.chatbot.designer.builder.services.topic.TypeTopicServices;
import io.vertigo.chatbot.designer.utils.AbstractChatbotDtObjectValidator;
import io.vertigo.chatbot.designer.utils.StringUtils;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.core.locale.LocaleMessageText;
import io.vertigo.datamodel.structure.definitions.DtField;
import io.vertigo.datastore.filestore.model.FileInfoURI;
import io.vertigo.datastore.filestore.model.VFile;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.stereotype.Validate;
import io.vertigo.vega.webservice.validation.DtObjectErrors;
import io.vertigo.vega.webservice.validation.UiMessageStack;

import static io.vertigo.chatbot.designer.utils.ListUtils.listLimitReached;

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
	private ChatbotCustomConfigServices chatbotCustomConfigServices;

	@Inject
	private FontFamilyServices fontFamilyServices;

	private static final ViewContextKey<TypeTopic> typeTopicListKey = ViewContextKey.of("typeTopicList");
	// template for creation
	private static final ViewContextKey<Boolean> deletePopinKey = ViewContextKey.of("deletePopin");
	private static final ViewContextKey<FileInfoURI> botTmpPictureUriKey = ViewContextKey.of("botTmpPictureUri");
	private static final ViewContextKey<ChatbotCustomConfig> chatbotCustomConfigKey = ViewContextKey.of("chatbotCustomConfig");

	private static final ViewContextKey<FontFamily> fontFamiliesKey = ViewContextKey.of("fontFamilies");

	@GetMapping("/{botId}")
	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
		final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);

		viewContext.publishRef(deletePopinKey, false);
		viewContext.publishFileInfoURI(botTmpPictureUriKey, null);

		viewContext.publishDtList(typeTopicListKey, typeTopicServices.getAllTypeTopic());
		viewContext.publishDtList(fontFamiliesKey, fontFamilyServices.findAll());
		viewContext.publishDto(chatbotCustomConfigKey, chatbotCustomConfigServices.getChatbotCustomConfigByBotId(botId));
		super.initBreadCrums(viewContext, bot);
		toModeReadOnly();
		listLimitReached(viewContext, uiMessageStack);
	}

	@GetMapping("/{botId}/avatar")
	public VFile getAvatar(@PathVariable("botId") final Long botId) {
		return chatbotServices.getAvatar(chatbotServices.getChatbotById(botId));
	}

	@GetMapping("/avatar")
	public VFile getAvatar() {
		return chatbotServices.getNoAvatar();
	}

	@GetMapping("/new")
	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack) {
		initEmptyCommonContext(viewContext);
		viewContext.publishDtList(typeTopicListKey, typeTopicServices.getAllTypeTopic());
		viewContext.publishDtList(fontFamiliesKey, fontFamilyServices.findAll());
		viewContext.publishFileInfoURI(botTmpPictureUriKey, null);
		viewContext.publishDto(chatbotCustomConfigKey, chatbotCustomConfigServices.getDefaultChatbotCustomConfig());
		super.initEmptyBreadcrums(viewContext);
		toModeCreate();
		listLimitReached(viewContext, uiMessageStack);
	}

	@PostMapping("/_edit")
	public void doEdit() {
		toModeEdit();
	}

	@PostMapping("/_save")
	@Secured("BotUser")
	public String doSave(final ViewContext viewContext, final UiMessageStack uiMessageStack,
			@ViewAttribute("bot") final Chatbot bot,
			@ViewAttribute("botTmpPictureUri") final Optional<FileInfoURI> personPictureFile,
		 	@ViewAttribute("chatbotCustomConfig")  @Validate(ChatbotCustomConfigValidator.class) final ChatbotCustomConfig chatbotCustomConfig) {

		final Chatbot savedChatbot = chatbotServices.saveChatbot(bot, personPictureFile, chatbotCustomConfig);

		return "redirect:/bot/" + savedChatbot.getBotId();
	}

	@Override
	protected String getBreadCrums(final Chatbot object) {
		return LocaleMessageText.of(BotMultilingualResources.BOT_DETAIL).getDisplay();
	}

	/**
	 * Check if value field is not empty or meaningless html.
	 */
	public static final class ChatbotCustomConfigValidator extends AbstractChatbotDtObjectValidator<ChatbotCustomConfig> {
		private static final Pattern emailPattern = Pattern.compile("^[_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*(\\.[a-zA-Z0-9-]{2,3})+$");

		/** {@inheritDoc} */
		@Override
		protected void checkMonoFieldConstraints(final ChatbotCustomConfig chatbotCustomConfig, final DtField dtField, final DtObjectErrors dtObjectErrors) {
			super.checkMonoFieldConstraints(chatbotCustomConfig, dtField, dtObjectErrors);
			if (DtDefinitions.ChatbotCustomConfigFields.botEmailAddress.name().equals(dtField.name())) {
				final String value = (String) dtField.getDataAccessor().getValue(chatbotCustomConfig);
				if (!StringUtils.isHtmlEmpty(value)) {
					final Matcher matcher = emailPattern.matcher(value);
					if (!matcher.matches()) {
						dtObjectErrors.addError(dtField.name(), LocaleMessageText.of(ConstraintResources.INVALID_EMAIL));
					}
				}
			}
		}
	}
}
