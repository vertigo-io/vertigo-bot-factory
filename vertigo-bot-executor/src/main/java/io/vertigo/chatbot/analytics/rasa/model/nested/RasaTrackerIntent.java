package io.vertigo.chatbot.analytics.rasa.model.nested;

import java.math.BigDecimal;

public class RasaTrackerIntent {

	private String name;

	private BigDecimal confidence;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return the confidence
	 */
	public BigDecimal getConfidence() {
		return confidence;
	}

	/**
	 * @param confidence the confidence to set
	 */
	public void setConfidence(final BigDecimal confidence) {
		this.confidence = confidence;
	}


}
