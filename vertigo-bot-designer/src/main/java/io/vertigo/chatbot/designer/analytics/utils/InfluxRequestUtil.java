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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.FluxColumn;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;

import io.vertigo.core.lang.Assertion;
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

	public static TimedDatas getTimeSeries(final InfluxDBClient influxDBClient, final String appName, final List<DataRequest> dataRequests, final DataFilter dataFilter, final TimeFilter timeFilter) {
		Assertion.check()
				.isNotNull(dataRequests)
				.isNotNull(dataFilter)
				.isNotNull(timeFilter.getDim());// we check dim is not null because we need it
		//---
		final String q = buildTimedQuery(appName, dataRequests, dataFilter, timeFilter)
				.toString();

		return executeTimedQuery(influxDBClient, q);

	}

	private static StringBuilder buildTimedQuery(final String appName, final List<DataRequest> dataRequests, final DataFilter dataFilter, final TimeFilter timeFilter) {
		final StringBuilder queryBuilder = buildGlobalDataVariable(appName, dataFilter, timeFilter);

		for (int i = 0; i < dataRequests.size(); i++) {
			queryBuilder.append("aggData").append(i).append(" = data \n");
			dataRequests.get(i).addFluxProcessing(queryBuilder, timeFilter);
		}

		if (dataRequests.size() == 1) {
			queryBuilder.append("aggData0 \n");
		} else {
			// union works with 2 tables minimum
			queryBuilder.append("union(tables: [")
					.append(IntStream.range(0, dataRequests.size()).mapToObj(i -> "aggData" + i).collect(Collectors.joining(", ")))
					.append("]) \n");
		}
		queryBuilder.append("|> pivot(rowKey:[\"_time\"], columnKey: [\"_field\"], valueColumn: \"_value\") \n")
				// add default values
				.append("|> map(fn: (r) => ({ r with ")
				.append(dataRequests.stream().map(DataRequest::getOutputName).map(m -> "\"" + m + "\": if exists r[\"" + m + "\"] then r[\"" + m + "\"] else 0.0").collect(Collectors.joining(", ")))
				.append("}))\n")
				.append("|> group()");

		return queryBuilder;
	}

	private static StringBuilder buildGlobalDataVariable(final String appName, final DataFilter dataFilter, final TimeFilter timeFilter) {
		final StringBuilder dataVariableBuilder = new StringBuilder("data = from(bucket:\"" + appName + "\") \n")
				.append("|> range(start: " + timeFilter.getFrom() + ", stop: " + timeFilter.getTo() + ") \n")
				.append("|> filter(fn: (r) => \n")
				.append("r._measurement == \"" + dataFilter.getMeasurement() + "\" \n");

		if (!dataFilter.getFilters().isEmpty()) {
			// filter tags values
			dataVariableBuilder
					.append(
							dataFilter.getFilters().entrySet().stream()
									.filter(e -> e.getValue() != null && !"*".equals(e.getValue()))
									.map(e -> " and r." + e.getKey() + " == \"" + e.getValue() + "\"")
									.collect(Collectors.joining()))
					.append(" \n");
		}
		dataVariableBuilder.append(")\n")// end filter
				.append("|> group()\n")
				.append('\n'); // end data variable declaration
		return dataVariableBuilder;
	}

	private static Map<String, Object> buildMapValue(final FluxRecord record) {
		final Map<String, Object> values = new HashMap<>(record.getValues());
		values.remove("_time");
		values.remove("table");
		values.remove("result");
		return values;
	}

}
