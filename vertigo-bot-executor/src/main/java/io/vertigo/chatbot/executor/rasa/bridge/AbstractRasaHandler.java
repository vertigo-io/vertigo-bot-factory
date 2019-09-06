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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertigo.lang.VSystemException;

public abstract class AbstractRasaHandler {

	protected static final String PYTHON_PATH = "D:\\DT\\chatbot\\factory\\python";
	protected static final String BOT_PATH = "D:\\DT\\chatbot\\factory\\bot\\";

	protected static final Logger LOGGER = LogManager.getLogger("rasa");

	protected Process execRasa(final String command, final String... additionalArgs) {
		return execRasa(command, null, null, additionalArgs);
	}

	protected Process execRasa(final String command, final Consumer<String> logConsumer, final Runnable endCallback, final String... additionalArgs) {
		final List<String> rasaCommand = new ArrayList<>();

		rasaCommand.add(PYTHON_PATH + "/Scripts/rasa");
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
				.directory(new File(BOT_PATH))
				.redirectErrorStream(true); // WTF, tout arrive dans la sortie d'erreur... Autant tout merger du coup

		Process process;
		try {
			process = builder.start();
		} catch (final IOException e) {
			throw new VSystemException(e, "Erreur lors du lancement du processus de Rasa");
		}

		logInputStream(Level.INFO, process.getInputStream(), logConsumer, endCallback);
		// logInputStream(Level.WARN, rasaProcess.getErrorStream());

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

}
