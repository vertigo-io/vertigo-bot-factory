package io.vertigo.chatbot.commons.domain;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.stereotype.Field;
import io.vertigo.datamodel.structure.util.DtObjectUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class UnknownSentenceToUpdateIhm implements DtObject {
	private static final long serialVersionUID = 1L;

	private Long unkSeId;
	private String text;
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Unknown sentence Id'.
	 * @return Long unkSeId
	 */
	@Field(smartType = "STyId", label = "Unknown sentence Id")
	public Long getUnkSeId() {
		return unkSeId;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Unknown sentence Id'.
	 * @param unkSeId Long
	 */
	public void setUnkSeId(final Long unkSeId) {
		this.unkSeId = unkSeId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'User text'.
	 * @return String text
	 */
	@Field(smartType = "STyLabel", label = "User text")
	public String getText() {
		return text;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'User text'.
	 * @param text String
	 */
	public void setText(final String text) {
		this.text = text;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}
