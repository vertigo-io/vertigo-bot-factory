package io.vertigo.chatbot.designer.domain.commons;

import java.io.Serializable;

import io.vertigo.datamodel.structure.model.MasterDataEnum;
import io.vertigo.datamodel.structure.model.UID;

public enum PersonRoleEnum implements MasterDataEnum<io.vertigo.chatbot.designer.domain.commons.PersonRole> {

	RAdmin("RAdmin"), //
	RUser("RUser")
	;

	private final Serializable entityId;

	private PersonRoleEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.chatbot.designer.domain.commons.PersonRole> getEntityUID() {
		return UID.of(io.vertigo.chatbot.designer.domain.commons.PersonRole.class, entityId);
	}

}
