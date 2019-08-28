package io.vertigo.chatbot.executor.rasa.config;

import java.util.List;

import io.vertigo.lang.Assertion;

public class RasaAction {
	
	private static final String NEW_LINE = "\r\n";

	private final String name;
	private final boolean isUtterance;
	private final boolean isSmallTalk;
	private final List<String> texts;
	
	public static RasaAction newStUtterance(String name, List<String> texts) {
		return new RasaAction("utter_st_" + name, true, true, texts);
	}
	
	public static RasaAction newUtterance(String name, List<String> texts) {
		return new RasaAction("utter_" + name, true, false, texts);
	}
	
	public static RasaAction newCustomAction(String name) {
		return new RasaAction("action_" + name, false, false, null);
	}
	
	private RasaAction(String name, boolean isUtterance, boolean isSmallTalk, List<String> texts) {
		this.name = name;
		this.isUtterance = isUtterance;
		this.isSmallTalk = isSmallTalk;
		this.texts = texts;
	}

	public String getName() {
		return name;
	}

	public boolean isUtterance() {
		return isUtterance;
	}

	public boolean isSmallTalk() {
		return isSmallTalk;
	}

	public List<String> getTexts() {
		return texts;
	}
	
	public String getUtterTemplate() {
		Assertion.checkState(isUtterance, "Cette action n'est pas un utter");
		// ----
		
		StringBuilder template = new StringBuilder(); 
		
		template.append("  ").append(name).append(':').append(NEW_LINE);
		
		for (String answer : texts) {
			template.append("    - text: \"");
			template.append(answer.replaceAll("[\\r\\n]+", "<br>").replaceAll("[\\\"]", "\\\\\\\""));
			template.append('"').append(NEW_LINE);
		}
		
		template.append(NEW_LINE);
		
		return template.toString();
	}
}
