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
package io.vertigo.chatbot.commons.domain;

import java.io.Serializable;

import io.vertigo.dynamo.domain.model.MasterDataEnum;
import io.vertigo.dynamo.domain.model.UID;

public enum PersonRoleEnum implements MasterDataEnum<io.vertigo.chatbot.commons.domain.PersonRole> {

	RAdmin("RAdmin"), //
	RUser("RUser")
	;

	private final Serializable entityId;

	private PersonRoleEnum(final Serializable id) {
		entityId = id;
	}

	@Override
	public UID<io.vertigo.chatbot.commons.domain.PersonRole> getEntityUID() {
		return UID.of(io.vertigo.chatbot.commons.domain.PersonRole.class, entityId);
	}

}
