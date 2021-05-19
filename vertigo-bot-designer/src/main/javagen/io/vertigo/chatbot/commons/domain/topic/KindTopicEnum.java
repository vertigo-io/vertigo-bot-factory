package io.vertigo.chatbot.commons.domain.topic;

import java.io.Serializable;

import io.vertigo.datamodel.structure.model.MasterDataEnum;
import io.vertigo.datamodel.structure.model.UID;

public enum KindTopicEnum implements MasterDataEnum<io.vertigo.chatbot.commons.domain.topic.KindTopic> {

	START("START"), //
	END("END"), //
	FAILURE("FAILURE"), //
	NORMAL("NORMAL")
	;

	private final Serializable entityId;

	private KindTopicEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.chatbot.commons.domain.topic.KindTopic> getEntityUID() {
		return UID.of(io.vertigo.chatbot.commons.domain.topic.KindTopic.class, entityId);
	}

}
