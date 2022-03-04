package io.vertigo.chatbot.designer.domain.topic.export;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.stereotype.Field;
import io.vertigo.datamodel.structure.util.DtObjectUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class ResponseButtonUrlExport implements DtObject {
	private static final long serialVersionUID = 1L;

	private Long topId;
	private String topCode;
	private String text;
	private String url;
	private Boolean newTab;
	
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
	 * Récupère la valeur de la propriété 'Topic code'.
	 * @return String topCode
	 */
	@Field(smartType = "STyText", label = "Topic code")
	public String getTopCode() {
		return topCode;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Topic code'.
	 * @param topCode String
	 */
	public void setTopCode(final String topCode) {
		this.topCode = topCode;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Button text'.
	 * @return String text
	 */
	@Field(smartType = "STyText", label = "Button text")
	public String getText() {
		return text;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Button text'.
	 * @param text String
	 */
	public void setText(final String text) {
		this.text = text;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Button url'.
	 * @return String url
	 */
	@Field(smartType = "STyUrl", label = "Button url")
	public String getUrl() {
		return url;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Button url'.
	 * @param url String
	 */
	public void setUrl(final String url) {
		this.url = url;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'New tab'.
	 * @return Boolean newTab
	 */
	@Field(smartType = "STyYesNo", label = "New tab")
	public Boolean getNewTab() {
		return newTab;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'New tab'.
	 * @param newTab Boolean
	 */
	public void setNewTab(final Boolean newTab) {
		this.newTab = newTab;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}