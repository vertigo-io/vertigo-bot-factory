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
public final class SmallTalkIhm implements DataObject {
	private static final long serialVersionUID = 1L;

	private Long topId;
	private String title;
	private Long smtId;
	private Boolean isEnabled;
	private String catLabel;
	
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
	 * Récupère la valeur de la propriété 'SmallTalk label'.
	 * @return String title
	 */
	@Field(smartType = "STyLabel", label = "SmallTalk label")
	public String getTitle() {
		return title;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'SmallTalk label'.
	 * @param title String
	 */
	public void setTitle(final String title) {
		this.title = title;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'SmallTalk id'.
	 * @return Long smtId
	 */
	@Field(smartType = "STyId", label = "SmallTalk id")
	public Long getSmtId() {
		return smtId;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'SmallTalk id'.
	 * @param smtId Long
	 */
	public void setSmtId(final Long smtId) {
		this.smtId = smtId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Enabled'.
	 * @return Boolean isEnabled
	 */
	@Field(smartType = "STyYesNo", label = "Enabled")
	public Boolean getIsEnabled() {
		return isEnabled;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Enabled'.
	 * @param isEnabled Boolean
	 */
	public void setIsEnabled(final Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Category'.
	 * @return String catLabel
	 */
	@Field(smartType = "STyLabel", label = "Category")
	public String getCatLabel() {
		return catLabel;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Category'.
	 * @param catLabel String
	 */
	public void setCatLabel(final String catLabel) {
		this.catLabel = catLabel;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DataModelUtil.toString(this);
	}
}
