package io.vertigo.chatbot.engine.plugins.bt.command.bot;

import io.vertigo.ai.bt.BTCondition;
import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VSystemException;

import java.util.List;

public class BotIf implements BTNode {

	private final List<BTNode> nodes;

	public BotIf(List<BTNode> nodes) {
		Assertion.check()
				.isTrue(nodes.stream().anyMatch(b -> b instanceof BTCondition), "You need to provide one condition in a IF ELSE node");
		BTCondition condition = (BTCondition) nodes.stream().filter(b -> b instanceof BTCondition).findFirst().orElseThrow();
		nodes.remove(condition);
		nodes.add(0, condition);
		this.nodes = nodes;
	}

	public List<BTNode> getNodes() {
		return nodes;
	}

	@Override
	public BTStatus eval() {
		throw new VSystemException("'if' must be inside 'ifelse'");
	}
}
