package io.vertigo.chatbot.designer.domain;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.structure.model.Entity;
import io.vertigo.datastore.impl.entitystore.EnumStoreVAccessor;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datastore.impl.entitystore.StoreVAccessor;
import io.vertigo.datamodel.structure.stereotype.Field;
import io.vertigo.datamodel.structure.util.DtObjectUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class ContextEnvironmentValue implements Entity {
	private static final long serialVersionUID = 1L;

	private Long cenvalId;
	private String value;

	@io.vertigo.datamodel.structure.stereotype.Association(
			name = "AContextEnvironmentValueContext",
			fkFieldName = "cvaId",
			primaryDtDefinitionName = "DtContextValue",
			primaryIsNavigable = true,
			primaryRole = "ContextValue",
			primaryLabel = "Context",
			primaryMultiplicity = "1..1",
			foreignDtDefinitionName = "DtContextEnvironmentValue",
			foreignIsNavigable = false,
			foreignRole = "ContextEnvironmentValue",
			foreignLabel = "ContextEnvironmentValue",
			foreignMultiplicity = "0..*")
	private final StoreVAccessor<io.vertigo.chatbot.commons.domain.ContextValue> cvaIdAccessor = new StoreVAccessor<>(io.vertigo.chatbot.commons.domain.ContextValue.class, "ContextValue");

	@io.vertigo.datamodel.structure.stereotype.Association(
			name = "AContextEnvironmentValue",
			fkFieldName = "cenvId",
			primaryDtDefinitionName = "DtContextEnvironment",
			primaryIsNavigable = true,
			primaryRole = "ContextEnvironment",
			primaryLabel = "Environment",
			primaryMultiplicity = "1..1",
			foreignDtDefinitionName = "DtContextEnvironmentValue",
			foreignIsNavigable = false,
			foreignRole = "ContextEnvironmentValue",
			foreignLabel = "ContextEnvironmentValue",
			foreignMultiplicity = "0..*")
	private final StoreVAccessor<io.vertigo.chatbot.designer.domain.ContextEnvironment> cenvIdAccessor = new StoreVAccessor<>(io.vertigo.chatbot.designer.domain.ContextEnvironment.class, "ContextEnvironment");

	@io.vertigo.datamodel.structure.stereotype.Association(
			name = "AContextEnvironmentValueTypeOperator",
			fkFieldName = "tyopCd",
			primaryDtDefinitionName = "DtTypeOperator",
			primaryIsNavigable = true,
			primaryRole = "TypeOperator",
			primaryLabel = "Value operator",
			primaryMultiplicity = "1..1",
			foreignDtDefinitionName = "DtContextEnvironmentValue",
			foreignIsNavigable = false,
			foreignRole = "ContextEnvironmentValue",
			foreignLabel = "ContextEnvironmentValue",
			foreignMultiplicity = "0..*")
	private final EnumStoreVAccessor<io.vertigo.chatbot.designer.domain.TypeOperator, io.vertigo.chatbot.designer.domain.TypeOperatorEnum> tyopCdAccessor = new EnumStoreVAccessor<>(io.vertigo.chatbot.designer.domain.TypeOperator.class, "TypeOperator", io.vertigo.chatbot.designer.domain.TypeOperatorEnum.class);

	/** {@inheritDoc} */
	@Override
	public UID<ContextEnvironmentValue> getUID() {
		return UID.of(this);
	}
	
	/**
	 * Champ : ID.
	 * Récupère la valeur de la propriété 'Context environment value id'.
	 * @return Long cenvalId <b>Obligatoire</b>
	 */
	@Field(smartType = "STyId", type = "ID", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Context environment value id")
	public Long getCenvalId() {
		return cenvalId;
	}

	/**
	 * Champ : ID.
	 * Définit la valeur de la propriété 'Context environment value id'.
	 * @param cenvalId Long <b>Obligatoire</b>
	 */
	public void setCenvalId(final Long cenvalId) {
		this.cenvalId = cenvalId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Value'.
	 * @return String value
	 */
	@Field(smartType = "STyLabel", label = "Value")
	public String getValue() {
		return value;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Value'.
	 * @param value String
	 */
	public void setValue(final String value) {
		this.value = value;
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Context'.
	 * @return Long cvaId <b>Obligatoire</b>
	 */
	@io.vertigo.datamodel.structure.stereotype.ForeignKey(smartType = "STyId", label = "Context", fkDefinition = "DtContextValue", cardinality = io.vertigo.core.lang.Cardinality.ONE )
	public Long getCvaId() {
		return (Long) cvaIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Context'.
	 * @param cvaId Long <b>Obligatoire</b>
	 */
	public void setCvaId(final Long cvaId) {
		cvaIdAccessor.setId(cvaId);
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Environment'.
	 * @return Long cenvId <b>Obligatoire</b>
	 */
	@io.vertigo.datamodel.structure.stereotype.ForeignKey(smartType = "STyId", label = "Environment", fkDefinition = "DtContextEnvironment", cardinality = io.vertigo.core.lang.Cardinality.ONE )
	public Long getCenvId() {
		return (Long) cenvIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Environment'.
	 * @param cenvId Long <b>Obligatoire</b>
	 */
	public void setCenvId(final Long cenvId) {
		cenvIdAccessor.setId(cenvId);
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Value operator'.
	 * @return String tyopCd <b>Obligatoire</b>
	 */
	@io.vertigo.datamodel.structure.stereotype.ForeignKey(smartType = "STyCode", label = "Value operator", fkDefinition = "DtTypeOperator", cardinality = io.vertigo.core.lang.Cardinality.ONE )
	public String getTyopCd() {
		return (String) tyopCdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Value operator'.
	 * @param tyopCd String <b>Obligatoire</b>
	 */
	public void setTyopCd(final String tyopCd) {
		tyopCdAccessor.setId(tyopCd);
	}

 	/**
	 * Association : Environment.
	 * @return l'accesseur vers la propriété 'Environment'
	 */
	public StoreVAccessor<io.vertigo.chatbot.designer.domain.ContextEnvironment> contextEnvironment() {
		return cenvIdAccessor;
	}

 	/**
	 * Association : Context.
	 * @return l'accesseur vers la propriété 'Context'
	 */
	public StoreVAccessor<io.vertigo.chatbot.commons.domain.ContextValue> contextValue() {
		return cvaIdAccessor;
	}

 	/**
	 * Association : Value operator.
	 * @return l'accesseur vers la propriété 'Value operator'
	 */
	public EnumStoreVAccessor<io.vertigo.chatbot.designer.domain.TypeOperator, io.vertigo.chatbot.designer.domain.TypeOperatorEnum> typeOperator() {
		return tyopCdAccessor;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}
