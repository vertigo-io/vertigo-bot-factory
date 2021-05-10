package io.vertigo.chatbot.designer.builder.services.topic.export;

import java.util.Map;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.TopicExport;
import io.vertigo.datamodel.structure.model.DtList;

public interface TopicsExportServices {

	/**
	 * Get topicExport by bot
	 *
	 * @param bot chatbot to export
	 * @return List of topicExports
	 */
	public DtList<TopicExport> exportTopics(final Chatbot bot);

	/**
	 * Create or get bt to topic
	 *
	 * @param bot chatbot to export
	 * @return map<topId, bt> associated
	 */
	public Map<Long, String> mapTopicToBt(final Chatbot bot);
}
