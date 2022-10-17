package io.vertigo.chatbot.designer.domain.monitoring;

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
public final class AlertingEvent implements Entity {
	private static final long serialVersionUID = 1L;

	private Long ageId;
	private java.time.Instant date;
	private String componentName;
	private Boolean alive;

	@io.vertigo.datamodel.structure.stereotype.Association(
			name = "AAlertingEventChatbot",
			fkFieldName = "botId",
			primaryDtDefinitionName = "DtChatbot",
			primaryIsNavigable = true,
			primaryRole = "Chatbot",
			primaryLabel = "Chatbot",
			primaryMultiplicity = "0..1",
			foreignDtDefinitionName = "DtAlertingEvent",
			foreignIsNavigable = false,
			foreignRole = "AlertingEvent",
			foreignLabel = "AlertingEvent",
			foreignMultiplicity = "0..*")
	private final StoreVAccessor<io.vertigo.chatbot.commons.domain.Chatbot> botIdAccessor = new StoreVAccessor<>(io.vertigo.chatbot.commons.domain.Chatbot.class, "Chatbot");

	@io.vertigo.datamodel.structure.stereotype.Association(
			name = "AAlertingEventNode",
			fkFieldName = "nodeId",
			primaryDtDefinitionName = "DtChatbotNode",
			primaryIsNavigable = true,
			primaryRole = "ChatbotNode",
			primaryLabel = "Node",
			primaryMultiplicity = "0..1",
			foreignDtDefinitionName = "DtAlertingEvent",
			foreignIsNavigable = false,
			foreignRole = "AlertingEvent",
			foreignLabel = "AlertingEvent",
			foreignMultiplicity = "0..*")
	private final StoreVAccessor<io.vertigo.chatbot.commons.domain.ChatbotNode> nodeIdAccessor = new StoreVAccessor<>(io.vertigo.chatbot.commons.domain.ChatbotNode.class, "ChatbotNode");

	/** {@inheritDoc} */
	@Override
	public UID<AlertingEvent> getUID() {
		return UID.of(this);
	}
	
	/**
	 * Champ : ID.
	 * Récupère la valeur de la propriété 'ID'.
	 * @return Long ageId <b>Obligatoire</b>
	 */
	@Field(smartType = "STyId", type = "ID", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "ID")
	public Long getAgeId() {
		return ageId;
	}

	/**
	 * Champ : ID.
	 * Définit la valeur de la propriété 'ID'.
	 * @param ageId Long <b>Obligatoire</b>
	 */
	public void setAgeId(final Long ageId) {
		this.ageId = ageId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Date'.
	 * @return Instant date <b>Obligatoire</b>
	 */
	@Field(smartType = "STyInstant", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Date")
	public java.time.Instant getDate() {
		return date;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Date'.
	 * @param date Instant <b>Obligatoire</b>
	 */
	public void setDate(final java.time.Instant date) {
		this.date = date;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Component name'.
	 * @return String componentName <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Component name")
	public String getComponentName() {
		return componentName;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Component name'.
	 * @param componentName String <b>Obligatoire</b>
	 */
	public void setComponentName(final String componentName) {
		this.componentName = componentName;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Alive'.
	 * @return Boolean alive <b>Obligatoire</b>
	 */
	@Field(smartType = "STyYesNo", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Alive")
	public Boolean getAlive() {
		return alive;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Alive'.
	 * @param alive Boolean <b>Obligatoire</b>
	 */
	public void setAlive(final Boolean alive) {
		this.alive = alive;
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Chatbot'.
	 * @return Long botId
	 */
	@io.vertigo.datamodel.structure.stereotype.ForeignKey(smartType = "STyId", label = "Chatbot", fkDefinition = "DtChatbot" )
	public Long getBotId() {
		return (Long) botIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Chatbot'.
	 * @param botId Long
	 */
	public void setBotId(final Long botId) {
		botIdAccessor.setId(botId);
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Node'.
	 * @return Long nodeId
	 */
	@io.vertigo.datamodel.structure.stereotype.ForeignKey(smartType = "STyId", label = "Node", fkDefinition = "DtChatbotNode" )
	public Long getNodeId() {
		return (Long) nodeIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Node'.
	 * @param nodeId Long
	 */
	public void setNodeId(final Long nodeId) {
		nodeIdAccessor.setId(nodeId);
	}

 	/**
	 * Association : Chatbot.
	 * @return l'accesseur vers la propriété 'Chatbot'
	 */
	public StoreVAccessor<io.vertigo.chatbot.commons.domain.Chatbot> chatbot() {
		return botIdAccessor;
	}

 	/**
	 * Association : Node.
	 * @return l'accesseur vers la propriété 'Node'
	 */
	public StoreVAccessor<io.vertigo.chatbot.commons.domain.ChatbotNode> chatbotNode() {
		return nodeIdAccessor;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}
