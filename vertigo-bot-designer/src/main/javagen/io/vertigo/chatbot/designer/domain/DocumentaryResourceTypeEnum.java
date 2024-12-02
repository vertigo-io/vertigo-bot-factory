package io.vertigo.chatbot.designer.domain;

import java.io.Serializable;

import io.vertigo.datamodel.structure.model.MasterDataEnum;
import io.vertigo.datamodel.structure.model.UID;

public enum DocumentaryResourceTypeEnum implements MasterDataEnum<io.vertigo.chatbot.designer.domain.DocumentaryResourceType> {

	URL("URL"), //
	FILE("FILE")
	;

	private final Serializable entityId;

	private DocumentaryResourceTypeEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.chatbot.designer.domain.DocumentaryResourceType> getEntityUID() {
		return UID.of(io.vertigo.chatbot.designer.domain.DocumentaryResourceType.class, entityId);
	}

}
