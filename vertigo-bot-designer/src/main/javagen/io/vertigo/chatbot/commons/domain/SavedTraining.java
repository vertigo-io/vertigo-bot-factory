package io.vertigo.chatbot.commons.domain;

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
public final class SavedTraining implements Entity {
	private static final long serialVersionUID = 1L;

	private Long savedTraId;
	private String name;
	private java.time.Instant creationTime;
	private String description;
	private String botExport;

	@io.vertigo.datamodel.data.stereotype.Association(
			name = "ASavedTraining",
			fkFieldName = "attFileInfoId",
			primaryDtDefinitionName = "DtAttachmentFileInfo",
			primaryIsNavigable = true,
			primaryRole = "AttachmentFileInfo",
			primaryLabel = "Attachment File Info",
			primaryMultiplicity = "1..1",
			foreignDtDefinitionName = "DtSavedTraining",
			foreignIsNavigable = false,
			foreignRole = "SavedTraining",
			foreignLabel = "SavedTraining",
			foreignMultiplicity = "0..*")
	private final StoreVAccessor<io.vertigo.chatbot.commons.domain.AttachmentFileInfo> attFileInfoIdAccessor = new StoreVAccessor<>(io.vertigo.chatbot.commons.domain.AttachmentFileInfo.class, "AttachmentFileInfo");

	@io.vertigo.datamodel.data.stereotype.Association(
			name = "ASavedTrainingTraining",
			fkFieldName = "traId",
			primaryDtDefinitionName = "DtTraining",
			primaryIsNavigable = true,
			primaryRole = "Training",
			primaryLabel = "Training",
			primaryMultiplicity = "1..1",
			foreignDtDefinitionName = "DtSavedTraining",
			foreignIsNavigable = false,
			foreignRole = "SavedTraining",
			foreignLabel = "SavedTraining",
			foreignMultiplicity = "0..*")
	private final StoreVAccessor<io.vertigo.chatbot.commons.domain.Training> traIdAccessor = new StoreVAccessor<>(io.vertigo.chatbot.commons.domain.Training.class, "Training");

	@io.vertigo.datamodel.data.stereotype.Association(
			name = "ASavedTrainingChatbot",
			fkFieldName = "botId",
			primaryDtDefinitionName = "DtChatbot",
			primaryIsNavigable = true,
			primaryRole = "Chatbot",
			primaryLabel = "Bot",
			primaryMultiplicity = "1..1",
			foreignDtDefinitionName = "DtSavedTraining",
			foreignIsNavigable = false,
			foreignRole = "SavedTraining",
			foreignLabel = "SavedTraining",
			foreignMultiplicity = "0..*")
	private final StoreVAccessor<io.vertigo.chatbot.commons.domain.Chatbot> botIdAccessor = new StoreVAccessor<>(io.vertigo.chatbot.commons.domain.Chatbot.class, "Chatbot");

	/** {@inheritDoc} */
	@Override
	public UID<SavedTraining> getUID() {
		return UID.of(this);
	}
	
	/**
	 * Champ : ID.
	 * Récupère la valeur de la propriété 'ID'.
	 * @return Long savedTraId <b>Obligatoire</b>
	 */
	@Field(smartType = "STyId", type = "ID", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "ID")
	public Long getSavedTraId() {
		return savedTraId;
	}

	/**
	 * Champ : ID.
	 * Définit la valeur de la propriété 'ID'.
	 * @param savedTraId Long <b>Obligatoire</b>
	 */
	public void setSavedTraId(final Long savedTraId) {
		this.savedTraId = savedTraId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Name'.
	 * @return String name <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Name")
	public String getName() {
		return name;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Name'.
	 * @param name String <b>Obligatoire</b>
	 */
	public void setName(final String name) {
		this.name = name;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Creation time'.
	 * @return Instant creationTime <b>Obligatoire</b>
	 */
	@Field(smartType = "STyInstant", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Creation time")
	public java.time.Instant getCreationTime() {
		return creationTime;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Creation time'.
	 * @param creationTime Instant <b>Obligatoire</b>
	 */
	public void setCreationTime(final java.time.Instant creationTime) {
		this.creationTime = creationTime;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Description'.
	 * @return String description
	 */
	@Field(smartType = "STyText", label = "Description")
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
	 * Récupère la valeur de la propriété 'Bot Export'.
	 * @return String botExport <b>Obligatoire</b>
	 */
	@Field(smartType = "STyJson", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Bot Export")
	public String getBotExport() {
		return botExport;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Bot Export'.
	 * @param botExport String <b>Obligatoire</b>
	 */
	public void setBotExport(final String botExport) {
		this.botExport = botExport;
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Attachment File Info'.
	 * @return Long attFileInfoId <b>Obligatoire</b>
	 */
	@io.vertigo.datamodel.data.stereotype.ForeignKey(smartType = "STyId", label = "Attachment File Info", fkDefinition = "DtAttachmentFileInfo", cardinality = io.vertigo.core.lang.Cardinality.ONE )
	public Long getAttFileInfoId() {
		return (Long) attFileInfoIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Attachment File Info'.
	 * @param attFileInfoId Long <b>Obligatoire</b>
	 */
	public void setAttFileInfoId(final Long attFileInfoId) {
		attFileInfoIdAccessor.setId(attFileInfoId);
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Training'.
	 * @return Long traId <b>Obligatoire</b>
	 */
	@io.vertigo.datamodel.data.stereotype.ForeignKey(smartType = "STyId", label = "Training", fkDefinition = "DtTraining", cardinality = io.vertigo.core.lang.Cardinality.ONE )
	public Long getTraId() {
		return (Long) traIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Training'.
	 * @param traId Long <b>Obligatoire</b>
	 */
	public void setTraId(final Long traId) {
		traIdAccessor.setId(traId);
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Bot'.
	 * @return Long botId <b>Obligatoire</b>
	 */
	@io.vertigo.datamodel.data.stereotype.ForeignKey(smartType = "STyId", label = "Bot", fkDefinition = "DtChatbot", cardinality = io.vertigo.core.lang.Cardinality.ONE )
	public Long getBotId() {
		return (Long) botIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Bot'.
	 * @param botId Long <b>Obligatoire</b>
	 */
	public void setBotId(final Long botId) {
		botIdAccessor.setId(botId);
	}

 	/**
	 * Association : Attachment File Info.
	 * @return l'accesseur vers la propriété 'Attachment File Info'
	 */
	public StoreVAccessor<io.vertigo.chatbot.commons.domain.AttachmentFileInfo> attachmentFileInfo() {
		return attFileInfoIdAccessor;
	}

 	/**
	 * Association : Bot.
	 * @return l'accesseur vers la propriété 'Bot'
	 */
	public StoreVAccessor<io.vertigo.chatbot.commons.domain.Chatbot> chatbot() {
		return botIdAccessor;
	}

 	/**
	 * Association : Training.
	 * @return l'accesseur vers la propriété 'Training'
	 */
	public StoreVAccessor<io.vertigo.chatbot.commons.domain.Training> training() {
		return traIdAccessor;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DataModelUtil.toString(this);
	}
}
