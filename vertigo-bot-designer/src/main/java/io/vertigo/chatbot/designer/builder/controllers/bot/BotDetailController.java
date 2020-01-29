package io.vertigo.chatbot.designer.builder.controllers.bot;

import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.commons.domain.UtterText;
import io.vertigo.chatbot.designer.builder.services.DesignerServices;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.dynamo.domain.model.FileInfoURI;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.ui.impl.springmvc.controller.AbstractVSpringMvcController;
import io.vertigo.vega.webservice.stereotype.QueryParam;

@Controller
@RequestMapping("/bot")
public class BotDetailController extends AbstractVSpringMvcController {

	@Inject
	private DesignerServices designerServices;

	@Inject
	private CommonBotDetailController commonBotDetailController;

	private static final ViewContextKey<UtterText> defaultKey = ViewContextKey.of("default");
	private static final ViewContextKey<UtterText> welcomeKey = ViewContextKey.of("welcome");

	private static final ViewContextKey<ChatbotNode> nodeListKey = ViewContextKey.of("nodeList");
	private static final ViewContextKey<ChatbotNode> nodeEditKey = ViewContextKey.of("nodeEdit");
	private static final ViewContextKey<ChatbotNode> nodeNewKey = ViewContextKey.of("nodeNew"); // template for creation


	@GetMapping("/{botId}")
	public void initContext(final ViewContext viewContext, @PathVariable("botId") final Long botId) {
		final Chatbot bot = commonBotDetailController.initCommonContext(viewContext, botId);

		viewContext.publishDto(defaultKey, designerServices.getDefaultTextByBot(bot));
		viewContext.publishDto(welcomeKey, designerServices.getWelcomeTextByBot(bot));

		viewContext.publishDtList(nodeListKey, designerServices.getAllNodesByBotId(bot.getBotId()));
		initNodeEdit(viewContext);

		toModeReadOnly();
	}

	private void initNodeEdit(final ViewContext viewContext) {
		viewContext.publishDto(nodeEditKey, new ChatbotNode());

		final ChatbotNode tempalteCreation = new ChatbotNode();
		tempalteCreation.setColor("#00838f");
		tempalteCreation.setIsDev(false);
		viewContext.publishDto(nodeNewKey, tempalteCreation);
	}

	@GetMapping("/new")
	public void initContext(final ViewContext viewContext) {
		commonBotDetailController.initEmptyCommonContext(viewContext);

		final UtterText newDefault = new UtterText();
		newDefault.setText("Sorry, I don't understand.");
		viewContext.publishDto(defaultKey, newDefault);

		final UtterText newWelcome = new UtterText();
		newWelcome.setText("Hello !");
		viewContext.publishDto(welcomeKey, newWelcome);

		viewContext.publishDtList(nodeListKey, new DtList<>(ChatbotNode.class));
		initNodeEdit(viewContext);

		toModeCreate();
	}

	@PostMapping("/_edit")
	public void doEdit() {
		toModeEdit();
	}

	@PostMapping("/_save")
	public String doSave(@ViewAttribute("bot") final Chatbot bot,
			@QueryParam("botTmpPictureUri") final Optional<FileInfoURI> personPictureFile,
			@ViewAttribute("default") final UtterText defaultText,
			@ViewAttribute("welcome") final UtterText welcome) {

		final Chatbot savedChatbot = designerServices.saveChatbot(bot, personPictureFile, defaultText, welcome);

		return "redirect:/bot/" + savedChatbot.getBotId();
	}

	@PostMapping("/_saveNode")
	public ViewContext doSaveNode(final ViewContext viewContext,
			@ViewAttribute("bot") final Chatbot bot,
			@ViewAttribute("nodeEdit") final ChatbotNode nodeEdit) {

		nodeEdit.setBotId(bot.getBotId());

		designerServices.saveNode(nodeEdit);

		viewContext.publishDtList(nodeListKey, designerServices.getAllNodesByBotId(bot.getBotId()));
		viewContext.publishDto(nodeEditKey, new ChatbotNode()); // reset nodeEdit so previous values are not used for subsequent requests

		return viewContext;
	}

	@PostMapping("/_deleteNode")
	public ViewContext doDeleteNode(final ViewContext viewContext,
			@ViewAttribute("bot") final Chatbot bot,
			@RequestParam("nodId") final Long nodId) {

		designerServices.deleteNode(nodId);

		viewContext.publishDtList(nodeListKey, designerServices.getAllNodesByBotId(bot.getBotId()));

		return viewContext;
	}

}