package io.vertigo.chatbot.designer.builder.controllers.bot;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.designer.builder.services.NodeServices;
import io.vertigo.ui.core.ViewContext;
import io.vertigo.ui.core.ViewContextKey;
import io.vertigo.ui.impl.springmvc.argumentresolvers.ViewAttribute;
import io.vertigo.vega.webservice.validation.UiMessageStack;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;

import static io.vertigo.chatbot.designer.utils.ListUtils.listLimitReached;

@Controller
@RequestMapping("/bot/{botId}/environments")
@Secured("SuperAdm")
public class EnvironmentController extends AbstractBotListEntityController<ChatbotNode> {

	private static final ViewContextKey<ChatbotNode> nodeListKey = ViewContextKey.of("nodeList");
	private static final ViewContextKey<ChatbotNode> nodeEditKey = ViewContextKey.of("nodeEdit");
	private static final ViewContextKey<ChatbotNode> nodeNewKey = ViewContextKey.of("nodeNew");

	@Inject
	private NodeServices nodeServices;

	@GetMapping("/")
	public void initContext(final ViewContext viewContext, final UiMessageStack uiMessageStack, @PathVariable("botId") final Long botId) {
		final Chatbot bot = initCommonContext(viewContext, uiMessageStack, botId);
		viewContext.publishDtList(nodeListKey, nodeServices.getNodesByBot(bot));
		viewContext.publishDto(nodeEditKey, new ChatbotNode());
		final ChatbotNode templateCreation = new ChatbotNode();
		templateCreation.setColor("#00838f");
		templateCreation.setIsDev(false);
		templateCreation.setIsUpToDate(false);
		viewContext.publishDto(nodeNewKey, templateCreation);
		super.initBreadCrums(viewContext, ChatbotNode.class);
	}

	@PostMapping("/_saveNode")
	public ViewContext doSaveNode(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot,
								  @ViewAttribute("nodeEdit") final ChatbotNode nodeEdit, final UiMessageStack uiMessageStack) {

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
		listLimitReached(viewContext, uiMessageStack);

		return viewContext;
	}

	@PostMapping("/_deleteNode")
	public ViewContext doDeleteNode(final ViewContext viewContext, @ViewAttribute("bot") final Chatbot bot,
									@RequestParam("nodId") final Long nodId, final UiMessageStack uiMessageStack) {

		nodeServices.deleteNode(nodId);

		viewContext.publishDtList(nodeListKey, nodeServices.getNodesByBot(bot));

		listLimitReached(viewContext, uiMessageStack);

		return viewContext;
	}
}
