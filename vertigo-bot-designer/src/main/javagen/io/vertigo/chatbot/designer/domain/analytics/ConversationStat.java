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
public final class ConversationStat implements DataObject {
	private static final long serialVersionUID = 1L;

	private String sessionId;
	private java.time.Instant date;
	private String modelName;
	private Long interactions;
	private Boolean ended;
	private Long rating;
	private String ratingComment;
	private String lastTopic;
	
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
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Interactions'.
	 * @return Long interactions
	 */
	@Field(smartType = "STyNumber", label = "Interactions")
	public Long getInteractions() {
		return interactions;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Interactions'.
	 * @param interactions Long
	 */
	public void setInteractions(final Long interactions) {
		this.interactions = interactions;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Ended'.
	 * @return Boolean ended
	 */
	@Field(smartType = "STyYesNo", label = "Ended")
	public Boolean getEnded() {
		return ended;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Ended'.
	 * @param ended Boolean
	 */
	public void setEnded(final Boolean ended) {
		this.ended = ended;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Rating'.
	 * @return Long rating
	 */
	@Field(smartType = "STyNumber", label = "Rating")
	public Long getRating() {
		return rating;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Rating'.
	 * @param rating Long
	 */
	public void setRating(final Long rating) {
		this.rating = rating;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Comment'.
	 * @return String ratingComment
	 */
	@Field(smartType = "STyText", label = "Comment")
	public String getRatingComment() {
		return ratingComment;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Comment'.
	 * @param ratingComment String
	 */
	public void setRatingComment(final String ratingComment) {
		this.ratingComment = ratingComment;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Last topic'.
	 * @return String lastTopic
	 */
	@Field(smartType = "STyLabel", label = "Last topic")
	public String getLastTopic() {
		return lastTopic;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Last topic'.
	 * @param lastTopic String
	 */
	public void setLastTopic(final String lastTopic) {
		this.lastTopic = lastTopic;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DataModelUtil.toString(this);
	}
}
