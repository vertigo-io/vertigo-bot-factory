package io.vertigo.ai.plugins.nlu.rasa.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.vertigo.ai.plugins.nlu.rasa.mda.ConfigFile;
import io.vertigo.ai.plugins.nlu.rasa.mda.CustomRepresenter;
import io.vertigo.ai.plugins.nlu.rasa.mda.Pipeline;
import io.vertigo.ai.plugins.nlu.rasa.mda.RasaIntentNlu;
import io.vertigo.core.lang.VSystemException;

/**
 * @author vbaillet
 */
public final class FileIOUtil {

	private FileIOUtil() {
		//utils
	}

	public static ConfigFile getConfigFile(final URL configUrl) {
		try (final FileReader reader = new FileReader(new File(configUrl.getPath()))) {

			final Constructor constructor = new Constructor(ConfigFile.class);
			final Yaml yaml = new Yaml(constructor);
			return yaml.load(reader);
		} catch (final Exception e) {
			// FIXME VBA
			e.printStackTrace();
		}
		return new ConfigFile();
	}

	public static Yaml createCustomYamlFile() {
		//make the list in block
		final DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

		//Sort the tag and remove warning in yaml file
		final CustomRepresenter representer = new CustomRepresenter();
		representer.addClassTag(RasaIntentNlu.class, Tag.MAP);
		representer.addClassTag(Pipeline.class, Tag.MAP);

		return new Yaml(representer, options);
	}

	public static byte[] getYamlByteArrayFromMap(final Map<String, Object> map) {
		final Yaml yaml = FileIOUtil.createCustomYamlFile();
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		final OutputStreamWriter writer = new OutputStreamWriter(output);
		yaml.dump(map, writer);
		return output.toByteArray();

	}

	public static ObjectMapper createCustomObjectMapper() {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		return mapper;
	}

	public static <T> String getJsonStringFromObject(final ObjectMapper mapper, final T object) {
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
		} catch (final JsonProcessingException e) {
			throw new VSystemException(e, "error during the creation of json from {0}.", object);
		}
	}

	public static <T> T getObjectFromJson(final ObjectMapper mapper, final String json, final Class<T> clazz) {
		try {
			return mapper.readValue(json, clazz);
		} catch (final JsonProcessingException e) {
			throw new VSystemException(e, "error during reading json from {0}.", json);
		}
	}

	public static ObjectNode createNode(final ObjectMapper mapper, final String key, final String value) {
		final ObjectNode node = mapper.createObjectNode();
		node.put(key, value);
		return node;
	}

}
