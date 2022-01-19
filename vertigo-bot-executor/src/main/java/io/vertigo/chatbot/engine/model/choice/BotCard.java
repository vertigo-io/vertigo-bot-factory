package io.vertigo.chatbot.engine.model.choice;

import io.vertigo.core.lang.Assertion;

import java.util.List;
import java.util.Optional;

public class BotCard implements IBotChoice {

	private final String label;

	private final String payload;

	private final String content;

	private final Optional<String> imageUrl;

	public BotCard(final String label, final String payload, final String content, final Optional<String> imageUrl) {
		this.label = label;
		this.payload = payload;
		this.content = content;
		this.imageUrl = imageUrl;
	}


	public static IBotChoice of(final String[] params) {
		Assertion.check().isTrue(params.length >= 3, "BotCard need at least 3 params");
		//--
		String imageUrl = null;
		if (params.length == 4 ) {
			imageUrl = params[3];
		}
		return new BotCard(params[0], params[1], params[2], Optional.ofNullable(imageUrl));
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getPayload() {
		return payload;
	}

	public String getContent() { return content; }

	public Optional<String> getImageUrl() { return imageUrl; }

	@Override
	public String[] exportParams() {
		if (imageUrl.isPresent()) {
			return List.of(label, payload, content, imageUrl.get()).toArray(String[]::new);
		} else {
			return List.of(label, payload, content).toArray(String[]::new);
		}
	}
}
