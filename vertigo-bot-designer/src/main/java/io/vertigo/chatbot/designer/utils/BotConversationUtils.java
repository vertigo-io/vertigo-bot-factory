package io.vertigo.chatbot.designer.utils;

import java.util.Map;

import io.vertigo.chatbot.engine.model.BotInput;
import io.vertigo.chatbot.engine.model.TalkInput;

public class BotConversationUtils {

	private BotConversationUtils() {
		// Nothing to do
	}

	public static String createBotInput(final String message, final Map<String, Object> metadatas) {
		final BotInput input = new BotInput(message, metadatas);
		return ObjectConvertionUtils.objectToJson(input);

	}

	public static String createBotInput(final TalkInput talkInput) {
		String message = talkInput.getMessage();
		Map<String, Object> payload = talkInput.getMetadatas();
		return ObjectConvertionUtils.objectToJson(new BotInput(message, payload));
	}

	public static String createBotInput(final String message) {
		BotInput input = new BotInput(message);
		return ObjectConvertionUtils.objectToJson(input);
	}

}
