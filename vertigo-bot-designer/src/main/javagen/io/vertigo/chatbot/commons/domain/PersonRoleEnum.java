package io.vertigo.chatbot.commons.domain;

import java.io.Serializable;

import io.vertigo.datamodel.structure.model.MasterDataEnum;
import io.vertigo.datamodel.structure.model.UID;

public enum PersonRoleEnum implements MasterDataEnum<io.vertigo.chatbot.commons.domain.PersonRole> {

	RAdmin("RAdmin"), //
	RUser("RUser")
	;

	private final Serializable entityId;

	private PersonRoleEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.chatbot.commons.domain.PersonRole> getEntityUID() {
		return UID.of(io.vertigo.chatbot.commons.domain.PersonRole.class, entityId);
	}

}
