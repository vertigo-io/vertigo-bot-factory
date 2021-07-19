package io.vertigo.chatbot.designer.builder.services.topic.export;

import java.util.Map;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.TopicExport;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.Entity;

public interface TopicExportInterfaceServices<D extends Entity> {

	boolean handleObject(final Topic topic);

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

	public String getBasicBt(Chatbot bot, String ktoCd);

}
