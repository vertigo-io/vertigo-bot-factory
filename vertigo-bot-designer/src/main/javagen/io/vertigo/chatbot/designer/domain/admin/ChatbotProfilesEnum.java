package io.vertigo.chatbot.designer.domain.admin;

import java.io.Serializable;

import io.vertigo.datamodel.structure.model.MasterDataEnum;
import io.vertigo.datamodel.structure.model.UID;

public enum ChatbotProfilesEnum implements MasterDataEnum<io.vertigo.chatbot.designer.domain.admin.ChatbotProfiles> {

	CADMIN("ADMIN"), //
	CMODIFIEUR("MODIFIEUR"), //
	CCONSUL("CONSUL")
	;

	private final Serializable entityId;

	private ChatbotProfilesEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.chatbot.designer.domain.admin.ChatbotProfiles> getEntityUID() {
		return UID.of(io.vertigo.chatbot.designer.domain.admin.ChatbotProfiles.class, entityId);
	}

}
