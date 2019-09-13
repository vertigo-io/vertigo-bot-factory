package io.vertigo.chatbot.commons.domain;

import io.vertigo.dynamo.domain.model.Entity;
import io.vertigo.dynamo.domain.model.ListVAccessor;
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
public final class Intent implements Entity {
	private static final long serialVersionUID = 1L;

	private Long intId;
	private String title;
	private String description;
	private Boolean isSmallTalk;
	private Boolean isEnabled;

	@io.vertigo.dynamo.domain.stereotype.Association(
			name = "AIntentChatbot",
			fkFieldName = "botId",
			primaryDtDefinitionName = "DtChatbot",
			primaryIsNavigable = true,
			primaryRole = "Chatbot",
			primaryLabel = "Chatbot",
			primaryMultiplicity = "1..1",
			foreignDtDefinitionName = "DtIntent",
			foreignIsNavigable = false,
			foreignRole = "Intent",
			foreignLabel = "Intent",
			foreignMultiplicity = "0..*")
	private final VAccessor<io.vertigo.chatbot.commons.domain.Chatbot> botIdAccessor = new VAccessor<>(io.vertigo.chatbot.commons.domain.Chatbot.class, "Chatbot");

	@io.vertigo.dynamo.domain.stereotype.Association(
			name = "AIntentIntentTrainingSentence",
			fkFieldName = "intId",
			primaryDtDefinitionName = "DtIntent",
			primaryIsNavigable = false,
			primaryRole = "Intent",
			primaryLabel = "Intent",
			primaryMultiplicity = "1..1",
			foreignDtDefinitionName = "DtIntentTrainingSentence",
			foreignIsNavigable = true,
			foreignRole = "IntentTrainingSentence",
			foreignLabel = "IntentTrainingSentence",
			foreignMultiplicity = "0..*")
	private final ListVAccessor<io.vertigo.chatbot.commons.domain.IntentTrainingSentence> intentTrainingSentenceAccessor = new ListVAccessor<>(this, "AIntentIntentTrainingSentence", "IntentTrainingSentence");

	@io.vertigo.dynamo.domain.stereotype.Association(
			name = "AIntentUtterText",
			fkFieldName = "intId",
			primaryDtDefinitionName = "DtIntent",
			primaryIsNavigable = false,
			primaryRole = "Intent",
			primaryLabel = "Intent",
			primaryMultiplicity = "0..1",
			foreignDtDefinitionName = "DtUtterText",
			foreignIsNavigable = true,
			foreignRole = "UtterText",
			foreignLabel = "UtterText",
			foreignMultiplicity = "0..*")
	private final ListVAccessor<io.vertigo.chatbot.commons.domain.UtterText> utterTextAccessor = new ListVAccessor<>(this, "AIntentUtterText", "UtterText");

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
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Description'.
	 * @return String description
	 */
	@Field(domain = "DoLabel", label = "Description")
	public String getDescription() {
		return description;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Description'.
	 * @param description String
	 */
	public void setDescription(final String description) {
		this.description = description;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'SmallTalk'.
	 * @return Boolean isSmallTalk <b>Obligatoire</b>
	 */
	@Field(domain = "DoYesNo", required = true, label = "SmallTalk")
	public Boolean getIsSmallTalk() {
		return isSmallTalk;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'SmallTalk'.
	 * @param isSmallTalk Boolean <b>Obligatoire</b>
	 */
	public void setIsSmallTalk(final Boolean isSmallTalk) {
		this.isSmallTalk = isSmallTalk;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Enabled'.
	 * @return Boolean isEnabled <b>Obligatoire</b>
	 */
	@Field(domain = "DoYesNo", required = true, label = "Enabled")
	public Boolean getIsEnabled() {
		return isEnabled;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Enabled'.
	 * @param isEnabled Boolean <b>Obligatoire</b>
	 */
	public void setIsEnabled(final Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Chatbot'.
	 * @return Long botId <b>Obligatoire</b>
	 */
	@Field(domain = "DoId", type = "FOREIGN_KEY", required = true, label = "Chatbot")
	public Long getBotId() {
		return (Long) botIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Chatbot'.
	 * @param botId Long <b>Obligatoire</b>
	 */
	public void setBotId(final Long botId) {
		botIdAccessor.setId(botId);
	}

 	/**
	 * Association : Chatbot.
	 * @return l'accesseur vers la propriété 'Chatbot'
	 */
	public VAccessor<io.vertigo.chatbot.commons.domain.Chatbot> chatbot() {
		return botIdAccessor;
	}

	/**
	 * Association : IntentTrainingSentence.
	 * @return l'accesseur vers la propriété 'IntentTrainingSentence'
	 */
	public ListVAccessor<io.vertigo.chatbot.commons.domain.IntentTrainingSentence> intentTrainingSentence() {
		return intentTrainingSentenceAccessor;
	}

	/**
	 * Association : UtterText.
	 * @return l'accesseur vers la propriété 'UtterText'
	 */
	public ListVAccessor<io.vertigo.chatbot.commons.domain.UtterText> utterText() {
		return utterTextAccessor;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}