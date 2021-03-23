package io.vertigo.ai.plugins.nlu.rasa;

import java.util.ArrayList;
import java.util.List;

import io.vertigo.chatbot.executor.rasa.config.Pipeline;

public class ConfigFile {

	public String getLanguage() {
		return language;
	}

	public void setLanguage(final String language) {
		this.language = language;
	}

	public List<Pipeline> getPipeline() {
		return pipeline;
	}

	public void setPipeline(final List<Pipeline> pipelines) {
		pipeline = pipelines;
	}

	private String language;

	private List<Pipeline> pipeline = new ArrayList<Pipeline>();
}
