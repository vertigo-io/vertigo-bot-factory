package io.vertigo.chatbot.designer.domain.analytics;

import java.io.Serializable;

import io.vertigo.datamodel.data.model.MasterDataEnum;
import io.vertigo.datamodel.data.model.UID;

public enum RatingOptionEnum implements MasterDataEnum<io.vertigo.chatbot.designer.domain.analytics.RatingOption> {

	ONE_STAR("1"), //
	TWO_STARS("2"), //
	THREE_STARS("3"), //
	FOUR_STARS("4"), //
	FIVE_STARS("5")
	;

	private final Serializable entityId;

	private RatingOptionEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.chatbot.designer.domain.analytics.RatingOption> getEntityUID() {
		return UID.of(io.vertigo.chatbot.designer.domain.analytics.RatingOption.class, entityId);
	}

}
