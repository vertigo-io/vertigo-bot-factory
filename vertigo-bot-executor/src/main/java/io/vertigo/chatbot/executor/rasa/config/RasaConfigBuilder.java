package io.vertigo.chatbot.executor.rasa.config;

import java.util.ArrayList;
import java.util.List;

import io.vertigo.chatbot.executor.domain.RasaConfig;

public class RasaConfigBuilder {

	private static final String NEW_LINE = "\r\n";

	private final String defaultText;
	private final String welcomeText;

	private final List<RasaAction> actions = new ArrayList<>();
	private final List<RasaIntent> intents = new ArrayList<>();

	public RasaConfigBuilder(final String defaultText, final String welcomeText) {
		this.defaultText = defaultText;
		this.welcomeText = welcomeText;
	}

	public RasaConfigBuilder addSmallTalk(final String name, final List<String> nlus, final List<String> messages) {
		//		Assertion.checkState(!utterances.containsKey(name), "Le code de message {1} existe déjà", name);
		// ----
		final RasaAction action = RasaAction.newStUtterance(name, messages);
		final RasaIntent intent = RasaIntent.newSmallTalk(name, nlus, action);

		actions.add(action);
		intents.add(intent);

		return this;
	}

	public RasaConfig build() {
		final RasaConfig rasaConfig = new RasaConfig();

		rasaConfig.setDomain(doBuildDomain());
		rasaConfig.setNlu(doBuildNlu());
		rasaConfig.setStories(doBuildStories());

		return rasaConfig;
	}

	private String doBuildDomain() {
		final StringBuilder retour = new StringBuilder();

		retour.append("intents:");
		retour.append(NEW_LINE);

		retour.append("  - start");
		retour.append(NEW_LINE);

		for (final RasaIntent intent : intents) {
			retour.append(intent.getDomainDeclaration());
			retour.append(NEW_LINE);
		}
		retour.append(NEW_LINE);

		// ajout des actions
		retour.append("actions:");
		retour.append(NEW_LINE);
		retour.append("  - utter_default");
		retour.append(NEW_LINE);
		retour.append("  - utter_start");
		retour.append(NEW_LINE);

		for (final RasaAction action : actions) {
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
		retour.append("    - text: \"");
		retour.append(defaultText);
		retour.append("\"");
		retour.append(NEW_LINE);
		retour.append(NEW_LINE);

		retour.append("  utter_start:");
		retour.append(NEW_LINE);
		retour.append("    - text: \"");
		retour.append(welcomeText);
		retour.append("\"");
		retour.append(NEW_LINE);
		retour.append(NEW_LINE);

		for (final RasaAction action : actions) {
			retour.append(action.getUtterTemplate());
		}

		return retour.toString();
	}

	private String doBuildNlu() {
		final StringBuilder retour = new StringBuilder();

		for (final RasaIntent intent : intents) {
			retour.append(intent.getNluDeclaration())
			.append(NEW_LINE);
		}

		return retour.toString();
	}

	private String doBuildStories() {
		return "## start" + NEW_LINE +
				"* start" + NEW_LINE +
				"    - utter_start";
	}
}
