package io.vertigo.chatbot.engine.plugins.bt.jira.model;

import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.core.lang.VSystemException;

public class JiraField implements BTNode {

	private String key;
	private String question;
	private String fieldType;
	private String value;

	public JiraField(final String key, final String question, final String fieldType) {
		this.key = key;
		this.question = question;
		this.fieldType = fieldType;
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

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public BTStatus eval() {
		throw new VSystemException("Jira field must be inside a jira section");
	}

}
