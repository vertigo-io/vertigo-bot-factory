package io.vertigo.chatbot.engine.model;

import io.vertigo.ai.bt.BTNode;
import io.vertigo.ai.bt.BTStatus;
import io.vertigo.core.lang.VSystemException;

/**
 * Choice in the most generic sense.<br>
 * This class can be used as a parent class for all types of user choices, for example a button or a carrousel.<br>
 * Implementers must declare a method '<i>public static IBotChoice of(final String[] params)</i>'
 *
 * @author skerdudou
 */
public interface IBotChoice extends BTNode {

	/**
	 * @return the label
	 */
	String getLabel();

	/**
	 * @return the payload
	 */
	String getPayload();

	/**
	 * Export params as String that can be used by static 'of' method to reconstruct the instance.
	 *
	 * @return all the parameters
	 */
	String[] exportParams();

	@Override
	default BTStatus eval() {
		throw new VSystemException("Choices must be inside a choose section");
	}
}
