package io.vertigo.chatbot.executor.rasa.bridge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertigo.chatbot.commons.domain.ExecutorState;
import io.vertigo.chatbot.executor.domain.RasaConfig;
import io.vertigo.core.component.Activeable;
import io.vertigo.core.component.Component;
import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.dynamo.impl.file.model.FSFile;
import io.vertigo.lang.VSystemException;
import io.vertigo.lang.VUserException;

public class RasaHandler implements Component, Activeable {

	private static final String PYTHON_PATH = "D:\\DT\\chatbot\\factory\\python";
	private static final String BOT_PATH = "D:\\DT\\chatbot\\factory\\bot";
	private static final String TRAINING_MODEL_DIR = "trainingModel";

	private static final Logger LOGGER = LogManager.getLogger("rasa");

	private Process rasaProcess;
	private Process rasaTrainingProcess;

	private StringBuilder trainingLog;

	@Override
	public void start() {
		LOGGER.info("Lancement de Rasa");
		
		rasaProcess = execRasa("run", "--enable-api");
	}

	@Override
	public void stop() {
		// stop child process
		
		if (rasaProcess.isAlive()) {
			rasaProcess.destroyForcibly();
		}
		
		if (rasaTrainingProcess.isAlive()) {
			rasaTrainingProcess.destroyForcibly();
		}
	}
	

	public void trainModel(final RasaConfig config, final Long id) {
		if (isTraining()) {
			throw new VUserException("Entrainement déjà en cours");
		}
		
		writeToRasaFile(config.getDomain(), "domain.yml");
		writeToRasaFile(config.getStories(), "data/stories.md");
		writeToRasaFile(config.getNlu(), "data/nlu.md");
		
		trainingLog = new StringBuilder();
		rasaTrainingProcess = execRasa("train", trainingLog::append, () -> LOGGER.info("Entrainement terminé !"),
										"--out", TRAINING_MODEL_DIR,
										"--fixed-model-name", id.toString()
										);
	}
	
	public ExecutorState getState() {
		ExecutorState retour = new ExecutorState();
		
		retour.setName("Rasa node");
		retour.setTrainingInProgress(isTraining());
		retour.setLoadedModelVersion(-1L);
		retour.setLatestTrainingLog(getTrainingLog());
		
		return retour;
	}

	private boolean isTraining() {
		return rasaTrainingProcess != null && rasaTrainingProcess.isAlive();
	}
	
	private String getTrainingLog() {
		if (trainingLog == null) {
			return "Lancez un entrainement...";
		}
		return trainingLog.toString();
	}


	private Process execRasa(final String command, final String... additionalArgs) {
		return execRasa(command, null, null, additionalArgs);
	}
	
	private Process execRasa(final String command, final Consumer<String> logConsumer, final Runnable endCallback, final String... additionalArgs) {
			List<String> rasaCommand = new ArrayList<>();
			
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
			
			
			ProcessBuilder builder = new ProcessBuilder(rasaCommand)
					.directory(new File(BOT_PATH))
					.redirectErrorStream(true); // WTF, tout arrive dans la sortie d'erreur... Autant tout merger du coup
	
			Process process;
			try {
				process = builder.start();
			} catch (IOException e) {
				throw new VSystemException(e, "Erreur lors du lancement du processus de Rasa");
			}
	
			logInputStream(Level.INFO, process.getInputStream(), logConsumer, endCallback);
			// logInputStream(Level.WARN, rasaProcess.getErrorStream());
			
			return process;
		}


	private static void logInputStream(final Level level, final InputStream is, final Consumer<String> logConsumer, final Runnable endCallback) {
		Thread logWatcher = new Thread(() -> {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	
			String line;
			try {
				while ((line = reader.readLine()) != null) {
					LOGGER.log(level, line);
					if (logConsumer != null) {
						logConsumer.accept(line + "\r\n");						
					}
				}
			} catch (IOException e) {
				LOGGER.error("Error reading rasa output", e);
			}
			
			if (endCallback != null) {
				endCallback.run();
			}
	
		});
	
		logWatcher.start();
	}
	
	private void writeToRasaFile(final String content, final String relativePath) {
		try (FileOutputStream outputStream = new FileOutputStream(BOT_PATH + "/" + relativePath)) {
			outputStream.write(content.getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new VSystemException(e, "Impossible d'écrire le fichier de configuration de Rasa");
		}
	}

	public VFile getModel(final Long id) {
		try {
			return new FSFile(id + ".zip", "application/zip", getModelPath(id));
		} catch (IOException e) {
			throw new VSystemException(e, "Impossible de lire le fichier du modèle");
		}
	}

	public boolean delModel(final Long id) {
		return getModelPath(id).toFile().delete();
	}
	
	private Path getModelPath(final Long id) {
		return Paths.get(BOT_PATH, TRAINING_MODEL_DIR, + id + ".tar.gz");
	}
}
