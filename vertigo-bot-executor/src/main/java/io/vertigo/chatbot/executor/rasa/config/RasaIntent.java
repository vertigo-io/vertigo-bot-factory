package io.vertigo.chatbot.executor.rasa.config;

import java.util.List;

import io.vertigo.lang.Assertion;

public class RasaIntent {
	
	private static final String NEW_LINE = "\r\n";

	private final String name;
	private final List<String> nlus;
	private final RasaAction trigger;
	
	public static RasaIntent newSmallTalk(String name, List<String> nlus, RasaAction trigger) {
		Assertion.checkArgument(trigger.isSmallTalk(), "Le trigger doit être une action de small talk");
		// ----
		return new RasaIntent("st_" + name, nlus, trigger);
	}
	
	public static RasaIntent newGenericIntent(String name, List<String> nlus) {
		return new RasaIntent(name, nlus, null);
	}
	
	private RasaIntent(String name, List<String> nlus, RasaAction trigger) {
		this.name = name;
		this.nlus = nlus;
		this.trigger = trigger;
	}

	public String getName() {
		return name;
	}

	public List<String> getNlus() {
		return nlus;
	}
	
	public RasaAction getTrigger() {
		return trigger;
	}

	public String getNluDeclaration() {
		StringBuilder nluDef = new StringBuilder(); 
		
		nluDef.append("## intent:").append(name).append(NEW_LINE);
		
		for (String question : nlus) {
			nluDef.append("- ").append(question).append(NEW_LINE);
		}
		
		nluDef.append(NEW_LINE);
		
		return nluDef.toString();
	}
	
	public String getDomainDeclaration() {
		String retour = "- " + name;
		
		if (trigger != null) {
			retour += " {triggers:" + trigger.getName() + "}";
		}
		
		return retour;
	}
}
