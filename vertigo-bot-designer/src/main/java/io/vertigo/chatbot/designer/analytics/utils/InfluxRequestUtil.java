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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.FluxColumn;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;

import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.Tuple;
import io.vertigo.database.timeseries.DataFilter;
import io.vertigo.database.timeseries.TabularDataSerie;
import io.vertigo.database.timeseries.TabularDatas;
import io.vertigo.database.timeseries.TimeFilter;
import io.vertigo.database.timeseries.TimedDataSerie;
import io.vertigo.database.timeseries.TimedDatas;

/**
 * Inspired by FluxInfluxDbTimeSeriesPlugin to handle filter on field values (could be considered as a bad practice, tags are designed for filtering and fields for aggregation).
 *
 * @author skerdudou
 */
public final class InfluxRequestUtil {

	private InfluxRequestUtil() {
		// util
	}

	public static TabularDatas executeTabularQuery(final InfluxDBClient influxDBClient, final String q) {
		final List<FluxTable> queryResult = influxDBClient.getQueryApi().query(q);
		if (!queryResult.isEmpty()) {
			final FluxTable table = queryResult.get(0);
			if (table.getRecords() != null && !table.getRecords().isEmpty()) {

				final List<TabularDataSerie> dataSeries = table.getRecords()
						.stream()
						.map(record -> new TabularDataSerie(
								buildMapValue(record)))
						.collect(Collectors.toList());
				return new TabularDatas(dataSeries, table.getColumns().stream()
						.filter(column -> !"table".equals(column.getLabel()))
						.filter(column -> !"result".equals(column.getLabel()))
						.map(FluxColumn::getLabel).collect(Collectors.toList()));
			}
		}
		return new TabularDatas(Collections.emptyList(), Collections.emptyList());

	}

	public static TimedDatas executeTimedQuery(final InfluxDBClient influxDBClient, final String q) {
		final List<FluxTable> queryResult = influxDBClient.getQueryApi().query(q);
		if (!queryResult.isEmpty()) {
			final FluxTable table = queryResult.get(0);
			if (table.getRecords() != null && !table.getRecords().isEmpty()) {

				final List<TimedDataSerie> dataSeries = table.getRecords()
						.stream()
						.map(record -> new TimedDataSerie(
								record.getTime(),
								buildMapValue(record)))
						.collect(Collectors.toList());
				return new TimedDatas(dataSeries, table.getColumns().stream()
						.filter(column -> !"_time".equals(column.getLabel()))
						.filter(column -> !"table".equals(column.getLabel()))
						.filter(column -> !"result".equals(column.getLabel()))
						.map(FluxColumn::getLabel).collect(Collectors.toList()));//we remove the time
			}
		}
		return new TimedDatas(Collections.emptyList(), Collections.emptyList());
	}

	private static String buildGlobalDataVariable(final String appName, final List<String> measures, final DataFilter dataFilter, final Map<String, String> fieldFilters, final TimeFilter timeFilter,
			final String... groupBy) {
		final StringBuilder dataVariableBuilder = new StringBuilder("data = from(bucket:\"" + appName + "\") \n")
				.append("|> range(start: " + timeFilter.getFrom() + ", stop: " + timeFilter.getTo() + ") \n")
				.append("|> filter(fn: (r) => \n")
				.append("r._measurement == \"" + dataFilter.getMeasurement() + "\" \n");

		final Set<String> fields = getMeasureFields(measures);
		// add the global data with all the fields we need
		if (!fields.isEmpty()) {
			dataVariableBuilder.append("and (");
			dataVariableBuilder.append(
					Stream.concat(fields.stream(), fieldFilters.keySet().stream())
							.distinct()
							.map(field -> "r._field ==\"" + field + "\"")
							.collect(Collectors.joining(" or ")));

			dataVariableBuilder.append(") \n");
		}
		if (!dataFilter.getFilters().isEmpty()) {
			// filter tags values
			dataVariableBuilder
					.append(
							dataFilter.getFilters().entrySet().stream()
									.filter(e -> e.getValue() != null && !"*".equals(e.getValue()))
									.map(e -> " and (not exists r." + e.getKey() + " or r." + e.getKey() + "==\"" + e.getValue() + "\")")
									.collect(Collectors.joining()))
					.append(" \n");
		}
		dataVariableBuilder.append(")\n");// end filter

		final String groupByFields = Stream.of(groupBy).collect(Collectors.joining("\", \"", "\"", "\""));
		dataVariableBuilder
				.append("|> keep(columns: [\"_time\",\"_field\", \"_value\"" + (groupBy.length > 0 ? ", " + groupByFields : "") + "]) \n")
				.append("|> pivot(rowKey:[\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\") \n");

		// filter fields values
		if (!fieldFilters.isEmpty()) {
			dataVariableBuilder.append("|> filter(fn: (r) => (1==1 and ") // 1==1 in case of empty stream after filter
					.append(
							fieldFilters.entrySet().stream()
									.filter(e -> e.getValue() != null && !"*".equals(e.getValue()))
									.map(e -> "exists r." + e.getKey() + " and r." + e.getKey() + "==" + e.getValue())
									.collect(Collectors.joining(" and ")))
					.append(")) \n");
		}

		dataVariableBuilder.append('\n'); // end data variable declaration
		return dataVariableBuilder.toString();
	}

