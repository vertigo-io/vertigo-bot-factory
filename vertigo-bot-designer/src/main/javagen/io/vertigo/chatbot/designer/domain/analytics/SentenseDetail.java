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
public final class SentenseDetail implements DataObject {
	private static final long serialVersionUID = 1L;

	private String messageId;
	private java.time.Instant date;
	private String text;
	private String intentRasa;
	private java.math.BigDecimal confidence;
	private Long topId;
	private String modelName;
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Rasa message ID'.
	 * @return String messageId
	 */
	@Field(smartType = "STyIdStr", label = "Rasa message ID")
	public String getMessageId() {
		return messageId;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Rasa message ID'.
	 * @param messageId String
	 */
	public void setMessageId(final String messageId) {
		this.messageId = messageId;
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
	 * Récupère la valeur de la propriété 'User text'.
	 * @return String text
	 */
	@Field(smartType = "STyLabel", label = "User text")
	public String getText() {
		return text;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'User text'.
	 * @param text String
	 */
	public void setText(final String text) {
		this.text = text;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Rasa intent'.
	 * @return String intentRasa
	 */
	@Field(smartType = "STyLabel", label = "Rasa intent")
	public String getIntentRasa() {
		return intentRasa;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Rasa intent'.
	 * @param intentRasa String
	 */
	public void setIntentRasa(final String intentRasa) {
		this.intentRasa = intentRasa;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Confidence'.
	 * @return BigDecimal confidence
	 */
	@Field(smartType = "STyPercentage", label = "Confidence")
	public java.math.BigDecimal getConfidence() {
		return confidence;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Confidence'.
	 * @param confidence BigDecimal
	 */
	public void setConfidence(final java.math.BigDecimal confidence) {
		this.confidence = confidence;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Topic selection'.
	 * @return Long topId
	 */
	@Field(smartType = "STyId", label = "Topic selection")
	public Long getTopId() {
		return topId;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Topic selection'.
	 * @param topId Long
	 */
	public void setTopId(final Long topId) {
		this.topId = topId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Model Name'.
	 * @return String modelName
	 */
	@Field(smartType = "STyLabel", label = "Model Name")
	public String getModelName() {
		return modelName;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Model Name'.
	 * @param modelName String
	 */
	public void setModelName(final String modelName) {
		this.modelName = modelName;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DataModelUtil.toString(this);
	}
}
