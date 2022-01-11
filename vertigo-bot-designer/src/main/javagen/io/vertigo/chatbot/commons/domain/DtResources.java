package io.vertigo.chatbot.commons.domain;

import io.vertigo.core.locale.MessageKey;

/**
 * Attention cette classe est générée automatiquement !
 * Resources du module io.vertigo.chatbot.commons.domain
 */
public enum DtResources implements MessageKey {

	/***********************************************************
	/** BotExport.
	/***********************************************************
	/**
	 * chatbot.
	 */
	fldBotExport$bot,
	/**
	 * welcome BT.
	 */
	fldBotExport$welcomeBT,
	/**
	 * end BT.
	 */
	fldBotExport$endBT,
	/**
	 * fallback BT.
	 */
	fldBotExport$fallbackBT,
	/**
	 * idle BT.
	 */
	fldBotExport$idleBT,
	/**
	 * topics export.
	 */
	fldBotExport$topics,
	/**
	 * Welcome tours map.
	 */
	fldBotExport$welcomeTours,
	/**
	 * map Context.
	 */
	fldBotExport$mapContext,

	/***********************************************************
	/** Chatbot.
	/***********************************************************
	/**
	 * ID.
	 */
	fldChatbot$botId,
	/**
	 * Name.
	 */
	fldChatbot$name,
	/**
	 * Description.
	 */
	fldChatbot$description,
	/**
	 * Creation date.
	 */
	fldChatbot$creationDate,
	/**
	 * Status.
	 */
	fldChatbot$status,
	/**
	 * Avatar.
	 */
	fldChatbot$filIdAvatar,

	/***********************************************************
	/** ChatbotCustomConfig.
	/***********************************************************
	/**
	 * Context value id.
	 */
	fldChatbotCustomConfig$cccId,
	/**
	 * Rating.
	 */
	fldChatbotCustomConfig$rating,
	/**
	 * Rating message.
	 */
	fldChatbotCustomConfig$ratingMessage,
	/**
	 * Chatbot.
	 */
	fldChatbotCustomConfig$botId,

	/***********************************************************
	/** ChatbotNode.
	/***********************************************************
	/**
	 * ID.
	 */
	fldChatbotNode$nodId,
	/**
	 * Name.
	 */
	fldChatbotNode$name,
	/**
	 * URL.
	 */
	fldChatbotNode$url,
	/**
	 * Dev node.
	 */
	fldChatbotNode$isDev,
	/**
	 * Color.
	 */
	fldChatbotNode$color,
	/**
	 * ApiKey.
	 */
	fldChatbotNode$apiKey,
	/**
	 * Is up to date.
	 */
	fldChatbotNode$isUpToDate,
	/**
	 * Chatbot.
	 */
	fldChatbotNode$botId,
	/**
	 * Loaded model.
	 */
	fldChatbotNode$traId,

	/***********************************************************
	/** ContextValue.
	/***********************************************************
	/**
	 * Context value id.
	 */
	fldContextValue$cvaId,
	/**
	 * Label.
	 */
	fldContextValue$label,
	/**
	 * Xpath.
	 */
	fldContextValue$xpath,
	/**
	 * Chatbot.
	 */
	fldContextValue$botId,

	/***********************************************************
	/** ExecutorConfiguration.
	/***********************************************************
	/**
	 * Bot ID.
	 */
	fldExecutorConfiguration$botId,
	/**
	 * Node ID.
	 */
	fldExecutorConfiguration$nodId,
	/**
	 * Model ID.
	 */
	fldExecutorConfiguration$traId,
	/**
	 * Model name.
	 */
	fldExecutorConfiguration$modelName,
	/**
	 * NLU Threshold.
	 */
	fldExecutorConfiguration$nluThreshold,
	/**
	 * Avatar.
	 */
	fldExecutorConfiguration$avatar,
	/**
	 * Additional config for executor.
	 */
	fldExecutorConfiguration$customConfig,

	/***********************************************************
	/** MediaFileInfo.
	/***********************************************************
	/**
	 * Id.
	 */
	fldMediaFileInfo$filId,
	/**
	 * Name.
	 */
	fldMediaFileInfo$fileName,
	/**
	 * MimeType.
	 */
	fldMediaFileInfo$mimeType,
	/**
	 * Size.
	 */
	fldMediaFileInfo$length,
	/**
	 * Modification Date.
	 */
	fldMediaFileInfo$lastModified,
	/**
	 * path.
	 */
	fldMediaFileInfo$filePath,
	/**
	 * data.
	 */
	fldMediaFileInfo$fileData,

	/***********************************************************
	/** RunnerInfo.
	/***********************************************************
	/**
	 * Name.
	 */
	fldRunnerInfo$name,
	/**
	 * Node state.
	 */
	fldRunnerInfo$state,
	/**
	 * Component version.
	 */
	fldRunnerInfo$agentVersion,
	/**
	 * Model version.
	 */
	fldRunnerInfo$loadedModelVersion,

