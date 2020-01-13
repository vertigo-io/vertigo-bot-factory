package io.vertigo.chatbot.designer.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.vertigo.account.security.UserSession;
import io.vertigo.chatbot.commons.domain.Person;
import io.vertigo.dynamo.domain.model.FileInfoURI;

/**
 * UserSession.
 *
 * @author skerdudou.
 */
public class DesignerUserSession extends UserSession {

	private static final long serialVersionUID = 6554705638477598434L;

	private Person loggedPerson;

	private final List<FileInfoURI> tmpFiles = new ArrayList<>();

	/**
	 * @return the loggedPerson
	 */
	public Person getLoggedPerson() {
		return loggedPerson;
	}

	/**
	 * @param loggedPerson the loggedPerson to set
	 */
	public void setLoggedPerson(final Person loggedPerson) {
		this.loggedPerson = loggedPerson;
	}

	/** {@inheritDoc} */
	@Override
	public Locale getLocale() {
		return Locale.FRANCE;
	}

	public List<FileInfoURI> getTmpFiles() {
		return tmpFiles;
	}


}
