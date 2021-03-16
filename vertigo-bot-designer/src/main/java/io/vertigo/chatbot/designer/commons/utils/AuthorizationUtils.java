package io.vertigo.chatbot.designer.commons.utils;

import javax.inject.Inject;

import io.vertigo.account.authorization.AuthorizationManager;
import io.vertigo.account.authorization.VSecurityException;
import io.vertigo.account.authorization.definitions.OperationName;
import io.vertigo.core.locale.MessageText;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.structure.model.KeyConcept;

public class AuthorizationUtils implements Component {

	@Inject
	private static AuthorizationManager authorizationManager;

	public static <K extends KeyConcept> void checkRights(final K keyConcept, final OperationName<K> operation, final String message) {
		if (!authorizationManager.isAuthorized(keyConcept, operation)) {
			throw new VSecurityException(MessageText.of(message));//no too sharp info here : may use log
		}
	}
}
