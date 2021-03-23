package io.vertigo.ai.plugins.nlu.rasa;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;

import io.vertigo.ai.impl.nlu.NluEnginePlugin;
import io.vertigo.ai.impl.nlu.NluManagerImpl;
import io.vertigo.ai.nlu.VIntent;
import io.vertigo.ai.nlu.VRecognitionResult;
import io.vertigo.chatbot.executor.rasa.config.Pipeline;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.param.ParamValue;
import io.vertigo.core.resource.ResourceManager;

public class RasaNluEnginePlugin implements NluEnginePlugin {

	private static final String NEW_LINE = "\r\n";

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
		final ConfigFile config = getConfigFile();
		final List<RasaIntentNlu> intents = createRasaIntents();

		try {

			//TODO change this with outputstream
			final FileWriter writer = new FileWriter(new File("C:\\Users\\vbaillet\\Documents\\Python_Scripts\\python_env\\python\\rasa\\test\\essai.yaml"));

			//make the list in block
			final DumperOptions options = new DumperOptions();
			options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

			//Sort the tag and remove warning in yaml file
			final CustomRepresenter representer = new CustomRepresenter();
			representer.addClassTag(RasaIntentNlu.class, Tag.MAP);
			representer.addClassTag(Pipeline.class, Tag.MAP);

			//Create map and launch the dump
			final Yaml yaml = new Yaml(representer, options);
			final Map<Object, Object> map = new LinkedHashMap<>();
			map.put("language", config.getLanguage());
			map.put("pipeline", config.getPipeline());
			map.put("nlu", intents);

			yaml.dump(map, writer);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	private ConfigFile getConfigFile() {
		try (final FileReader reader = new FileReader(new File(configFileUrl.getPath()))) {

			final Constructor constructor = new Constructor(ConfigFile.class);
			final Yaml yaml = new Yaml(constructor);
			return yaml.load(reader);
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ConfigFile();
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
