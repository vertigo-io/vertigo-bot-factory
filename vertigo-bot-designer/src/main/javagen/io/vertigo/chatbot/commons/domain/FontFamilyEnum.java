package io.vertigo.chatbot.commons.domain;

import java.io.Serializable;

import io.vertigo.datamodel.data.model.MasterDataEnum;
import io.vertigo.datamodel.data.model.UID;

public enum FontFamilyEnum implements MasterDataEnum<io.vertigo.chatbot.commons.domain.FontFamily> {

	ARIAL("ARIAL"), //
	VERDANA("VERDANA"), //
	HELVETICA("HELVETICA"), //
	TAHOMA("TAHOMA"), //
	TREBUCHET("TREBUCHET"), //
	TIME_NEW_ROMAN("TIME_NEW_ROMAN"), //
	GEORGIA("GEORGIA"), //
	GARAMOND("GARAMOND"), //
	COURIER("COURIER"), //
	BRUSH("BRUSH")
	;

	private final Serializable entityId;

	private FontFamilyEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.chatbot.commons.domain.FontFamily> getEntityUID() {
		return UID.of(io.vertigo.chatbot.commons.domain.FontFamily.class, entityId);
	}

}
