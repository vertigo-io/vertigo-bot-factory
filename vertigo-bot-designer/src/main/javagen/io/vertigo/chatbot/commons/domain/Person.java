package io.vertigo.chatbot.commons.domain;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.structure.model.Entity;
import io.vertigo.datastore.impl.entitystore.EnumStoreVAccessor;
import io.vertigo.datastore.impl.entitystore.StoreListVAccessor;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datastore.impl.entitystore.StoreVAccessor;
import io.vertigo.datamodel.structure.stereotype.Field;
import io.vertigo.datamodel.structure.util.DtObjectUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class Person implements Entity {
	private static final long serialVersionUID = 1L;

	private Long perId;
	private String login;
	private String name;

	@io.vertigo.datamodel.structure.stereotype.Association(
			name = "APersonGroups",
			fkFieldName = "grpId",
			primaryDtDefinitionName = "DtGroups",
			primaryIsNavigable = true,
			primaryRole = "Groups",
			primaryLabel = "Group",
			primaryMultiplicity = "0..1",
			foreignDtDefinitionName = "DtPerson",
			foreignIsNavigable = false,
			foreignRole = "Person",
			foreignLabel = "Person",
			foreignMultiplicity = "0..*")
	private final StoreVAccessor<io.vertigo.chatbot.commons.domain.Groups> grpIdAccessor = new StoreVAccessor<>(io.vertigo.chatbot.commons.domain.Groups.class, "Groups");

	@io.vertigo.datamodel.structure.stereotype.Association(
			name = "APersonRole",
			fkFieldName = "rolCd",
			primaryDtDefinitionName = "DtPersonRole",
			primaryIsNavigable = true,
			primaryRole = "PersonRole",
			primaryLabel = "Role",
			primaryMultiplicity = "1..1",
			foreignDtDefinitionName = "DtPerson",
			foreignIsNavigable = false,
			foreignRole = "Person",
			foreignLabel = "Person",
			foreignMultiplicity = "0..*")
	private final EnumStoreVAccessor<io.vertigo.chatbot.commons.domain.PersonRole, io.vertigo.chatbot.commons.domain.PersonRoleEnum> rolCdAccessor = new EnumStoreVAccessor<>(io.vertigo.chatbot.commons.domain.PersonRole.class, "PersonRole", io.vertigo.chatbot.commons.domain.PersonRoleEnum.class);

	@io.vertigo.datamodel.structure.stereotype.AssociationNN(
			name = "AnnChatbotPerson",
			tableName = "CHA_PER_RIGHTS",
			dtDefinitionA = "DtPerson",
			dtDefinitionB = "DtChatbot",
			navigabilityA = true,
			navigabilityB = true,
			roleA = "Persons",
			roleB = "Chatbots",
			labelA = "Persons",
			labelB = "Chatbots")
	private final StoreListVAccessor<io.vertigo.chatbot.commons.domain.Chatbot> chatbotsAccessor = new StoreListVAccessor<>(this, "AnnChatbotPerson", "Chatbots");

	/** {@inheritDoc} */
	@Override
	public UID<Person> getUID() {
		return UID.of(this);
	}
	
	/**
	 * Champ : ID.
	 * Récupère la valeur de la propriété 'Id'.
	 * @return Long perId <b>Obligatoire</b>
	 */
	@Field(smartType = "STyId", type = "ID", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Id")
	public Long getPerId() {
		return perId;
	}

	/**
	 * Champ : ID.
	 * Définit la valeur de la propriété 'Id'.
	 * @param perId Long <b>Obligatoire</b>
	 */
	public void setPerId(final Long perId) {
		this.perId = perId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Login'.
	 * @return String login <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Login")
	public String getLogin() {
		return login;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Login'.
	 * @param login String <b>Obligatoire</b>
	 */
	public void setLogin(final String login) {
		this.login = login;
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
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Group'.
	 * @return Long grpId
	 */
	@io.vertigo.datamodel.structure.stereotype.ForeignKey(smartType = "STyId", label = "Group", fkDefinition = "DtGroups" )
	public Long getGrpId() {
		return (Long) grpIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Group'.
	 * @param grpId Long
	 */
	public void setGrpId(final Long grpId) {
		grpIdAccessor.setId(grpId);
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Role'.
	 * @return String rolCd <b>Obligatoire</b>
	 */
	@io.vertigo.datamodel.structure.stereotype.ForeignKey(smartType = "STyCode", label = "Role", fkDefinition = "DtPersonRole" )
	public String getRolCd() {
		return (String) rolCdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Role'.
	 * @param rolCd String <b>Obligatoire</b>
	 */
	public void setRolCd(final String rolCd) {
		rolCdAccessor.setId(rolCd);
	}

 	/**
	 * Association : Group.
	 * @return l'accesseur vers la propriété 'Group'
	 */
	public StoreVAccessor<io.vertigo.chatbot.commons.domain.Groups> groups() {
		return grpIdAccessor;
	}

 	/**
	 * Association : Role.
	 * @return l'accesseur vers la propriété 'Role'
	 */
	public EnumStoreVAccessor<io.vertigo.chatbot.commons.domain.PersonRole, io.vertigo.chatbot.commons.domain.PersonRoleEnum> personRole() {
		return rolCdAccessor;
	}

	/**
	 * Association : Chatbots.
	 * @return l'accesseur vers la propriété 'Chatbots'
	 */
	public StoreListVAccessor<io.vertigo.chatbot.commons.domain.Chatbot> chatbots() {
		return chatbotsAccessor;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}
