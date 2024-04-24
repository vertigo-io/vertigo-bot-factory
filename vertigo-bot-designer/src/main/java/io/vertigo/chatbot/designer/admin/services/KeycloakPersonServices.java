package io.vertigo.chatbot.designer.admin.services;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

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
	 * @param name
	 * @param email
	 * @param role
	 * @return the person created
	 */
	public void initPerson(final String login, final String name, final String email, final String role) {
		Person newPerson = new Person();
		newPerson.setLogin(login.toLowerCase());
		newPerson.setName(name);
		newPerson.setRolCd(role);
		newPerson.setEmail(email);
		createPerson(newPerson);
	}

	/**
	 * Create a person
	 *
	 * @param newPerson to create
	 * @return the person after the creation
	 */
	public Person createPerson(final Person newPerson) {
		return personDAO.create(newPerson);

	}

	public String getRoleFromClaims(final Map<String, Object> claims) {
		final List<?> groups = (List<?>) claims.get("groups");
		if (claims.isEmpty() || groups.isEmpty()) {
			return PersonRoleEnum.RUser.name();
		}
		return (String) groups.get(0);
	}

	public Person getPersonToConnect(final Long perId) {
		Assertion.check().isNotNull(perId);
		return personDAO.get(perId);
	}
}
