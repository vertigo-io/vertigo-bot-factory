package io.vertigo.chatbot.engine.model;

import io.vertigo.ai.bb.BlackBoard;

public final class BotState {
	private final BlackBoard bb;
	private final TopicDefinition topic;

	public BotState(final BlackBoard bb, final TopicDefinition topic) {
		this.bb = bb;
		this.topic = topic;
	}

	/**
	 * @return the bb
	 */
	public BlackBoard getBb() {
		return bb;
	}

	/**
	 * @return the topic
	 */
	public TopicDefinition getTopic() {
		return topic;
	}

}
