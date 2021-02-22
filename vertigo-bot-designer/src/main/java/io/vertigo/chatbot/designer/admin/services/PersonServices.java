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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.vertigo.chatbot.commons.dao.PersonDAO;
import io.vertigo.chatbot.commons.domain.Chatbot;
import io.vertigo.chatbot.commons.domain.Person;
import io.vertigo.chatbot.designer.admin.person.PersonPAO;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.structure.definitions.association.DtListURIForNNAssociation;
import io.vertigo.datamodel.structure.model.DtList;
import io.vertigo.datamodel.structure.model.DtListState;
import io.vertigo.datamodel.structure.model.UID;
import io.vertigo.datastore.entitystore.EntityStoreManager;

@Transactional
public class PersonServices implements Component {

	@Inject
	private PersonPAO personPAO;

	@Inject
	private PersonDAO personDAO;

	@Inject
	private EntityStoreManager entityStoreManager;

	public DtList<Person> getAllPersons() {
		return personDAO.findAll(Criterions.alwaysTrue(), DtListState.of(100));
	}

	public Person getPersonById(final Long perId) {
		Assertion.check().isNotNull(perId);
		// ---
		final Person person = personDAO.get(perId);
		person.chatbots().load();
		return person;
	}

	/**
	 * Save the person with its chatbots
	 * 
	 * @param person person to save
	 * @param chatbotsSelectedStr the chatbots list in string
	 * @return the person updated
	 */
	public Person savePerson(final Person person, final String chatbotsSelectedStr) {
		Assertion.check().isNotNull(person);

		final Person savedPerson = personDAO.save(person);

		updateChatBotListUID(savedPerson, createListChatbot(chatbotsSelectedStr));
		return savedPerson;
	}

	/**
	 * Create the chatbot list UID from the chatbots string
	 * 
	 * @param chatbotsSelectedStr the chatbotsString
	 * @return the UID list
	 */
	private List<UID> createListChatbot(final String chatbotsSelectedStr) {
		if (chatbotsSelectedStr.isEmpty()) {
			return Collections.emptyList();
		} else {
			return Arrays.stream(chatbotsSelectedStr.split(","))
					.map(Long::parseLong)
					.map(id -> UID.of(Chatbot.class, id))
					.collect(Collectors.toList());
		}
	}

	/**
	 * Init person and save it
	 * Empty collections is the default value for chatbot
	 * 
	 * @param login of the user
	 * @return the person created
	 */
	public Person initPerson(String login) {
		Person newPerson = new Person();
		newPerson.setLogin(login.toLowerCase());
		newPerson.setName(login);
		newPerson.setRolCd("RUser");
		newPerson = createPerson(newPerson);
		updateChatBotList(newPerson, Collections.emptyList());
		return newPerson;
	}

	/**
	 * Create a person
	 * 
	 * @param newPerson to create
	 * @return the person after the creation
	 */
	public Person createPerson(Person newPerson) {
		return personDAO.create(newPerson);

	}

	/**
	 * Update the chatbots list of a person
	 * 
	 * @param person to update
	 * @param chatbotIds the new list
	 */
	private void updateChatBotList(Person person, List<Chatbot> chatbots) {
		List<UID> chatbotIds = chatbots.stream().map(id -> UID.of(Chatbot.class, id)).collect(Collectors.toList());
		this.updateChatBotListUID(person, chatbotIds);
	}

	private void updateChatBotListUID(Person person, List<UID> chatbotIds) {
		entityStoreManager.getBrokerNN()
				.updateNN(DtListURIForNNAssociation.class.cast(person.chatbots().getDtListURI()), chatbotIds);
	}

	public void deletePerson(final Person person) {
		Long perId = person.getPerId();
		this.personPAO.removeAllChaPerRightByPerId(perId);
		this.personDAO.delete(perId);
	}
}
