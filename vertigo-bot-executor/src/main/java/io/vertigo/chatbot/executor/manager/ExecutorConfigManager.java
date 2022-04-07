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
package io.vertigo.chatbot.executor.manager;

import io.vertigo.chatbot.commons.domain.BotExport;
import io.vertigo.chatbot.engine.services.FileServices;
import io.vertigo.chatbot.executor.ExecutorPlugin;
import io.vertigo.chatbot.executor.model.ExecutorGlobalConfig;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.node.component.Activeable;
import io.vertigo.core.node.component.Manager;
import io.vertigo.core.param.Param;
import io.vertigo.core.param.ParamManager;
import io.vertigo.vega.engines.webservice.json.JsonEngine;
import org.apache.commons.io.FileUtils;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExecutorConfigManager implements Manager, Activeable {

	private final ParamManager paramManager;
	private final JsonEngine jsonEngine;

	private File configDataFile;
	private File contextDataFile;
	private ExecutorGlobalConfig executorGlobalConfig;
	private HashMap<String, String> contextMap;
	private final List<ExecutorPlugin> plugins = new ArrayList<>();

	@Inject
	private FileServices fileServices;

	@Inject
	public ExecutorConfigManager(
			final ParamManager paramManager,
			final JsonEngine jsonEngine) {

		Assertion.check()
				.isNotNull(paramManager)
				.isNotNull(jsonEngine);
		//--
		this.paramManager = paramManager;
		this.jsonEngine = jsonEngine;
	}

	@Override
	public void start() {

		final String configDataFilePath = paramManager.getOptionalParam("CONFIG_DATA_FILE").map(Param::getValueAsString).orElse("/tmp/runnerConfig");

		configDataFile = new File(configDataFilePath);

		if (configDataFile.exists() && configDataFile.canRead()) {
			try {
				final String json = FileUtils.readFileToString(configDataFile, StandardCharsets.UTF_8);
				executorGlobalConfig = jsonEngine.fromJson(json, ExecutorGlobalConfig.class);
			} catch (final Exception e) {
				throw new VSystemException(e, "Error reading parameter file {0}", configDataFilePath);
			}
			plugins.forEach(executorPlugin -> executorPlugin.refreshConfig(executorGlobalConfig));

			// Migration purpose as 18/02/2020
			if (executorGlobalConfig.getExecutorConfiguration() != null && executorGlobalConfig.getExecutorConfiguration().getNluThreshold() == null) {
				executorGlobalConfig.getExecutorConfiguration().setNluThreshold(BigDecimal.valueOf(0.6));
			}
		} else {
			executorGlobalConfig = new ExecutorGlobalConfig();
		}
		final String contextDataFilePath = paramManager.getOptionalParam("CONTEXT_DATA_FILE").map(Param::getValueAsString).orElse("/tmp/contextConfig");
		contextDataFile = new File(contextDataFilePath);
		if (contextDataFile.exists() && contextDataFile.canRead()) {
			try {
				final String json = FileUtils.readFileToString(contextDataFile, StandardCharsets.UTF_8);
				contextMap = jsonEngine.fromJson(json, HashMap.class);
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			contextMap = new HashMap<String, String>();
		}
	}

	@Override
	public void stop() {
		// Nothing
	}

	public synchronized void saveConfig(final ExecutorGlobalConfig executorGlobalConfig) {
		this.executorGlobalConfig = executorGlobalConfig;
		plugins.forEach(executorPlugin -> executorPlugin.refreshConfig(executorGlobalConfig));
		final String json = jsonEngine.toJson(executorGlobalConfig);

		try {
			FileUtils.writeStringToFile(configDataFile, json, StandardCharsets.UTF_8);
		} catch (final IOException e) {
			throw new VSystemException(e, "Error writing parameter file {0}", configDataFile.getPath());
		}
	}

	/**
	 * @return the executorConfiguration
	 */
	public ExecutorGlobalConfig getConfig() {
		return executorGlobalConfig;
	}

	public HashMap<String, String> getContextMap() {
		return contextMap;
	}

	public synchronized void updateMapContext(final BotExport botExport) {

		try {
			FileUtils.writeStringToFile(contextDataFile, botExport.getMapContext(), StandardCharsets.UTF_8);
			contextMap = jsonEngine.fromJson(botExport.getMapContext(), HashMap.class);
		} catch (final IOException e) {
			throw new VSystemException(e, "Error writing parameter file {0}", contextDataFile.getPath());
		}
	}

	public void addPlugin(final ExecutorPlugin executorPlugin) {
		plugins.add(executorPlugin);
	}
}
