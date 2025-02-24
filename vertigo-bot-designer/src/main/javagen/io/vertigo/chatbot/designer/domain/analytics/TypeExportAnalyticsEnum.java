package io.vertigo.chatbot.designer.domain.analytics;

import java.io.Serializable;

import io.vertigo.datamodel.data.model.MasterDataEnum;
import io.vertigo.datamodel.data.model.UID;

public enum TypeExportAnalyticsEnum implements MasterDataEnum<io.vertigo.chatbot.designer.domain.analytics.TypeExportAnalytics> {

	UNKNOWN_MESSAGES("UNKNOWN_MESSAGES"), //
	USER_ACTIONS_CONVERSATIONS("USER_ACTIONS_CONVERSATIONS"), //
	CONVERSATIONS("CONVERSATIONS"), //
	CATEGORIES("CATEGORIES"), //
	TOPIC_USAGE("TOPIC_USAGE")
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
