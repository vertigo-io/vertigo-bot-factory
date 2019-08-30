package io.vertigo.chatbot.executor.rasa.util;

public class StringUtils {
	
	private StringUtils() {
		// Classe utilitaire
	}

	public static String labelToCode(String label) {
		return label.replaceAll("[\\s\\:]", "_");
	}
}
