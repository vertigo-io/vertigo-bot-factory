package io.vertigo.chatbot.commons.domain;

import java.io.Serializable;

import io.vertigo.datamodel.data.model.MasterDataEnum;
import io.vertigo.datamodel.data.model.UID;

public enum JiraFieldEnum implements MasterDataEnum<io.vertigo.chatbot.commons.domain.JiraField> {

	SUMMARY("SUMMARY"), //
	DESCRIPTION("DESCRIPTION"), //
	TYPE("TYPE"), //
	COMPONENT("COMPONENT"), //
	AFFECTS_VERSION("AFFECTS_VERSION"), //
	FIX_VERSION("FIX_VERSION"), //
	PRIORITY("PRIORITY"), //
	ASSIGNEE("ASSIGNEE"), //
	REPORTER("REPORTER"), //
	ATTACHMENT("ATTACHMENT")
	;

	private final Serializable entityId;

	private JiraFieldEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.chatbot.commons.domain.JiraField> getEntityUID() {
		return UID.of(io.vertigo.chatbot.commons.domain.JiraField.class, entityId);
	}

}
