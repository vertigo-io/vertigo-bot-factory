package io.vertigo.ai.nlu.impl;

import javax.inject.Inject;

import io.vertigo.ai.nlu.NluManager;
import io.vertigo.ai.nlu.VIntent;
import io.vertigo.ai.nlu.VIntentResult;
import io.vertigo.core.lang.Assertion;

/**
 * @author skerdudou
 */
public class NluManagerImpl implements NluManager {
	private final NluEnginePlugin nluEnginePlugin;

	private boolean isModelLoaded = false;

	/**
	 * Constructor.
	 *
	 * @param nluEnginePlugin the NLU engine plugin
	 */
	@Inject
	public NluManagerImpl(
			final NluEnginePlugin nluEnginePlugin) {
		Assertion.check().isNotNull(nluEnginePlugin);
		//-----
		this.nluEnginePlugin = nluEnginePlugin;
	}

	@Override
	public VIntent registerIntent(final String code, final String description) {
		return nluEnginePlugin.registerIntent(code, description);
	}

	@Override
	public void addTrainingPhrase(final VIntent intent, final String trainingPhrase) {
		nluEnginePlugin.addTrainingPhrase(intent, trainingPhrase);
	}

	@Override
	public void trainAll() {
		nluEnginePlugin.trainAll();

		isModelLoaded = true;
	}

	@Override
	public VIntentResult recognize(final String sentence) {
		Assertion.check().isTrue(isModelLoaded, "NLU model not ready, use trainAll first.");

		return nluEnginePlugin.recognize(sentence);
	}

}
