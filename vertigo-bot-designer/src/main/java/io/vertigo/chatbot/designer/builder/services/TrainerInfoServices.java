package io.vertigo.chatbot.designer.builder.services;

import io.vertigo.account.authorization.annotations.SecuredOperation;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.ChatbotNode;
import io.vertigo.chatbot.commons.domain.TrainerInfo;
import io.vertigo.chatbot.commons.domain.Training;
import io.vertigo.chatbot.commons.domain.TrainingStatusEnum;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Optional;

@Transactional
public class TrainerInfoServices implements Component {

	@Inject
	private TrainingServices trainingServices;

	@Inject
	private NodeServices nodeServices;

	public TrainerInfo createTrainingState(final Chatbot bot) {
		final Optional<Training> currentTrainingOpt = trainingServices.getCurrentTraining(bot);
		if (currentTrainingOpt.isPresent()) {
			final Training training = currentTrainingOpt.get();
			return createTrainerInfo(training.getTraId(), true, training.getStartTime(), "Training" + training.getVersionNumber(), TrainingStatusEnum.TRAINING.name(), null);
		} else {
			return new TrainerInfo();
		}
	}

	public TrainerInfo createTrainingState(final Training training) {
		return createTrainerInfo(training.getTraId(),
				training.trainingStatus().getEnumValue() == TrainingStatusEnum.TRAINING,
				training.getStartTime(), "Training" + training.getVersionNumber(),
				training.trainingStatus().getEnumValue().name(), null);
	}

	public TrainerInfo createTrainerInfo(final Long traId, final boolean isTraining, final Instant startTime, final String name, final String strCd, final Instant endTime) {
		final TrainerInfo retour = new TrainerInfo();
		retour.setTraId(traId);
		retour.setTrainingInProgress(isTraining);
		retour.setStartTime(startTime);
		retour.setName(name);
		retour.setTrainingState(strCd);
		retour.setEndTime(endTime);
		return retour;
	}

	public TrainerInfo getTrainingState(@SecuredOperation("botContributor") final Chatbot bot, final TrainerInfo info) {
		final Optional<ChatbotNode> optDevNode = nodeServices.getDevNodeByBotId(bot.getBotId());

		if (info.getTraId() == null) {
			return new TrainerInfo();
		}

		if (!optDevNode.isPresent()) {
			final TrainerInfo trainerInfo = new TrainerInfo();
			trainerInfo.setName("No training node configured");
			return trainerInfo;
		}

		final Training training = trainingServices.getTraining(bot, info.getTraId());
		final String name = "Training" + training.getVersionNumber();
		final boolean isTraining = TrainingStatusEnum.TRAINING.name().equals(training.getStrCd());
		return createTrainerInfo(training.getTraId(), isTraining, training.getStartTime(), name, training.getStrCd(), training.getEndTime());
	}
}
