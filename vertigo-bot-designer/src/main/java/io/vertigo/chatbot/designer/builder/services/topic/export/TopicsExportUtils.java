package io.vertigo.chatbot.designer.builder.services.topic.export;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.vertigo.chatbot.commons.domain.TopicExport;
import io.vertigo.chatbot.commons.domain.topic.NluTrainingExport;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.datamodel.structure.model.DtList;

public class TopicsExportUtils {

	private TopicsExportUtils() {
		//classe utilitaire
	}

	public static DtList<TopicExport> mapTopicsToNluTrainingSentences(final DtList<Topic> topics, final DtList<NluTrainingExport> nlus, final Map<Long, String> mapBtTopic, final String prefix) {
		final DtList<TopicExport> exports = new DtList<TopicExport>(TopicExport.class);
		for (final Topic topic : topics) {
			final Long topId = topic.getTopId();
			final List<String> topicNlu = nlus.stream().filter(x -> x.getTopId().equals(topId)).map(x -> x.getText()).collect(Collectors.toList());
			final TopicExport export = new TopicExport();
			export.setName(prefix + topId);
			export.setNluTrainingSentences(topicNlu);
			export.setTopicBT(mapBtTopic.get(topId));
			exports.add(export);
		}
		return exports;
	}
}
