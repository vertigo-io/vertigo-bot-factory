package io.vertigo.chatbot.designer.domain;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.stereotype.Field;
import io.vertigo.datamodel.structure.util.DtObjectUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class ContextEnvironmentValueIhm implements DtObject {
	private static final long serialVersionUID = 1L;

	private Long cenvalId;
	private String value;
	private Long cvaId;
	private String label;
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Context environment value id'.
	 * @return Long cenvalId <b>Obligatoire</b>
	 */
	@Field(smartType = "STyId", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Context environment value id")
	public Long getCenvalId() {
		return cenvalId;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Context environment value id'.
	 * @param cenvalId Long <b>Obligatoire</b>
	 */
	public void setCenvalId(final Long cenvalId) {
		this.cenvalId = cenvalId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Value'.
	 * @return String value
	 */
	@Field(smartType = "STyLabel", label = "Value")
	public String getValue() {
		return value;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Value'.
	 * @param value String
	 */
	public void setValue(final String value) {
		this.value = value;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Context value id'.
	 * @return Long cvaId <b>Obligatoire</b>
	 */
	@Field(smartType = "STyId", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Context value id")
	public Long getCvaId() {
		return cvaId;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Context value id'.
	 * @param cvaId Long <b>Obligatoire</b>
	 */
	public void setCvaId(final Long cvaId) {
		this.cvaId = cvaId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Context value label'.
	 * @return String label <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Context value label")
	public String getLabel() {
		return label;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Context value label'.
	 * @param label String <b>Obligatoire</b>
	 */
	public void setLabel(final String label) {
		this.label = label;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}
