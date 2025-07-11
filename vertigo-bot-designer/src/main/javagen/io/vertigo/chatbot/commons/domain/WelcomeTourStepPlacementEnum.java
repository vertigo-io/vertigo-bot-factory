package io.vertigo.chatbot.commons.domain;

import java.io.Serializable;

import io.vertigo.datamodel.data.model.MasterDataEnum;
import io.vertigo.datamodel.data.model.UID;

public enum WelcomeTourStepPlacementEnum implements MasterDataEnum<io.vertigo.chatbot.commons.domain.WelcomeTourStepPlacement> {

	AUTO("AUTO"), //
	RIGHT("RIGHT"), //
	LEFT("LEFT"), //
	TOP("TOP"), //
	BOTTOM("BOTTOM")
	;

	private final Serializable entityId;

	private WelcomeTourStepPlacementEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.chatbot.commons.domain.WelcomeTourStepPlacement> getEntityUID() {
		return UID.of(io.vertigo.chatbot.commons.domain.WelcomeTourStepPlacement.class, entityId);
	}

}
