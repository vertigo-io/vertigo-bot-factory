package io.vertigo.chatbot.executor.rasa.util;

public class StringUtils {

	private StringUtils() {
		// Classe utilitaire
	}

	public static String labelToCode(final String label) {
		return label.replaceAll("[^\\p{L}0-9]", "_");
	}
}
