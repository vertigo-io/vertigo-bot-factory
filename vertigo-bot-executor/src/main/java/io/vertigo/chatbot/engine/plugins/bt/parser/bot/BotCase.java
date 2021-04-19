package io.vertigo.chatbot.engine.plugins.bt.parser.bot;

import java.util.List;

import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.core.lang.VSystemException;

/**
 * Node for switch text parsing ONLY.
 *
 * @author skerdudou
 */
public final class BotCase implements BTNode {
	private final String compare;
	private final List<BTNode> nodes;

	BotCase(final String compare, final List<BTNode> nodes) {
		this.compare = compare;
		this.nodes = nodes;
	}

	/**
	 * @return the compare
	 */
	public String getCompare() {
		return compare;
	}

	/**
	 * @return the nodes
	 */
	public List<BTNode> getNodes() {
		return nodes;
	}

	@Override
	public BTStatus eval() {
		throw new VSystemException("'case' must be inside 'switch'.");
	}
}
