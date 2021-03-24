package io.vertigo.chatbot.engine.model.choice;

/**
 * Choice in the most generic sense.<br>
 * This class can be used as a parent class for all types of user choices, for example a button or a carrousel.
 *
 * @author skerdudou
 */
public interface IBotChoice {

	/**
	 * @return the label
	 */
	String getLabel();

	/**
	 * @param label the label to set
	 */
	void setLabel(String label);

	/**
	 * @return the payload
	 */
	String getPayload();

	/**
	 * @param payload the payload to set
	 */
	void setPayload(String payload);

}
