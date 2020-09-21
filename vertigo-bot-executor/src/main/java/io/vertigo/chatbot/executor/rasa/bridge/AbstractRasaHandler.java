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
package io.vertigo.chatbot.executor.rasa.bridge;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertigo.core.component.Activeable;
import io.vertigo.core.component.Component;
import io.vertigo.core.param.ParamManager;
import io.vertigo.lang.VSystemException;

public abstract class AbstractRasaHandler implements Component, Activeable {

	protected static final Logger LOGGER = LogManager.getLogger("rasa");

	@Inject
	private ParamManager paramManager;

	private String rasaPath;
	private String botPath;

	@Override
	public void start() {
		rasaPath = paramManager.getParam("RASA_EXE_PATH").getValueAsString();
		botPath = paramManager.getParam("RASA_BOT_PATH").getValueAsString();

		LOGGER.info("Using rasaPath {}", rasaPath);
		LOGGER.info("Using botPath {}", botPath);
	}

	protected Process execRasa(final String command, final String... additionalArgs) {
		return execRasa(command, null, null, additionalArgs);
	}

	protected Process execRasa(final String command, final Consumer<String> logConsumer, final Runnable endCallback, final String... additionalArgs) {
		final List<String> rasaCommand = new ArrayList<>();

		rasaCommand.add(rasaPath);
		rasaCommand.add(command);
		Collections.addAll(rasaCommand, additionalArgs);

		//		rasaCommand.add("--cors");
		//		rasaCommand.add("*");

		if (LOGGER.isDebugEnabled()) {
			rasaCommand.add("-vv"); // DEBUG
		} else if (LOGGER.isInfoEnabled()) {
			rasaCommand.add("-v"); // INFO
		} else {
			//			rasaCommand.add("--quiet"); // WARNING
		}

		final ProcessBuilder builder = new ProcessBuilder(rasaCommand)
				.directory(new File(botPath))
				.redirectErrorStream(true); // WTF, tout arrive dans la sortie d'erreur... Autant tout merger du coup

		Process process;
		try {
			process = builder.start();
		} catch (final IOException e) {
			throw new VSystemException(e, "Erreur lors du lancement du processus de Rasa");
		}

		logInputStream(Level.INFO, process.getInputStream(), logConsumer, endCallback);
		//		logInputStream(Level.INFO, process.getErrorStream(), logConsumer, null);

		return process;
	}

	private static void logInputStream(final Level level, final InputStream is, final Consumer<String> logConsumer, final Runnable endCallback) {
		final Thread logWatcher = new Thread(() -> {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(is));

			String line;
			try {
				while ((line = reader.readLine()) != null) {
					LOGGER.log(level, line);
					if (logConsumer != null) {
						logConsumer.accept(line + "\r\n");
					}
				}
			} catch (final IOException e) {
				LOGGER.error("Error reading rasa output", e);
			}

			if (endCallback != null) {
				endCallback.run();
			}

		});

		logWatcher.start();
	}

	/**
	 * @return the botPath
	 */
	public String getBotPath() {
		return botPath;
	}

}
