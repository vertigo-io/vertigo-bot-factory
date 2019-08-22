package io.vertigo.chatbot.factory.domain;

import io.vertigo.dynamo.domain.model.Entity;
import io.vertigo.dynamo.domain.model.ListVAccessor;
import io.vertigo.dynamo.domain.model.UID;
import io.vertigo.dynamo.domain.stereotype.Field;
import io.vertigo.dynamo.domain.util.DtObjectUtil;
import io.vertigo.lang.Generated;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class Intent implements Entity {
	private static final long serialVersionUID = 1L;

	private Long intId;
	private String title;

	@io.vertigo.dynamo.domain.stereotype.Association(
			name = "AIntentIntentText",
			fkFieldName = "intId",
			primaryDtDefinitionName = "DtIntent",
			primaryIsNavigable = false,
			primaryRole = "Intent",
			primaryLabel = "Intent",
			primaryMultiplicity = "1..1",
			foreignDtDefinitionName = "DtIntentText",
			foreignIsNavigable = true,
			foreignRole = "IntentText",
			foreignLabel = "IntentText",
			foreignMultiplicity = "0..*")
	private final ListVAccessor<io.vertigo.chatbot.factory.domain.IntentText> intentTextAccessor = new ListVAccessor<>(this, "AIntentIntentText", "IntentText");

	/** {@inheritDoc} */
	@Override
	public UID<Intent> getUID() {
		return UID.of(this);
	}
	
	/**
	 * Champ : ID.
	 * Récupère la valeur de la propriété 'ID'.
	 * @return Long intId <b>Obligatoire</b>
	 */
	@Field(domain = "DoId", type = "ID", required = true, label = "ID")
	public Long getIntId() {
		return intId;
	}

	/**
	 * Champ : ID.
	 * Définit la valeur de la propriété 'ID'.
	 * @param intId Long <b>Obligatoire</b>
	 */
	public void setIntId(final Long intId) {
		this.intId = intId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Titre'.
	 * @return String title <b>Obligatoire</b>
	 */
	@Field(domain = "DoLabel", required = true, label = "Titre")
	public String getTitle() {
		return title;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Titre'.
	 * @param title String <b>Obligatoire</b>
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * Association : IntentText.
	 * @return l'accesseur vers la propriété 'IntentText'
	 */
	public ListVAccessor<io.vertigo.chatbot.factory.domain.IntentText> intentText() {
		return intentTextAccessor;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}