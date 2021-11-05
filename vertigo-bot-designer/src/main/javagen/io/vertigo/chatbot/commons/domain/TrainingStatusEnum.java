package io.vertigo.chatbot.commons.domain;

import java.io.Serializable;

import io.vertigo.datamodel.structure.model.MasterDataEnum;
import io.vertigo.datamodel.structure.model.UID;

public enum TrainingStatusEnum implements MasterDataEnum<io.vertigo.chatbot.commons.domain.TrainingStatus> {

	OK("OK"), //
	KO("KO"), //
	TRAINING("TRAINING")
	;

	private final Serializable entityId;

	private TrainingStatusEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.chatbot.commons.domain.TrainingStatus> getEntityUID() {
		return UID.of(io.vertigo.chatbot.commons.domain.TrainingStatus.class, entityId);
	}

}
