package io.vertigo.chatbot.designer.builder.services.topic;

import java.util.Optional;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.*;
import io.vertigo.chatbot.designer.builder.model.topic.SaveTopicObject;
import io.vertigo.chatbot.designer.domain.commons.BotPredefinedTopic;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtObject;
import io.vertigo.datamodel.structure.model.Entity;

@Secured("BotUser")
public interface ITopicService<D extends Entity> {

	D save(final D object);

	D saveFromSaveTopicObject(final SaveTopicObject<D> saveObject);

	/**
	 * Save a topic and its specific elements depending on its type (smalltalk, scriptIntention)
	 * @param topic Global topic to save
	 * @param chatbot Topic's chatbot
	 * @param dtObject High level object holding different types of topic (smalltalk, scriptIntention ...)
	 * @param buttonList List of buttons
	 * @param utterTexts List of text responses
	 */
	void saveTopic(Topic topic, Chatbot chatbot,
						  DtObject dtObject,
						  final DtList<ResponseButton> buttonList,
						  final DtList<UtterText> utterTexts);

	void deleteIfExists(final Chatbot chatbot, final Topic topic);

	boolean handleObject(final Topic topic);

	void delete(D object);

	Optional<D> findByTopId(final Long topId);

	void createOrUpdateFromTopic(final Chatbot chatbot, final Topic topic, final String text);

	default boolean isEnabled(final DtObject object, final boolean isEnabled, final Chatbot bot) {
		return !(hasToBeDeactivated(object, bot)) && isEnabled;
	}

	boolean hasToBeDeactivated(final DtObject object, final Chatbot bot);

	String getDeactivateMessage();

	BotPredefinedTopic getBotPredefinedTopicByTopId(Long topId);
}
