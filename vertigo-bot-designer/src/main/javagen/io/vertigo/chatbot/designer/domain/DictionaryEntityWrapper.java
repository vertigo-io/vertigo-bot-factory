package io.vertigo.chatbot.designer.domain;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.data.model.DataObject;
import io.vertigo.datamodel.data.stereotype.Field;
import io.vertigo.datamodel.data.util.DataModelUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class DictionaryEntityWrapper implements DataObject {
	private static final long serialVersionUID = 1L;

	private Long dicEntId;
	private String dictionaryEntityLabel;
	private String synonymsList;
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Dictionary Entity id'.
	 * @return Long dicEntId
	 */
	@Field(smartType = "STyId", label = "Dictionary Entity id")
	public Long getDicEntId() {
		return dicEntId;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Dictionary Entity id'.
	 * @param dicEntId Long
	 */
	public void setDicEntId(final Long dicEntId) {
		this.dicEntId = dicEntId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Dictionary Entity Label'.
	 * @return String dictionaryEntityLabel
	 */
	@Field(smartType = "STyLabel", label = "Dictionary Entity Label")
	public String getDictionaryEntityLabel() {
		return dictionaryEntityLabel;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Dictionary Entity Label'.
	 * @param dictionaryEntityLabel String
	 */
	public void setDictionaryEntityLabel(final String dictionaryEntityLabel) {
		this.dictionaryEntityLabel = dictionaryEntityLabel;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Synonyms list'.
	 * @return String synonymsList
	 */
	@Field(smartType = "STyLabel", label = "Synonyms list")
	public String getSynonymsList() {
		return synonymsList;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Synonyms list'.
	 * @param synonymsList String
	 */
	public void setSynonymsList(final String synonymsList) {
		this.synonymsList = synonymsList;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DataModelUtil.toString(this);
	}
}
