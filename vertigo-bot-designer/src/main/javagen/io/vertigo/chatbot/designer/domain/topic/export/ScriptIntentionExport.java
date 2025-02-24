package io.vertigo.chatbot.designer.domain.topic.export;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.data.model.DataObject;
import io.vertigo.datamodel.data.stereotype.Field;
import io.vertigo.datamodel.data.util.DataModelUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class ScriptIntentionExport implements DataObject {
	private static final long serialVersionUID = 1L;

	private Long topId;
	private String bt;
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Topic id'.
	 * @return Long topId
	 */
	@Field(smartType = "STyId", label = "Topic id")
	public Long getTopId() {
		return topId;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Topic id'.
	 * @param topId Long
	 */
	public void setTopId(final Long topId) {
		this.topId = topId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Bt'.
	 * @return String bt
	 */
	@Field(smartType = "STyText", label = "Bt")
	public String getBt() {
		return bt;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Bt'.
	 * @param bt String
	 */
	public void setBt(final String bt) {
		this.bt = bt;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DataModelUtil.toString(this);
	}
}
