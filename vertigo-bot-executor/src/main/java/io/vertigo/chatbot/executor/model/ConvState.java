package io.vertigo.chatbot.executor.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import io.vertigo.chatbot.engine.model.BotState;

public final class ConvState {
	private final List<BotState> stateList;
	private Instant lastActivity;

	public ConvState() {
		stateList = new ArrayList<>();
		updateLastActivity();
	}

	/**
	 * @return the stateList
	 */
	public List<BotState> getStateList() {
		return stateList;
	}

	/**
	 * @return the lastActivity
	 */
	public Instant getLastActivity() {
		return lastActivity;
	}

	/**
	 * Update last activity to "now".
	 */
	public void updateLastActivity() {
		lastActivity = Instant.now();
	}

}
