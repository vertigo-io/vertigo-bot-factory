package io.vertigo.ai.plugins.nlu.rasa.mda;

import java.util.ArrayList;
import java.util.List;

public final class ConfigFile {

	private String language;
	private List<Pipeline> pipeline = new ArrayList<>();

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(final String language) {
		this.language = language;
	}

	/**
	 * @return the pipeline
	 */
	public List<Pipeline> getPipeline() {
		return pipeline;
	}

	/**
	 * @param pipeline the pipeline to set
	 */
	public void setPipeline(final List<Pipeline> pipeline) {
		this.pipeline = pipeline;
	}

}
