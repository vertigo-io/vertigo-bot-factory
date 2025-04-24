package io.vertigo.chatbot.commons.domain;

import java.io.Serializable;

import io.vertigo.datamodel.data.model.MasterDataEnum;
import io.vertigo.datamodel.data.model.UID;

public enum UnknownSentenceStatusEnum implements MasterDataEnum<io.vertigo.chatbot.commons.domain.UnknownSentenceStatus> {

	TO_TREAT("TO_TREAT"), //
	TREATED("TREATED"), //
	REJECTED("REJECTED")
	;

	private final Serializable entityId;

	private UnknownSentenceStatusEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.chatbot.commons.domain.UnknownSentenceStatus> getEntityUID() {
		return UID.of(io.vertigo.chatbot.commons.domain.UnknownSentenceStatus.class, entityId);
	}

}
