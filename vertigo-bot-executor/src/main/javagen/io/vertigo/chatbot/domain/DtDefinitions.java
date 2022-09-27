package io.vertigo.chatbot.domain;

import java.util.Arrays;
import java.util.Iterator;

import io.vertigo.core.lang.Generated;
import io.vertigo.datamodel.structure.definitions.DtFieldName;

/**
 * This class is automatically generated.
 * DO NOT EDIT THIS FILE DIRECTLY.
 */
@Generated
public final class DtDefinitions implements Iterable<Class<?>> {

	/**
	 * Enumération des DtDefinitions.
	 */
	public enum Definitions {
		/** Objet de données Attachment. */
		Attachment(io.vertigo.chatbot.commons.domain.Attachment.class),
		/** Objet de données AttachmentExport. */
		AttachmentExport(io.vertigo.chatbot.commons.domain.AttachmentExport.class),
		/** Objet de données AttachmentFileInfo. */
		AttachmentFileInfo(io.vertigo.chatbot.commons.domain.AttachmentFileInfo.class),
		/** Objet de données BotExport. */
		BotExport(io.vertigo.chatbot.commons.domain.BotExport.class),
		/** Objet de données Chatbot. */
		Chatbot(io.vertigo.chatbot.commons.domain.Chatbot.class),
		/** Objet de données ChatbotCustomConfig. */
		ChatbotCustomConfig(io.vertigo.chatbot.commons.domain.ChatbotCustomConfig.class),
		/** Objet de données ConfluenceSettingExport. */
		ConfluenceSettingExport(io.vertigo.chatbot.commons.domain.ConfluenceSettingExport.class),
		/** Objet de données ContextValue. */
		ContextValue(io.vertigo.chatbot.commons.domain.ContextValue.class),
		/** Objet de données ExecutorConfiguration. */
		ExecutorConfiguration(io.vertigo.chatbot.commons.domain.ExecutorConfiguration.class),
		/** Objet de données FontFamily. */
		FontFamily(io.vertigo.chatbot.commons.domain.FontFamily.class),
		/** Objet de données JiraFieldSettingExport. */
		JiraFieldSettingExport(io.vertigo.chatbot.commons.domain.JiraFieldSettingExport.class),
		/** Objet de données JiraSettingExport. */
		JiraSettingExport(io.vertigo.chatbot.commons.domain.JiraSettingExport.class),
		/** Objet de données MediaFileInfo. */
		MediaFileInfo(io.vertigo.chatbot.commons.domain.MediaFileInfo.class),
		/** Objet de données RasaConfig. */
		RasaConfig(io.vertigo.chatbot.executor.domain.RasaConfig.class),
		/** Objet de données RunnerInfo. */
		RunnerInfo(io.vertigo.chatbot.commons.domain.RunnerInfo.class),
		/** Objet de données TopicExport. */
		TopicExport(io.vertigo.chatbot.commons.domain.TopicExport.class),
		/** Objet de données TrainerInfo. */
		TrainerInfo(io.vertigo.chatbot.commons.domain.TrainerInfo.class),
		/** Objet de données WelcomeTourExport. */
		WelcomeTourExport(io.vertigo.chatbot.commons.domain.WelcomeTourExport.class)		;

		private final Class<?> clazz;

		private Definitions(final Class<?> clazz) {
			this.clazz = clazz;
		}

		/** 
		 * Classe associée.
		 * @return Class d'implémentation de l'objet 
		 */
		public Class<?> getDtClass() {
			return clazz;
		}
	}

	/**
	 * Enumération des champs de Attachment.
	 */
	public enum AttachmentFields implements DtFieldName<io.vertigo.chatbot.commons.domain.Attachment> {
		/** Propriété 'Attachment id'. */
		attId,
		/** Propriété 'Label'. */
		label,
		/** Propriété 'MimeType'. */
		type,
		/** Propriété 'Size'. */
		length,
		/** Propriété 'AttachmentFileInfo'. */
		attFiId,
		/** Propriété 'Chatbot'. */
		botId	}

	/**
	 * Enumération des champs de AttachmentExport.
	 */
	public enum AttachmentExportFields implements DtFieldName<io.vertigo.chatbot.commons.domain.AttachmentExport> {
		/** Propriété 'Label'. */
		label,
		/** Propriété 'Name'. */
		fileName,
		/** Propriété 'MimeType'. */
		mimeType,
		/** Propriété 'Size'. */
		length,
		/** Propriété 'Data'. */
		fileData	}

