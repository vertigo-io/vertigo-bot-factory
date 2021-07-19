package io.vertigo.chatbot.designer.builder.services.topic;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.UtterText;
import io.vertigo.datamodel.structure.model.Entity;

public interface TopicInterfaceServices<D extends Entity> {

	D save(final D object);

	void delete(final Chatbot chatbot, final D object, final Topic topic);

	boolean handleObject(final Topic topic);

	void delete(D object);

	D findByTopId(final Long topId);

	void initializeBasic(final Chatbot chatbot, final Topic topic, final String text);

	boolean isEnabled(final D object, final boolean isEnabled, final Chatbot bot);

	UtterText getBasicUtterTextByTopId(Long topId);
}
