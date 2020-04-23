package io.vertigo.chatbot.commons.domain;

import io.vertigo.dynamo.domain.model.DtObject;
import io.vertigo.dynamo.domain.stereotype.Field;
import io.vertigo.dynamo.domain.util.DtObjectUtil;
import io.vertigo.lang.Generated;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class ExecutorConfiguration implements DtObject {
	private static final long serialVersionUID = 1L;

	private Long botId;
	private Long nodId;
	private Long traId;
	private String modelName;
	private java.math.BigDecimal nluThreshold;
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Bot ID'.
	 * @return Long botId <b>Obligatoire</b>
	 */
	@Field(domain = "DoId", required = true, label = "Bot ID")
	public Long getBotId() {
		return botId;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Bot ID'.
	 * @param botId Long <b>Obligatoire</b>
	 */
	public void setBotId(final Long botId) {
		this.botId = botId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Node ID'.
	 * @return Long nodId <b>Obligatoire</b>
	 */
	@Field(domain = "DoId", required = true, label = "Node ID")
	public Long getNodId() {
		return nodId;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Node ID'.
	 * @param nodId Long <b>Obligatoire</b>
	 */
	public void setNodId(final Long nodId) {
		this.nodId = nodId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Model ID'.
	 * @return Long traId <b>Obligatoire</b>
	 */
	@Field(domain = "DoId", required = true, label = "Model ID")
	public Long getTraId() {
		return traId;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Model ID'.
	 * @param traId Long <b>Obligatoire</b>
	 */
	public void setTraId(final Long traId) {
		this.traId = traId;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'Model name'.
	 * @return String modelName <b>Obligatoire</b>
	 */
	@Field(domain = "DoLabel", required = true, label = "Model name")
	public String getModelName() {
		return modelName;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'Model name'.
	 * @param modelName String <b>Obligatoire</b>
	 */
	public void setModelName(final String modelName) {
		this.modelName = modelName;
	}
	
	/**
	 * Champ : DATA.
	 * Récupère la valeur de la propriété 'NLU Threshold'.
	 * @return BigDecimal nluThreshold <b>Obligatoire</b>
	 */
	@Field(domain = "DoPercentage", required = true, label = "NLU Threshold")
	public java.math.BigDecimal getNluThreshold() {
		return nluThreshold;
	}

	/**
	 * Champ : DATA.
	 * Définit la valeur de la propriété 'NLU Threshold'.
	 * @param nluThreshold BigDecimal <b>Obligatoire</b>
	 */
	public void setNluThreshold(final java.math.BigDecimal nluThreshold) {
		this.nluThreshold = nluThreshold;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return DtObjectUtil.toString(this);
	}
}