	public static TimedDatas getTimeSeries(final InfluxDBClient influxDBClient, final String appName, final List<String> measures, final DataFilter dataFilter, final Map<String, String> fieldFilters,
			final TimeFilter timeFilter) {
		Assertion.check()
				.isNotNull(measures)
				.isNotNull(dataFilter)
				.isNotNull(timeFilter.getDim());// we check dim is not null because we need it
		//---
		final String q = buildTimedQuery(appName, measures, dataFilter, fieldFilters, timeFilter)
				.toString();

		return executeTimedQuery(influxDBClient, q);

	}

	private static StringBuilder buildTimedQuery(final String appName, final List<String> measures, final DataFilter dataFilter, final Map<String, String> fieldFilters, final TimeFilter timeFilter) {
		final String globalDataVariable = buildGlobalDataVariable(appName, measures, dataFilter, fieldFilters, timeFilter, new String[] {});
		final StringBuilder queryBuilder = new StringBuilder(globalDataVariable);

		for (int i = 0; i < measures.size(); i++) {
			final var measure = measures.get(i);
			final String[] measureDetails = measure.split(":");
			final String measureField = measureDetails[0];
			final String measureFunction = buildMeasureFunction(measureDetails[1]);

			queryBuilder.append("aggData").append(i).append(" = data \n")
					.append("|> filter(fn: (r) => exists r.").append(measureField).append(") \n")
					.append("|> rename(columns: {").append(measureField).append(": \"_value\"}) \n")
					.append("|> window(every: ").append(timeFilter.getDim()).append(", createEmpty:true ) \n")
					.append("|> ").append(measureFunction).append(" \n")
					.append("|> set(key: \"_field\", value:\"").append(measure).append("\" ) \n")
					.append("|> toFloat() \n")
					.append("|> rename(columns: {_start: \"_time\"}) \n")
					.append("|> drop(columns: [ \"_stop\"]) \n\n");
		}

		if (measures.size() == 1) {
			queryBuilder.append("aggData0 \n");
		} else {
			// union works with 2 tables minimum
			queryBuilder.append("union(tables: [")
					.append(IntStream.range(0, measures.size()).mapToObj(i -> "aggData" + i).collect(Collectors.joining(", ")))
					.append("]) \n");
		}
		queryBuilder.append("|> pivot(rowKey:[\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\") \n")
				// add default values
				.append("|> map(fn: (r) => ({ r with ")
				.append(measures.stream().map(m -> "\"" + m + "\": if exists r[\"" + m + "\"] then r[\"" + m + "\"] else 0.0").collect(Collectors.joining(", ")))
				.append("}))\n")
				.append("|> group()");

		return queryBuilder;
	}

	private static Set<String> getMeasureFields(final List<String> measures) {
		final Set<String> fields = new HashSet<>();
		for (final String measure : measures) {
			final String[] measureDetails = measure.split(":");
			fields.add(measureDetails[0]);
		}
		return fields;
	}

	private static String buildMeasureFunction(final String function) {
		final Tuple<String, List<String>> aggregateFunction = parseAggregateFunction(function);
		// append function name
		final StringBuilder measureQueryBuilder = new java.lang.StringBuilder(aggregateFunction.getVal1()).append("(");
		// append parameters
		if (!aggregateFunction.getVal2().isEmpty()) {

			measureQueryBuilder.append(aggregateFunction.getVal2()
					.stream()
					.map(param -> param.split("_"))
					.map(paramAsArray -> paramAsArray[0] + ": " + paramAsArray[1])
					.collect(Collectors.joining(", ")));
		}
		measureQueryBuilder.append(')');
		return measureQueryBuilder.toString();
	}

	private static Tuple<String, List<String>> parseAggregateFunction(final String aggregateFunction) {
		final int firstSeparatorIndex = aggregateFunction.indexOf("__");
		if (firstSeparatorIndex > -1) {
			return Tuple.of(
					aggregateFunction.substring(0, firstSeparatorIndex),
					Arrays.asList(aggregateFunction.substring(firstSeparatorIndex + 2).split("__")));
		}
		return Tuple.of(aggregateFunction, Collections.emptyList());
	}

	private static Map<String, Object> buildMapValue(final FluxRecord record) {
		final Map<String, Object> values = new HashMap<>(record.getValues());
		values.remove("_time");
		values.remove("table");
		values.remove("result");
		return values;
	}

}