	/***********************************************************
	/** SavedTraining.
	/***********************************************************
	/**
	 * ID.
	 */
	fldSavedTraining$savedTraId,
	/**
	 * Name.
	 */
	fldSavedTraining$name,
	/**
	 * Creation time.
	 */
	fldSavedTraining$creationTime,
	/**
	 * Description.
	 */
	fldSavedTraining$description,
	/**
	 * Bot Export.
	 */
	fldSavedTraining$botExport,
	/**
	 * Training.
	 */
	fldSavedTraining$traId,
	/**
	 * Bot.
	 */
	fldSavedTraining$botId,

	/***********************************************************
	/** SavedTrainingCriteria.
	/***********************************************************
	/**
	 * Search text.
	 */
	fldSavedTrainingCriteria$text,
	/**
	 * From.
	 */
	fldSavedTrainingCriteria$fromDate,
	/**
	 * To.
	 */
	fldSavedTrainingCriteria$toDate,

	/***********************************************************
	/** TopicExport.
	/***********************************************************
	/**
	 * Topic name.
	 */
	fldTopicExport$name,
	/**
	 * nluTrainingSentences.
	 */
	fldTopicExport$nluTrainingSentences,
	/**
	 * Topic BT.
	 */
	fldTopicExport$topicBT,

	/***********************************************************
	/** TrainerInfo.
	/***********************************************************
	/**
	 * Training id.
	 */
	fldTrainerInfo$traId,
	/**
	 * Name.
	 */
	fldTrainerInfo$name,
	/**
	 * Training in progress.
	 */
	fldTrainerInfo$trainingInProgress,
	/**
	 * Training state.
	 */
	fldTrainerInfo$trainingState,
	/**
	 * Training log.
	 */
	fldTrainerInfo$latestTrainingLog,
	/**
	 * Start time.
	 */
	fldTrainerInfo$startTime,
	/**
	 * End time.
	 */
	fldTrainerInfo$endTime,
	/**
	 * Training percentage.
	 */
	fldTrainerInfo$trainingPercent,
	/**
	 * Duration.
	 */
	fldTrainerInfo$duration,

	/***********************************************************
	/** Training.
	/***********************************************************
	/**
	 * ID.
	 */
	fldTraining$traId,
	/**
	 * Start time.
	 */
	fldTraining$startTime,
	/**
	 * End time.
	 */
	fldTraining$endTime,
	/**
	 * Version.
	 */
	fldTraining$versionNumber,
	/**
	 * Log.
	 */
	fldTraining$log,
	/**
	 * Informations.
	 */
	fldTraining$infos,
	/**
	 * Warnings.
	 */
	fldTraining$warnings,
	/**
	 * NLU Threshold.
	 */
	fldTraining$nluThreshold,
	/**
	 * Duration.
	 */
	fldTraining$duration,
	/**
	 * Chatbot.
	 */
	fldTraining$botId,
	/**
	 * Model.
	 */
	fldTraining$filIdModel,
	/**
	 * Status.
	 */
	fldTraining$strCd,

	/***********************************************************
	/** TrainingStatus.
	/***********************************************************
	/**
	 * ID.
	 */
	fldTrainingStatus$strCd,
	/**
	 * Label.
	 */
	fldTrainingStatus$label,
	/**
	 * LabelFr.
	 */
	fldTrainingStatus$labelFr,

	/***********************************************************
	/** UnknownSentenceDetail.
	/***********************************************************
	/**
	 * Unknown sentence id.
	 */
	fldUnknownSentenceDetail$unkSeId,
	/**
	 * Date.
	 */
	fldUnknownSentenceDetail$date,
	/**
	 * User text.
	 */
	fldUnknownSentenceDetail$text,
	/**
	 * Model Name.
	 */
	fldUnknownSentenceDetail$modelName,
	/**
	 * Status.
	 */
	fldUnknownSentenceDetail$status,
	/**
	 * Bot.
	 */
	fldUnknownSentenceDetail$botId,

	/***********************************************************
	/** UnknownSentenceStatus.
	/***********************************************************
	/**
	 * ID.
	 */
	fldUnknownSentenceStatus$strCd,
	/**
	 * Label.
	 */
	fldUnknownSentenceStatus$label,
	/**
	 * LabelFr.
	 */
	fldUnknownSentenceStatus$labelFr,

	/***********************************************************
	/** UnknownSentenceToUpdateIhm.
	/***********************************************************
	/**
	 * Unknown sentence Id.
	 */
	fldUnknownSentenceToUpdateIhm$unkSeId,
	/**
	 * User text.
	 */
	fldUnknownSentenceToUpdateIhm$text,

	/***********************************************************
	/** UnknownSentencesCriteria.
	/***********************************************************
	/**
	 * Chatbot selection.
	 */
	fldUnknownSentencesCriteria$botId,
	/**
	 * From.
	 */
	fldUnknownSentencesCriteria$fromDate,
	/**
	 * To.
	 */
	fldUnknownSentencesCriteria$toDate,
	/**
	 * Status.
	 */
	fldUnknownSentencesCriteria$status,

	/***********************************************************
	/** WelcomeTour.
	/***********************************************************
	/**
	 * Welcome tour id.
	 */
	fldWelcomeTour$welId,
	/**
	 * Label.
	 */
	fldWelcomeTour$label,
	/**
	 * Technical code.
	 */
	fldWelcomeTour$technicalCode,
	/**
	 * Chatbot.
	 */
	fldWelcomeTour$botId,
}
