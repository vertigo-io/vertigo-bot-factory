package io.vertigo.chatbot.designer.utils;

import java.util.Optional;

import io.vertigo.account.authorization.VSecurityException;
import io.vertigo.account.security.VSecurityManager;
import io.vertigo.chatbot.designer.commons.DesignerUserSession;
import io.vertigo.chatbot.designer.domain.commons.Person;
import io.vertigo.core.locale.MessageText;
import io.vertigo.core.node.Node;

public class UserSessionUtils {

	private UserSessionUtils() {
		//classe utilitaire
	}

	private static VSecurityManager securityManager = Node.getNode().getComponentSpace().resolve(VSecurityManager.class);

	public static DesignerUserSession getUserSession() {
		return securityManager.<DesignerUserSession>getCurrentUserSession().orElseThrow(() -> new VSecurityException(MessageText.of("No active session found")));
	}

	public static boolean isAuthenticated() {
		final Optional<DesignerUserSession> userSession = securityManager.<DesignerUserSession>getCurrentUserSession();
		return !userSession.isPresent() ? false : userSession.get().isAuthenticated();
	}

	public static Person getLoggedPerson() {
		return getUserSession().getLoggedPerson();
	}

}
