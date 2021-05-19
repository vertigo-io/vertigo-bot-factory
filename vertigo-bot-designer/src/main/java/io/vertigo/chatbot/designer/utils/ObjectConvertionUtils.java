package io.vertigo.chatbot.designer.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;

import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.Node;
import io.vertigo.vega.engines.webservice.json.JsonEngine;

public final class ObjectConvertionUtils {

	private ObjectConvertionUtils() {
		// Nothing utils class
	}

	private static final JsonEngine jsonEngine = Node.getNode().getComponentSpace().resolve(JsonEngine.class);

	public static String objectToJson(final Object input) {
		return jsonEngine.toJson(input);
	}

	public static <D> D jsonToObject(final String json, final Type type) {
		return jsonEngine.fromJson(json, type);
	}

	public static <D> D objectFromByteArray(final byte[] object) {
		ByteArrayInputStream bis = new ByteArrayInputStream(object);

		try (ObjectInput in = new ObjectInputStream(bis);) {
			return (D) in.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new VSystemException("error");
		}

	}
}
