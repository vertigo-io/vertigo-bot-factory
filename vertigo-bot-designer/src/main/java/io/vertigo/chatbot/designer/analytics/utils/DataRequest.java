package io.vertigo.chatbot.designer.analytics.utils;

import java.util.List;
import java.util.Map;

import io.vertigo.core.lang.Assertion;
import io.vertigo.database.timeseries.TimeFilter;

public class DataRequest {

	private final boolean isTag;
	private final String name;
	private final String functionName;
	private final Map<String, String> functionParams;
	private final List<String> filters;
	private final boolean forceNullToZero;

	private final String outputName;

	public DataRequest(final boolean isTag, final String name, final String functionName, final Map<String, String> functionParams, final List<String> filters, final boolean forceNullToZero,
			final String outputName) {
		Assertion.check().isFalse(forceNullToZero && !filters.isEmpty(), "can't force null to zero if filtering on certain data.");

		this.isTag = isTag;
		this.name = name;
		this.functionName = functionName;
		this.functionParams = functionParams;
		this.filters = filters;
		this.forceNullToZero = forceNullToZero;
		this.outputName = outputName;
	}

	/**
	 * @return the outputName
	 */
	public String getOutputName() {
		return outputName;
	}

	public void addFluxProcessing(final StringBuilder script, final TimeFilter timeFilter) {
		addFilter(script);
		script.append("|> keep(columns: [\"_time\", \"").append(name).append("\"])\n"); // keep _time and data column only (speed up the request)
		script.append("|> rename(columns: {").append(name).append(": \"_value\"})\n");
		if (forceNullToZero) {
			final String defaultValue = isTag ? "\"0\"" : "0";
			script.append("|> map(fn: (r) => ({ r with \"_value\": if exists r._value and r._value != \"\" then r._value else ")
					.append(defaultValue)
					.append("\"}))\n");
		}

		script.append("|> window(every: ").append(timeFilter.getDim()).append(", createEmpty:true )\n");
		if (functionName != "count") {
			script.append("|> toFloat()\n");
		}
		addFunctionCall(script);
		script.append("|> set(key: \"_field\", value:\"").append(outputName).append("\" )\n")
				.append("|> toFloat() \n")
				.append("|> rename(columns: {_start: \"_time\"})\n")
				.append("|> drop(columns: [ \"_stop\"]) \n\n");
	}

	private void addFilter(final StringBuilder script) {
		final String measureFilter = isTag ? "name" : name;

		script.append("|> filter(fn: (r) => r._field == \"").append(measureFilter).append("\"");
		for (final var filter : filters) {
			script.append(" and r.")
					.append(name)
					.append(" ")
					.append(filter);
		}
		script.append(")\n"); // end filter
	}

	private void addFunctionCall(final StringBuilder script) {
		script.append("|> ").append(functionName).append("(");

		boolean previousParam = false;
		for (final var paramEntry : functionParams.entrySet()) {
			if (previousParam) {
				script.append(", ");
			}
			script.append(paramEntry.getKey())
					.append(":")
					.append(paramEntry.getValue());

			previousParam = true;
		}

		script.append(")\n");
	}

}
