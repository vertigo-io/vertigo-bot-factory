package io.vertigo.chatbot.designer.builder.services.topic.export;

import java.util.Map;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.TypeTopicEnum;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.Entity;

public interface TopicExportInterfaceServices<D extends Entity> {

	TypeTopicEnum getHandleObject();

	String getBasicBt(Chatbot bot, String ktoCd);

	Map<Long, String> mapTopicToBt(Chatbot bot);

	DtList<Topic> getAllNonTechnicalAndActiveTopicByBot(Chatbot bot, TypeTopicEnum typeEnum);

	default boolean handleObject(final Topic topic) {
		return getHandleObject().name().equals(topic.getTtoCd());
	}
}
