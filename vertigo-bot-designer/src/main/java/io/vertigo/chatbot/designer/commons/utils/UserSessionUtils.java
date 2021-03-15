package io.vertigo.chatbot.designer.commons.utils;

import java.util.Optional;

import javax.inject.Inject;

import io.vertigo.account.authorization.VSecurityException;
import io.vertigo.account.security.VSecurityManager;
import io.vertigo.chatbot.designer.commons.DesignerUserSession;
import io.vertigo.chatbot.designer.domain.commons.Person;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.locale.MessageText;
import io.vertigo.core.node.component.Component;

@Transactional
public class UserSessionUtils implements Component {

	@Inject
	private VSecurityManager securityManager;

	public DesignerUserSession getUserSession() {
		return securityManager.<DesignerUserSession>getCurrentUserSession().orElseThrow(() -> new VSecurityException(MessageText.of("No active session found")));
	}

	public boolean isAuthenticated() {
		final Optional<DesignerUserSession> userSession = securityManager.<DesignerUserSession>getCurrentUserSession();
		return !userSession.isPresent() ? false : userSession.get().isAuthenticated();
	}

	public Person getLoggedPerson() {
		return getUserSession().getLoggedPerson();
	}

}
