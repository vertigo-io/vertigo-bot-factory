package io.vertigo.chatbot.commons.domain;

import java.io.Serializable;

import io.vertigo.datamodel.structure.model.MasterDataEnum;
import io.vertigo.datamodel.structure.model.UID;

public enum ResponseTypeEnum implements MasterDataEnum<io.vertigo.chatbot.commons.domain.ResponseType> {

	RICH_TEXT("RICH_TEXT"), //
	RANDOM_TEXT("RANDOM_TEXT")
	;

	private final Serializable entityId;

	private ResponseTypeEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.chatbot.commons.domain.ResponseType> getEntityUID() {
		return UID.of(io.vertigo.chatbot.commons.domain.ResponseType.class, entityId);
	}

}
