package io.vertigo.chatbot.designer.analytics.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataRequestBuilder {

	private final boolean isTag;
	private final String name;
	private final String functionName;
	private final Map<String, String> functionParams;
	private final List<String> filters;
	private boolean forceNullToZero;
	private String outputName;

	public static DataRequestBuilder ofTag(final String name, final String function) {
		return new DataRequestBuilder(true, name, function);
	}

	public static DataRequestBuilder ofMeasurment(final String name, final String function) {
		return new DataRequestBuilder(false, name, function);
	}

	private DataRequestBuilder(final boolean isTag, final String name, final String functionName) {
		this.isTag = isTag;
		this.name = name;
		this.functionName = functionName;
		functionParams = new HashMap<>();
		filters = new ArrayList<>();
		forceNullToZero = false;
	}

	public DataRequestBuilder addFunctionParam(final String name, final String value) {
		functionParams.put(name, value);
		return this;
	}

	public DataRequestBuilder addFilter(final String operator, final String value) {
		final String separator = isTag ? "\"" : "";
		final String filter = operator + " " + separator + value + separator;

		filters.add(filter);
		return this;
	}

	public DataRequestBuilder forceNullToZero() {
		forceNullToZero = true;
		return this;
	}

	public DataRequestBuilder withOutputName(final String outputName) {
		this.outputName = outputName;
		return this;
	}

	public DataRequest build() {
		final var resolvedOutputName = outputName == null ? name + ":" + functionName : outputName;
		return new DataRequest(isTag, name, functionName, functionParams, filters, forceNullToZero, resolvedOutputName);
	}

}
