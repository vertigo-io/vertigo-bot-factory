package io.vertigo.chatbot.engine.model.choice;

import io.vertigo.core.lang.Assertion;

import java.util.List;

public class BotFileButton implements IBotChoice {

	private final String label;
	private final String payload;
	private final String fileContent;
	private final String fileName;

	public BotFileButton(String label, String payload, String fileContent, String fileName) {
		this.label = label;
		this.payload = payload;
		this.fileName = fileName;
		this.fileContent = fileContent;
	}

	public static IBotChoice of(final String[] params) {
		Assertion.check().isTrue(params.length == 2, "BotFileButton need exactly 3 params");
		return new BotFileButton(params[0], params[1],null, null);
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getPayload() {
		return payload;
	}

	public String getFileName() {
		return fileName;
	}

	@Override
	public String[] exportParams() {
		return List.of(label, payload).toArray(String[]::new);
	}
}
