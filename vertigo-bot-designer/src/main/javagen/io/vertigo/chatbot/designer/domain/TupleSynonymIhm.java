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
public final class TupleSynonymIhm implements DataObject {
	private static final long serialVersionUID = 1L;

	private String word;
	private String synonymLabel;
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Word'.
	 * @return String word
	 */
	@Field(smartType = "STyLabel", label = "Word")
	public String getWord() {
		return word;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Word'.
	 * @param word String
	 */
	public void setWord(final String word) {
		this.word = word;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Synonym label'.
	 * @return String synonymLabel
	 */
	@Field(smartType = "STyLabel", label = "Synonym label")
	public String getSynonymLabel() {
		return synonymLabel;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Synonym label'.
	 * @param synonymLabel String
	 */
	public void setSynonymLabel(final String synonymLabel) {
		this.synonymLabel = synonymLabel;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DataModelUtil.toString(this);
	}
}
