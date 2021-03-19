/**
 * vertigo - simple java starter
 *
 * Copyright (C) 2020, Vertigo.io, team@vertigo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vertigo.chatbot.designer.commons;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.vertigo.account.security.UserSession;
import io.vertigo.chatbot.designer.domain.commons.Person;
import io.vertigo.datastore.filestore.model.FileInfoURI;

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

	public List<FileInfoURI> getTmpFiles() {
		return tmpFiles;
	}

	/** {@inheritDoc} */
	@Override
	public Locale getLocale() {
		return Locale.FRANCE;
	}

	/** {@inheritDoc} */
	@Override
	public ZoneId getZoneId() {
		return ZoneId.of("Europe/Paris");
	}

}
