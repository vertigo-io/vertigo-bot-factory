package io.vertigo.chatbot.commons.domain;

import java.io.Serializable;

import io.vertigo.datamodel.structure.model.MasterDataEnum;
import io.vertigo.datamodel.structure.model.UID;

public enum ChatbotFormatEnum implements MasterDataEnum<io.vertigo.chatbot.commons.domain.ChatbotFormat> {

	CLASSIC("CLASSIC"), //
	INTERACTIVE_HELP("INTERACTIVE_HELP"), //
	FAQ("FAQ")
	;

	private final Serializable entityId;

	private ChatbotFormatEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.chatbot.commons.domain.ChatbotFormat> getEntityUID() {
		return UID.of(io.vertigo.chatbot.commons.domain.ChatbotFormat.class, entityId);
	}

}
