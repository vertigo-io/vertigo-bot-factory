package io.vertigo.chatbot.authorization;

import io.vertigo.account.authorization.definitions.Authorization;
import io.vertigo.account.authorization.definitions.AuthorizationName;
import io.vertigo.account.authorization.definitions.OperationName;
import io.vertigo.core.node.Node;
import io.vertigo.chatbot.commons.domain.Chatbot;

/**
 * Warning. This class is generated automatically !
 *
 * Enum of the security authorizations and operations associated with secured entities known by the application.
 */
public final class SecuredEntities {

	private SecuredEntities() {
		//private constructor
	}

	/**
	 * Authorizations of Chatbot.
	 */
	public enum ChatbotAuthorizations implements AuthorizationName {
		/** auth admFct. */
		AtzChatbot$admFct,
		/** Visibilité globale pour le super admin. Sans régles = toujours ok.. */
		AtzChatbot$admin,
		/** auth contributeur. */
		AtzChatbot$contributeur,
		/** Visibilité si affecté à l'utilisateur'.. */
		AtzChatbot$read,
		/** auth visiteur. */
		AtzChatbot$visiteur,
		/** Modification si affecté à l'utilisateur.. */
		AtzChatbot$write;

		/**
		 * Get the associated authorization.
		 *
		 * @param code authorization code
		 * @return authorization
		 */
		public static Authorization of(final String code) {
			return Node.getNode().getDefinitionSpace().resolve(code, Authorization.class);
		}

		/**
		 * Get the associated authorization.
		 *
		 * @return role
		 */
		public Authorization getAuthorization() {
			return of(name());
		}
	}

	/**
	 * Operations of Chatbot.
	 */
	public enum ChatbotOperations implements OperationName<Chatbot> {
		/** auth admFct. */
		admFct,
		/** Visibilité globale pour le super admin. Sans régles = toujours ok.. */
		admin,
		/** auth contributeur. */
		contributeur,
		/** Visibilité si affecté à l'utilisateur'.. */
		read,
		/** auth visiteur. */
		visiteur,
		/** Modification si affecté à l'utilisateur.. */
		write;
	}
}
