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

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.Instant;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

/**
 * Deserializer of Rasa timestamp to Java Instant (rounded to millisecond). Rasa format is in seconds with fractions.
 * Ex : 1575552481.6152802 => 2019-12-05T13:28:01.615Z
 *
 * @author skerdudou
 */
public class GsonRasaTimestampDeserializer implements JsonDeserializer<Instant> {

	@Override
	public Instant deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) {
		final BigDecimal rasaTime = json.getAsBigDecimal();
		final long rasaTimeMili = rasaTime.multiply(BigDecimal.valueOf(1000)).longValue(); // rounded to milliseconds

		return Instant.ofEpochMilli(rasaTimeMili);
	}

}
