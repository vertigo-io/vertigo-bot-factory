package io.vertigo.chatbot.analytics.rasa.model;

import java.math.BigDecimal;

public class RasaTrackerActionEvent extends AbstractRasaTrackerEvent {
	private String name;
	private String policy;
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
	 * @return the policy
	 */
	public String getPolicy() {
		return policy;
	}
	/**
	 * @param policy the policy to set
	 */
	public void setPolicy(final String policy) {
		this.policy = policy;
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
