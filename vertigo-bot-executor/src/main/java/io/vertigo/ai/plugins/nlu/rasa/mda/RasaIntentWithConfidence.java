package io.vertigo.ai.plugins.nlu.rasa.mda;

public class RasaIntentWithConfidence {

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Double getConfidence() {
		return confidence;
	}

	public void setConfidence(final Double confidence) {
		this.confidence = confidence;
	}

	private String id;

	private String name;

	private Double confidence;
}
