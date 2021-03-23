package io.vertigo.ai.plugins.nlu.rasa;

public class RasaIntentNlu {
	public String getExamples() {
		return examples;
	}

	public void setExamples(final String examples) {
		this.examples = examples;
	}

	public RasaIntentNlu(final String intent, final String examples) {
		this.intent = intent;
		this.examples = examples;
	}

	public String getIntent() {
		return intent;
	}

	public void setIntent(final String intent) {
		this.intent = intent;
	}

	private String intent;

	private String examples;

}
