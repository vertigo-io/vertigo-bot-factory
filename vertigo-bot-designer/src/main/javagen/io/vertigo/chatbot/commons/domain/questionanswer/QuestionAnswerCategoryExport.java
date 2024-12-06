package io.vertigo.chatbot.commons.domain.questionanswer;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.data.model.DataObject;
import io.vertigo.datamodel.data.stereotype.Field;
import io.vertigo.datamodel.data.util.DataModelUtil;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class QuestionAnswerCategoryExport implements DataObject {
	private static final long serialVersionUID = 1L;

	private String label;
	private String isEnabled;
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Question-Answer category label'.
	 * @return String label <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Question-Answer category label")
	public String getLabel() {
		return label;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Question-Answer category label'.
	 * @param label String <b>Obligatoire</b>
	 */
	public void setLabel(final String label) {
		this.label = label;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Enabled'.
	 * @return String isEnabled <b>Obligatoire</b>
	 */
	@Field(smartType = "STyLabel", cardinality = io.vertigo.core.lang.Cardinality.ONE, label = "Enabled")
	public String getIsEnabled() {
		return isEnabled;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Enabled'.
	 * @param isEnabled String <b>Obligatoire</b>
	 */
	public void setIsEnabled(final String isEnabled) {
		this.isEnabled = isEnabled;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DataModelUtil.toString(this);
	}
}
