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
public final class Action implements Entity {
	private static final long serialVersionUID = 1L;

	private Long actId;
	private String title;

	@io.vertigo.dynamo.domain.stereotype.Association(
			name = "AActionChatbot",
			fkFieldName = "botId",
			primaryDtDefinitionName = "DtChatbot",
			primaryIsNavigable = true,
			primaryRole = "Chatbot",
			primaryLabel = "Chatbot",
			primaryMultiplicity = "1..1",
			foreignDtDefinitionName = "DtAction",
			foreignIsNavigable = false,
			foreignRole = "Action",
			foreignLabel = "Action",
			foreignMultiplicity = "0..*")
	private final VAccessor<io.vertigo.chatbot.commons.domain.Chatbot> botIdAccessor = new VAccessor<>(io.vertigo.chatbot.commons.domain.Chatbot.class, "Chatbot");

	@io.vertigo.dynamo.domain.stereotype.Association(
			name = "AActionUtterText",
			fkFieldName = "actId",
			primaryDtDefinitionName = "DtAction",
			primaryIsNavigable = false,
			primaryRole = "Action",
			primaryLabel = "Action",
			primaryMultiplicity = "0..1",
			foreignDtDefinitionName = "DtUtterText",
			foreignIsNavigable = true,
			foreignRole = "UtterText",
			foreignLabel = "UtterText",
			foreignMultiplicity = "0..*")
	private final ListVAccessor<io.vertigo.chatbot.commons.domain.UtterText> utterTextAccessor = new ListVAccessor<>(this, "AActionUtterText", "UtterText");

	/** {@inheritDoc} */
	@Override
	public UID<Action> getUID() {
		return UID.of(this);
	}
	
	/**
	 * Champ : ID.
	 * Récupère la valeur de la propriété 'ID'.
	 * @return Long actId <b>Obligatoire</b>
	 */
	@Field(domain = "DoId", type = "ID", required = true, label = "ID")
	public Long getActId() {
		return actId;
	}

	/**
	 * Champ : ID.
	 * Définit la valeur de la propriété 'ID'.
	 * @param actId Long <b>Obligatoire</b>
	 */
	public void setActId(final Long actId) {
		this.actId = actId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Text'.
	 * @return String title <b>Obligatoire</b>
	 */
	@Field(domain = "DoLabel", required = true, label = "Text")
	public String getTitle() {
		return title;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Text'.
	 * @param title String <b>Obligatoire</b>
	 */
	public void setTitle(final String title) {
		this.title = title;
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