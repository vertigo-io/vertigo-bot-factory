package io.vertigo.chatbot.commons.domain.topic;

import java.io.Serializable;

import io.vertigo.datamodel.data.model.MasterDataEnum;
import io.vertigo.datamodel.data.model.UID;

public enum TypeTopicEnum implements MasterDataEnum<io.vertigo.chatbot.commons.domain.topic.TypeTopic> {

	SMALLTALK("SMALLTALK"), //
	SCRIPTINTENTION("SCRIPTINTENTION")
	;

	private final Serializable entityId;

	private TypeTopicEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.chatbot.commons.domain.topic.TypeTopic> getEntityUID() {
		return UID.of(io.vertigo.chatbot.commons.domain.topic.TypeTopic.class, entityId);
	}

}
