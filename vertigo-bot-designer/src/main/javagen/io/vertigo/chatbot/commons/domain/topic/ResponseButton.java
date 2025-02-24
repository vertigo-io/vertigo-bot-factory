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
public final class ResponseButton implements Entity {
	private static final long serialVersionUID = 1L;

	private Long btnId;
	private String text;

	@io.vertigo.datamodel.data.stereotype.Association(
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
	private final StoreVAccessor<io.vertigo.chatbot.commons.domain.topic.SmallTalk> smtIdAccessor = new StoreVAccessor<>(io.vertigo.chatbot.commons.domain.topic.SmallTalk.class, "SmallTalk");

	@io.vertigo.datamodel.data.stereotype.Association(
			name = "AResponseButtonTopicResponse",
			fkFieldName = "topIdResponse",
			primaryDtDefinitionName = "DtTopic",
			primaryIsNavigable = true,
			primaryRole = "TopicResponse",
			primaryLabel = "TopicResponse",
			primaryMultiplicity = "1..1",
			foreignDtDefinitionName = "DtResponseButton",
			foreignIsNavigable = false,
			foreignRole = "ResponseButton",
			foreignLabel = "ResponseButton",
			foreignMultiplicity = "0..*")
	private final StoreVAccessor<io.vertigo.chatbot.commons.domain.topic.Topic> topIdResponseAccessor = new StoreVAccessor<>(io.vertigo.chatbot.commons.domain.topic.Topic.class, "TopicResponse");

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
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'TopicResponse'.
	 * @return Long topIdResponse <b>Obligatoire</b>
	 */
	@io.vertigo.datamodel.data.stereotype.ForeignKey(smartType = "STyId", label = "TopicResponse", fkDefinition = "DtTopic", cardinality = io.vertigo.core.lang.Cardinality.ONE )
	public Long getTopIdResponse() {
		return (Long) topIdResponseAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'TopicResponse'.
	 * @param topIdResponse Long <b>Obligatoire</b>
	 */
	public void setTopIdResponse(final Long topIdResponse) {
		topIdResponseAccessor.setId(topIdResponse);
	}

 	/**
	 * Association : TopicResponse.
	 * @return l'accesseur vers la propriété 'TopicResponse'
	 */
	public StoreVAccessor<io.vertigo.chatbot.commons.domain.topic.Topic> topicResponse() {
		return topIdResponseAccessor;
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
