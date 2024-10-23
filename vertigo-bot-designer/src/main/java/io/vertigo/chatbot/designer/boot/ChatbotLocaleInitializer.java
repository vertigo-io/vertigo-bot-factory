package io.vertigo.chatbot.designer.boot;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.DtResources;
import io.vertigo.chatbot.commons.multilingual.ConstraintResources;
import io.vertigo.chatbot.commons.multilingual.attachment.AttachmentMultilingualResources;
import io.vertigo.chatbot.commons.multilingual.bot.BotMultilingualResources;
import io.vertigo.chatbot.commons.multilingual.categories.CategoriesMultilingualResources;
import io.vertigo.chatbot.commons.multilingual.context.ContextValueMultilingualResources;
import io.vertigo.chatbot.commons.multilingual.dictionaryEntities.DictionaryEntityMultilingualResources;
import io.vertigo.chatbot.commons.multilingual.export.ExportMultilingualResources;
import io.vertigo.chatbot.commons.multilingual.extensions.ExtensionsMultilingualResources;
import io.vertigo.chatbot.commons.multilingual.kindTopic.KindTopicMultilingualResources;
import io.vertigo.chatbot.commons.multilingual.model.ModelMultilingualResources;
import io.vertigo.chatbot.commons.multilingual.person.PersonMultilingualResources;
import io.vertigo.chatbot.commons.multilingual.queAnsCategory.QueAnsCategoryMultilingualResources;
import io.vertigo.chatbot.commons.multilingual.questionAnswer.QuestionAnswerMultilingualResources;
import io.vertigo.chatbot.commons.multilingual.topicFileExport.TopicFileExportMultilingualResources;
import io.vertigo.chatbot.commons.multilingual.topics.TopicsMultilingualResources;
import io.vertigo.chatbot.commons.multilingual.utils.UtilsMultilingualResources;
import io.vertigo.chatbot.designer.analytics.multilingual.AnalyticsMultilingualResources;
import io.vertigo.chatbot.designer.domain.commons.EnumResource;
import io.vertigo.core.locale.LocaleManager;
import io.vertigo.core.node.component.ComponentInitializer;

public class ChatbotLocaleInitializer implements ComponentInitializer {

	@Inject
	private LocaleManager localeManager;

	@Override
	public void init() {
		// Vertigo
		localeManager.add("io.vertigo.basics.constraint.Constraint", io.vertigo.basics.constraint.Resources.values());

		// ChatbotCommon
		localeManager.add("io.vertigo.chatbot.commons.multilingual.kindTopic.KindTopicMultilingualResources", KindTopicMultilingualResources.values());
		localeManager.add("io.vertigo.chatbot.commons.multilingual.ConstraintResources", ConstraintResources.values());

		// ChatbotDesigner
		localeManager.add("io.vertigo.chatbot.commons.domain.DtResources", DtResources.values());
		localeManager.add("io.vertigo.chatbot.designer.domain.DtResources", io.vertigo.chatbot.designer.domain.DtResources.values());
		localeManager.add("io.vertigo.chatbot.designer.domain.analytics.DtResources", io.vertigo.chatbot.designer.domain.analytics.DtResources.values());
		localeManager.add("io.vertigo.chatbot.designer.domain.admin.DtResources", io.vertigo.chatbot.designer.domain.admin.DtResources.values());
		localeManager.add("io.vertigo.chatbot.designer.domain.commons.DtResources", io.vertigo.chatbot.designer.domain.commons.DtResources.values());
		localeManager.add("io.vertigo.chatbot.commons.domain.topic.DtResources", io.vertigo.chatbot.commons.domain.topic.DtResources.values());
		localeManager.add("io.vertigo.chatbot.commons.domain.questionanswer.DtResources", io.vertigo.chatbot.commons.domain.questionanswer.DtResources.values());
		localeManager.add("io.vertigo.chatbot.designer.domain.commons.EnumResources", EnumResource.values());
		localeManager.add("io.vertigo.chatbot.commons.multilingual.topics.TopicsMultilingualResources", TopicsMultilingualResources.values());
		localeManager.add("io.vertigo.chatbot.commons.multilingual.topicFileExport.TopicFileExportMultilingualResources", TopicFileExportMultilingualResources.values());
		localeManager.add("io.vertigo.chatbot.commons.multilingual.model.ModelMultilingualResources", ModelMultilingualResources.values());
		localeManager.add("io.vertigo.chatbot.commons.multilingual.utils.UtilsMultilingualResources", UtilsMultilingualResources.values());
		localeManager.add("io.vertigo.chatbot.commons.multilingual.person.PersonMultilingualResources", PersonMultilingualResources.values());
		localeManager.add("io.vertigo.chatbot.commons.multilingual.context.ContextValueMultilingualResources", ContextValueMultilingualResources.values());
		localeManager.add("io.vertigo.chatbot.designer.analytics.multilingual.AnalyticsMultilingualResources", AnalyticsMultilingualResources.values());
		localeManager.add("io.vertigo.chatbot.commons.multilingual.bot.BotMultilingualResources", BotMultilingualResources.values());
		localeManager.add("io.vertigo.chatbot.commons.multilingual.categories.CategoriesMultilingualResources", CategoriesMultilingualResources.values());
		localeManager.add("io.vertigo.chatbot.commons.multilingual.export.ExportMultilingualResources", ExportMultilingualResources.values());
		localeManager.add("io.vertigo.chatbot.commons.multilingual.dictionaryEntities.DictionaryEntityMultilingualResources", DictionaryEntityMultilingualResources.values());
		localeManager.add("io.vertigo.chatbot.commons.multilingual.extensions.ExtensionsMultilingualResources", ExtensionsMultilingualResources.values());
		localeManager.add("io.vertigo.chatbot.commons.multilingual.attachment.AttachmentMultilingualResources", AttachmentMultilingualResources.values());
		localeManager.add("io.vertigo.chatbot.commons.multilingual.queAnsCategory.QueAnsCategoryMultilingualResources", QueAnsCategoryMultilingualResources.values());
		localeManager.add("io.vertigo.chatbot.commons.multilingual.questionAnswer.QuestionAnswerMultilingualResources", QuestionAnswerMultilingualResources.values());
	}
}
