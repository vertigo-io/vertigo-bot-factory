package io.vertigo.chatbot.designer.domain.admin;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.structure.model.DtStaticMasterData;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datamodel.structure.stereotype.Field;
import io.vertigo.datamodel.structure.util.DtObjectUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class ChatbotProfiles implements DtStaticMasterData {
	private static final long serialVersionUID = 1L;

	private String chpCd;
	private String label;
	private Long sortOrder;

	/** {@inheritDoc} */
	@Override
	public UID<ChatbotProfiles> getUID() {
		return UID.of(this);
	}
	
	/**
	 * Champ : ID.
	 * Récupère la valeur de la propriété 'ID'.
	 * @return String chpCd <b>Obligatoire</b>
	 */
	@Field(smartType = "STyCode", type = "ID", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "ID")
	public String getChpCd() {
		return chpCd;
	}

	/**
	 * Champ : ID.
	 * Définit la valeur de la propriété 'ID'.
	 * @param chpCd String <b>Obligatoire</b>
	 */
	public void setChpCd(final String chpCd) {
		this.chpCd = chpCd;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Title'.
	 * @return String label <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Title")
	@io.vertigo.datamodel.structure.stereotype.DisplayField
	public String getLabel() {
		return label;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Title'.
	 * @param label String <b>Obligatoire</b>
	 */
	public void setLabel(final String label) {
		this.label = label;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Order'.
	 * @return Long sortOrder <b>Obligatoire</b>
	 */
	@Field(smartType = "STyNumber", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Order")
	@io.vertigo.datamodel.structure.stereotype.SortField
	public Long getSortOrder() {
		return sortOrder;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Order'.
	 * @param sortOrder Long <b>Obligatoire</b>
	 */
	public void setSortOrder(final Long sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}
