package io.vertigo.chatbot.executor.rasa.config;

import java.util.ArrayList;
import java.util.List;

import io.vertigo.chatbot.executor.domain.RasaConfig;

public class RasaConfigBuilder {
	
	private static final String NEW_LINE = "\r\n";
	
	private List<RasaAction> actions = new ArrayList<>();
	private List<RasaIntent> intents = new ArrayList<>();
	
	public RasaConfigBuilder addSmallTalk(String name, List<String> nlus, List<String> messages) {
//		Assertion.checkState(!utterances.containsKey(name), "Le code de message {1} existe déjà", name);
		// ----
		RasaAction action = RasaAction.newStUtterance(name, messages);
		RasaIntent intent = RasaIntent.newSmallTalk(name, nlus, action);
		
		actions.add(action);
		intents.add(intent);
		
		return this;
	}
	
	public RasaConfig build() {
		RasaConfig rasaConfig = new RasaConfig();
		
		rasaConfig.setDomain(doBuildDomain());
		rasaConfig.setNlu(doBuildNlu());
		rasaConfig.setStories(doBuildStories());
		
		return rasaConfig;
	}
	
	private String doBuildDomain() {
		StringBuilder retour = new StringBuilder();
		
		retour.append("intents:");
		retour.append(NEW_LINE);
		
		retour.append("  - start");
		retour.append(NEW_LINE);
		
		for (RasaIntent intent : intents) {
			retour.append("  - ");
			retour.append(intent.getCode());
			retour.append(NEW_LINE);
		}
		retour.append(NEW_LINE);
		
		// ajout des actions
		retour.append("actions:");
		retour.append(NEW_LINE);
		retour.append("  - utter_default");
		retour.append(NEW_LINE);
		
		for (RasaAction action : actions) {
			retour.append("  - ");
			retour.append(action.getCode());
			retour.append(NEW_LINE);
		}
		retour.append(NEW_LINE);
		
		// ajout des templates
		retour.append("templates:");
		retour.append(NEW_LINE);
		
		retour.append("  utter_default:");
		retour.append(NEW_LINE); 
		retour.append("    - text: \"Je n'ai pas compris, merci de reformuler.\""); 
		retour.append(NEW_LINE);
		retour.append(NEW_LINE);
		
		for (RasaAction action : actions) {
			retour.append(action.getUtterTemplate());
		}
		
		return retour.toString();
	}
	
	private String doBuildNlu() {
		StringBuilder retour = new StringBuilder();
		
		for (RasaIntent intent : intents) {
			retour.append(intent.getNluDeclaration())
				.append(NEW_LINE);
		}
		
		return retour.toString();
	}
	
	private String doBuildStories() {
		return "## start" + NEW_LINE +
			   "* start" + NEW_LINE +
			   "    - utter_default";
	}
}
