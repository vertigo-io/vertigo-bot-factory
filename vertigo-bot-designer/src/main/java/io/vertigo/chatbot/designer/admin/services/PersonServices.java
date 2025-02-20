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
import io.vertigo.chatbot.designer.builder.services.bot.ChatbotProfilServices;
import io.vertigo.chatbot.designer.dao.commons.PersonDAO;
import io.vertigo.chatbot.designer.domain.commons.Person;
import io.vertigo.commons.transaction.Transactional;
import io.vertigo.core.node.component.Component;
import io.vertigo.datamodel.criteria.Criterions;
import io.vertigo.datamodel.data.model.DtList;
import io.vertigo.datamodel.data.model.DtListState;

import static io.vertigo.chatbot.designer.utils.ListUtils.MAX_ELEMENTS_PLUS_ONE;


@Transactional
@Secured("SuperAdm")
public class PersonServices implements Component {

	@Inject
	private PersonDAO personDAO;

	@Inject
	private ChatbotProfilServices chatbotProfilServices;

	public DtList<Person> getAllPersons() {
		return personDAO.findAll(Criterions.alwaysTrue(), DtListState.of(MAX_ELEMENTS_PLUS_ONE));
	}

	public void deletePerson(final Long perId) {
		chatbotProfilServices.deleteProfilByPerId(perId);
		personDAO.delete(perId);
	}
}
