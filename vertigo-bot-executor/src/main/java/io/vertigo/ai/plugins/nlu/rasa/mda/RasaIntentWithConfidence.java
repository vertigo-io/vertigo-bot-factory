package io.vertigo.ai.plugins.nlu.rasa.mda;

public final class RasaIntentWithConfidence {

	private String id;
	private String name;
	private Double confidence;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(final String id) {
		this.id = id;
	}

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
	public Double getConfidence() {
		return confidence;
	}

	/**
	 * @param confidence the confidence to set
	 */
	public void setConfidence(final Double confidence) {
		this.confidence = confidence;
	}

}
