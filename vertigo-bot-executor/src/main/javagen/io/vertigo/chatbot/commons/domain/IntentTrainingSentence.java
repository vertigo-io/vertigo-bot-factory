package io.vertigo.chatbot.commons.domain;

import io.vertigo.dynamo.domain.model.Entity;
import io.vertigo.dynamo.domain.model.UID;
import io.vertigo.dynamo.domain.model.VAccessor;
import io.vertigo.dynamo.domain.stereotype.Field;
import io.vertigo.dynamo.domain.util.DtObjectUtil;
import io.vertigo.lang.Generated;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class IntentTrainingSentence implements Entity {
	private static final long serialVersionUID = 1L;

	private Long itsId;
	private String text;

	@io.vertigo.dynamo.domain.stereotype.Association(
			name = "AIntentIntentTrainingSentence",
			fkFieldName = "intId",
			primaryDtDefinitionName = "DtIntent",
			primaryIsNavigable = false,
			primaryRole = "Intent",
			primaryLabel = "SmallTalkIntent",
			primaryMultiplicity = "1..1",
			foreignDtDefinitionName = "DtIntentTrainingSentence",
			foreignIsNavigable = true,
			foreignRole = "IntentTrainingSentence",
			foreignLabel = "IntentTrainingSentence",
			foreignMultiplicity = "0..*")
	private final VAccessor<io.vertigo.chatbot.commons.domain.Intent> intIdAccessor = new VAccessor<>(io.vertigo.chatbot.commons.domain.Intent.class, "Intent");

	/** {@inheritDoc} */
	@Override
	public UID<IntentTrainingSentence> getUID() {
		return UID.of(this);
	}
	
	/**
	 * Champ : ID.
	 * Récupère la valeur de la propriété 'ID'.
	 * @return Long itsId <b>Obligatoire</b>
	 */
	@Field(domain = "DoId", type = "ID", required = true, label = "ID")
	public Long getItsId() {
		return itsId;
	}

	/**
	 * Champ : ID.
	 * Définit la valeur de la propriété 'ID'.
	 * @param itsId Long <b>Obligatoire</b>
	 */
	public void setItsId(final Long itsId) {
		this.itsId = itsId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Text'.
	 * @return String text <b>Obligatoire</b>
	 */
	@Field(domain = "DoLabel", required = true, label = "Text")
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
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'SmallTalkIntent'.
	 * @return Long intId <b>Obligatoire</b>
	 */
	@Field(domain = "DoId", type = "FOREIGN_KEY", required = true, label = "SmallTalkIntent")
	public Long getIntId() {
		return (Long) intIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'SmallTalkIntent'.
	 * @param intId Long <b>Obligatoire</b>
	 */
	public void setIntId(final Long intId) {
		intIdAccessor.setId(intId);
	}

 	/**
	 * Association : SmallTalkIntent.
	 * @return l'accesseur vers la propriété 'SmallTalkIntent'
	 */
	public VAccessor<io.vertigo.chatbot.commons.domain.Intent> intent() {
		return intIdAccessor;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}
