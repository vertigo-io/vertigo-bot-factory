package io.vertigo.chatbot.commons.domain;

import java.io.Serializable;

import io.vertigo.datamodel.data.model.MasterDataEnum;
import io.vertigo.datamodel.data.model.UID;

public enum TypeOperatorEnum implements MasterDataEnum<io.vertigo.chatbot.commons.domain.TypeOperator> {

	EQUAL("EQUALS"), //
	CONTAIN("CONTAINS")
	;

	private final Serializable entityId;

	private TypeOperatorEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.chatbot.commons.domain.TypeOperator> getEntityUID() {
		return UID.of(io.vertigo.chatbot.commons.domain.TypeOperator.class, entityId);
	}

}
