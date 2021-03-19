package io.vertigo.chatbot.designer.builder.services;

import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.account.authorization.AuthorizationManager;
import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.authorization.SecuredEntities.ChatbotOperations;
import io.vertigo.chatbot.commons.dao.ChatbotNodeDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.designer.builder.chatbotNode.ChatbotNodePAO;
import io.vertigo.chatbot.domain.DtDefinitions.ChatbotNodeFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

@Transactional
@Secured("BotUser")
public class NodeServices implements Component {

	@Inject
	private ChatbotNodePAO chatbotNodePAO;

	@Inject
	private ChatbotNodeDAO chatbotNodeDAO;

	@Inject
	private AuthorizationManager authorizationManager;

	public ChatbotNode getNodeByNodeId(final Long nodId) {
		return chatbotNodeDAO.get(nodId);
	}

	public DtList<ChatbotNode> getAllNodesByBotId(final Long botId) {
		return chatbotNodeDAO.findAll(Criterions.isEqualTo(ChatbotNodeFields.botId, botId), DtListState.of(100));
	}

	public Optional<ChatbotNode> getDevNodeByBotId(final Long botId) {
		return chatbotNodeDAO.findOptional(
				Criterions.isEqualTo(ChatbotNodeFields.botId, botId)
						.and(Criterions.isEqualTo(ChatbotNodeFields.isDev, true)));
	}

	public void saveNode(final ChatbotNode node) {
		if (node.getNodId() != null) {
			// enforce previous values
			final ChatbotNode previousValues = chatbotNodeDAO.get(node.getNodId());

			node.setBotId(previousValues.getBotId());
			node.setTraId(previousValues.getTraId());
		}

		if (Boolean.TRUE.equals(node.getIsDev())) {
			// enforce only one dev node
			chatbotNodePAO.resetDevNode(node.getBotId());
		}

		chatbotNodeDAO.save(node);
	}

	public void save(final ChatbotNode node) {
		chatbotNodeDAO.save(node);
	}

	public void deleteNode(final Long nodId) {
		chatbotNodeDAO.delete(nodId);
	}

	public DtList<ChatbotNode> getNodesByBotId(final Chatbot bot) {
		return getNodesByBot(bot);
	}

	public DtList<ChatbotNode> getNodesByBot(final Chatbot chatbot) {
		if (authorizationManager.isAuthorized(chatbot, ChatbotOperations.botAdm)) {
			return getAllNodesByBotId(chatbot.getBotId());
		}
		final DtList<ChatbotNode> nodes = new DtList<ChatbotNode>(ChatbotNode.class);
		final Optional<ChatbotNode> devNode = getDevNodeByBotId(chatbot.getBotId());
		if (devNode.isPresent()) {
			nodes.add(devNode.get());
		}
		return nodes;
	}

	public void deleteChatbotNodeByBot(final Chatbot bot) {
		chatbotNodePAO.removeChatbotNodeByBotId(bot.getBotId());
	}
}