	/**
	 * Enumération des champs de AttachmentFileInfo.
	 */
	public enum AttachmentFileInfoFields implements DtFieldName<io.vertigo.chatbot.commons.domain.AttachmentFileInfo> {
		/** Propriété 'Id'. */
		attFiId,
		/** Propriété 'Name'. */
		fileName,
		/** Propriété 'MimeType'. */
		mimeType,
		/** Propriété 'Size'. */
		length,
		/** Propriété 'Modification Date'. */
		lastModified,
		/** Propriété 'path'. */
		filePath	}

	/**
	 * Enumération des champs de BotExport.
	 */
	public enum BotExportFields implements DtFieldName<io.vertigo.chatbot.commons.domain.BotExport> {
		/** Propriété 'chatbot'. */
		bot,
		/** Propriété 'welcome BT'. */
		welcomeBT,
		/** Propriété 'end BT'. */
		endBT,
		/** Propriété 'fallback BT'. */
		fallbackBT,
		/** Propriété 'idle BT'. */
		idleBT,
		/** Propriété 'rating BT'. */
		ratingBT,
		/** Propriété 'topics export'. */
		topics,
		/** Propriété 'Welcome tours'. */
		welcomeTours,
		/** Propriété 'Confluence setting'. */
		confluenceSetting,
		/** Propriété 'Jira setting'. */
		jiraSetting,
		/** Propriété 'Jira fields setting'. */
		jiraFieldSetting,
		/** Propriété 'map Context'. */
		mapContext	}

	/**
	 * Enumération des champs de Chatbot.
	 */
	public enum ChatbotFields implements DtFieldName<io.vertigo.chatbot.commons.domain.Chatbot> {
		/** Propriété 'ID'. */
		botId,
		/** Propriété 'Name'. */
		name,
		/** Propriété 'Description'. */
		description,
		/** Propriété 'Creation date'. */
		creationDate,
		/** Propriété 'Status'. */
		status,
		/** Propriété 'Avatar'. */
		filIdAvatar	}

	/**
	 * Enumération des champs de ChatbotCustomConfig.
	 */
	public enum ChatbotCustomConfigFields implements DtFieldName<io.vertigo.chatbot.commons.domain.ChatbotCustomConfig> {
		/** Propriété 'Context value id'. */
		cccId,
		/** Propriété 'Bot email address'. */
		botEmailAddress,
		/** Propriété 'Reinitialization button'. */
		reinitializationButton,
		/** Propriété 'Bot background color'. */
		backgroundColor,
		/** Propriété 'Bot font color'. */
		fontColor,
		/** Propriété 'Display avatar'. */
		displayAvatar,
		/** Propriété 'Total maximum attachment size'. */
		totalMaxAttachmentSize,
		/** Propriété 'Disable NlU'. */
		disableNlu,
		/** Propriété 'fontFamily'. */
		fofCd,
		/** Propriété 'Chatbot'. */
		botId	}

	/**
	 * Enumération des champs de ConfluenceSettingExport.
	 */
	public enum ConfluenceSettingExportFields implements DtFieldName<io.vertigo.chatbot.commons.domain.ConfluenceSettingExport> {
		/** Propriété 'Knowledge base URL'. */
		url,
		/** Propriété 'Login'. */
		login,
		/** Propriété 'Password'. */
		password,
		/** Propriété 'Number max of results'. */
		numberOfResults	}

	/**
	 * Enumération des champs de ContextValue.
	 */
	public enum ContextValueFields implements DtFieldName<io.vertigo.chatbot.commons.domain.ContextValue> {
		/** Propriété 'Context value id'. */
		cvaId,
		/** Propriété 'Label'. */
		label,
		/** Propriété 'Xpath'. */
		xpath,
		/** Propriété 'Chatbot'. */
		botId	}

	/**
	 * Enumération des champs de ExecutorConfiguration.
	 */
	public enum ExecutorConfigurationFields implements DtFieldName<io.vertigo.chatbot.commons.domain.ExecutorConfiguration> {
		/** Propriété 'Bot ID'. */
		botId,
		/** Propriété 'Node ID'. */
		nodId,
		/** Propriété 'Model ID'. */
		traId,
		/** Propriété 'Model name'. */
		modelName,
		/** Propriété 'NLU Threshold'. */
		nluThreshold,
		/** Propriété 'Avatar'. */
		avatar,
		/** Propriété 'Additional config for executor'. */
		customConfig	}

