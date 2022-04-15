/**
 * vertigo - application development platform
 *
 * Copyright (C) 2013-2022, Vertigo.io, team@vertigo.io
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
package io.vertigo.chatbot.designer.analytics.utils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.vertigo.core.lang.Assertion;
import io.vertigo.database.timeseries.TimeFilter;

/**
 * Light builder designed for flat tabular datas and to improve readability.
 *
 * @author skerdudou
 */
public final class InfluxRequestBuilder {

	private final StringBuilder request;

	public InfluxRequestBuilder(final String appName) {
		request = new StringBuilder("from(bucket:\"")
				.append(appName)
				.append("\") \n");
	}

	public InfluxRequestBuilder filterFields(final String measurement, final List<String> fields) {
		Assertion.check().isFalse(fields.isEmpty(), "Need at least one field");

		request.append("|> filter(fn: (r) => ")
				.append("r._measurement == \"")
				.append(measurement)
				.append("\" and (")
				.append(fields.stream().map(f -> "r._field==\"" + f + "\"").collect(Collectors.joining(" or ")))
				.append("))\n");
		return this;
	}

	public InfluxRequestBuilder filterColumn(final Map<String, String> filters) {
		if (filters.isEmpty()) {
			return this;
		}

		filter(filters.entrySet().stream()
				.map(e -> "exists r." + e.getKey() + " and r." + e.getKey() + " == " + e.getValue())
				.collect(Collectors.joining(" and ")));
		return this;
	}

	public InfluxRequestBuilder filter(final String filter) {
		request.append("|> filter(fn: (r) => ")
				.append(filter)
				.append(")\n");
		return this;
	}

	public InfluxRequestBuilder range(final TimeFilter timeFilter) {
		request.append("|> range(start: ")
				.append(timeFilter.getFrom())
				.append(", stop: ")
				.append(timeFilter.getTo())
				.append(") \n");

		return this;
	}

	public InfluxRequestBuilder keep(final List<String> columns) {
		request.append("|> keep(columns: [")
				.append(columns.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(",")))
				.append("]) \n");

		return this;
	}

	public InfluxRequestBuilder group(final List<String> columns) {
		request.append("|> group(columns: [")
				.append(columns.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(",")))
				.append("]) \n");

		return this;
	}

	public InfluxRequestBuilder pivot() {
		request.append("|> pivot(rowKey:[\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\") \n");
		return this;
	}

	public InfluxRequestBuilder append(final String s) {
		request.append(s).append("\n");
		return this;
	}

	public String build() {
		return request
				.append("|> group() \n") // merge all tables
				.toString();
	}

	public String build(final long limit) {
		return request
				.append("|> group() \n") // merge all tables
				.append("|> limit(n:").append(limit).append(") \n") // limit is per table (must be done after group())
				.toString();
	}

}
