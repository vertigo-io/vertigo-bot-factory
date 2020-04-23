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
package io.vertigo.chatbot.designer.admin.services;

import java.util.List;

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.account.impl.authentication.PasswordHelper;
import io.vertigo.chatbot.commons.dao.PersonDAO;
import io.vertigo.chatbot.commons.domain.Person;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.component.Component;
import io.vertigo.dynamo.criteria.Criterions;
import io.vertigo.dynamo.domain.metamodel.association.DtListURIForNNAssociation;
import io.vertigo.dynamo.domain.model.DtList;
import io.vertigo.dynamo.domain.model.DtListState;
import io.vertigo.dynamo.domain.model.UID;
import io.vertigo.dynamo.store.StoreManager;
import io.vertigo.lang.Assertion;

@Transactional
@Secured("AdmPer")
public class PersonServices implements Component {

	@Inject
	private PersonDAO personDAO;

	@Inject
	private StoreManager storeManager;

	public DtList<Person> getAllPersons() {
		return personDAO.findAll(Criterions.alwaysTrue(), DtListState.of(100));
	}

	public Person getPersonById(final Long perId) {
		Assertion.checkNotNull(perId);
		// ---
		final Person person = personDAO.get(perId);
		person.chatbots().load();
		return person;
	}

	public Person savePerson(final Person person, final List<UID> chatbotIds) {
		Assertion.checkNotNull(person);
		// ---
		if (person.getPasswordNew() != null && !person.getPasswordNew().isEmpty()) {
			final PasswordHelper passwordHelper = new PasswordHelper();
			final String encodedPassword = passwordHelper.createPassword(person.getPasswordNew());
			person.setPassword(encodedPassword);
		}
		final Person savedPerson = personDAO.save(person);
		storeManager.getDataStore().getBrokerNN()
				.updateNN(DtListURIForNNAssociation.class.cast(person.chatbots().getDtListURI()), chatbotIds);
		return savedPerson;
	}
}
