package io.vertigo.chatbot.commons.domain;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.structure.model.Entity;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datastore.impl.entitystore.StoreVAccessor;
import io.vertigo.datamodel.structure.stereotype.Field;
import io.vertigo.datamodel.structure.util.DtObjectUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class ResponseButton implements Entity {
	private static final long serialVersionUID = 1L;

	private Long btnId;
	private String text;

	@io.vertigo.datamodel.structure.stereotype.Association(
			name = "ASmallTalkResponseButtons",
			fkFieldName = "smtId",
			primaryDtDefinitionName = "DtSmallTalk",
			primaryIsNavigable = true,
			primaryRole = "SmallTalk",
			primaryLabel = "SmallTalk",
			primaryMultiplicity = "0..1",
			foreignDtDefinitionName = "DtResponseButton",
			foreignIsNavigable = false,
			foreignRole = "ResponseButton",
			foreignLabel = "ResponseButton",
			foreignMultiplicity = "0..*")
	private final StoreVAccessor<io.vertigo.chatbot.commons.domain.SmallTalk> smtIdAccessor = new StoreVAccessor<>(io.vertigo.chatbot.commons.domain.SmallTalk.class, "SmallTalk");

	@io.vertigo.datamodel.structure.stereotype.Association(
			name = "AResponseButtonSmallTalkResponse",
			fkFieldName = "smtIdResponse",
			primaryDtDefinitionName = "DtSmallTalk",
			primaryIsNavigable = true,
			primaryRole = "SmallTalkResponse",
			primaryLabel = "SmallTalkResponse",
			primaryMultiplicity = "1..1",
			foreignDtDefinitionName = "DtResponseButton",
			foreignIsNavigable = false,
			foreignRole = "ResponseButton",
			foreignLabel = "ResponseButton",
			foreignMultiplicity = "0..*")
	private final StoreVAccessor<io.vertigo.chatbot.commons.domain.SmallTalk> smtIdResponseAccessor = new StoreVAccessor<>(io.vertigo.chatbot.commons.domain.SmallTalk.class, "SmallTalkResponse");

	@io.vertigo.datamodel.structure.stereotype.Association(
			name = "AChatbotWelcomeButtons",
			fkFieldName = "botIdWelcome",
			primaryDtDefinitionName = "DtChatbot",
			primaryIsNavigable = true,
			primaryRole = "chatbotWelcome",
			primaryLabel = "welcome buttons",
			primaryMultiplicity = "0..1",
			foreignDtDefinitionName = "DtResponseButton",
			foreignIsNavigable = false,
			foreignRole = "ResponseButton",
			foreignLabel = "ResponseButton",
			foreignMultiplicity = "0..*")
	private final StoreVAccessor<io.vertigo.chatbot.commons.domain.Chatbot> botIdWelcomeAccessor = new StoreVAccessor<>(io.vertigo.chatbot.commons.domain.Chatbot.class, "chatbotWelcome");

	@io.vertigo.datamodel.structure.stereotype.Association(
			name = "AChatbotDefaultButtons",
			fkFieldName = "botIdDefault",
			primaryDtDefinitionName = "DtChatbot",
			primaryIsNavigable = true,
			primaryRole = "chatbotDefault",
			primaryLabel = "Default buttons",
			primaryMultiplicity = "0..1",
			foreignDtDefinitionName = "DtResponseButton",
			foreignIsNavigable = false,
			foreignRole = "ResponseButton",
			foreignLabel = "ResponseButton",
			foreignMultiplicity = "0..*")
	private final StoreVAccessor<io.vertigo.chatbot.commons.domain.Chatbot> botIdDefaultAccessor = new StoreVAccessor<>(io.vertigo.chatbot.commons.domain.Chatbot.class, "chatbotDefault");

	/** {@inheritDoc} */
	@Override
	public UID<ResponseButton> getUID() {
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
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'SmallTalk'.
	 * @return Long smtId
	 */
	@io.vertigo.datamodel.structure.stereotype.ForeignKey(smartType = "STyId", label = "SmallTalk", fkDefinition = "DtSmallTalk" )
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
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'SmallTalkResponse'.
	 * @return Long smtIdResponse <b>Obligatoire</b>
	 */
	@io.vertigo.datamodel.structure.stereotype.ForeignKey(smartType = "STyId", label = "SmallTalkResponse", fkDefinition = "DtSmallTalk" )
	public Long getSmtIdResponse() {
		return (Long) smtIdResponseAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'SmallTalkResponse'.
	 * @param smtIdResponse Long <b>Obligatoire</b>
	 */
	public void setSmtIdResponse(final Long smtIdResponse) {
		smtIdResponseAccessor.setId(smtIdResponse);
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'welcome buttons'.
	 * @return Long botIdWelcome
	 */
	@io.vertigo.datamodel.structure.stereotype.ForeignKey(smartType = "STyId", label = "welcome buttons", fkDefinition = "DtChatbot" )
	public Long getBotIdWelcome() {
		return (Long) botIdWelcomeAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'welcome buttons'.
	 * @param botIdWelcome Long
	 */
	public void setBotIdWelcome(final Long botIdWelcome) {
		botIdWelcomeAccessor.setId(botIdWelcome);
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Default buttons'.
	 * @return Long botIdDefault
	 */
	@io.vertigo.datamodel.structure.stereotype.ForeignKey(smartType = "STyId", label = "Default buttons", fkDefinition = "DtChatbot" )
	public Long getBotIdDefault() {
		return (Long) botIdDefaultAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Default buttons'.
	 * @param botIdDefault Long
	 */
	public void setBotIdDefault(final Long botIdDefault) {
		botIdDefaultAccessor.setId(botIdDefault);
	}

 	/**
	 * Association : Default buttons.
	 * @return l'accesseur vers la propriété 'Default buttons'
	 */
	public StoreVAccessor<io.vertigo.chatbot.commons.domain.Chatbot> chatbotDefault() {
		return botIdDefaultAccessor;
	}

 	/**
	 * Association : welcome buttons.
	 * @return l'accesseur vers la propriété 'welcome buttons'
	 */
	public StoreVAccessor<io.vertigo.chatbot.commons.domain.Chatbot> chatbotWelcome() {
		return botIdWelcomeAccessor;
	}

 	/**
	 * Association : SmallTalkResponse.
	 * @return l'accesseur vers la propriété 'SmallTalkResponse'
	 */
	public StoreVAccessor<io.vertigo.chatbot.commons.domain.SmallTalk> smallTalkResponse() {
		return smtIdResponseAccessor;
	}

 	/**
	 * Association : SmallTalk.
	 * @return l'accesseur vers la propriété 'SmallTalk'
	 */
	public StoreVAccessor<io.vertigo.chatbot.commons.domain.SmallTalk> smallTalk() {
		return smtIdAccessor;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}
