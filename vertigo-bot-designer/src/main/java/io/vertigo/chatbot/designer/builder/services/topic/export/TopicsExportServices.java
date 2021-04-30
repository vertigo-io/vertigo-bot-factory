package io.vertigo.chatbot.designer.builder.services.topic.export;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.TopicExport;
import io.vertigo.datamodel.structure.model.DtList;

public interface TopicsExportServices {

	public DtList<TopicExport> exportTopics(final Chatbot bot);
}
