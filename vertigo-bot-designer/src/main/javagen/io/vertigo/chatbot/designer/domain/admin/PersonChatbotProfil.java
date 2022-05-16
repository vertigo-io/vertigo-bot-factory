package io.vertigo.chatbot.designer.domain.admin;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.stereotype.Field;
import io.vertigo.datamodel.structure.util.DtObjectUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class PersonChatbotProfil implements DtObject {
	private static final long serialVersionUID = 1L;

	private Long botId;
	private Long perId;
	private Long chpId;
	private String name;
	private String profilLabel;
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Chatbot selection'.
	 * @return Long botId
	 */
	@Field(smartType = "STyId", label = "Chatbot selection")
	public Long getBotId() {
		return botId;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Chatbot selection'.
	 * @param botId Long
	 */
	public void setBotId(final Long botId) {
		this.botId = botId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Person id'.
	 * @return Long perId
	 */
	@Field(smartType = "STyId", label = "Person id")
	public Long getPerId() {
		return perId;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Person id'.
	 * @param perId Long
	 */
	public void setPerId(final Long perId) {
		this.perId = perId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'ProfilPerPerson id'.
	 * @return Long chpId
	 */
	@Field(smartType = "STyId", label = "ProfilPerPerson id")
	public Long getChpId() {
		return chpId;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'ProfilPerPerson id'.
	 * @param chpId Long
	 */
	public void setChpId(final Long chpId) {
		this.chpId = chpId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Person name'.
	 * @return String name
	 */
	@Field(smartType = "STyLabel", label = "Person name")
	public String getName() {
		return name;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Person name'.
	 * @param name String
	 */
	public void setName(final String name) {
		this.name = name;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Profil label'.
	 * @return String profilLabel
	 */
	@Field(smartType = "STyLabel", label = "Profil label")
	public String getProfilLabel() {
		return profilLabel;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Profil label'.
	 * @param profilLabel String
	 */
	public void setProfilLabel(final String profilLabel) {
		this.profilLabel = profilLabel;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}
