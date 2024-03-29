package io.vertigo.chatbot.designer.builder.services;

import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.account.authorization.AuthorizationManager;
import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.authorization.SecuredEntities.ChatbotOperations;
import io.vertigo.chatbot.commons.dao.ChatbotNodeDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.commons.multilingual.model.ModelMultilingualResources;
import io.vertigo.chatbot.designer.builder.chatbotNode.ChatbotNodePAO;
import io.vertigo.chatbot.designer.dao.monitoring.AlertingEventDAO;
import io.vertigo.chatbot.domain.DtDefinitions;
import io.vertigo.chatbot.domain.DtDefinitions.ChatbotNodeFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.lang.VUserException;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;

@Transactional
public class NodeServices implements Component {

	@Inject
	private ChatbotNodePAO chatbotNodePAO;

	@Inject
	private ChatbotNodeDAO chatbotNodeDAO;

	@Inject
	private ConfluenceSettingServices confluenceSettingServices;

	@Inject
	private JiraSettingServices jiraSettingServices;

	@Inject
	private AlertingEventDAO alertingEventDAO;

	@Inject
	private AuthorizationManager authorizationManager;

	@Secured("BotUser")
	public ChatbotNode getNodeByNodeId(@SecuredOperation("botContributor") final Chatbot bot, final Long nodId) {
		final ChatbotNode node = chatbotNodeDAO.get(nodId);
		if (!node.getBotId().equals(bot.getBotId())) {
			throw new VSystemException("this node is not a part of this bot");
		}
		return node;
	}

	@Secured("BotUser")
	public DtList<ChatbotNode> getAllNodesByBot(@SecuredOperation("botVisitor") final Chatbot bot) {
		return chatbotNodeDAO.findAll(Criterions.isEqualTo(ChatbotNodeFields.botId, bot.getBotId()), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}

	public DtList<ChatbotNode> getAllNodesByBotsForMonitoring(final Chatbot bot) {
		return chatbotNodeDAO.findAll(Criterions.isEqualTo(ChatbotNodeFields.botId, bot.getBotId()), DtListState.of(null));
	}

	@Secured("BotUser")
	public Optional<ChatbotNode> getDevNodeByBotId(final Long botId) {
		return chatbotNodeDAO.findOptional(
				Criterions.isEqualTo(ChatbotNodeFields.botId, botId)
						.and(Criterions.isEqualTo(ChatbotNodeFields.isDev, true)));
	}

	@Secured("SuperAdm")
	public void saveNode(final ChatbotNode node) {
		if (node.getNodId() != null) {
			final ChatbotNode previousValues = chatbotNodeDAO.get(node.getNodId());

			// enforce previous values
			node.setBotId(previousValues.getBotId());
			node.setTraId(previousValues.getTraId());

			//if the node has modification, it is flagged as not uptodate
			if (!previousValues.getApiKey().equals(node.getApiKey()) || !previousValues.getUrl().equals(node.getUrl())) {
				node.setIsUpToDate(false);
			}
		} else {
			node.setIsUpToDate(false); // a new node is not uptodate
		}

		if (Boolean.TRUE.equals(node.getIsDev())) {
			// enforce only one dev node
			chatbotNodePAO.resetDevNode(node.getBotId());
		}

		chatbotNodeDAO.save(node);
	}

	@Secured("BotUser")
	public void save(@SecuredOperation("botContributor") final Chatbot bot, final ChatbotNode node) {
		chatbotNodeDAO.save(node);
	}

	@Secured("SuperAdm")
	public void deleteNode(@SecuredOperation("botAdm") final Chatbot bot, final Long nodId) {
		confluenceSettingServices.deleteAllByNodeId(bot, nodId);
		jiraSettingServices.deleteAllByNodeId(bot, nodId);
		alertingEventDAO.findAll(Criterions.isEqualTo(DtDefinitions.AlertingEventFields.nodeId, nodId),
				DtListState.of(MAX_ELEMENTS_PLUS_ONE)).forEach(alertingEvent -> alertingEventDAO.delete(alertingEvent.getAgeId()));
		chatbotNodeDAO.delete(nodId);
	}

	@Secured("BotUser")
	public DtList<ChatbotNode> getNodesByBot(@SecuredOperation("botVisitor") final Chatbot chatbot) {
		if (authorizationManager.isAuthorized(chatbot, ChatbotOperations.botAdm)) {
			return getAllNodesByBot(chatbot);
		}
		final DtList<ChatbotNode> nodes = new DtList<>(ChatbotNode.class);
		final Optional<ChatbotNode> devNode = getDevNodeByBotId(chatbot.getBotId());
		if (devNode.isPresent()) {
			nodes.add(devNode.get());
		}
		return nodes;
	}

	@Secured("BotUser")
	public void deleteChatbotNodeByBot(@SecuredOperation("botAdm") final Chatbot bot) {
		chatbotNodePAO.removeChatbotNodeByBotId(bot.getBotId());
	}

	@Secured("BotUser")
	public ChatbotNode getDevNodeFromList(final DtList<ChatbotNode> nodeList) {
		return nodeList.stream()
				.filter(ChatbotNode::getIsDev)
				.findFirst()
				.orElseThrow(() -> new VUserException(ModelMultilingualResources.MISSING_NODE_ERROR));
	}

	@Secured("BotUser")
	public void updateNodes(final Chatbot bot) {
		final DtList<ChatbotNode> listNode = getNodesByBot(bot);
		for (final ChatbotNode node : listNode) {
			node.setIsUpToDate(false);
			saveNode(node);
		}
	}
}
