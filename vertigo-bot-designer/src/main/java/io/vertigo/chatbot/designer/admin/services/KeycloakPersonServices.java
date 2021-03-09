package io.vertigo.chatbot.designer.admin.services;

import java.util.List;

import javax.inject.Inject;

import org.keycloak.representations.IDToken;

import io.vertigo.chatbot.designer.dao.commons.PersonDAO;
import io.vertigo.chatbot.designer.domain.commons.Person;
import io.vertigo.chatbot.designer.domain.commons.PersonRoleEnum;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.lang.Assertion;
import io.vertigo.core.node.component.Component;

@Transactional
public class KeycloakPersonServices implements Component {

	@Inject
	private PersonDAO personDAO;

	/**
	 * Init person and save it
	 * Empty collections is the default value for chatbot
	 * 
	 * @param login of the user
	 * @param rol
	 * @param name
	 * @return the person created
	 */
	public Person initPerson(final String login, final String name, final String rol) {
		Person newPerson = new Person();
		newPerson.setLogin(login.toLowerCase());
		newPerson.setName(name);
		newPerson.setRolCd(rol);
		newPerson = createPerson(newPerson);
		//By default user has no profil
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
	 * Get the role from keycloak groups
	 * By default the groups RUser is used
	 * 
	 * @param token
	 * @return the role
	 */
	public String getRoleFromToken(IDToken token) {
		List<?> groups = (List<?>) token.getOtherClaims().get("groups");
		if (groups.isEmpty()) {
			return PersonRoleEnum.RUser.name();
		}
		return (String) groups.get(0);
	}

	/**
	 * Get the username from the auth token
	 * 
	 * @param token
	 * @return the name of connected user
	 */
	public String getNameFromToken(IDToken token) {
		final String name = token.getName() != null ? token.getName() : "";
		return name;
	}

	public Person getPersonToConnect(final Long perId) {
		Assertion.check().isNotNull(perId);
		// ---
		final Person person = personDAO.get(perId);
		return person;
	}
}
