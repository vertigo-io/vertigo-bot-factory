package io.vertigo.chatbot.engine.plugins.bt.jira.model;

import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.core.lang.VSystemException;

public class JiraField implements BTNode {

	private String key;
	private String question;

	public JiraField(final String key, final String question) {
		setKey(key);
		setQuestion(question);
	}

	public String getKey() {
		return key;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(final String question) {
		this.question = question;
	}

	@Override
	public BTStatus eval() {
		throw new VSystemException("Jira field must be inside a jira section");
	}

}
