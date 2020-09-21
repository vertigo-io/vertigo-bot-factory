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

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;

import io.vertigo.chatbot.commons.domain.ExecutorConfiguration;
import io.vertigo.core.component.Activeable;
import io.vertigo.core.component.Manager;
import io.vertigo.core.param.ParamManager;
import io.vertigo.lang.VSystemException;
import io.vertigo.vega.engines.webservice.json.JsonEngine;

public class ExecutorConfigManager implements Manager, Activeable {

	@Inject
	private ParamManager paramManager;

	@Inject
	private JsonEngine jsonEngine;

	private File configDataFile;
	private ExecutorConfiguration executorConfiguration;

	@Override
	public void start() {
		final String configDataFilePath = paramManager.getParam("CONFIG_DATA_FILE").getValueAsString();
		configDataFile = new File(configDataFilePath);

		if (configDataFile.exists() && configDataFile.canRead()) {
			try {
				final String json = FileUtils.readFileToString(configDataFile, StandardCharsets.UTF_8);
				executorConfiguration = jsonEngine.fromJson(json, ExecutorConfiguration.class);
			} catch (final Exception e) {
				throw new VSystemException(e, "Error reading parameter file {0}", configDataFilePath);
			}

			// Migration purpose as 18/02/2020
			if (executorConfiguration.getNluThreshold() == null) {
				executorConfiguration.setNluThreshold(BigDecimal.valueOf(0.6));
			}
		} else {
			executorConfiguration = new ExecutorConfiguration();
		}
	}

	@Override
	public void stop() {
		// Nothing
	}


	public void saveConfig(final ExecutorConfiguration myExecutorConfiguration) {
		this.executorConfiguration = myExecutorConfiguration;

		final String json = jsonEngine.toJson(myExecutorConfiguration);

		try {
			FileUtils.writeStringToFile(configDataFile, json, StandardCharsets.UTF_8);
		} catch (final IOException e) {
			throw new VSystemException(e, "Error writing parameter file {0}", configDataFile.getPath());
		}
	}

	/**
	 * @return the executorConfiguration
	 */
	public ExecutorConfiguration getConfig() {
		return executorConfiguration;
	}



}
