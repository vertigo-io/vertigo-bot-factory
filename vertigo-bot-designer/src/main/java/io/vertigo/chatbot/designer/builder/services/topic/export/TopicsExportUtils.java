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

	public static DtList<TopicExport> mapTopicsToNluTrainingSentences(final DtList<Topic> topics, final DtList<NluTrainingExport> nlus, final Map<Long, String> mapBtTopic, final StringBuilder logs) {
		final DtList<TopicExport> exports = new DtList<>(TopicExport.class);
		for (final Topic topic : topics) {
			logs.append("Export topic " + topic.getCode() + ":\r\n");

			final Long topId = topic.getTopId();
			final String code = topic.getCode();
			final List<String> topicNlu = nlus.stream()
					.filter(x -> x.getTopId().equals(topId))
					.map(NluTrainingExport::getText)
					.collect(Collectors.toList());
			final TopicExport export = new TopicExport();
			export.setName(code);
			export.setNluTrainingSentences(topicNlu);
			export.setTopicBT(mapBtTopic.get(topId));
			exports.add(export);
			logs.append("Nlu : " + topicNlu + "\r\n");
		}
		return exports;
	}
}
