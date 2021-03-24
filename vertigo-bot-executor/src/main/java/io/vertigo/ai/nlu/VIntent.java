package io.vertigo.ai.nlu;

import io.vertigo.core.lang.Assertion;

/**
 * An intent is the classification of what a user wants to say.
 *
 * @author skerdudou
 */
public final class VIntent {

	private final String code;

	private VIntent(final String code) {
		Assertion.check().isNotBlank(code);
		//--
		this.code = code;
	}

	public static VIntent of(final String code) {
		return new VIntent(code);
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

}