	/**
	 * Enumération des champs de FontFamily.
	 */
	public enum FontFamilyFields implements DtFieldName<io.vertigo.chatbot.commons.domain.FontFamily> {
		/** Propriété 'ID'. */
		fofCd,
		/** Propriété 'Title'. */
		label,
		/** Propriété 'TitleFr'. */
		labelFr	}

	/**
	 * Enumération des champs de JiraFieldSettingExport.
	 */
	public enum JiraFieldSettingExportFields implements DtFieldName<io.vertigo.chatbot.commons.domain.JiraFieldSettingExport> {
		/** Propriété 'Jira field key'. */
		fieldKey,
		/** Propriété 'Enabled'. */
		enabled,
		/** Propriété 'Mandatory'. */
		mandatory	}

	/**
	 * Enumération des champs de JiraSettingExport.
	 */
	public enum JiraSettingExportFields implements DtFieldName<io.vertigo.chatbot.commons.domain.JiraSettingExport> {
		/** Propriété 'Jira URL'. */
		url,
		/** Propriété 'Login'. */
		login,
		/** Propriété 'Password'. */
		password,
		/** Propriété 'Project'. */
		project	}

	/**
	 * Enumération des champs de MediaFileInfo.
	 */
	public enum MediaFileInfoFields implements DtFieldName<io.vertigo.chatbot.commons.domain.MediaFileInfo> {
		/** Propriété 'Id'. */
		filId,
		/** Propriété 'Name'. */
		fileName,
		/** Propriété 'MimeType'. */
		mimeType,
		/** Propriété 'Size'. */
		length,
		/** Propriété 'Modification Date'. */
		lastModified,
		/** Propriété 'path'. */
		filePath,
		/** Propriété 'data'. */
		fileData	}

	/**
	 * Enumération des champs de RasaConfig.
	 */
	public enum RasaConfigFields implements DtFieldName<io.vertigo.chatbot.executor.domain.RasaConfig> {
		/** Propriété 'domain'. */
		domain,
		/** Propriété 'stories'. */
		stories,
		/** Propriété 'nlu'. */
		nlu,
		/** Propriété 'config'. */
		config	}

	/**
	 * Enumération des champs de RunnerInfo.
	 */
	public enum RunnerInfoFields implements DtFieldName<io.vertigo.chatbot.commons.domain.RunnerInfo> {
		/** Propriété 'Name'. */
		name,
		/** Propriété 'Node state'. */
		state,
		/** Propriété 'Component version'. */
		agentVersion,
		/** Propriété 'Model version'. */
		loadedModelVersion	}

	/**
	 * Enumération des champs de TopicExport.
	 */
	public enum TopicExportFields implements DtFieldName<io.vertigo.chatbot.commons.domain.TopicExport> {
		/** Propriété 'Topic name'. */
		name,
		/** Propriété 'nluTrainingSentences'. */
		nluTrainingSentences,
		/** Propriété 'Topic BT'. */
		topicBT,
		/** Propriété 'Unreachable'. */
		unreachable	}

	/**
	 * Enumération des champs de TrainerInfo.
	 */
	public enum TrainerInfoFields implements DtFieldName<io.vertigo.chatbot.commons.domain.TrainerInfo> {
		/** Propriété 'Training id'. */
		traId,
		/** Propriété 'Name'. */
		name,
		/** Propriété 'Training in progress'. */
		trainingInProgress,
		/** Propriété 'Training state'. */
		trainingState,
		/** Propriété 'Training log'. */
		latestTrainingLog,
		/** Propriété 'Start time'. */
		startTime,
		/** Propriété 'End time'. */
		endTime,
		/** Propriété 'Training percentage'. */
		trainingPercent,
		/** Propriété 'Duration'. */
		duration	}

	/**
	 * Enumération des champs de WelcomeTourExport.
	 */
	public enum WelcomeTourExportFields implements DtFieldName<io.vertigo.chatbot.commons.domain.WelcomeTourExport> {
		/** Propriété 'Label'. */
		label,
		/** Propriété 'Technical code'. */
		technicalCode,
		/** Propriété 'Shepherd config'. */
		config	}

	/** {@inheritDoc} */
	@Override
	public Iterator<Class<?>> iterator() {
		return new Iterator<>() {
			private Iterator<Definitions> it = Arrays.asList(Definitions.values()).iterator();

			/** {@inheritDoc} */
			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			/** {@inheritDoc} */
			@Override
			public Class<?> next() {
				return it.next().getDtClass();
			}
		};
	}
}
