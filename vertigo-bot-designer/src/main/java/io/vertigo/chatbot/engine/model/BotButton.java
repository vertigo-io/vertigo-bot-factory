package io.vertigo.chatbot.engine.model;

import java.util.List;

import io.vertigo.core.lang.Assertion;

/**
 * Basic implementation of IBotChoice for simple buttons.
 *
 * @author skerdudou
 */
public final class BotButton implements IBotChoice {
	private final String label;
	private final String payload;

	public BotButton(final String label, final String payload) {
		this.label = label;
		this.payload = payload;
	}

	public static IBotChoice of(final String[] params) {
		Assertion.check().isTrue(params.length == 2, "BotButton need exactly 2 params");
		//--
		return new BotButton(params[0], params[1]);
	}

	/**
	 * @return the label
	 */
	@Override
	public String getLabel() {
		return label;
	}

	/**
	 * @return the payload
	 */
	@Override
	public String getPayload() {
		return payload;
	}

	@Override
	public String[] exportParams() {
		return List.of(label, payload).toArray(String[]::new);
	}

}
