package io.vertigo.chatbot.designer.domain;

import java.io.Serializable;

import io.vertigo.datamodel.structure.model.MasterDataEnum;
import io.vertigo.datamodel.structure.model.UID;

public enum HistoryActionEnum implements MasterDataEnum<io.vertigo.chatbot.designer.domain.HistoryAction> {

	ADDED("ADDED"), //
	DELETED("DELETED"), //
	UPDATED("UPDATED")
	;

	private final Serializable entityId;

	private HistoryActionEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.chatbot.designer.domain.HistoryAction> getEntityUID() {
		return UID.of(io.vertigo.chatbot.designer.domain.HistoryAction.class, entityId);
	}

}
