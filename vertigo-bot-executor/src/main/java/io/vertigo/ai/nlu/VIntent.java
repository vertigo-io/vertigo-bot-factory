package io.vertigo.ai.nlu;

import io.vertigo.core.lang.Assertion;

/**
 * An intent is the classification of what a user wants to say.
 *
 * @author skerdudou
 */
public final class VIntent {

	private final String code;

	private final String description;

	private VIntent(final String code, final String description) {
		Assertion.check().isNotBlank(code);
		//--
		this.code = code;
		this.description = description;
	}

	public static VIntent of(final String code, final String description) {
		return new VIntent(code, description);
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

}
