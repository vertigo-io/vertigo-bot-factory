package io.vertigo.ai.plugins.nlu.rasa.mda;

public final class RasaIntentNlu {

	private String intent;
	private String examples;

	public RasaIntentNlu(final String intent, final String examples) {
		this.intent = intent;
		this.examples = examples;
	}

	/**
	 * @return the intent
	 */
	public String getIntent() {
		return intent;
	}

	/**
	 * @param intent the intent to set
	 */
	public void setIntent(final String intent) {
		this.intent = intent;
	}

	/**
	 * @return the examples
	 */
	public String getExamples() {
		return examples;
	}

	/**
	 * @param examples the examples to set
	 */
	public void setExamples(final String examples) {
		this.examples = examples;
	}

}
