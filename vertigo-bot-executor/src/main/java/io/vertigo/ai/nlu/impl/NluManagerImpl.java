package io.vertigo.ai.nlu.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertigo.ai.nlu.NluManager;
import io.vertigo.ai.nlu.VIntent;
import io.vertigo.ai.nlu.VRecognitionResult;
import io.vertigo.core.lang.Assertion;

/**
 * @author skerdudou
 */
public class NluManagerImpl implements NluManager {
	private final List<NluEnginePlugin> nluEnginePlugins;

	public static final String DEFAULT_PLUGIN_NAME = "main";

	/**
	 * Constructor.
	 *
	 * @param nluEnginePlugins List of NLU engine plugins
	 */
	@Inject
	public NluManagerImpl(
			final List<NluEnginePlugin> nluEnginePlugins) {
		Assertion.check().isNotNull(nluEnginePlugins);
		//-----
		checkDuplicateNames(nluEnginePlugins);

		this.nluEnginePlugins = nluEnginePlugins;
	}

	private static void checkDuplicateNames(final List<NluEnginePlugin> nluEnginePlugins) {
		final var items = new HashSet<String>();

		final Set<String> duplicates = nluEnginePlugins.stream()
				.map(NluEnginePlugin::getName)
				.filter(n -> !items.add(n)) // Set.add() returns false if the element was already in the set.
				.collect(Collectors.toSet());

		if (!duplicates.isEmpty()) {
			throw new IllegalStateException("Multiple NLU plugin with same name '" + String.join(", ", duplicates) + "'");
		}
	}

	private NluEnginePlugin getEngineByName(final String name) {
		Assertion.check().isNotBlank(name);

		return nluEnginePlugins.stream()
				.filter(e -> e.getName().equals(name))
				.findFirst()
				.orElseThrow();
	}

	@Override
	public void registerIntent(final VIntent intent) {
		registerIntent(intent, DEFAULT_PLUGIN_NAME);
	}

	@Override
	public void registerIntent(final VIntent intent, final String engineName) {
		getEngineByName(engineName).registerIntent(intent);
	}

	@Override
	public void addTrainingPhrase(final VIntent intent, final String trainingPhrase) {
		addTrainingPhrase(intent, trainingPhrase, DEFAULT_PLUGIN_NAME);
	}

	@Override
	public void addTrainingPhrase(final VIntent intent, final String trainingPhrase, final String engineName) {
		getEngineByName(engineName).addTrainingPhrase(intent, trainingPhrase);
	}

	@Override
	public void trainAll() {
		nluEnginePlugins.forEach(NluEnginePlugin::train);
	}

	@Override
	public VRecognitionResult recognize(final String sentence) {
		return recognize(sentence, DEFAULT_PLUGIN_NAME);
	}

	@Override
	public VRecognitionResult recognize(final String sentence, final String engineName) {
		return getEngineByName(engineName).recognize(sentence);
	}

	@Override
	public boolean isReady() {
		return isReady(DEFAULT_PLUGIN_NAME);
	}

	@Override
	public boolean isReady(final String engineName) {
		return getEngineByName(engineName).isReady();
	}

}
