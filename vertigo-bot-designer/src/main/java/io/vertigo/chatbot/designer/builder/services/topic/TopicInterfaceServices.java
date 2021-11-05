package io.vertigo.chatbot.designer.builder.services.topic;

import java.util.Optional;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.UtterText;
import io.vertigo.chatbot.designer.builder.model.topic.SaveTopicObject;
import io.vertigo.chatbot.designer.domain.commons.BotPredefinedTopic;
import io.vertigo.datamodel.structure.model.Entity;

public interface TopicInterfaceServices<D extends Entity> {

	D save(final D object);

	D saveFromSaveTopicObject(final SaveTopicObject<D> saveObject);

	void deleteIfExists(final Chatbot chatbot, final Topic topic);

	boolean handleObject(final Topic topic);

	void delete(D object);

	Optional<D> findByTopId(final Long topId);

	void createOrUpdateFromTopic(final Chatbot chatbot, final Topic topic, final String text);

	default boolean isEnabled(final D object, final boolean isEnabled, final Chatbot bot) {
		return !(hasToBeDeactivated(object, bot)) && isEnabled;
	}

	boolean hasToBeDeactivated(final D object, final Chatbot bot);

	UtterText getBasicUtterTextByTopId(Long topId);

	String getDeactivateMessage();

	BotPredefinedTopic getBotPredefinedTopicByTopId(Long topId);
}
