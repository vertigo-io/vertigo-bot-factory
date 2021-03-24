package io.vertigo.ai.plugins.nlu.rasa;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertigo.ai.impl.nlu.NluEnginePlugin;
import io.vertigo.ai.impl.nlu.NluManagerImpl;
import io.vertigo.ai.nlu.VIntent;
import io.vertigo.ai.nlu.VIntentClassification;
import io.vertigo.ai.nlu.VRecognitionResult;
import io.vertigo.ai.plugins.nlu.rasa.helper.FileIOHelper;
import io.vertigo.ai.plugins.nlu.rasa.helper.RasaHttpSenderHelper;
import io.vertigo.ai.plugins.nlu.rasa.mda.ConfigFile;
import io.vertigo.ai.plugins.nlu.rasa.mda.MessageToRecognize;
import io.vertigo.ai.plugins.nlu.rasa.mda.RasaIntentNlu;
import io.vertigo.ai.plugins.nlu.rasa.mda.RasaIntentWithConfidence;
import io.vertigo.ai.plugins.nlu.rasa.mda.RasaParsingResponse;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.param.ParamValue;
import io.vertigo.core.resource.ResourceManager;

public class RasaNluEnginePlugin implements NluEnginePlugin {

	private final String name;
	private final String rasaUrl;

	private final URL configFileUrl;

	private final AtomicBoolean ready;
	private final List<VIntent> intentList;
	private final Map<VIntent, List<String>> trainingPhrases;

	@Inject
	public RasaNluEnginePlugin(
			@ParamValue("rasaUrl") final String rasaUrl,
			@ParamValue("configFile") final Optional<String> configFileOpt,
			@ParamValue("pluginName") final Optional<String> pluginNameOpt,
			final ResourceManager resourceManager) {

		Assertion.check().isNotBlank(rasaUrl);

		this.rasaUrl = rasaUrl;
		name = pluginNameOpt.orElse(NluManagerImpl.DEFAULT_PLUGIN_NAME);

		final var configFileName = configFileOpt.orElse("rasa-config.yaml"); // in classpath by default
		Assertion.check().isNotBlank(configFileName);

		configFileUrl = resourceManager.resolve(configFileName);

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

		//Yaml file
		final ConfigFile config = FileIOHelper.getConfigFile(configFileUrl);
		final List<RasaIntentNlu> intents = createRasaIntents();

		//Create map and launch the dump
		final Map<String, Object> map = new LinkedHashMap<>();
		map.put("language", config.getLanguage());
		map.put("pipeline", config.getPipeline());
		map.put("nlu", intents);

		//train
		final String filename = RasaHttpSenderHelper.launchTraining(rasaUrl, map);

		//put model
		RasaHttpSenderHelper.putModel(rasaUrl, filename);
		ready.set(true);
	}

	private List<RasaIntentNlu> createRasaIntents() {
		final List<RasaIntentNlu> result = new ArrayList<>();
		for (final Entry<VIntent, List<String>> entry : trainingPhrases.entrySet()) {
			final RasaIntentNlu intent = new RasaIntentNlu(entry.getKey().getCode(), createTrainingSetence(entry.getValue()));
			result.add(intent);
		}
		return result;
	}

	private String createTrainingSetence(final List<String> value) {
		if (value.isEmpty()) {
			return "";
		}
		value.set(0, "- " + value.get(0));
		return String.join("\n- ", value);
	}

	/** {@inheritDoc} */
	@Override
	public VRecognitionResult recognize(final String sentence) {
		if (!ready.get()) {
			throw new IllegalStateException("NLU engine '" + getName() + "' is not ready to recognize sentenses.");
		}

		final MessageToRecognize message = new MessageToRecognize(sentence);

		final RasaParsingResponse response = RasaHttpSenderHelper.getIntentFromRasa(rasaUrl, message);

		return getVRecognitionResult(response);

	}

	private VRecognitionResult getVRecognitionResult(final RasaParsingResponse response) {
		final String rawSentence = response.getIntent().getName();
		final List<VIntentClassification> intentClassificationList = response.getIntent_ranking().stream().map(intent -> createVIntentClassificationFromRasaIntent(intent))
				.collect(Collectors.toList());
		return new VRecognitionResult(rawSentence, intentClassificationList);
	}

	private VIntentClassification createVIntentClassificationFromRasaIntent(final RasaIntentWithConfidence rasaIntent) {
		final VIntent intent = VIntent.of(rasaIntent.getName(), "");
		return new VIntentClassification(intent, rasaIntent.getConfidence());
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
