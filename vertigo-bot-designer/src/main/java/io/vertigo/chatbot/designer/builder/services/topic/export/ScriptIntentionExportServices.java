package io.vertigo.chatbot.designer.builder.services.topic.export;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.TopicExport;
import io.vertigo.chatbot.commons.domain.topic.NluTrainingExport;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.chatbot.designer.builder.topic.export.ExportPAO;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtList;

@Transactional
public class ScriptIntentionExportServices implements TopicsExportServices, Component {

	private final static String PREFIX = "si_";

	@Inject
	private TopicServices topicServices;

	@Inject
	private ExportPAO exportPAO;

	@Override
	public DtList<TopicExport> exportTopics(final Chatbot bot) {
		final DtList<Topic> topics = topicServices.getAllTopicRelativeScriptIntentionByBot(bot);
		final DtList<NluTrainingExport> nlus = exportPAO.exportScriptIntentionRelativeTrainingSentence(bot.getBotId());
		return TopicsExportUtils.mapTopicsToNluTrainingSentences(topics, nlus, PREFIX);

	}
}
