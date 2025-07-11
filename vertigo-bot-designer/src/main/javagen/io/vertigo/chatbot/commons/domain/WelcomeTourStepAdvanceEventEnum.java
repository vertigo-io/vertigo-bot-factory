package io.vertigo.chatbot.commons.domain;

import java.io.Serializable;

import io.vertigo.datamodel.data.model.MasterDataEnum;
import io.vertigo.datamodel.data.model.UID;

public enum WelcomeTourStepAdvanceEventEnum implements MasterDataEnum<io.vertigo.chatbot.commons.domain.WelcomeTourStepAdvanceEvent> {

	CLICK("CLICK"), //
	FOCUS("FOCUS"), //
	SUBMIT("SUBMIT"), //
	OVER("OVER")
	;

	private final Serializable entityId;

	private WelcomeTourStepAdvanceEventEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.chatbot.commons.domain.WelcomeTourStepAdvanceEvent> getEntityUID() {
		return UID.of(io.vertigo.chatbot.commons.domain.WelcomeTourStepAdvanceEvent.class, entityId);
	}

}
