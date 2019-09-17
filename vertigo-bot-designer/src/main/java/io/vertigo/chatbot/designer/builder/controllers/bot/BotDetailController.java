package io.vertigo.chatbot.designer.builder.controllers.bot;

import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.UtterText;
import io.vertigo.chatbot.designer.builder.services.DesignerServices;
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

	@GetMapping("/{botId}")
	public void initContext(final ViewContext viewContext, @PathVariable("botId") final Long botId) {
		final Chatbot bot = commonBotDetailController.initCommonContext(viewContext, botId);

		viewContext.publishDto(defaultKey, designerServices.getDefaultByBot(bot));
		viewContext.publishDto(welcomeKey, designerServices.getWelcomeByBot(bot));

		toModeReadOnly();
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

}