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
public final class DocumentaryResourceExport implements DtObject {
	private static final long serialVersionUID = 1L;

	private String title;
	private String dreTypeCd;
	private String url;
	private String fileName;
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Title'.
	 * @return String title <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Title")
	public String getTitle() {
		return title;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Title'.
	 * @param title String <b>Obligatoire</b>
	 */
	public void setTitle(final String title) {
		this.title = title;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Resource Type'.
	 * @return String dreTypeCd <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Resource Type")
	public String getDreTypeCd() {
		return dreTypeCd;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Resource Type'.
	 * @param dreTypeCd String <b>Obligatoire</b>
	 */
	public void setDreTypeCd(final String dreTypeCd) {
		this.dreTypeCd = dreTypeCd;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Url'.
	 * @return String url
	 */
	@Field(smartType = "STyText", label = "Url")
	public String getUrl() {
		return url;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Url'.
	 * @param url String
	 */
	public void setUrl(final String url) {
		this.url = url;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'File name'.
	 * @return String fileName
	 */
	@Field(smartType = "STyLabel", label = "File name")
	public String getFileName() {
		return fileName;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'File name'.
	 * @param fileName String
	 */
	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}
