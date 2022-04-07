package io.vertigo.chatbot.engine;

import io.vertigo.chatbot.engine.model.TopicDefinition;
import io.vertigo.core.node.component.Manager;

import java.util.UUID;

public interface BotManager extends Manager {

	/**
	 * Create a new botEngine on the default BlackBoard. A random conversation ID is used.
	 *
	 * @return a bot engine specific to this context
	 */
	BotEngine createBotEngine();

	/**
	 * Create a new botEngine on the BlackBoard identified by its name. A random conversation ID is used.
	 *
	 * @param storeName the name of the blackboard
	 * @return a bot engine specific to this context
	 */
	BotEngine createBotEngine(String storeName);

	/**
	 * Create a new botEngine on the default BlackBoard with fixed conversation ID.
	 *
	 * @param convId the name of the blackboard
	 * @return a bot engine specific to this context
	 */
	BotEngine createBotEngine(UUID convId);

	/**
	 * Create a new botEngine on the BlackBoard identified by its name and with fixed conversation ID.
	 *
	 * @param convId the name of the blackboard
	 * @param storeName the name of the blackboard
	 * @return a bot engine specific to this context
	 */
	BotEngine createBotEngine(UUID convId, String storeName);

	/**
	 * Update bot configuration. Warning, this operation is relatively slow as it depends on NLU training.
	 *
	 * @param newTopics new topics to replace old ones
	 */
	void updateConfig(Iterable<TopicDefinition> newTopics, StringBuilder logs);

}
