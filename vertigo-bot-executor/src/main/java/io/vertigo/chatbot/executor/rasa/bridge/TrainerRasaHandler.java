package io.vertigo.chatbot.executor.rasa.bridge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.vertigo.chatbot.commons.domain.BotExport;
import io.vertigo.chatbot.commons.domain.NluTrainingSentence;
import io.vertigo.chatbot.commons.domain.SmallTalkExport;
import io.vertigo.chatbot.commons.domain.TrainerInfo;
import io.vertigo.chatbot.commons.domain.UtterText;
import io.vertigo.chatbot.executor.domain.RasaConfig;
import io.vertigo.chatbot.executor.rasa.config.RasaConfigBuilder;
import io.vertigo.core.component.Activeable;
import io.vertigo.core.component.Component;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.dynamo.file.model.VFile;
import io.vertigo.dynamo.impl.file.model.FSFile;
import io.vertigo.lang.VSystemException;
import io.vertigo.lang.VUserException;

public class TrainerRasaHandler extends AbstractRasaHandler implements Component, Activeable {

	private static final String TRAINING_MODEL_DIR = "trainingModel";

	private Process rasaTrainingProcess;

	private StringBuilder trainingLog;

	private Instant startTime;
	private Instant endTime;
	private String state;

	private long trainingPercent;
	private TrainingPhases trainingPhase;
	private long phasePercent;
	private long coreAccuacy;
	private long nluAccuacy;

	private enum TrainingPhases{
		INIT(0,2), STORY(2,8), CORE(10,70), NLU(80,15), FINALIZE(95,5);

		public final long base;
		public final long size;

		private TrainingPhases(final long base, final long size) {
			this.base = base;
			this.size = size;
		}
	}

	private final Pattern coreAccuacyExtractor = Pattern.compile(".*- acc:\\s*([\\d\\.]+)");
	private final Pattern coreEpochExtractor = Pattern.compile("Epoch \\s*(\\d+)/(\\d+)");
	private final Pattern nluInfoExtractor = Pattern.compile("Epochs:\\s*(\\d+).*acc=([\\d\\.]+)");


	@Override
	public void start() {
		state = "Ready";
	}

	@Override
	public void stop() {
		// stop child process
		if (rasaTrainingProcess != null && rasaTrainingProcess.isAlive()) {
			rasaTrainingProcess.destroyForcibly();
		}
	}


	public void trainModel(final BotExport bot, final DtList<SmallTalkExport> smallTalkList, final Long modelId, final Consumer<Boolean> trainingCallback) {
		if (isTraining()) {
			throw new VUserException("Training already in progress");
		}
		startTime = Instant.now();
		endTime = null;
		state = "Training";
		trainingPhase = TrainingPhases.INIT;
		trainingPercent = 0;
		coreAccuacy = -1;
		nluAccuacy = -1;

		final RasaConfig config = generateRasaConfig(bot, smallTalkList);

		writeToRasaFile(config.getDomain(), "domain.yml");
		writeToRasaFile(config.getStories(), "data/stories.md");
		writeToRasaFile(config.getNlu(), "data/nlu.md");

		trainingLog = new StringBuilder();

		rasaTrainingProcess = execRasa("train", this::processTrainingLog, () -> postTrainModel(modelId, trainingCallback),
				"--out", TRAINING_MODEL_DIR,
				"--fixed-model-name", modelId.toString()
				);
	}

	private void processTrainingLog(final String in) {
		trainingLog.append(in);

		updateTrainingPhase(in);
		updatePhasePercent(in);
		updateTrainingPercent();
	}

	private void updateTrainingPhase(final String logLine) {
		if (logLine.startsWith("Training Core model...")) {
			trainingPhase = TrainingPhases.STORY;
			phasePercent = 0;
		} else if (logLine.contains("rasa.core.policies.keras_policy  - Fitting model with")) {
			trainingPhase = TrainingPhases.CORE;
			phasePercent = 0;
		} else if (logLine.startsWith("Training NLU model...")) {
			trainingPhase = TrainingPhases.NLU;
			phasePercent = 0;
		} else if (logLine.startsWith("NLU model training completed.")) {
			trainingPhase = TrainingPhases.FINALIZE;
			phasePercent = 0;
		}
	}

