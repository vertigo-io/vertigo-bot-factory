package io.vertigo.chatbot.commons;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;

import io.vertigo.app.Home;
import io.vertigo.vega.engines.webservice.json.JsonEngine;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GsonProvider<T> implements MessageBodyReader<T>, MessageBodyWriter<T> {

	private final JsonEngine jsonEngine;

	public GsonProvider() {
		jsonEngine = Home.getApp().getComponentSpace().resolve(JsonEngine.class);
	}

	@Override
	public boolean isReadable(final Class<?> type, final Type genericType,
			final Annotation[] annotations, final MediaType mediaType) {
		return true;
	}

	@Override
	public T readFrom(final Class<T> type, final Type genericType, final Annotation[] annotations,
			final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders,
			final InputStream entityStream) throws IOException {

		return jsonEngine.fromJson(IOUtils.toString(entityStream, StandardCharsets.UTF_8), type);
	}

	@Override
	public boolean isWriteable(final Class<?> type, final Type genericType,
			final Annotation[] annotations, final MediaType mediaType) {
		return true;
	}

	@Override
	public long getSize(final T t, final Class<?> type, final Type genericType,
			final Annotation[] annotations, final MediaType mediaType) {
		return -1;
	}

	@Override
	public void writeTo(final T t, final Class<?> type, final Type genericType, final Annotation[] annotations,
			final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders,
			final OutputStream entityStream) throws IOException {

		final PrintWriter printWriter = new PrintWriter(entityStream);
		try {
			final String json = jsonEngine.toJson(t);
			printWriter.write(json);
			printWriter.flush();
		} finally {
			printWriter.close();
		}
	}
}