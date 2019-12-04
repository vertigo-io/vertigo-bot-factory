package io.vertigo.chatbot.analytics.rasa.model.nested;

import java.math.BigDecimal;
import java.util.Optional;

public class RasaTrackerIntent {

	private Optional<String> name = Optional.empty();

	private BigDecimal confidence;

	/**
	 * @return the name
	 */
	public Optional<String> getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(final Optional<String> name) {
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
