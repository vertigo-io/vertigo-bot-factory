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
		/** Objet de données BotPredefinedTopic. */
		BotPredefinedTopic(io.vertigo.chatbot.designer.domain.commons.BotPredefinedTopic.class),
		/** Objet de données Chatbot. */
		Chatbot(io.vertigo.chatbot.commons.domain.Chatbot.class),
		/** Objet de données ChatbotCustomConfig. */
		ChatbotCustomConfig(io.vertigo.chatbot.commons.domain.ChatbotCustomConfig.class),
		/** Objet de données ChatbotNode. */
		ChatbotNode(io.vertigo.chatbot.commons.domain.ChatbotNode.class),
		/** Objet de données ChatbotProfiles. */
		ChatbotProfiles(io.vertigo.chatbot.designer.domain.admin.ChatbotProfiles.class),
		/** Objet de données ConfluenceSetting. */
		ConfluenceSetting(io.vertigo.chatbot.commons.domain.ConfluenceSetting.class),
		/** Objet de données ConfluenceSettingExport. */
		ConfluenceSettingExport(io.vertigo.chatbot.commons.domain.ConfluenceSettingExport.class),
		/** Objet de données ContextValue. */
		ContextValue(io.vertigo.chatbot.commons.domain.ContextValue.class),
		/** Objet de données DictionaryEntity. */
		DictionaryEntity(io.vertigo.chatbot.designer.domain.DictionaryEntity.class),
		/** Objet de données DictionaryEntityWrapper. */
		DictionaryEntityWrapper(io.vertigo.chatbot.designer.domain.DictionaryEntityWrapper.class),
		/** Objet de données ExecutorConfiguration. */
		ExecutorConfiguration(io.vertigo.chatbot.commons.domain.ExecutorConfiguration.class),
		/** Objet de données Groups. */
		Groups(io.vertigo.chatbot.designer.domain.commons.Groups.class),
		/** Objet de données History. */
		History(io.vertigo.chatbot.designer.domain.History.class),
		/** Objet de données HistoryAction. */
		HistoryAction(io.vertigo.chatbot.designer.domain.HistoryAction.class),
		/** Objet de données HistoryCriteria. */
		HistoryCriteria(io.vertigo.chatbot.designer.domain.HistoryCriteria.class),
		/** Objet de données JiraField. */
		JiraField(io.vertigo.chatbot.commons.domain.JiraField.class),
		/** Objet de données JiraFieldSetting. */
		JiraFieldSetting(io.vertigo.chatbot.commons.domain.JiraFieldSetting.class),
		/** Objet de données JiraFieldSettingExport. */
		JiraFieldSettingExport(io.vertigo.chatbot.commons.domain.JiraFieldSettingExport.class),
		/** Objet de données JiraSetting. */
		JiraSetting(io.vertigo.chatbot.commons.domain.JiraSetting.class),
		/** Objet de données JiraSettingExport. */
		JiraSettingExport(io.vertigo.chatbot.commons.domain.JiraSettingExport.class),
		/** Objet de données KindTopic. */
		KindTopic(io.vertigo.chatbot.commons.domain.topic.KindTopic.class),
		/** Objet de données MediaFileInfo. */
		MediaFileInfo(io.vertigo.chatbot.commons.domain.MediaFileInfo.class),
		/** Objet de données NluTrainingExport. */
		NluTrainingExport(io.vertigo.chatbot.commons.domain.topic.NluTrainingExport.class),
		/** Objet de données NluTrainingSentence. */
		NluTrainingSentence(io.vertigo.chatbot.commons.domain.topic.NluTrainingSentence.class),
		/** Objet de données Person. */
		Person(io.vertigo.chatbot.designer.domain.commons.Person.class),
		/** Objet de données PersonChatbotProfil. */
		PersonChatbotProfil(io.vertigo.chatbot.designer.domain.admin.PersonChatbotProfil.class),
		/** Objet de données PersonRole. */
		PersonRole(io.vertigo.chatbot.designer.domain.commons.PersonRole.class),
		/** Objet de données ProfilPerChatbot. */
		ProfilPerChatbot(io.vertigo.chatbot.designer.domain.admin.ProfilPerChatbot.class),
		/** Objet de données ResponseButton. */
		ResponseButton(io.vertigo.chatbot.commons.domain.topic.ResponseButton.class),
		/** Objet de données ResponseButtonExport. */
		ResponseButtonExport(io.vertigo.chatbot.designer.domain.topic.export.ResponseButtonExport.class),
		/** Objet de données ResponseButtonUrl. */
		ResponseButtonUrl(io.vertigo.chatbot.commons.domain.topic.ResponseButtonUrl.class),
		/** Objet de données ResponseButtonUrlExport. */
		ResponseButtonUrlExport(io.vertigo.chatbot.designer.domain.topic.export.ResponseButtonUrlExport.class),
		/** Objet de données ResponseType. */
		ResponseType(io.vertigo.chatbot.commons.domain.topic.ResponseType.class),
		/** Objet de données RunnerInfo. */
		RunnerInfo(io.vertigo.chatbot.commons.domain.RunnerInfo.class),
		/** Objet de données SavedTraining. */
		SavedTraining(io.vertigo.chatbot.commons.domain.SavedTraining.class),
		/** Objet de données SavedTrainingCriteria. */
		SavedTrainingCriteria(io.vertigo.chatbot.commons.domain.SavedTrainingCriteria.class),
		/** Objet de données ScriptIntention. */
		ScriptIntention(io.vertigo.chatbot.commons.domain.topic.ScriptIntention.class),
		/** Objet de données ScriptIntentionExport. */
		ScriptIntentionExport(io.vertigo.chatbot.designer.domain.topic.export.ScriptIntentionExport.class),
		/** Objet de données ScriptIntentionIhm. */
		ScriptIntentionIhm(io.vertigo.chatbot.commons.domain.topic.ScriptIntentionIhm.class),
		/** Objet de données SelectProfilChatbotPerson. */
		SelectProfilChatbotPerson(io.vertigo.chatbot.designer.domain.admin.SelectProfilChatbotPerson.class),
		/** Objet de données SelectTopicCategory. */
		SelectTopicCategory(io.vertigo.chatbot.commons.domain.topic.SelectTopicCategory.class),
		/** Objet de données SelectionOption. */
		SelectionOption(io.vertigo.chatbot.designer.domain.commons.SelectionOption.class),
		/** Objet de données SentenseDetail. */
		SentenseDetail(io.vertigo.chatbot.designer.domain.analytics.SentenseDetail.class),
		/** Objet de données SessionExport. */
		SessionExport(io.vertigo.chatbot.designer.domain.analytics.SessionExport.class),
		/** Objet de données SmallTalk. */
		SmallTalk(io.vertigo.chatbot.commons.domain.topic.SmallTalk.class),
		/** Objet de données SmallTalkIhm. */
		SmallTalkIhm(io.vertigo.chatbot.commons.domain.topic.SmallTalkIhm.class),
		/** Objet de données SmallTalkWrapper. */
		SmallTalkWrapper(io.vertigo.chatbot.commons.domain.topic.SmallTalkWrapper.class),
		/** Objet de données StatCriteria. */
		StatCriteria(io.vertigo.chatbot.designer.domain.analytics.StatCriteria.class),
		/** Objet de données Synonym. */
		Synonym(io.vertigo.chatbot.designer.domain.Synonym.class),
		/** Objet de données TopIntent. */
		TopIntent(io.vertigo.chatbot.designer.domain.analytics.TopIntent.class),
		/** Objet de données Topic. */
		Topic(io.vertigo.chatbot.commons.domain.topic.Topic.class),
		/** Objet de données TopicCategory. */
		TopicCategory(io.vertigo.chatbot.commons.domain.topic.TopicCategory.class),
		/** Objet de données TopicCriteria. */
		TopicCriteria(io.vertigo.chatbot.commons.domain.topic.TopicCriteria.class),
		/** Objet de données TopicExport. */
		TopicExport(io.vertigo.chatbot.commons.domain.TopicExport.class),
		/** Objet de données TopicFileExport. */
		TopicFileExport(io.vertigo.chatbot.commons.domain.topic.TopicFileExport.class),
		/** Objet de données TopicIhm. */
		TopicIhm(io.vertigo.chatbot.commons.domain.topic.TopicIhm.class),
		/** Objet de données TopicLabel. */
		TopicLabel(io.vertigo.chatbot.commons.domain.topic.TopicLabel.class),
		/** Objet de données TrainerInfo. */
		TrainerInfo(io.vertigo.chatbot.commons.domain.TrainerInfo.class),
		/** Objet de données Training. */
		Training(io.vertigo.chatbot.commons.domain.Training.class),
		/** Objet de données TrainingStatus. */
		TrainingStatus(io.vertigo.chatbot.commons.domain.TrainingStatus.class),
		/** Objet de données TupleSynonymIhm. */
		TupleSynonymIhm(io.vertigo.chatbot.designer.domain.TupleSynonymIhm.class),
		/** Objet de données TypeExportAnalytics. */
		TypeExportAnalytics(io.vertigo.chatbot.designer.domain.analytics.TypeExportAnalytics.class),
		/** Objet de données TypeTopic. */
		TypeTopic(io.vertigo.chatbot.commons.domain.topic.TypeTopic.class),
		/** Objet de données UnknownSentenceDetail. */
		UnknownSentenceDetail(io.vertigo.chatbot.commons.domain.UnknownSentenceDetail.class),
		/** Objet de données UnknownSentenceStatus. */
		UnknownSentenceStatus(io.vertigo.chatbot.commons.domain.UnknownSentenceStatus.class),
		/** Objet de données UnknownSentenceToUpdateIhm. */
		UnknownSentenceToUpdateIhm(io.vertigo.chatbot.commons.domain.UnknownSentenceToUpdateIhm.class),
		/** Objet de données UnknownSentenseExport. */
		UnknownSentenseExport(io.vertigo.chatbot.designer.domain.analytics.UnknownSentenseExport.class),
		/** Objet de données UtterText. */
		UtterText(io.vertigo.chatbot.commons.domain.topic.UtterText.class),
		/** Objet de données UtterTextExport. */
		UtterTextExport(io.vertigo.chatbot.designer.domain.topic.export.UtterTextExport.class),
		/** Objet de données WelcomeTour. */
		WelcomeTour(io.vertigo.chatbot.commons.domain.WelcomeTour.class)		;

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
		/** Propriété 'topics export'. */
		topics,
		/** Propriété 'Welcome tours map'. */
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
	 * Enumération des champs de BotPredefinedTopic.
	 */
	public enum BotPredefinedTopicFields implements DtFieldName<io.vertigo.chatbot.designer.domain.commons.BotPredefinedTopic> {
		/** Propriété 'Id topic'. */
		topId,
		/** Propriété 'Type topic code'. */
		ttoCd,
		/** Propriété 'SmallTalk / Script'. */
		value	}

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
		/** Propriété 'Rating'. */
		rating,
		/** Propriété 'Rating message'. */
		ratingMessage,
		/** Propriété 'Bot email address'. */
		botEmailAddress,
		/** Propriété 'Reinitialization button'. */
		reinitializationButton,
		/** Propriété 'Bot background color'. */
		backgroundColor,
		/** Propriété 'Bot font family'. */
		fontFamily,
		/** Propriété 'Display avatar'. */
		displayAvatar,
		/** Propriété 'Total maximum attachment size'. */
		totalMaxAttachmentSize,
		/** Propriété 'Chatbot'. */
		botId	}

	/**
	 * Enumération des champs de ChatbotNode.
	 */
	public enum ChatbotNodeFields implements DtFieldName<io.vertigo.chatbot.commons.domain.ChatbotNode> {
		/** Propriété 'ID'. */
		nodId,
		/** Propriété 'Name'. */
		name,
		/** Propriété 'URL'. */
		url,
		/** Propriété 'Dev node'. */
		isDev,
		/** Propriété 'Color'. */
		color,
		/** Propriété 'ApiKey'. */
		apiKey,
		/** Propriété 'Is up to date'. */
		isUpToDate,
		/** Propriété 'Chatbot'. */
		botId,
		/** Propriété 'Loaded model'. */
		traId	}

	/**
	 * Enumération des champs de ChatbotProfiles.
	 */
	public enum ChatbotProfilesFields implements DtFieldName<io.vertigo.chatbot.designer.domain.admin.ChatbotProfiles> {
		/** Propriété 'ID'. */
		chpCd,
		/** Propriété 'Title'. */
		label,
		/** Propriété 'Order'. */
		sortOrder	}

	/**
	 * Enumération des champs de ConfluenceSetting.
	 */
	public enum ConfluenceSettingFields implements DtFieldName<io.vertigo.chatbot.commons.domain.ConfluenceSetting> {
		/** Propriété 'Confluence setting id'. */
		conSetId,
		/** Propriété 'Knowledge base URL'. */
		url,
		/** Propriété 'Login'. */
		login,
		/** Propriété 'Password'. */
		password,
		/** Propriété 'Number max of results'. */
		numberOfResults,
		/** Propriété 'Chatbot'. */
		botId,
		/** Propriété 'Node'. */
		nodId	}

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
	 * Enumération des champs de DictionaryEntity.
	 */
	public enum DictionaryEntityFields implements DtFieldName<io.vertigo.chatbot.designer.domain.DictionaryEntity> {
		/** Propriété 'Dictionary entity id'. */
		dicEntId,
		/** Propriété 'Label'. */
		label,
		/** Propriété 'Chatbot'. */
		botId	}

	/**
	 * Enumération des champs de DictionaryEntityWrapper.
	 */
	public enum DictionaryEntityWrapperFields implements DtFieldName<io.vertigo.chatbot.designer.domain.DictionaryEntityWrapper> {
		/** Propriété 'Dictionary Entity id'. */
		dicEntId,
		/** Propriété 'Dictionary Entity Label'. */
		dictionaryEntityLabel,
		/** Propriété 'Synonyms list'. */
		synonymsList	}

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
	 * Enumération des champs de Groups.
	 */
	public enum GroupsFields implements DtFieldName<io.vertigo.chatbot.designer.domain.commons.Groups> {
		/** Propriété 'Id'. */
		grpId,
		/** Propriété 'Name'. */
		name	}

	/**
	 * Enumération des champs de History.
	 */
	public enum HistoryFields implements DtFieldName<io.vertigo.chatbot.designer.domain.History> {
		/** Propriété 'History id'. */
		histId,
		/** Propriété 'Date'. */
		date,
		/** Propriété 'Class name'. */
		className,
		/** Propriété 'Label'. */
		message,
		/** Propriété 'User'. */
		userName,
		/** Propriété 'Action'. */
		hacCd,
		/** Propriété 'Chatbot'. */
		botId	}

	/**
	 * Enumération des champs de HistoryAction.
	 */
	public enum HistoryActionFields implements DtFieldName<io.vertigo.chatbot.designer.domain.HistoryAction> {
		/** Propriété 'ID'. */
		hacCd,
		/** Propriété 'Title'. */
		label,
		/** Propriété 'TitleFr'. */
		labelFr	}

	/**
	 * Enumération des champs de HistoryCriteria.
	 */
	public enum HistoryCriteriaFields implements DtFieldName<io.vertigo.chatbot.designer.domain.HistoryCriteria> {
		/** Propriété 'From'. */
		fromDate,
		/** Propriété 'To'. */
		toDate,
		/** Propriété 'Action'. */
		hacCd,
		/** Propriété 'Search'. */
		text	}

	/**
	 * Enumération des champs de JiraField.
	 */
	public enum JiraFieldFields implements DtFieldName<io.vertigo.chatbot.commons.domain.JiraField> {
		/** Propriété 'ID'. */
		jirFieldCd,
		/** Propriété 'Jira id'. */
		jiraId,
		/** Propriété 'Title'. */
		label,
		/** Propriété 'TitleFr'. */
		labelFr	}

	/**
	 * Enumération des champs de JiraFieldSetting.
	 */
	public enum JiraFieldSettingFields implements DtFieldName<io.vertigo.chatbot.commons.domain.JiraFieldSetting> {
		/** Propriété 'Jira field setting id'. */
		jirFieldSetId,
		/** Propriété 'Jira field enabled'. */
		enabled,
		/** Propriété 'Jira field mandatory'. */
		mandatory,
		/** Propriété 'Chatbot'. */
		botId,
		/** Propriété 'Field'. */
		jirFieldCd	}

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
	 * Enumération des champs de JiraSetting.
	 */
	public enum JiraSettingFields implements DtFieldName<io.vertigo.chatbot.commons.domain.JiraSetting> {
		/** Propriété 'Jira setting id'. */
		jirSetId,
		/** Propriété 'Jira URL'. */
		url,
		/** Propriété 'Login'. */
		login,
		/** Propriété 'Password'. */
		password,
		/** Propriété 'Jira project'. */
		project,
		/** Propriété 'Chatbot'. */
		botId,
		/** Propriété 'Node'. */
		nodId	}

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
	 * Enumération des champs de KindTopic.
	 */
	public enum KindTopicFields implements DtFieldName<io.vertigo.chatbot.commons.domain.topic.KindTopic> {
		/** Propriété 'ID'. */
		ktoCd,
		/** Propriété 'Label'. */
		label,
		/** Propriété 'Description'. */
		description,
		/** Propriété 'Default text'. */
		defaultText	}

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
	 * Enumération des champs de NluTrainingExport.
	 */
	public enum NluTrainingExportFields implements DtFieldName<io.vertigo.chatbot.commons.domain.topic.NluTrainingExport> {
		/** Propriété 'topId'. */
		topId,
		/** Propriété 'nlu ID'. */
		ntsId,
		/** Propriété 'Text'. */
		text	}

	/**
	 * Enumération des champs de NluTrainingSentence.
	 */
	public enum NluTrainingSentenceFields implements DtFieldName<io.vertigo.chatbot.commons.domain.topic.NluTrainingSentence> {
		/** Propriété 'ID'. */
		ntsId,
		/** Propriété 'Text'. */
		text,
		/** Propriété 'SmallTalk'. */
		topId	}

	/**
	 * Enumération des champs de Person.
	 */
	public enum PersonFields implements DtFieldName<io.vertigo.chatbot.designer.domain.commons.Person> {
		/** Propriété 'Id'. */
		perId,
		/** Propriété 'Login'. */
		login,
		/** Propriété 'Name'. */
		name,
		/** Propriété 'Group'. */
		grpId,
		/** Propriété 'Role'. */
		rolCd	}

	/**
	 * Enumération des champs de PersonChatbotProfil.
	 */
	public enum PersonChatbotProfilFields implements DtFieldName<io.vertigo.chatbot.designer.domain.admin.PersonChatbotProfil> {
		/** Propriété 'Chatbot selection'. */
		botId,
		/** Propriété 'ProfilPerPerson id'. */
		chpId,
		/** Propriété 'Person name'. */
		name,
		/** Propriété 'Profil label'. */
		profilLabel	}

	/**
	 * Enumération des champs de PersonRole.
	 */
	public enum PersonRoleFields implements DtFieldName<io.vertigo.chatbot.designer.domain.commons.PersonRole> {
		/** Propriété 'Code'. */
		rolCd,
		/** Propriété 'Label'. */
		label,
		/** Propriété 'Order'. */
		sortOrder	}

	/**
	 * Enumération des champs de ProfilPerChatbot.
	 */
	public enum ProfilPerChatbotFields implements DtFieldName<io.vertigo.chatbot.designer.domain.admin.ProfilPerChatbot> {
		/** Propriété 'ID'. */
		chpId,
		/** Propriété 'Chatbot'. */
		botId,
		/** Propriété 'Person'. */
		perId,
		/** Propriété 'Profil pour un chatbot'. */
		chpCd	}

	/**
	 * Enumération des champs de ResponseButton.
	 */
	public enum ResponseButtonFields implements DtFieldName<io.vertigo.chatbot.commons.domain.topic.ResponseButton> {
		/** Propriété 'ID'. */
		btnId,
		/** Propriété 'Text'. */
		text,
		/** Propriété 'SmallTalk'. */
		smtId,
		/** Propriété 'TopicResponse'. */
		topIdResponse	}

	/**
	 * Enumération des champs de ResponseButtonExport.
	 */
	public enum ResponseButtonExportFields implements DtFieldName<io.vertigo.chatbot.designer.domain.topic.export.ResponseButtonExport> {
		/** Propriété 'Topic id'. */
		topId,
		/** Propriété 'Topic code'. */
		topCode,
		/** Propriété 'Topic code response'. */
		topCodeResponse,
		/** Propriété 'Button text'. */
		text	}

	/**
	 * Enumération des champs de ResponseButtonUrl.
	 */
	public enum ResponseButtonUrlFields implements DtFieldName<io.vertigo.chatbot.commons.domain.topic.ResponseButtonUrl> {
		/** Propriété 'ID'. */
		btnId,
		/** Propriété 'Text'. */
		text,
		/** Propriété 'URL'. */
		url,
		/** Propriété 'New tab'. */
		newTab,
		/** Propriété 'SmallTalk'. */
		smtId	}

	/**
	 * Enumération des champs de ResponseButtonUrlExport.
	 */
	public enum ResponseButtonUrlExportFields implements DtFieldName<io.vertigo.chatbot.designer.domain.topic.export.ResponseButtonUrlExport> {
		/** Propriété 'Topic id'. */
		topId,
		/** Propriété 'Topic code'. */
		topCode,
		/** Propriété 'Button text'. */
		text,
		/** Propriété 'Button url'. */
		url,
		/** Propriété 'New tab'. */
		newTab	}

	/**
	 * Enumération des champs de ResponseType.
	 */
	public enum ResponseTypeFields implements DtFieldName<io.vertigo.chatbot.commons.domain.topic.ResponseType> {
		/** Propriété 'ID'. */
		rtyId,
		/** Propriété 'Title'. */
		label,
		/** Propriété 'TitleFR'. */
		labelFr,
		/** Propriété 'Order'. */
		sortOrder	}

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
	 * Enumération des champs de SavedTraining.
	 */
	public enum SavedTrainingFields implements DtFieldName<io.vertigo.chatbot.commons.domain.SavedTraining> {
		/** Propriété 'ID'. */
		savedTraId,
		/** Propriété 'Name'. */
		name,
		/** Propriété 'Creation time'. */
		creationTime,
		/** Propriété 'Description'. */
		description,
		/** Propriété 'Bot Export'. */
		botExport,
		/** Propriété 'Training'. */
		traId,
		/** Propriété 'Bot'. */
		botId	}

	/**
	 * Enumération des champs de SavedTrainingCriteria.
	 */
	public enum SavedTrainingCriteriaFields implements DtFieldName<io.vertigo.chatbot.commons.domain.SavedTrainingCriteria> {
		/** Propriété 'Search text'. */
		text,
		/** Propriété 'From'. */
		fromDate,
		/** Propriété 'To'. */
		toDate	}

	/**
	 * Enumération des champs de ScriptIntention.
	 */
	public enum ScriptIntentionFields implements DtFieldName<io.vertigo.chatbot.commons.domain.topic.ScriptIntention> {
		/** Propriété 'ID'. */
		sinId,
		/** Propriété 'Script'. */
		script,
		/** Propriété 'Topic'. */
		topId	}

	/**
	 * Enumération des champs de ScriptIntentionExport.
	 */
	public enum ScriptIntentionExportFields implements DtFieldName<io.vertigo.chatbot.designer.domain.topic.export.ScriptIntentionExport> {
		/** Propriété 'Topic id'. */
		topId,
		/** Propriété 'Bt'. */
		bt	}

	/**
	 * Enumération des champs de ScriptIntentionIhm.
	 */
	public enum ScriptIntentionIhmFields implements DtFieldName<io.vertigo.chatbot.commons.domain.topic.ScriptIntentionIhm> {
		/** Propriété 'Topic id'. */
		topId,
		/** Propriété 'Script Intention label'. */
		title,
		/** Propriété 'Script Intention id'. */
		sinId,
		/** Propriété 'Enabled'. */
		isEnabled	}

	/**
	 * Enumération des champs de SelectProfilChatbotPerson.
	 */
	public enum SelectProfilChatbotPersonFields implements DtFieldName<io.vertigo.chatbot.designer.domain.admin.SelectProfilChatbotPerson> {
		/** Propriété 'Person id'. */
		perId,
		/** Propriété 'Profil cd'. */
		prfId	}

	/**
	 * Enumération des champs de SelectTopicCategory.
	 */
	public enum SelectTopicCategoryFields implements DtFieldName<io.vertigo.chatbot.commons.domain.topic.SelectTopicCategory> {
		/** Propriété 'Topic category id'. */
		topCatId	}

	/**
	 * Enumération des champs de SelectionOption.
	 */
	public enum SelectionOptionFields implements DtFieldName<io.vertigo.chatbot.designer.domain.commons.SelectionOption> {
		/** Propriété 'Label'. */
		label,
		/** Propriété 'Value'. */
		value	}

	/**
	 * Enumération des champs de SentenseDetail.
	 */
	public enum SentenseDetailFields implements DtFieldName<io.vertigo.chatbot.designer.domain.analytics.SentenseDetail> {
		/** Propriété 'Rasa message ID'. */
		messageId,
		/** Propriété 'Date'. */
		date,
		/** Propriété 'User text'. */
		text,
		/** Propriété 'Rasa intent'. */
		intentRasa,
		/** Propriété 'Confidence'. */
		confidence,
		/** Propriété 'Topic selection'. */
		topId,
		/** Propriété 'Model Name'. */
		modelName	}

	/**
	 * Enumération des champs de SessionExport.
	 */
	public enum SessionExportFields implements DtFieldName<io.vertigo.chatbot.designer.domain.analytics.SessionExport> {
		/** Propriété 'Date'. */
		date,
		/** Propriété 'Model Name'. */
		modelName,
		/** Propriété 'Date training'. */
		dateTraining,
		/** Propriété 'Bot name'. */
		botName,
		/** Propriété 'Node name'. */
		node,
		/** Propriété 'Date bot creation'. */
		creationBot	}

	/**
	 * Enumération des champs de SmallTalk.
	 */
	public enum SmallTalkFields implements DtFieldName<io.vertigo.chatbot.commons.domain.topic.SmallTalk> {
		/** Propriété 'ID'. */
		smtId,
		/** Propriété 'Is conversation over ?'. */
		isEnd,
		/** Propriété 'Topic'. */
		topId,
		/** Propriété 'Response type'. */
		rtyId	}

	/**
	 * Enumération des champs de SmallTalkIhm.
	 */
	public enum SmallTalkIhmFields implements DtFieldName<io.vertigo.chatbot.commons.domain.topic.SmallTalkIhm> {
		/** Propriété 'Topic id'. */
		topId,
		/** Propriété 'SmallTalk label'. */
		title,
		/** Propriété 'SmallTalk id'. */
		smtId,
		/** Propriété 'Enabled'. */
		isEnabled,
		/** Propriété 'Category'. */
		catLabel	}

	/**
	 * Enumération des champs de SmallTalkWrapper.
	 */
	public enum SmallTalkWrapperFields implements DtFieldName<io.vertigo.chatbot.commons.domain.topic.SmallTalkWrapper> {
		/** Propriété 'SmallTalk'. */
		smallTalk,
		/** Propriété 'Buttons'. */
		buttons,
		/** Propriété 'Buttons url'. */
		buttonsUrl,
		/** Propriété 'Utter texts'. */
		utterTexts	}

	/**
	 * Enumération des champs de StatCriteria.
	 */
	public enum StatCriteriaFields implements DtFieldName<io.vertigo.chatbot.designer.domain.analytics.StatCriteria> {
		/** Propriété 'Chatbot selection'. */
		botId,
		/** Propriété 'Time option'. */
		timeOption,
		/** Propriété 'Node selection'. */
		nodId,
		/** Propriété 'From'. */
		fromDate,
		/** Propriété 'To'. */
		toDate,
		/** Propriété 'From'. */
		fromInstant,
		/** Propriété 'To'. */
		toInstant	}

	/**
	 * Enumération des champs de Synonym.
	 */
	public enum SynonymFields implements DtFieldName<io.vertigo.chatbot.designer.domain.Synonym> {
		/** Propriété 'Synonym id'. */
		synId,
		/** Propriété 'Label'. */
		label,
		/** Propriété 'Chatbot'. */
		botId,
		/** Propriété 'DictionaryEntity'. */
		dicEntId	}

	/**
	 * Enumération des champs de TopIntent.
	 */
	public enum TopIntentFields implements DtFieldName<io.vertigo.chatbot.designer.domain.analytics.TopIntent> {
		/** Propriété 'Topic ID'. */
		topId,
		/** Propriété 'Rasa intent'. */
		intentRasa,
		/** Propriété 'Topic code'. */
		code,
		/** Propriété 'Count'. */
		count	}

	/**
	 * Enumération des champs de Topic.
	 */
	public enum TopicFields implements DtFieldName<io.vertigo.chatbot.commons.domain.topic.Topic> {
		/** Propriété 'ID'. */
		topId,
		/** Propriété 'Title'. */
		title,
		/** Propriété 'Description'. */
		description,
		/** Propriété 'Enabled'. */
		isEnabled,
		/** Propriété 'Code'. */
		code,
		/** Propriété 'Type du topic'. */
		ttoCd,
		/** Propriété 'Chatbot'. */
		botId,
		/** Propriété 'Topic'. */
		topCatId,
		/** Propriété 'Kind of topic'. */
		ktoCd	}

	/**
	 * Enumération des champs de TopicCategory.
	 */
	public enum TopicCategoryFields implements DtFieldName<io.vertigo.chatbot.commons.domain.topic.TopicCategory> {
		/** Propriété 'Topic category id'. */
		topCatId,
		/** Propriété 'Code'. */
		code,
		/** Propriété 'Topic category label'. */
		label,
		/** Propriété 'Category level'. */
		level,
		/** Propriété 'Enabled'. */
		isEnabled,
		/** Propriété 'Technical'. */
		isTechnical,
		/** Propriété 'Chatbot'. */
		botId	}

	/**
	 * Enumération des champs de TopicCriteria.
	 */
	public enum TopicCriteriaFields implements DtFieldName<io.vertigo.chatbot.commons.domain.topic.TopicCriteria> {
		/** Propriété 'Category selection'. */
		catId	}

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
	 * Enumération des champs de TopicFileExport.
	 */
	public enum TopicFileExportFields implements DtFieldName<io.vertigo.chatbot.commons.domain.topic.TopicFileExport> {
		/** Propriété 'Code'. */
		code,
		/** Propriété 'Title'. */
		title,
		/** Propriété 'Category'. */
		category,
		/** Propriété 'Description'. */
		description,
		/** Propriété 'Tag'. */
		tag,
		/** Propriété 'Start date'. */
		dateStart,
		/** Propriété 'End date'. */
		dateEnd,
		/** Propriété 'Active'. */
		active,
		/** Propriété 'Training phrases'. */
		trainingPhrases,
		/** Propriété 'Topic type'. */
		typeTopic,
		/** Propriété 'Topic kind'. */
		kindTopic,
		/** Propriété 'Script'. */
		script,
		/** Propriété 'Response'. */
		response,
		/** Propriété 'Buttons'. */
		buttons,
		/** Propriété 'Buttons URL'. */
		buttonsUrl,
		/** Propriété 'End of conversation'. */
		isEnd,
		/** Propriété 'labels'. */
		labels	}

	/**
	 * Enumération des champs de TopicIhm.
	 */
	public enum TopicIhmFields implements DtFieldName<io.vertigo.chatbot.commons.domain.topic.TopicIhm> {
		/** Propriété 'Topic id'. */
		topId,
		/** Propriété 'Code Type'. */
		ttoCd,
		/** Propriété 'Type'. */
		type,
		/** Propriété 'Topic label'. */
		title,
		/** Propriété 'SmallTalk id'. */
		smtId,
		/** Propriété 'Script intention id'. */
		sinId,
		/** Propriété 'Category'. */
		catLabel,
		/** Propriété 'Enabled'. */
		isEnabled,
		/** Propriété 'Code Kind'. */
		ktoCd,
		/** Propriété 'Code'. */
		code,
		/** Propriété 'Labels'. */
		labels	}

	/**
	 * Enumération des champs de TopicLabel.
	 */
	public enum TopicLabelFields implements DtFieldName<io.vertigo.chatbot.commons.domain.topic.TopicLabel> {
		/** Propriété 'Label id'. */
		labelId,
		/** Propriété 'Label label'. */
		label,
		/** Propriété 'Chatbot'. */
		botId	}

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
	 * Enumération des champs de Training.
	 */
	public enum TrainingFields implements DtFieldName<io.vertigo.chatbot.commons.domain.Training> {
		/** Propriété 'ID'. */
		traId,
		/** Propriété 'Start time'. */
		startTime,
		/** Propriété 'End time'. */
		endTime,
		/** Propriété 'Version'. */
		versionNumber,
		/** Propriété 'Log'. */
		log,
		/** Propriété 'Informations'. */
		infos,
		/** Propriété 'Warnings'. */
		warnings,
		/** Propriété 'NLU Threshold'. */
		nluThreshold,
		/** Propriété 'Duration'. */
		duration,
		/** Propriété 'Chatbot'. */
		botId,
		/** Propriété 'Model'. */
		filIdModel,
		/** Propriété 'Status'. */
		strCd	}

	/**
	 * Enumération des champs de TrainingStatus.
	 */
	public enum TrainingStatusFields implements DtFieldName<io.vertigo.chatbot.commons.domain.TrainingStatus> {
		/** Propriété 'ID'. */
		strCd,
		/** Propriété 'Label'. */
		label,
		/** Propriété 'LabelFr'. */
		labelFr	}

	/**
	 * Enumération des champs de TupleSynonymIhm.
	 */
	public enum TupleSynonymIhmFields implements DtFieldName<io.vertigo.chatbot.designer.domain.TupleSynonymIhm> {
		/** Propriété 'Word'. */
		word,
		/** Propriété 'Synonym label'. */
		synonymLabel	}

	/**
	 * Enumération des champs de TypeExportAnalytics.
	 */
	public enum TypeExportAnalyticsFields implements DtFieldName<io.vertigo.chatbot.designer.domain.analytics.TypeExportAnalytics> {
		/** Propriété 'Code'. */
		teaCd,
		/** Propriété 'Title'. */
		label,
		/** Propriété 'Titre'. */
		labelFr,
		/** Propriété 'Bot related'. */
		botRelated	}

	/**
	 * Enumération des champs de TypeTopic.
	 */
	public enum TypeTopicFields implements DtFieldName<io.vertigo.chatbot.commons.domain.topic.TypeTopic> {
		/** Propriété 'ID'. */
		ttoCd,
		/** Propriété 'Title'. */
		label,
		/** Propriété 'TitleFr'. */
		labelFr	}

	/**
	 * Enumération des champs de UnknownSentenceDetail.
	 */
	public enum UnknownSentenceDetailFields implements DtFieldName<io.vertigo.chatbot.commons.domain.UnknownSentenceDetail> {
		/** Propriété 'Unknown sentence id'. */
		unkSeId,
		/** Propriété 'Date'. */
		date,
		/** Propriété 'User text'. */
		text,
		/** Propriété 'Model Name'. */
		modelName,
		/** Propriété 'Status'. */
		status,
		/** Propriété 'Bot'. */
		botId	}

	/**
	 * Enumération des champs de UnknownSentenceStatus.
	 */
	public enum UnknownSentenceStatusFields implements DtFieldName<io.vertigo.chatbot.commons.domain.UnknownSentenceStatus> {
		/** Propriété 'ID'. */
		strCd,
		/** Propriété 'Label'. */
		label,
		/** Propriété 'LabelFr'. */
		labelFr	}

	/**
	 * Enumération des champs de UnknownSentenceToUpdateIhm.
	 */
	public enum UnknownSentenceToUpdateIhmFields implements DtFieldName<io.vertigo.chatbot.commons.domain.UnknownSentenceToUpdateIhm> {
		/** Propriété 'Unknown sentence Id'. */
		unkSeId,
		/** Propriété 'User text'. */
		text	}

	/**
	 * Enumération des champs de UnknownSentenseExport.
	 */
	public enum UnknownSentenseExportFields implements DtFieldName<io.vertigo.chatbot.designer.domain.analytics.UnknownSentenseExport> {
		/** Propriété 'Date'. */
		date,
		/** Propriété 'User text'. */
		text,
		/** Propriété 'Model Name'. */
		modelName,
		/** Propriété 'Date training'. */
		dateTraining,
		/** Propriété 'Confidence'. */
		confidence,
		/** Propriété 'Bot name'. */
		botName,
		/** Propriété 'Node name'. */
		node,
		/** Propriété 'Date bot creation'. */
		creationBot	}

	/**
	 * Enumération des champs de UtterText.
	 */
	public enum UtterTextFields implements DtFieldName<io.vertigo.chatbot.commons.domain.topic.UtterText> {
		/** Propriété 'ID'. */
		uttId,
		/** Propriété 'Text'. */
		text,
		/** Propriété 'SmallTalk'. */
		smtId	}

	/**
	 * Enumération des champs de UtterTextExport.
	 */
	public enum UtterTextExportFields implements DtFieldName<io.vertigo.chatbot.designer.domain.topic.export.UtterTextExport> {
		/** Propriété 'Topic id'. */
		topId,
		/** Propriété 'Utters'. */
		utterTexts,
		/** Propriété 'Response type'. */
		responseType,
		/** Propriété 'Is conversation over ?'. */
		isEnd,
		/** Propriété 'Kind Topic Code'. */
		ktoCd	}

	/**
	 * Enumération des champs de WelcomeTour.
	 */
	public enum WelcomeTourFields implements DtFieldName<io.vertigo.chatbot.commons.domain.WelcomeTour> {
		/** Propriété 'Welcome tour id'. */
		welId,
		/** Propriété 'Label'. */
		label,
		/** Propriété 'Technical code'. */
		technicalCode,
		/** Propriété 'Chatbot'. */
		botId	}

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
