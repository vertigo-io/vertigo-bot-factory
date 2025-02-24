package io.vertigo.chatbot.designer.domain.analytics;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.data.model.DataObject;
import io.vertigo.datamodel.data.stereotype.Field;
import io.vertigo.datamodel.data.util.DataModelUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class ConversationDetail implements DataObject {
	private static final long serialVersionUID = 1L;

	private String sessionId;
	private java.time.Instant date;
	private String text;
	private Boolean isUserMessage;
	private Boolean isBotMessage;
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'sessionId'.
	 * @return String sessionId
	 */
	@Field(smartType = "STyIdStr", label = "sessionId")
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'sessionId'.
	 * @param sessionId String
	 */
	public void setSessionId(final String sessionId) {
		this.sessionId = sessionId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Date'.
	 * @return Instant date
	 */
	@Field(smartType = "STyInstant", label = "Date")
	public java.time.Instant getDate() {
		return date;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Date'.
	 * @param date Instant
	 */
	public void setDate(final java.time.Instant date) {
		this.date = date;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Text'.
	 * @return String text
	 */
	@Field(smartType = "STyLabel", label = "Text")
	public String getText() {
		return text;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Text'.
	 * @param text String
	 */
	public void setText(final String text) {
		this.text = text;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'IsUserMessage'.
	 * @return Boolean isUserMessage
	 */
	@Field(smartType = "STyYesNo", label = "IsUserMessage")
	public Boolean getIsUserMessage() {
		return isUserMessage;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'IsUserMessage'.
	 * @param isUserMessage Boolean
	 */
	public void setIsUserMessage(final Boolean isUserMessage) {
		this.isUserMessage = isUserMessage;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'IsBotMessage'.
	 * @return Boolean isBotMessage
	 */
	@Field(smartType = "STyYesNo", label = "IsBotMessage")
	public Boolean getIsBotMessage() {
		return isBotMessage;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'IsBotMessage'.
	 * @param isBotMessage Boolean
	 */
	public void setIsBotMessage(final Boolean isBotMessage) {
		this.isBotMessage = isBotMessage;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DataModelUtil.toString(this);
	}
}
