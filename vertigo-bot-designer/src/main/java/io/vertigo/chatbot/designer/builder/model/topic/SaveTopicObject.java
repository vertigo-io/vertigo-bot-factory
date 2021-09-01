package io.vertigo.chatbot.designer.builder.model.topic;

import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.topic.ResponseButton;
import io.vertigo.chatbot.commons.domain.topic.Topic;
import io.vertigo.chatbot.commons.domain.topic.UtterText;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.Entity;

/**
 * Use for mutualise save function of topic
 *
 * @author vbaillet
 * @param <D> topic type
 */
public final class SaveTopicObject<D extends Entity> {

	private Topic topic;

	private Chatbot bot;

	private D object;

	//Need for smalltalk, null for scriptintention
	private DtList<ResponseButton> buttons;

	//Need for smalltalk, null for scriptintention
	private DtList<UtterText> utters;

	/**
	 * Use for smalltalk
	 *
	 * @param topic
	 * @param bot
	 * @param object
	 * @param buttons
	 * @param utters
	 */
	public SaveTopicObject(final Topic topic, final Chatbot bot, final D object, final DtList<ResponseButton> buttons, final DtList<UtterText> utters) {
		super();
		this.topic = topic;
		this.bot = bot;
		this.object = object;
		this.buttons = buttons;
		this.utters = utters;
	}

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(final Topic topic) {
		this.topic = topic;
	}

	public Chatbot getBot() {
		return bot;
	}

	public void setBot(final Chatbot bot) {
		this.bot = bot;
	}

	public D getObject() {
		return object;
	}

	public void setObject(final D object) {
		this.object = object;
	}

	public DtList<ResponseButton> getButtons() {
		return buttons;
	}

	public void setButtons(final DtList<ResponseButton> buttons) {
		this.buttons = buttons;
	}

	public DtList<UtterText> getUtters() {
		return utters;
	}

	public void setUtters(final DtList<UtterText> utters) {
		this.utters = utters;
	}

}
