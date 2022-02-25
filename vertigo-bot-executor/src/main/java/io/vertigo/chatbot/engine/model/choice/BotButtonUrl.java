package io.vertigo.chatbot.engine.model.choice;

import io.vertigo.core.lang.Assertion;

import java.util.List;

import static io.vertigo.chatbot.engine.plugins.bt.command.bot.BotNodeProvider.isValidURL;

public class BotButtonUrl implements IBotChoice {

	private final String label;
	private final String payload;
	private final String url;
	private final Boolean newTab;

	public BotButtonUrl(String label, String payload,  String url, boolean newTab) {
		this.label = label;
		this.payload = payload;
		this.url = url;
		this.newTab = newTab;
	}

	public static IBotChoice of(final String[] params) {
		Assertion.check().isTrue(params.length == 4, "BotButtonUrl need exactly 4 params");
		//--
		String url = params[2];
		Assertion.check().isTrue(isValidURL(url), "Not a valid URL");
		return new BotButtonUrl(params[0], params[1], url, Boolean.parseBoolean(params[3]));
	}

	public boolean isNewTab() {
		return newTab;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getPayload() {
		return payload;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public String[] exportParams() {
		return List.of(label, payload, url, newTab.toString()).toArray(String[]::new);
	}

}
