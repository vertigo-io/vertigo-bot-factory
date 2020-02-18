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


	public void saveConfig(final ExecutorConfiguration executorConfiguration) {
		this.executorConfiguration = executorConfiguration;

		final String json = jsonEngine.toJson(executorConfiguration);

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
