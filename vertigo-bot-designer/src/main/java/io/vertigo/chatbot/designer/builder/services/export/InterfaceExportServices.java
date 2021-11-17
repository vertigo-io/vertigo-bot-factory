package io.vertigo.chatbot.designer.builder.services.export;

import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.VUserException;

@Transactional
public interface InterfaceExportServices {

	/*
	* Return an error message with the line concerned
	*/
	public default String lineError(final int i) {
		return "[Line " + i + "] ";
	}

	public default void errorManagement(final int i, final String erreur) {
		final StringBuilder errorMessage = new StringBuilder(lineError(i));
		errorMessage.append(erreur);
		throw new VUserException(errorMessage.toString());
	}

}
