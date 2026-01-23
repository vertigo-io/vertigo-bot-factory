package io.vertigo.chatbot.commons.domain;

import java.io.Serializable;

import io.vertigo.datamodel.data.model.MasterDataEnum;
import io.vertigo.datamodel.data.model.UID;

public enum JiraCustomFieldTypeEnum implements MasterDataEnum<io.vertigo.chatbot.commons.domain.JiraCustomFieldType> {

	STRING("STRING"), //
	NUMBER("NUMBER"), //
	ARRAY_LABELS("ARRAY_LABELS"), //
	DATE("DATE"), //
	DATETIME("DATETIME"), //
	SINGLE_OPTION("SINGLE_OPTION"), //
	MULTIPLE_OPTION("MULTIPLE_OPTION"), //
	TREE_OPTION("TREE_OPTION"), //
	SINGLE_USER("SINGLE_USER"), //
	MULTIPLE_USER("MULTIPLE_USER")
	;

	private final Serializable entityId;

	private JiraCustomFieldTypeEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.chatbot.commons.domain.JiraCustomFieldType> getEntityUID() {
		return UID.of(io.vertigo.chatbot.commons.domain.JiraCustomFieldType.class, entityId);
	}

}
