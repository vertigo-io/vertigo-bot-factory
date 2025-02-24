package io.vertigo.chatbot.commons.domain.topic;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.data.model.DataObject;
import io.vertigo.datamodel.data.stereotype.Field;
import io.vertigo.datamodel.data.util.DataModelUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class TopicCriteria implements DataObject {
	private static final long serialVersionUID = 1L;

	private Long catId;
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Category selection'.
	 * @return Long catId
	 */
	@Field(smartType = "STyId", label = "Category selection")
	public Long getCatId() {
		return catId;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Category selection'.
	 * @param catId Long
	 */
	public void setCatId(final Long catId) {
		this.catId = catId;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DataModelUtil.toString(this);
	}
}
