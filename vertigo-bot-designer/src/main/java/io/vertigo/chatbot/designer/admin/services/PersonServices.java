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

import javax.inject.Inject;

import io.vertigo.account.authorization.annotations.Secured;
import io.vertigo.chatbot.designer.admin.person.PersonPAO;
import io.vertigo.chatbot.designer.dao.commons.PersonDAO;
import io.vertigo.chatbot.designer.domain.commons.Person;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;

@Transactional
@Secured("AdmPer")
public class PersonServices implements Component {

	@Inject
	private PersonPAO personPAO;

	@Inject
	private PersonDAO personDAO;

	public DtList<Person> getAllPersons() {
		return personDAO.findAll(Criterions.alwaysTrue(), DtListState.of(100));
	}

	public Long getAdminPerNumber() {
		return personPAO.countAllAdminPer();
	}

	public Person getPersonById(final Long perId) {
		Assertion.check().isNotNull(perId);
		// ---
		final Person person = personDAO.get(perId);
		return person;
	}

	/**
	 * Save the person
	 *
	 * @param person person to save
	 * @return the person updated
	 */
	public Person savePerson(final Person person) {
		Assertion.check().isNotNull(person);
		final Person savedPerson = personDAO.save(person);
		return savedPerson;
	}

	public void deletePerson(final Person person) {
		final Long perId = person.getPerId();
		personPAO.removeAllChaPerRightByPerId(perId);
		personDAO.delete(perId);
	}
}
