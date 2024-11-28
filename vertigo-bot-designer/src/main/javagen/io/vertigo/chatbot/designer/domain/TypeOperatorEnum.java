package io.vertigo.chatbot.designer.domain;

import java.io.Serializable;

import io.vertigo.datamodel.structure.model.MasterDataEnum;
import io.vertigo.datamodel.structure.model.UID;

public enum TypeOperatorEnum implements MasterDataEnum<io.vertigo.chatbot.designer.domain.TypeOperator> {

	EQUAL("EQUALS"), //
	CONTAIN("CONTAINS")
	;

	private final Serializable entityId;

	private TypeOperatorEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.chatbot.designer.domain.TypeOperator> getEntityUID() {
		return UID.of(io.vertigo.chatbot.designer.domain.TypeOperator.class, entityId);
	}

}
