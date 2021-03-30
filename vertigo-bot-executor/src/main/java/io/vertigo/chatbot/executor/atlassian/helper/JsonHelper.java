package io.vertigo.chatbot.executor.atlassian.helper;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertigo.core.lang.VSystemException;

public class JsonHelper {

	private JsonHelper() {
		//utils
	}

	public static <T> ObjectMapper createCustomObjectMapper() {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		return mapper;
	}

	public static <T> String getJsonStringFromObject(final ObjectMapper mapper, final T object) {
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
		} catch (final JsonProcessingException e) {
			throw new VSystemException("error during the creation of json from {0}. Reason : {1}", object, e.getMessage());
		}
	}

	public static <T> T getObjectFromJson(final ObjectMapper mapper, final String json, final Class<T> clazz) {
		try {
			return mapper.readValue(json, clazz);
		} catch (final JsonProcessingException e) {
			throw new VSystemException("error during reading json from {0}. Reason : {1}", json, e.getMessage());
		}
	}

}