	private void updatePhasePercent(final String logLine) {
		switch (trainingPhase) {
		case CORE:
			final Matcher matcherEpoch = coreEpochExtractor.matcher(logLine);
			if (matcherEpoch.find()) {
				phasePercent = (100 * Long.valueOf(matcherEpoch.group(1))) / Long.valueOf(matcherEpoch.group(2));
			} else {
				final Matcher matcherAccuacy = coreAccuacyExtractor.matcher(logLine);
				if (matcherAccuacy.find()) {
					coreAccuacy =  Math.round(Float.valueOf(matcherAccuacy.group(1)) * 100);
				}
			}

			break;
		case NLU:
			final Matcher matcherNlu = nluInfoExtractor.matcher(logLine);
			if (matcherNlu.find()) {
				phasePercent = Long.valueOf(matcherNlu.group(1));
				nluAccuacy =  Math.round(Float.valueOf(matcherNlu.group(2)) * 100);
			}

			break;
		default:
			phasePercent = 0;
			break;
		}

	}

	private void updateTrainingPercent() {
		trainingPercent = trainingPhase.base + (trainingPhase.size * phasePercent / 100);
	}

	private void postTrainModel(final Long id, final Consumer<Boolean> trainingCallback) {
		LOGGER.info("Training complete !");

		endTime = Instant.now();

		final File modelFile = getModelPath(id).toFile();
		if (modelFile.exists()) {
			state = "Success";
			trainingCallback.accept(true);
		} else {
			state = "Error";
			trainingCallback.accept(false);
		}
	}

	public TrainerInfo getState() {
		final TrainerInfo retour = new TrainerInfo();

		retour.setName("Rasa node");
		retour.setTrainingState(state);
		retour.setTrainingInProgress(isTraining());
		retour.setLatestTrainingLog(getTrainingLog());
		retour.setStartTime(startTime);
		retour.setEndTime(endTime);
		retour.setTrainingPercent(trainingPercent);

		return retour;
	}

	public boolean isTraining() {
		return rasaTrainingProcess != null && rasaTrainingProcess.isAlive();
	}

	public String getTrainingLog() {
		if (trainingLog == null) {
			return "";
		}
		return trainingLog.toString();
	}


	public long getCoreAccuacy() {
		return coreAccuacy;
	}

	public long getNluAccuacy() {
		return nluAccuacy;
	}

	private void writeToRasaFile(final String content, final String relativePath) {
		try (FileOutputStream outputStream = new FileOutputStream(BOT_PATH + "/" + relativePath)) {
			outputStream.write(content.getBytes(StandardCharsets.UTF_8));
		} catch (final IOException e) {
			throw new VSystemException(e, "Impossible d'écrire le fichier de configuration de Rasa");
		}
	}

	public VFile getModel(final Long id) {
		try {
			return new FSFile(id + ".tar.gz", "application/zip", getModelPath(id));
		} catch (final IOException e) {
			throw new VSystemException(e, "Impossible de lire le fichier du modèle");
		}
	}

	public boolean delModel(final Long id) {
		return getModelPath(id).toFile().delete();
	}

	private Path getModelPath(final Long id) {
		return Paths.get(BOT_PATH, TRAINING_MODEL_DIR, + id + ".tar.gz");
	}

	public void stopTrain() {
		LOGGER.info("Training aborted !");
		stop();

		trainingLog.append("\n*** User stopped training ***");
	}

	private static RasaConfig generateRasaConfig(final BotExport bot, final DtList<SmallTalkExport> smallTalkList) {
		final String defaultText = bot.getDefaultText().getText();
		final String welcomeText = bot.getWelcomeText().getText();

		final RasaConfigBuilder rasaConfigBuilder = new RasaConfigBuilder(defaultText, welcomeText);


		for (final SmallTalkExport st : smallTalkList) {
			final List<String> utterTexts = st.getUtterTexts().stream()
					.map(UtterText::getText)
					.collect(Collectors.toList());

			final List<String> trainingSentences = st.getNluTrainingSentences().stream()
					.map(NluTrainingSentence::getText)
					.collect(Collectors.toList());

			rasaConfigBuilder.addSmallTalk(st.getSmallTalk().getTitle(), trainingSentences, utterTexts);
		}

		return rasaConfigBuilder.build();
	}


}
