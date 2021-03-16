package io.vertigo.chatbot.designer.builder.services;

import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.account.authorization.AuthorizationManager;
import io.vertigo.chatbot.authorization.SecuredEntities.ChatbotOperations;
import io.vertigo.chatbot.commons.dao.ChatbotNodeDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.designer.builder.BuilderPAO;
import io.vertigo.chatbot.designer.builder.services.bot.ChatbotServices;
import io.vertigo.chatbot.domain.DtDefinitions.ChatbotNodeFields;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

@Transactional
public class NodeServices implements Component {

	@Inject
	private BuilderPAO builderPAO;

	@Inject
	private ChatbotNodeDAO chatbotNodeDAO;

	@Inject
	private ChatbotServices chatbotServices;

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
			builderPAO.resetDevNode(node.getBotId());
		}

		chatbotNodeDAO.save(node);
	}

	public void deleteNode(final Long nodId) {
		chatbotNodeDAO.delete(nodId);
	}

	public DtList<ChatbotNode> getNodesByBotId(final Long botId) {
		return getNodesByBot(chatbotServices.getChatbotById(botId));
	}

	public DtList<ChatbotNode> getNodesByBot(final Chatbot chatbot) {
		if (authorizationManager.isAuthorized(chatbot, ChatbotOperations.admFct)) {
			return getAllNodesByBotId(chatbot.getBotId());
		}
		final DtList<ChatbotNode> nodes = new DtList<ChatbotNode>(ChatbotNode.class);
		final Optional<ChatbotNode> devNode = getDevNodeByBotId(chatbot.getBotId());
		if (devNode.isPresent()) {
			nodes.add(devNode.get());
		}
		return nodes;
	}
}
