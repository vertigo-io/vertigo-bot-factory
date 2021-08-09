package io.vertigo.chatbot.engine.plugins.bt.jira.model;

import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.core.lang.VSystemException;

public class JiraField implements BTNode {

	private String value;

	public JiraField(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	public BTStatus eval() {
		throw new VSystemException("Jira field must be inside a jira section");
	}

}
