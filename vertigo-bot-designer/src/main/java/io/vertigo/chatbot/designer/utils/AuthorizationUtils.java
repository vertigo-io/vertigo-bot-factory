package io.vertigo.chatbot.designer.utils;

import io.vertigo.account.authorization.AuthorizationManager;
import io.vertigo.account.authorization.VSecurityException;
import io.vertigo.account.authorization.definitions.OperationName;
import io.vertigo.core.locale.MessageText;
import io.vertigo.core.node.Node;
import io.vertigo.datamodel.structure.model.KeyConcept;

public class AuthorizationUtils {

	private AuthorizationUtils() {
		//Classe utilitaire
	}

	private static final AuthorizationManager authorizationManager = Node.getNode().getComponentSpace().resolve(AuthorizationManager.class);

	public static <K extends KeyConcept> void checkRights(final K keyConcept, final OperationName<K> operation, final String message) {
		if (!authorizationManager.isAuthorized(keyConcept, operation)) {
			throw new VSecurityException(MessageText.of(message));//no too sharp info here : may use log
		}
	}

	public static <K extends KeyConcept> boolean isAuthorized(final K keyConcept, final OperationName<K> operation) {
		return authorizationManager.isAuthorized(keyConcept, operation);
	}
}
