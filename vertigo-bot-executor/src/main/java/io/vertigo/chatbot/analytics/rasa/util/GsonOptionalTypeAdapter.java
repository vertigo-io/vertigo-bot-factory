/**
 * vertigo - simple java starter
 *
 * Copyright (C) 2020, Vertigo.io, team@vertigo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vertigo.chatbot.analytics.rasa.util;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class GsonOptionalTypeAdapter<E> extends TypeAdapter<Optional<E>> {

	public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
		@Override
		public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
			final Class<T> rawType = (Class<T>) type.getRawType();
			if (rawType != Optional.class) {
				return null;
			}
			final ParameterizedType parameterizedType = (ParameterizedType) type.getType();
			final Type actualType = parameterizedType.getActualTypeArguments()[0];
			final TypeAdapter<?> myAdapter = gson.getAdapter(TypeToken.get(actualType));
			return new GsonOptionalTypeAdapter(myAdapter);
		}
	};
	private final TypeAdapter<E> adapter;

	public GsonOptionalTypeAdapter(final TypeAdapter<E> adapter) {

		this.adapter = adapter;
	}

	@Override
	public void write(final JsonWriter out, final Optional<E> value) throws IOException {
		if (value.isPresent()) {
			adapter.write(out, value.get());
		} else {
			out.nullValue();
		}
	}

	@Override
	public Optional<E> read(final JsonReader in) throws IOException {
		final JsonToken peek = in.peek();
		if (peek != JsonToken.NULL) {
			return Optional.ofNullable(adapter.read(in));
		}
		in.nextNull();
		return Optional.empty();
	}

}
