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
		/** Objet de données BotExport. */
		BotExport(io.vertigo.chatbot.commons.domain.BotExport.class),
		/** Objet de données Chatbot. */
		Chatbot(io.vertigo.chatbot.commons.domain.Chatbot.class),
		/** Objet de données ChatbotNode. */
		ChatbotNode(io.vertigo.chatbot.commons.domain.ChatbotNode.class),
		/** Objet de données ChatbotProfiles. */
		ChatbotProfiles(io.vertigo.chatbot.designer.domain.admin.ChatbotProfiles.class),
		/** Objet de données ExecutorConfiguration. */
		ExecutorConfiguration(io.vertigo.chatbot.commons.domain.ExecutorConfiguration.class),
		/** Objet de données Groups. */
		Groups(io.vertigo.chatbot.designer.domain.commons.Groups.class),
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
		ResponseButtonExport(io.vertigo.chatbot.designer.builder.topic.export.ResponseButtonExport.class),
		/** Objet de données ResponseType. */
		ResponseType(io.vertigo.chatbot.commons.domain.topic.ResponseType.class),
		/** Objet de données RunnerInfo. */
		RunnerInfo(io.vertigo.chatbot.commons.domain.RunnerInfo.class),
		/** Objet de données ScriptIntention. */
		ScriptIntention(io.vertigo.chatbot.commons.domain.topic.ScriptIntention.class),
		/** Objet de données ScriptIntentionExport. */
		ScriptIntentionExport(io.vertigo.chatbot.designer.builder.topic.export.ScriptIntentionExport.class),
		/** Objet de données ScriptIntentionIhm. */
		ScriptIntentionIhm(io.vertigo.chatbot.commons.domain.topic.ScriptIntentionIhm.class),
		/** Objet de données SelectCategory. */
		SelectCategory(io.vertigo.chatbot.commons.domain.topic.SelectCategory.class),
		/** Objet de données SelectProfilChatbotPerson. */
		SelectProfilChatbotPerson(io.vertigo.chatbot.designer.domain.admin.SelectProfilChatbotPerson.class),
		/** Objet de données SelectionOption. */
		SelectionOption(io.vertigo.chatbot.designer.domain.commons.SelectionOption.class),
		/** Objet de données SentenseDetail. */
		SentenseDetail(io.vertigo.chatbot.designer.domain.SentenseDetail.class),
		/** Objet de données SmallTalk. */
		SmallTalk(io.vertigo.chatbot.commons.domain.topic.SmallTalk.class),
		/** Objet de données SmallTalkIhm. */
		SmallTalkIhm(io.vertigo.chatbot.commons.domain.topic.SmallTalkIhm.class),
		/** Objet de données StatCriteria. */
		StatCriteria(io.vertigo.chatbot.designer.domain.StatCriteria.class),
		/** Objet de données TopIntent. */
		TopIntent(io.vertigo.chatbot.designer.domain.TopIntent.class),
		/** Objet de données Topic. */
		Topic(io.vertigo.chatbot.commons.domain.topic.Topic.class),
		/** Objet de données TopicCategory. */
		TopicCategory(io.vertigo.chatbot.commons.domain.topic.TopicCategory.class),
		/** Objet de données TopicExport. */
		TopicExport(io.vertigo.chatbot.commons.domain.TopicExport.class),
		/** Objet de données TopicFileExport. */
		TopicFileExport(io.vertigo.chatbot.commons.domain.topic.TopicFileExport.class),
		/** Objet de données TopicIhm. */
		TopicIhm(io.vertigo.chatbot.commons.domain.topic.TopicIhm.class),
		/** Objet de données TrainerInfo. */
		TrainerInfo(io.vertigo.chatbot.commons.domain.TrainerInfo.class),
		/** Objet de données Training. */
		Training(io.vertigo.chatbot.commons.domain.Training.class),
		/** Objet de données TrainingStatus. */
		TrainingStatus(io.vertigo.chatbot.commons.domain.TrainingStatus.class),
		/** Objet de données TypeTopic. */
		TypeTopic(io.vertigo.chatbot.commons.domain.topic.TypeTopic.class),
		/** Objet de données UtterText. */
		UtterText(io.vertigo.chatbot.commons.domain.topic.UtterText.class),
		/** Objet de données UtterTextExport. */
		UtterTextExport(io.vertigo.chatbot.designer.builder.topic.export.UtterTextExport.class)		;

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
		/** Propriété 'fallback BT'. */
		topics	}

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
	public enum ResponseButtonExportFields implements DtFieldName<io.vertigo.chatbot.designer.builder.topic.export.ResponseButtonExport> {
		/** Propriété 'Topic id'. */
		topId,
		/** Propriété 'Topic code'. */
		topCode,
		/** Propriété 'Topic code response'. */
		topCodeResponse,
		/** Propriété 'Button text'. */
		text	}

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
	public enum ScriptIntentionExportFields implements DtFieldName<io.vertigo.chatbot.designer.builder.topic.export.ScriptIntentionExport> {
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
	 * Enumération des champs de SelectCategory.
	 */
	public enum SelectCategoryFields implements DtFieldName<io.vertigo.chatbot.commons.domain.topic.SelectCategory> {
		/** Propriété 'Category id'. */
		topCatId,
		/** Propriété 'Code'. */
		code	}

	/**
	 * Enumération des champs de SelectProfilChatbotPerson.
	 */
	public enum SelectProfilChatbotPersonFields implements DtFieldName<io.vertigo.chatbot.designer.domain.admin.SelectProfilChatbotPerson> {
		/** Propriété 'Person id'. */
		perId,
		/** Propriété 'Profil cd'. */
		prfId	}

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
	public enum SentenseDetailFields implements DtFieldName<io.vertigo.chatbot.designer.domain.SentenseDetail> {
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
		/** Propriété 'Small Talk selection'. */
		smtId	}

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
	 * Enumération des champs de StatCriteria.
	 */
	public enum StatCriteriaFields implements DtFieldName<io.vertigo.chatbot.designer.domain.StatCriteria> {
		/** Propriété 'Chatbot selection'. */
		botId,
		/** Propriété 'Time option'. */
		timeOption,
		/** Propriété 'Node selection'. */
		nodId	}

	/**
	 * Enumération des champs de TopIntent.
	 */
	public enum TopIntentFields implements DtFieldName<io.vertigo.chatbot.designer.domain.TopIntent> {
		/** Propriété 'Small Talk ID'. */
		smtId,
		/** Propriété 'Rasa intent'. */
		intentRasa,
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
	 * Enumération des champs de TopicExport.
	 */
	public enum TopicExportFields implements DtFieldName<io.vertigo.chatbot.commons.domain.TopicExport> {
		/** Propriété 'Topic name'. */
		name,
		/** Propriété 'nluTrainingSentences'. */
		nluTrainingSentences,
		/** Propriété 'Topic BT'. */
		topicBT	}

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
		/** Propriété 'Script'. */
		script,
		/** Propriété 'Response'. */
		response,
		/** Propriété 'Buttons'. */
		buttons,
		/** Propriété 'End of conversation'. */
		isEnd	}

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
		ktoCd	}

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
		/** Propriété 'Status'. */
		strCd,
		/** Propriété 'Model'. */
		filIdModel	}

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
	public enum UtterTextExportFields implements DtFieldName<io.vertigo.chatbot.designer.builder.topic.export.UtterTextExport> {
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
