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
		/** Objet de données ExecutorTrainingCallback. */
		ExecutorTrainingCallback(io.vertigo.chatbot.commons.domain.ExecutorTrainingCallback.class),
		/** Objet de données Groups. */
		Groups(io.vertigo.chatbot.designer.domain.commons.Groups.class),
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
		/** Objet de données ResponseType. */
		ResponseType(io.vertigo.chatbot.commons.domain.topic.ResponseType.class),
		/** Objet de données RunnerInfo. */
		RunnerInfo(io.vertigo.chatbot.commons.domain.RunnerInfo.class),
		/** Objet de données ScriptIntention. */
		ScriptIntention(io.vertigo.chatbot.commons.domain.topic.ScriptIntention.class),
		/** Objet de données ScriptIntentionIhm. */
		ScriptIntentionIhm(io.vertigo.chatbot.commons.domain.topic.ScriptIntentionIhm.class),
		/** Objet de données SelectProfilChatbotPerson. */
		SelectProfilChatbotPerson(io.vertigo.chatbot.designer.domain.admin.SelectProfilChatbotPerson.class),
		/** Objet de données SentenseDetail. */
		SentenseDetail(io.vertigo.chatbot.designer.domain.SentenseDetail.class),
		/** Objet de données SmallTalk. */
		SmallTalk(io.vertigo.chatbot.commons.domain.topic.SmallTalk.class),
		/** Objet de données SmallTalkExport. */
		SmallTalkExport(io.vertigo.chatbot.commons.domain.SmallTalkExport.class),
		/** Objet de données SmallTalkIhm. */
		SmallTalkIhm(io.vertigo.chatbot.commons.domain.topic.SmallTalkIhm.class),
		/** Objet de données StatCriteria. */
		StatCriteria(io.vertigo.chatbot.designer.domain.StatCriteria.class),
		/** Objet de données TopIntent. */
		TopIntent(io.vertigo.chatbot.designer.domain.TopIntent.class),
		/** Objet de données Topic. */
		Topic(io.vertigo.chatbot.commons.domain.topic.Topic.class),
		/** Objet de données TopicIhm. */
		TopicIhm(io.vertigo.chatbot.commons.domain.topic.TopicIhm.class),
		/** Objet de données TopicCategory. */
		TopicCategory(io.vertigo.chatbot.commons.domain.topic.TopicCategory.class),
		/** Objet de données TrainerInfo. */
		TrainerInfo(io.vertigo.chatbot.commons.domain.TrainerInfo.class),
		/** Objet de données Training. */
		Training(io.vertigo.chatbot.commons.domain.Training.class),
		/** Objet de données TypeTopic. */
		TypeTopic(io.vertigo.chatbot.commons.domain.topic.TypeTopic.class),
		/** Objet de données UtterText. */
		UtterText(io.vertigo.chatbot.commons.domain.topic.UtterText.class)		;

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
		/** Propriété 'fallback'. */
		fallbackText,
		/** Propriété 'fallback buttons'. */
		fallbackButtons,
		/** Propriété 'welcome'. */
		welcomeText,
		/** Propriété 'welcome buttons'. */
		welcomeButtons	}

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
		filIdAvatar,
		/** Propriété 'Welcome text'. */
		uttIdWelcome,
		/** Propriété 'Default text'. */
		uttIdDefault	}

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
		nluThreshold	}

	/**
	 * Enumération des champs de ExecutorTrainingCallback.
	 */
	public enum ExecutorTrainingCallbackFields implements DtFieldName<io.vertigo.chatbot.commons.domain.ExecutorTrainingCallback> {
		/** Propriété 'Training ID'. */
		trainingId,
		/** Propriété 'Succes'. */
		success,
		/** Propriété 'Logs'. */
		log,
		/** Propriété 'Informations'. */
		infos,
		/** Propriété 'Warnings'. */
		warnings,
		/** Propriété 'Client Api Key'. */
		apiKey	}

	/**
	 * Enumération des champs de Groups.
	 */
	public enum GroupsFields implements DtFieldName<io.vertigo.chatbot.designer.domain.commons.Groups> {
		/** Propriété 'Id'. */
		grpId,
		/** Propriété 'Name'. */
		name	}

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
		/** Propriété 'smt ID'. */
		smtId,
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
		/** Propriété 'SmallTalkResponse'. */
		smtIdResponse,
		/** Propriété 'welcome buttons'. */
		botIdWelcome,
		/** Propriété 'Default buttons'. */
		botIdDefault	}

	/**
	 * Enumération des champs de ResponseType.
	 */
	public enum ResponseTypeFields implements DtFieldName<io.vertigo.chatbot.commons.domain.topic.ResponseType> {
		/** Propriété 'ID'. */
		rtyId,
		/** Propriété 'Title'. */
		label,
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
		/** Propriété 'Topic'. */
		topId,
		/** Propriété 'Response type'. */
		rtyId	}

	/**
	 * Enumération des champs de SmallTalkExport.
	 */
	public enum SmallTalkExportFields implements DtFieldName<io.vertigo.chatbot.commons.domain.SmallTalkExport> {
		/** Propriété 'SmallTalk'. */
		smallTalk,
		/** Propriété 'nluTrainingSentences'. */
		nluTrainingSentences,
		/** Propriété 'response texts'. */
		utterTexts,
		/** Propriété 'buttons'. */
		buttons	}

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
		/** Propriété 'Type du topic'. */
		ttoCd,
		/** Propriété 'Chatbot'. */
		botId,
		/** Propriété 'Topic'. */
		topCatId	}

	/**
	 * Enumération des champs de TopicCategory.
	 */
	public enum TopicCategoryFields implements DtFieldName<io.vertigo.chatbot.commons.domain.topic.TopicCategory> {
		/** Propriété 'Topic category id'. */
		topCatId,
		/** Propriété 'Topic category label'. */
		label,
		/** Propriété 'Category level'. */
		level,
		/** Propriété 'Enabled'. */
		isEnabled,
		/** Propriété 'Chatbot'. */
		botId	}

	/**
	 * Enumération des champs de TopicIhm.
	 */
	public enum TopicIhmFields implements DtFieldName<io.vertigo.chatbot.commons.domain.topic.TopicIhm> {
		/** Propriété 'Topic id'. */
		topId,
		/** Propriété 'Type'. */
		type,
		/** Propriété 'Topic label'. */
		title,
		/** Propriété 'SmallTalk id'. */
		smtId,
		/** Propriété 'Script intention id'. */
		sinId,
		/** Propriété 'Enabled'. */
		isEnabled	}

	/**
	 * Enumération des champs de TrainerInfo.
	 */
	public enum TrainerInfoFields implements DtFieldName<io.vertigo.chatbot.commons.domain.TrainerInfo> {
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
		/** Propriété 'Status'. */
		status,
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
		filIdModel	}

	/**
	 * Enumération des champs de TypeTopic.
	 */
	public enum TypeTopicFields implements DtFieldName<io.vertigo.chatbot.commons.domain.topic.TypeTopic> {
		/** Propriété 'ID'. */
		ttoCd,
		/** Propriété 'Title'. */
		label	}

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
