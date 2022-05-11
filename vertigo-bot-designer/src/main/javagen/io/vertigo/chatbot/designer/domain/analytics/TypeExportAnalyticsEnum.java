package io.vertigo.chatbot.designer.domain.analytics;

import java.io.Serializable;

import io.vertigo.datamodel.structure.model.MasterDataEnum;
import io.vertigo.datamodel.structure.model.UID;

public enum TypeExportAnalyticsEnum implements MasterDataEnum<io.vertigo.chatbot.designer.domain.analytics.TypeExportAnalytics> {

	UNKNOWN_MESSAGES("UNKNOWN_MESSAGES"), //
	SESSIONS("SESSIONS"), //
	CONVERSATIONS("CONVERSATIONS"), //
	CATEGORIES("CATEGORIES")
	;

	private final Serializable entityId;

	private TypeExportAnalyticsEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.chatbot.designer.domain.analytics.TypeExportAnalytics> getEntityUID() {
		return UID.of(io.vertigo.chatbot.designer.domain.analytics.TypeExportAnalytics.class, entityId);
	}

}
