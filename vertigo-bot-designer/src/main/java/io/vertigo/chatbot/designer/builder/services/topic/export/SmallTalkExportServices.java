package io.vertigo.chatbot.designer.builder.services.topic.export;

import java.util.Map;

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
public class SmallTalkExportServices implements TopicsExportServices, Component {

	private final static String PREFIX = "smt_";

	@Inject
	private TopicServices topicServices;

	@Inject
	private ExportPAO exportPAO;

	@Override
	public DtList<TopicExport> exportTopics(final Chatbot bot) {
		final DtList<Topic> topics = topicServices.getAllTopicRelativeSmallTalkByBot(bot);
		final DtList<NluTrainingExport> nlus = exportPAO.exportSmallTalkRelativeTrainingSentence(bot.getBotId());
		final Map<Long, String> mapBtTopic = getBtForSmallTalk(topics, bot);
		final DtList<TopicExport> result = TopicsExportUtils.mapTopicsToNluTrainingSentences(topics, nlus, PREFIX);
		return result;
	}

	private Map<Long, String> getBtForSmallTalk(final DtList<Topic> topics, final Chatbot bot) {
		return null;
	}
}
