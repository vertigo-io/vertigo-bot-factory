package io.vertigo.chatbot.authorization;

import io.vertigo.account.authorization.definitions.Authorization;
import io.vertigo.account.authorization.definitions.AuthorizationName;
import io.vertigo.core.node.Node;

/**
 * Warning. This class is generated automatically !
 *
 * Enum of the authorizations known by the application.
 */
public enum GlobalAuthorizations implements AuthorizationName {

	/**
	 * BotUser.
	 */
	AtzBotUser,
	/**
	 * Super Adm of the application.
	 */
	AtzSuperAdm;

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
