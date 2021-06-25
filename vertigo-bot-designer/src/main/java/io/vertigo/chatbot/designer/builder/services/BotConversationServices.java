package io.vertigo.chatbot.designer.builder.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;
import java.util.Map;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.multilingual.utils.UtilsMultilingualResources;
import io.vertigo.chatbot.engine.model.BotInput;
import io.vertigo.chatbot.engine.model.TalkInput;
import io.vertigo.core.lang.VSystemException;
import io.vertigo.core.locale.MessageText;
import io.vertigo.core.node.component.Component;
import io.vertigo.vega.engines.webservice.json.JsonEngine;

public class BotConversationServices implements Component {

	@Inject
	private JsonEngine jsonEngine;

	public String createBotInput(final String message, final Map<String, Object> metadatas) {
		final BotInput input = new BotInput(message, metadatas);
		return objectToJson(input);

	}

	public String createBotInput(final TalkInput talkInput) {
		String message = talkInput.getMessage();
		if (talkInput.isButton()) {
			return objectToJson(new BotInput(Map.of("payload", message)));
		}
		return createBotInput(message);
	}

	public String objectToJson(final Object input) {
		return jsonEngine.toJson(input);
	}

	public <D> D jsonToObject(final String json, final Type type) {
		return jsonEngine.fromJson(json, type);
	}

	public <D> D objectFromByteArray(final byte[] object) {
		ByteArrayInputStream bis = new ByteArrayInputStream(object);

		try (ObjectInput in = new ObjectInputStream(bis);) {
			return (D) in.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new VSystemException(e.getMessage(), MessageText.of(UtilsMultilingualResources.SERIALIZATION_ERROR).getDisplay());
		}

	}

	public String createBotInput(final String message) {
		BotInput input = new BotInput(message);
		return objectToJson(input);
	}

}
