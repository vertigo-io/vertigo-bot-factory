package io.vertigo.chatbot.designer.domain.topic.export;

import java.io.Serializable;

import io.vertigo.datamodel.structure.model.MasterDataEnum;
import io.vertigo.datamodel.structure.model.UID;

public enum TypeBotExportEnum implements MasterDataEnum<io.vertigo.chatbot.designer.domain.topic.export.TypeBotExport> {

	CATEGORIES("CATEGORIES"), //
	TOPICS("TOPICS"), //
	DICTIONARY("DICTIONARY")
	;

	private final Serializable entityId;

	private TypeBotExportEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.chatbot.designer.domain.topic.export.TypeBotExport> getEntityUID() {
		return UID.of(io.vertigo.chatbot.designer.domain.topic.export.TypeBotExport.class, entityId);
	}

}
