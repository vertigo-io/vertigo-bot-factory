package io.vertigo.chatbot.designer.builder.services.topic;

public final class TopicsUtils {

	private TopicsUtils() {
		//classe utilitaire
	}

	public static boolean checkSpecialCharacters(final String string) {
		return string.contains("[") || string.contains("]") || string.contains("|") || string.contains("¤");
	}
}
