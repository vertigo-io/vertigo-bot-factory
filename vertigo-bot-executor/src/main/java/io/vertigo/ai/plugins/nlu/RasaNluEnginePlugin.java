package io.vertigo.ai.plugins.nlu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import io.vertigo.ai.impl.nlu.NluEnginePlugin;
import io.vertigo.ai.impl.nlu.NluManagerImpl;
import io.vertigo.ai.nlu.VIntent;
import io.vertigo.ai.nlu.VRecognitionResult;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.param.ParamValue;

public class RasaNluEnginePlugin implements NluEnginePlugin {

	private final String name;
	private final String rasaUrl;

	private final AtomicBoolean ready;
	private final List<VIntent> intentList;
	private final Map<VIntent, List<String>> trainingPhrases;

	@Inject
	public RasaNluEnginePlugin(
			@ParamValue("rasaUrl") final String rasaUrl,
			@ParamValue("pluginName") final Optional<String> optPluginName) {

		Assertion.check().isNotBlank(rasaUrl);

		this.rasaUrl = rasaUrl;
		name = optPluginName.orElse(NluManagerImpl.DEFAULT_PLUGIN_NAME);

		ready = new AtomicBoolean(false);
		intentList = new ArrayList<>();
		trainingPhrases = new HashMap<>();
	}

	/** {@inheritDoc} */
	@Override
	public void registerIntent(final VIntent intent) {
		Assertion.check().isNotNull(intent);
		//--
		intentList.add(intent);
	}

	/** {@inheritDoc} */
	@Override
	public void addTrainingPhrase(final VIntent intent, final String trainingPhrase) {
		Assertion.check()
				.isNotNull(intent)
				.isNotBlank(trainingPhrase);
		//--
		Assertion.check().isTrue(intentList.contains(intent), "Unknown intent '{0}' on NLU engine {1}.", intent.getCode(), getName());

		trainingPhrases.computeIfAbsent(intent, k -> new ArrayList<>())
				.add(trainingPhrase);
	}

	/** {@inheritDoc} */
	@Override
	public void train() {
		ready.set(false);

		// do train

		ready.set(true);
	}

	/** {@inheritDoc} */
	@Override
	public VRecognitionResult recognize(final String sentence) {
		if (!ready.get()) {
			throw new IllegalStateException("NLU engine '" + getName() + "' is not ready to recognize sentenses.");
		}

		// do recognize

		return new VRecognitionResult(sentence, new ArrayList<>());
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return name;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isReady() {
		return ready.get();
	}

}
