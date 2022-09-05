package io.vertigo.chatbot.engine.plugins.bt.command.bot;

import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.core.lang.VSystemException;

import java.util.List;

public class BotElse implements BTNode {

	private final List<BTNode> nodes;

	public BotElse(List<BTNode> nodes) {
		this.nodes = nodes;
	}

	public List<BTNode> getNodes() {
		return nodes;
	}

	@Override
	public BTStatus eval() {
		throw new VSystemException("'else' must be inside 'ifelse'");
	}
}
