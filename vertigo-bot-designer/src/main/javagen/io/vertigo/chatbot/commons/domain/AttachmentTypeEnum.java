package io.vertigo.chatbot.commons.domain;

import java.io.Serializable;

import io.vertigo.datamodel.structure.model.MasterDataEnum;
import io.vertigo.datamodel.structure.model.UID;

public enum AttachmentTypeEnum implements MasterDataEnum<io.vertigo.chatbot.commons.domain.AttachmentType> {

	DOCUMENT("DOCUMENT"), //
	ATTACHMENT("ATTACHMENT")
	;

	private final Serializable entityId;

	private AttachmentTypeEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.chatbot.commons.domain.AttachmentType> getEntityUID() {
		return UID.of(io.vertigo.chatbot.commons.domain.AttachmentType.class, entityId);
	}

}
