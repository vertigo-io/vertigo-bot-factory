package io.vertigo.chatbot.designer.builder.services.topic.export;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.TopicExport;
import io.vertigo.chatbot.commons.domain.topic.NluTrainingExport;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.designer.builder.services.topic.TopicServices;
import io.vertigo.chatbot.designer.builder.topic.export.ExportPAO;
import io.vertigo.chatbot.designer.builder.topic.export.ScriptIntentionExport;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.DtList;

@Transactional
public class ScriptIntentionExportServices implements TopicsExportServices, Component {

	@Inject
	private TopicServices topicServices;

	@Inject
	private ExportPAO exportPAO;

	@Override
	public DtList<TopicExport> exportTopics(final Chatbot bot) {
		final DtList<Topic> topics = topicServices.getAllTopicRelativeScriptIntentionByBot(bot);
		final DtList<NluTrainingExport> nlus = exportPAO.exportScriptIntentionRelativeTrainingSentence(bot.getBotId());
		final Map<Long, String> mapTopicBt = mapTopicToBt(bot);
		final DtList<TopicExport> exports = TopicsExportUtils.mapTopicsToNluTrainingSentences(topics, nlus, mapTopicBt);
		return exports;

	}

	@Override
	public Map<Long, String> mapTopicToBt(final Chatbot bot) {
		final DtList<ScriptIntentionExport> exports = exportPAO.getScriptIntentionExportByBotId(bot.getBotId());
		final Map<Long, String> result = new HashMap<>();
		for (final ScriptIntentionExport export : exports) {
			result.put(export.getTopId(), export.getBt());
		}
		return result;
	}
}
