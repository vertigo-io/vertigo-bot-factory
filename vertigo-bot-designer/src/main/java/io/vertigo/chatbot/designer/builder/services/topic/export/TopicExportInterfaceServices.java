package io.vertigo.chatbot.designer.builder.services.topic.export;

import java.util.Map;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.designer.builder.services.topic.MeaningServices;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.Entity;

public interface TopicExportInterfaceServices<D extends Entity> {

	public static final MeaningServices meaningServices = new MeaningServices();

	boolean handleObject(final Topic topic);

	public String getBasicBt(Chatbot bot, String ktoCd);

	Map<Long, String> mapTopicToBt(Chatbot bot);

	public DtList<Topic> getAllNonTechnicalTopicByBot(Chatbot bot);

}
