package io.vertigo.chatbot.commons.domain.questionanswer;

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
public final class QuestionAnswerContext implements Entity {
	private static final long serialVersionUID = 1L;

	private Long qacId;

	@io.vertigo.datamodel.structure.stereotype.Association(
			name = "AQuestionAnswerContextQuestionAnswer",
			fkFieldName = "qaId",
			primaryDtDefinitionName = "DtQuestionAnswer",
			primaryIsNavigable = true,
			primaryRole = "QuestionAnswer",
			primaryLabel = "Question answer id",
			primaryMultiplicity = "1..1",
			foreignDtDefinitionName = "DtQuestionAnswerContext",
			foreignIsNavigable = false,
			foreignRole = "QuestionAnswerContext",
			foreignLabel = "QuestionAnswerContext",
			foreignMultiplicity = "0..*")
	private final StoreVAccessor<io.vertigo.chatbot.commons.domain.questionanswer.QuestionAnswer> qaIdAccessor = new StoreVAccessor<>(io.vertigo.chatbot.commons.domain.questionanswer.QuestionAnswer.class, "QuestionAnswer");

	@io.vertigo.datamodel.structure.stereotype.Association(
			name = "AQuestionAnswerContextContextValue",
			fkFieldName = "cvaId",
			primaryDtDefinitionName = "DtContextValue",
			primaryIsNavigable = true,
			primaryRole = "ContextValue",
			primaryLabel = "Context value id",
			primaryMultiplicity = "1..1",
			foreignDtDefinitionName = "DtQuestionAnswerContext",
			foreignIsNavigable = false,
			foreignRole = "QuestionAnswerContext",
			foreignLabel = "QuestionAnswerContext",
			foreignMultiplicity = "0..*")
	private final StoreVAccessor<io.vertigo.chatbot.commons.domain.ContextValue> cvaIdAccessor = new StoreVAccessor<>(io.vertigo.chatbot.commons.domain.ContextValue.class, "ContextValue");

	@io.vertigo.datamodel.structure.stereotype.Association(
			name = "AQuestionAnswerContextContextPossibleValue",
			fkFieldName = "cpvId",
			primaryDtDefinitionName = "DtContextPossibleValue",
			primaryIsNavigable = true,
			primaryRole = "ContextPossibleValue",
			primaryLabel = "Context possible value id",
			primaryMultiplicity = "0..1",
			foreignDtDefinitionName = "DtQuestionAnswerContext",
			foreignIsNavigable = false,
			foreignRole = "QuestionAnswerContext",
			foreignLabel = "QuestionAnswerContext",
			foreignMultiplicity = "0..*")
	private final StoreVAccessor<io.vertigo.chatbot.commons.domain.ContextPossibleValue> cpvIdAccessor = new StoreVAccessor<>(io.vertigo.chatbot.commons.domain.ContextPossibleValue.class, "ContextPossibleValue");

	/** {@inheritDoc} */
	@Override
	public UID<QuestionAnswerContext> getUID() {
		return UID.of(this);
	}
	
	/**
	 * Champ : ID.
	 * Récupère la valeur de la propriété 'ID'.
	 * @return Long qacId <b>Obligatoire</b>
	 */
	@Field(smartType = "STyId", type = "ID", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "ID")
	public Long getQacId() {
		return qacId;
	}

	/**
	 * Champ : ID.
	 * Définit la valeur de la propriété 'ID'.
	 * @param qacId Long <b>Obligatoire</b>
	 */
	public void setQacId(final Long qacId) {
		this.qacId = qacId;
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Question answer id'.
	 * @return Long qaId <b>Obligatoire</b>
	 */
	@io.vertigo.datamodel.structure.stereotype.ForeignKey(smartType = "STyId", label = "Question answer id", fkDefinition = "DtQuestionAnswer", cardinality = io.vertigo.core.lang.Cardinality.ONE )
	public Long getQaId() {
		return (Long) qaIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Question answer id'.
	 * @param qaId Long <b>Obligatoire</b>
	 */
	public void setQaId(final Long qaId) {
		qaIdAccessor.setId(qaId);
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Context value id'.
	 * @return Long cvaId <b>Obligatoire</b>
	 */
	@io.vertigo.datamodel.structure.stereotype.ForeignKey(smartType = "STyId", label = "Context value id", fkDefinition = "DtContextValue", cardinality = io.vertigo.core.lang.Cardinality.ONE )
	public Long getCvaId() {
		return (Long) cvaIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Context value id'.
	 * @param cvaId Long <b>Obligatoire</b>
	 */
	public void setCvaId(final Long cvaId) {
		cvaIdAccessor.setId(cvaId);
	}
	
	/**
	 * Champ : FOREIGN_KEY.
	 * Récupère la valeur de la propriété 'Context possible value id'.
	 * @return Long cpvId
	 */
	@io.vertigo.datamodel.structure.stereotype.ForeignKey(smartType = "STyId", label = "Context possible value id", fkDefinition = "DtContextPossibleValue" )
	public Long getCpvId() {
		return (Long) cpvIdAccessor.getId();
	}

	/**
	 * Champ : FOREIGN_KEY.
	 * Définit la valeur de la propriété 'Context possible value id'.
	 * @param cpvId Long
	 */
	public void setCpvId(final Long cpvId) {
		cpvIdAccessor.setId(cpvId);
	}

 	/**
	 * Association : Context possible value id.
	 * @return l'accesseur vers la propriété 'Context possible value id'
	 */
	public StoreVAccessor<io.vertigo.chatbot.commons.domain.ContextPossibleValue> contextPossibleValue() {
		return cpvIdAccessor;
	}

 	/**
	 * Association : Context value id.
	 * @return l'accesseur vers la propriété 'Context value id'
	 */
	public StoreVAccessor<io.vertigo.chatbot.commons.domain.ContextValue> contextValue() {
		return cvaIdAccessor;
	}

 	/**
	 * Association : Question answer id.
	 * @return l'accesseur vers la propriété 'Question answer id'
	 */
	public StoreVAccessor<io.vertigo.chatbot.commons.domain.questionanswer.QuestionAnswer> questionAnswer() {
		return qaIdAccessor;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}