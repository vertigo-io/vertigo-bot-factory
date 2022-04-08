package io.vertigo.chatbot.designer.builder.services.topic;

public final class TopicsUtils {

	public static final String DEFAULT_TOPIC_CAT_CODE = "DEFAULT";

	private TopicsUtils() {
		//classe utilitaire
	}

	public static boolean checkSpecialCharacters(final String string) {
		return string.contains("[") || string.contains("]") || string.contains("|") || string.contains("¤");
	}
}
