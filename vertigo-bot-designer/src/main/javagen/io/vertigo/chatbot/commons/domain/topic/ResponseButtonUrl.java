package io.vertigo.chatbot.commons.domain.topic;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.data.model.Entity;
import io.vertigo.datamodel.data.model.UID;
import io.vertigo.datastore.impl.entitystore.StoreVAccessor;
import io.vertigo.datamodel.data.stereotype.Field;
import io.vertigo.datamodel.data.util.DataModelUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class ResponseButtonUrl implements Entity {
	private static final long serialVersionUID = 1L;

	private Long btnId;
	private String text;
	private String url;
	private Boolean newTab;

	@io.vertigo.datamodel.data.stereotype.Association(
			name = "ASmallTalkResponseButtonsUrl",
			fkFieldName = "smtId",
			primaryDtDefinitionName = "DtSmallTalk",
			primaryIsNavigable = true,
			primaryRole = "SmallTalk",
			primaryLabel = "SmallTalk",
			primaryMultiplicity = "0..1",
			foreignDtDefinitionName = "DtResponseButtonUrl",
			foreignIsNavigable = false,
			foreignRole = "ResponseButtonUrl",
			foreignLabel = "ResponseButtonUrl",
			foreignMultiplicity = "0..*")
	private final StoreVAccessor<io.vertigo.chatbot.commons.domain.topic.SmallTalk> smtIdAccessor = new StoreVAccessor<>(io.vertigo.chatbot.commons.domain.topic.SmallTalk.class, "SmallTalk");

	/** {@inheritDoc} */
	@Override
	public UID<ResponseButtonUrl> getUID() {
		return UID.of(this);
	}
	
	/**
	 * Champ : ID.
	 * Récupère la valeur de la propriété 'ID'.
	 * @return Long btnId <b>Obligatoire</b>
	 */
	@Field(smartType = "STyId", type = "ID", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "ID")
	public Long getBtnId() {
		return btnId;
	}

	/**
	 * Champ : ID.
	 * Définit la valeur de la propriété 'ID'.
	 * @param btnId Long <b>Obligatoire</b>
	 */
	public void setBtnId(final Long btnId) {
		this.btnId = btnId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Text'.
	 * @return String text <b>Obligatoire</b>
	 */
	@Field(smartType = "STyText", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Text")
	public String getText() {
		return text;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Text'.
	 * @param text String <b>Obligatoire</b>
	 */
	public void setText(final String text) {
		this.text = text;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'URL'.
	 * @return String url <b>Obligatoire</b>
	 */
	@Field(smartType = "STyUrl", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "URL")
	public String getUrl() {
		return url;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'URL'.
	 * @param url String <b>Obligatoire</b>
	 */
	public void setUrl(final String url) {
		this.url = url;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'New tab'.
	 * @return Boolean newTab <b>Obligatoire</b>
	 */
	@Field(smartType = "STyYesNo", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "New tab")
	public Boolean getNewTab() {
		return newTab;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'New tab'.
	 * @param newTab Boolean <b>Obligatoire</b>
	 */
	public void setNewTab(final Boolean newTab) {
		this.newTab = newTab;
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'SmallTalk'.
	 * @return Long smtId
	 */
	@io.vertigo.datamodel.data.stereotype.ForeignKey(smartType = "STyId", label = "SmallTalk", fkDefinition = "DtSmallTalk" )
	public Long getSmtId() {
		return (Long) smtIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'SmallTalk'.
	 * @param smtId Long
	 */
	public void setSmtId(final Long smtId) {
		smtIdAccessor.setId(smtId);
	}

 	/**
	 * Association : SmallTalk.
	 * @return l'accesseur vers la propriété 'SmallTalk'
	 */
	public StoreVAccessor<io.vertigo.chatbot.commons.domain.topic.SmallTalk> smallTalk() {
		return smtIdAccessor;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DataModelUtil.toString(this);
	}
}
