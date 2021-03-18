package io.vertigo.ai.nlu.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import io.vertigo.ai.nlu.VIntent;
import io.vertigo.ai.nlu.VRecognitionResult;
import io.vertigo.ai.nlu.impl.NluEnginePlugin;
import io.vertigo.ai.nlu.impl.NluManagerImpl;
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

		this.rasaUrl = rasaUrl;
		name = optPluginName.orElse(NluManagerImpl.DEFAULT_PLUGIN_NAME);

		ready = new AtomicBoolean(false);
		intentList = new ArrayList<>();
		trainingPhrases = new HashMap<>();
	}

	@Override
	public void registerIntent(final VIntent intent) {
		Assertion.check().isNotNull(intent);
		//--
		intentList.add(intent);
	}

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

	@Override
	public void train() {
		ready.set(false);

		// do train

		ready.set(true);
	}

	@Override
	public VRecognitionResult recognize(final String sentence) {
		if (!ready.get()) {
			throw new IllegalStateException("NLU engine '" + getName() + "' is not ready to recognize sentenses.");
		}

		// do recognize

		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isReady() {
		return ready.get();
	}

}
